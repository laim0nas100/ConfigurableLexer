package lt.lb.configurablelexer.utils;

import java.io.IOException;
import java.io.Reader;

/**
 *
 * Reader with ability to detect {@link Reader} ending, by reading some overhead
 * to the internal {@code char[]}
 *
 * @author laim0nas100
 */
public class OverheadReader extends Reader implements OverheadAware {

    protected Reader real;
    protected char[] overhead;

    protected int unreadOverheadSize = 0;
    protected int overheadOffset = 0;

    public OverheadReader(Reader real, int overheadLength) {
        this.real = real;
        overhead = new char[Math.max(overheadLength, 1)];
    }

    @Override
    public void reset() throws IOException {
        real.reset();
        overhead = new char[overhead.length];
        unreadOverheadSize = 0;
        overheadOffset = 0;
    }

    public OverheadReader(Reader real) {
        this(real, 1);
    }

    @Override
    public boolean hasOverhead() {
        return unreadOverheadSize > 0;
    }

    @Override
    public int read(char[] c, int off, int len) throws IOException {
        int toRead = Math.min(unreadOverheadSize, len);
        if (toRead > 0) {
            System.arraycopy(overhead, overheadOffset, c, off, toRead);
            unreadOverheadSize -= toRead;
            overheadOffset += toRead;
            len -= toRead;
            off += toRead;
        }

        if (len == 0 && unreadOverheadSize > 0) { // filled during buffer read
            return toRead;
        }
        int realRead = real.read(c, off, len);
        if (realRead == -1) {
            return toRead == 0 ? -1 : toRead;
        }

        // assume overhead is empty since we got here
        int overheadRead = real.read(overhead, 0, overhead.length);
        if (overheadRead != -1) {
            overheadOffset = 0;
            unreadOverheadSize = overheadRead;

        }
        return realRead + toRead;

    }

    @Override
    public void close() throws IOException {
        real.close();
    }

}
