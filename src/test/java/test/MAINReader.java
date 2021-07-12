package test;

import java.io.Reader;
import lt.lb.configurablelexer.utils.OverheadReader;
import lt.lb.configurablelexer.utils.ReusableStringReader;

/**
 *
 * @author laim0nas100
 */
public class MAINReader {

    public static void main(String[] args) throws Exception {
        Reader input = new ReusableStringReader("123456789_123456789_");

        OverheadReader reader = new OverheadReader(input, 6);
        StringBuilder sb = new StringBuilder();
        StringBuilder builder = new StringBuilder();
        while (true) {
            char[] buff = new char[5];
            int read = reader.read(buff);
            if (read == -1) {
                break;
            }
            sb.append(read).append("\n");
            builder.append(buff, 0, read);
            sb.append(reader.hasOverhead()).append("\n");
        }
        sb.append(builder);
        System.out.println(sb);
    }
}
