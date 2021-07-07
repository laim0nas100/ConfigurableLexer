package lt.lb.configurablelexer.token;

import java.io.IOException;
import java.io.Reader;
import java.util.Objects;
import lt.lb.commons.DLog;
import lt.lb.configurablelexer.token.CharInfo.CharInfoDefault;
import lt.lb.configurablelexer.utils.CharacterBuffer;
import lt.lb.configurablelexer.utils.CharacterUtils;

/**
 *
 * @author laim0nas100
 * @param <T>
 */
public abstract class BaseTokenizer<T extends ConfToken> extends BufferedConfTokenizer<T> {

    protected Reader input;
    protected int offset, bufferIndex, ioBufferLen, tokenLen;
    protected char[] buffer;
    CharacterBuffer ioBuffer;
    protected int maxTokenLen = 255;

    public BaseTokenizer() {
    }

    @Override
    public void reset(Reader input) {
        this.input = Objects.requireNonNull(input);
        buffer = new char[CharacterUtils.oversize(2, Character.BYTES)];
        offset = 0;
        bufferIndex = 0;
        ioBufferLen = 0;
        tokenLen = 0;
        ioBuffer = CharacterUtils.newCharacterBuffer(256);
        currentTokenIndex = 0;
        bufferedTokens = ConfTokenBuffer.empty();
        getCallbacks().reset();
    }

    @Override
    public boolean readToBuffer() throws Exception {
        int length = 0;
        while (true) {
            if (bufferIndex >= ioBufferLen) {
                //TODO, figure out the last char in reader
                offset += ioBufferLen;
                CharacterUtils.fill(ioBuffer, input); // read supplementary char aware with CharacterUtils
                if (ioBuffer.getLength() == 0) {
                    ioBufferLen = 0; // so next offset += dataLen won't decrement offset
                    if (length > 0) {
                        break;
                    } else {
                        return false;
                    }
                }
                ioBufferLen = ioBuffer.getLength();
                bufferIndex = 0;
            }
            // use CharacterUtils here to support < 3.1 UTF-16 code unit behavior if the char based methods are gone
            final int c = Character.codePointAt(ioBuffer.getBuffer(), bufferIndex, ioBuffer.getLength());
            final int charCount = Character.charCount(c);
            bufferIndex += charCount;

            boolean isTokenChar = isTokenChar(c);
            boolean isBreakChar = isBreakChar(c);
//            String str = Character.toString(c);
//            DLog.print(str);
            charListener(CharInfoDefault.of(isTokenChar, isBreakChar, false), c);
            if (isTokenChar) {               // if it's a token char
                if (length >= buffer.length - 1) { // check if a supplementary could run out of bounds
                    buffer = CharacterUtils.resizeBuffer(buffer, 2 + length);// make sure a supplementary fits in the buffer
                }
                length += Character.toChars(c, buffer, length); // buffer it, normalized
                if (length >= maxTokenLen) { // buffer overflow! make sure to check for >= surrogate pair could break == test
                    break;
                }
            } 
            if (isBreakChar || (!isTokenChar && length > 0)) {           // at non-Letter with chars or a break character
                break;                           // return 'em
            }
        }
        currentTokenIndex = 0;
        this.bufferedTokens = constructTokens(buffer, 0, length);

//        DLog.print(String.valueOf(buffer, offset, length), length, bufferedTokens);
        return true;
    }


    @Override
    public void close() throws IOException {
        input.close();
    }

    @Override
    public ConfTokenBuffer<T> constructTokens(char[] buffer, int offset, int length) throws Exception {
        return getCallbacks().constructTokens(buffer, offset, length);
    }

    @Override
    public boolean isTokenChar(int c) {
        return getCallbacks().isTokenChar(c);
    }

    @Override
    public boolean isBreakChar(int c) {
        return getCallbacks().isBreakChar(c);
    }
    
    

    @Override
    public void charListener(CharInfo chInfo, int c) {
        getCallbacks().charListener(chInfo, c);
    }

    protected abstract TokenizerCallbacks<T> getCallbacks();

    public static class DeferredConfTokenizer<T extends ConfToken> implements DelegatingConfTokenizer<T> {

        protected ConfTokenizer<T> deferred;

        public DeferredConfTokenizer() {
        }

        public ConfTokenizer<T> getDeferred() {
            return deferred;
        }

        public void setDeferred(ConfTokenizer<T> deferred) {
            this.deferred = deferred;
        }

        @Override
        public ConfTokenizer<T> delegate() {
            return Objects.requireNonNull(getDeferred(), "DeferredConfTokenizer must not be null");
        }

    }

}
