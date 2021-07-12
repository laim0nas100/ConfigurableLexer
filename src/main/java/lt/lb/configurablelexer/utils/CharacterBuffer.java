package lt.lb.configurablelexer.utils;

/**
 * A simple IO buffer to use with
 * {@link CharacterUtils#fill(CharacterBuffer, Reader)}.
 *
 * Copied from Lucene and modified.
 */
public class CharacterBuffer {

    private final char[] buffer;
    private int offset;
    private int length;
    char lastTrailingHighSurrogate;

    public CharacterBuffer(char[] buffer, int offset, int length) {
        this.buffer = buffer;
        this.offset = offset;
        this.length = length;
    }

    /**
     * Returns the internal buffer
     *
     * @return the buffer
     */
    public char[] getBuffer() {
        return buffer;
    }

    /**
     * Returns the data offset in the internal buffer.
     *
     * @return the offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Return the length of the data in the internal buffer starting at
     * {@link #getOffset()}
     *
     * @return the length
     */
    public int getLength() {
        return length;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setLastTrailingHighSurrogate(char lastTrailingHighSurrogate) {
        this.lastTrailingHighSurrogate = lastTrailingHighSurrogate;
    }

    /**
     * Resets the CharacterBuffer. All internals are reset to its default
     * values.
     */
    public void reset() {
        offset = 0;
        length = 0;
        lastTrailingHighSurrogate = 0;
    }
}
