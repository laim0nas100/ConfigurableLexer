package test;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import lt.lb.commons.DLog;
import lt.lb.configurablelexer.utils.OverheadReader;
import lt.lb.configurablelexer.utils.ReusableStringReader;

/**
 *
 * @author laim0nas100
 */
public class MAINReader {
    public static void main(String[] args) throws Exception{
         DLog main = DLog.main();
        main.async = true;
        main.stackTrace = false;
        main.surroundString = true;
        main.threadName = false;
        DLog.useTimeFormat(main, "HH:mm:ss.SSS ");
        Reader input = new ReusableStringReader("123456789_123456789_");
        
        OverheadReader reader = new OverheadReader(input,6);
        
        StringBuilder builder = new StringBuilder();
        while(true){
            char[] buff = new char[5];
             int read = reader.read(buff);
             if(read == -1){
                 break;
             }
             DLog.print(read);
             builder.append(buff, 0, read);
             DLog.print(reader.hasOverhead());
        }
        DLog.print(builder);
    }
}
