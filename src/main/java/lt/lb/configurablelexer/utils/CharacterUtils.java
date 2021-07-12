package lt.lb.configurablelexer.utils;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.io.IOException;
import java.io.Reader;

/**
 * Utility class to write tokenizers or token filters.
 *
 * Copy from Lucene.
 */
public class CharacterUtils {

    /**
     * Creates a new {@link CharacterBuffer} and allocates a <code>char[]</code>
     * of the given bufferSize.
     *
     * @param bufferSize the internal char buffer size, must be
     * <code>&gt;= 2</code>
     * @return a new {@link CharacterBuffer} instance.
     */
    public static CharacterBuffer newCharacterBuffer(final int bufferSize) {
        if (bufferSize < 2) {
            throw new IllegalArgumentException("buffersize must be >= 2");
        }
        return new CharacterBuffer(new char[bufferSize], 0, 0);
    }

    /**
     * Converts each unicode codepoint to lowerCase via
     * {@link Character#toLowerCase(int)} starting at the given offset.
     *
     * @param buffer the char buffer to lowercase
     * @param offset the offset to start at
     * @param limit the max char in the buffer to lower case
     */
    public static void toLowerCase(final char[] buffer, final int offset, final int limit) {
        assert buffer.length >= limit;
        assert 0 <= offset && offset <= buffer.length;
        for (int i = offset; i < limit;) {
            i += Character.toChars(
                    Character.toLowerCase(
                            Character.codePointAt(buffer, i, limit)), buffer, i);
        }
    }

    /**
     * Converts each unicode codepoint to UpperCase via
     * {@link Character#toUpperCase(int)} starting at the given offset.
     *
     * @param buffer the char buffer to UPPERCASE
     * @param offset the offset to start at
     * @param limit the max char in the buffer to lower case
     */
    public static void toUpperCase(final char[] buffer, final int offset, final int limit) {
        assert buffer.length >= limit;
        assert 0 <= offset && offset <= buffer.length;
        for (int i = offset; i < limit;) {
            i += Character.toChars(
                    Character.toUpperCase(
                            Character.codePointAt(buffer, i, limit)), buffer, i);
        }
    }

    /**
     * Converts a sequence of Java characters to a sequence of unicode code
     * points.
     *
     * @return the number of code points written to the destination buffer
     */
    public static int toCodePoints(char[] src, int srcOff, int srcLen, int[] dest, int destOff) {
        if (srcLen < 0) {
            throw new IllegalArgumentException("srcLen must be >= 0");
        }
        int codePointCount = 0;
        for (int i = 0; i < srcLen;) {
            final int cp = Character.codePointAt(src, srcOff + i, srcOff + srcLen);
            final int charCount = Character.charCount(cp);
            dest[destOff + codePointCount++] = cp;
            i += charCount;
        }
        return codePointCount;
    }

    /**
     * Converts a sequence of unicode code points to a sequence of Java
     * characters.
     *
     * @return the number of chars written to the destination buffer
     */
    public static int toChars(int[] src, int srcOff, int srcLen, char[] dest, int destOff) {
        if (srcLen < 0) {
            throw new IllegalArgumentException("srcLen must be >= 0");
        }
        int written = 0;
        for (int i = 0; i < srcLen; ++i) {
            written += Character.toChars(src[srcOff + i], dest, destOff + written);
        }
        return written;
    }

    /**
     * Fills the {@link CharacterBuffer} with characters read from the given
     * reader {@link Reader}. This method tries to read <code>numChars</code>
     * characters into the {@link CharacterBuffer}, each call to fill will start
     * filling the buffer from offset <code>0</code> up to
     * <code>numChars</code>. In case code points can span across 2 java
     * characters, this method may only fill <code>numChars - 1</code>
     * characters in order not to split in the middle of a surrogate pair, even
     * if there are remaining characters in the {@link Reader}.
     * <p>
     * This method guarantees that the given {@link CharacterBuffer} will never
     * contain a high surrogate character as the last element in the buffer
     * unless it is the last available character in the reader. In other words,
     * high and low surrogate pairs will always be preserved across buffer
     * boarders.
     * </p>
     * <p>
     * A return value of <code>false</code> means that this method call
     * exhausted the reader, but there may be some bytes which have been read,
     * which can be verified by checking whether
     * <code>buffer.getLength() &gt; 0</code>.
     * </p>
     *
     * @param buffer the buffer to fill.
     * @param reader the reader to read characters from.
     * @param numChars the number of chars to read
     * @return <code>false</code> if and only if reader.read returned -1 while
     * trying to fill the buffer
     * @throws IOException if the reader throws an {@link IOException}.
     */
    public static boolean fill(CharacterBuffer buffer, Reader reader, int numChars) throws IOException {
        assert buffer.getBuffer().length >= 2;
        if (numChars < 2 || numChars > buffer.getBuffer().length) {
            throw new IllegalArgumentException("numChars must be >= 2 and <= the buffer size");
        }
        final char[] charBuffer = buffer.getBuffer();
        buffer.setOffset(0);
        final int offset;

        // Install the previously saved ending high surrogate:
        if (buffer.lastTrailingHighSurrogate != 0) {
            charBuffer[0] = buffer.lastTrailingHighSurrogate;
            buffer.lastTrailingHighSurrogate = 0;
            offset = 1;
        } else {
            offset = 0;
        }

        final int read = readFully(reader, charBuffer, offset, numChars - offset);

        buffer.setLength(offset + read);
        final boolean result = buffer.getLength() == numChars;
        if (buffer.getLength() < numChars) {
            // We failed to fill the buffer. Even if the last char is a high
            // surrogate, there is nothing we can do
            return result;
        }

        if (Character.isHighSurrogate(charBuffer[buffer.getLength() - 1])) {
            int len = buffer.getLength() - 1;
            buffer.setLength(len);
            buffer.lastTrailingHighSurrogate = charBuffer[len];
        }
        return result;
    }

    /**
     * Convenience method which calls
     * <code>fill(buffer, reader, buffer.buffer.length)</code>.
     */
    public static boolean fill(CharacterBuffer buffer, Reader reader) throws IOException {
        return fill(buffer, reader, buffer.getBuffer().length);
    }

    static int readFully(Reader reader, char[] dest, int offset, int len) throws IOException {
        int read = 0;
        while (read < len) {
            final int r = reader.read(dest, offset + read, len - read);
            if (r == -1) {
                break;
            }
            read += r;
        }
        return read;
    }

    public static char[] resizeBuffer(char[] buffer, int newSize) {
        if (buffer.length < newSize) {
            // Not big enough; create a new array with slight
            // over allocation and preserve content
            final char[] newCharBuffer = new char[oversize(newSize, Character.BYTES)];
            System.arraycopy(buffer, 0, newCharBuffer, 0, buffer.length);
            return newCharBuffer;
        }
        return buffer;
    }
    public static final int MAX_ARRAY_LENGTH = Integer.MAX_VALUE - 8;

    public static int oversize(int minTargetSize, int bytesPerElement) {

        if (minTargetSize < 0) {
            // catch usage that accidentally overflows int
            throw new IllegalArgumentException("invalid array size " + minTargetSize);
        }

        if (minTargetSize == 0) {
            // wait until at least one element is requested
            return 0;
        }

        if (minTargetSize > MAX_ARRAY_LENGTH) {
            throw new IllegalArgumentException("requested array size " + minTargetSize + " exceeds maximum array in java (" + MAX_ARRAY_LENGTH + ")");
        }

        // asymptotic exponential growth by 1/8th, favors
        // spending a bit more CPU to not tie up too much wasted
        // RAM:
        int extra = minTargetSize >> 3;

        if (extra < 3) {
            // for very small arrays, where constant overhead of
            // realloc is presumably relatively high, we grow
            // faster
            extra = 3;
        }

        int newSize = minTargetSize + extra;

        // add 7 to allow for worst case byte alignment addition below:
        if (newSize + 7 < 0 || newSize + 7 > MAX_ARRAY_LENGTH) {
            // int overflowed, or we exceeded the maximum array length
            return MAX_ARRAY_LENGTH;
        }

        // round up to 8 byte alignment in 64bit env
        switch (bytesPerElement) {
            case 4:
                // round up to multiple of 2
                return (newSize + 1) & 0x7ffffffe;
            case 2:
                // round up to multiple of 4
                return (newSize + 3) & 0x7ffffffc;
            case 1:
                // round up to multiple of 8
                return (newSize + 7) & 0x7ffffff8;
            case 8:
            // no rounding
            default:
                // odd (invalid?) size
                return newSize;
        }
    }

}
