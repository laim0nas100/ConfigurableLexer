package lt.lb.configurablelexer.token;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.IntPredicate;
import lt.lb.configurablelexer.utils.CharacterBuffer;
import lt.lb.configurablelexer.utils.CharacterUtils;

/**
 *
 * @author laim0nas100
 * @param <T>
 */
public abstract class BaseTokenizer<T extends ConfToken> implements ConfTokenizer<T> {

    protected Reader input;
    protected int offset, bufferIndex, ioBufferLen, tokenLen;
    protected char[] buffer;
    CharacterBuffer ioBuffer;
    protected int maxTokenLen = 255;
    protected int currentTokenIndex;
    protected ConfTokenBuffer<T> bufferedTokens;

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
        ioBuffer = CharacterUtils.newCharacterBuffer(4096);
        currentTokenIndex = 0;
        bufferedTokens = ConfTokenBuffer.of();
    }

    @Override
    public boolean readToBuffer() throws Exception {
        int length = 0;
        while (true) {
            if (bufferIndex >= ioBufferLen) {
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
            charListener(isTokenChar, c);
            if (isTokenChar) {               // if it's a token char
                if (length >= buffer.length - 1) { // check if a supplementary could run out of bounds
                    buffer = CharacterUtils.resizeBuffer(buffer, 2 + length);// make sure a supplementary fits in the buffer
                }
                length += Character.toChars(c, buffer, length); // buffer it, normalized
                if (length >= maxTokenLen) { // buffer overflow! make sure to check for >= surrogate pair could break == test
                    break;
                }
            } else if (length > 0) {           // at non-Letter w/ chars
                break;                           // return 'em
            }
        }
        currentTokenIndex = 0;
        this.bufferedTokens = constructTokens(buffer, 0, length);
        return true;
    }

    @Override
    public T getCurrentBufferedToken() throws Exception {
        return this.bufferedTokens.get(this.currentTokenIndex);
    }

    @Override
    public boolean hasNextBufferedToken() {
        return this.bufferedTokens.size() > this.currentTokenIndex + 1;
    }

    @Override
    public T getNextBufferedToken() throws Exception {
        if (this.bufferedTokens.size() > this.currentTokenIndex + 1) {
            this.currentTokenIndex++;
            return getCurrentBufferedToken();
        }
        throw new IllegalStateException("Need to read from input, no more buffered tokens left");
    }

    @Override
    public void close() throws IOException {
        input.close();
    }

    @Override
    public ConfTokenBuffer<T> constructTokens(char[] buffer, int offset, int length) throws Exception {
        return getMainTokenizer().constructTokens(buffer, offset, length);
    }

    @Override
    public boolean isTokenChar(int c) {
        return getMainTokenizer().isTokenChar(c);
    }

    @Override
    public void charListener(boolean isTokenChar, int c) {
        getMainTokenizer().charListener(isTokenChar, c);
    }

    protected abstract ConfTokenizer<T> getMainTokenizer();

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
        public ConfTokenizer<T> getDelegate() {
            return Objects.requireNonNull(getDeferred(), "DeferredConfTokenizer must not be null");
        }

    }

}
