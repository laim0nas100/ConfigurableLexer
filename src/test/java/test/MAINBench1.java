package test;

import java.io.BufferedReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicLong;
import lt.lb.configurablelexer.Redirecter;
import lt.lb.configurablelexer.Redirecter;
import lt.lb.configurablelexer.token.simple.SimpleTokenizer;

/**
 *
 * @author laim0nas100
 */
public class MAINBench1 {


    public static void main(String[] args) throws Exception {

        URL resource = Redirecter.class.getResource("/bible.txt");

        BufferedReader input = Files.newBufferedReader(Paths.get(resource.toURI()), StandardCharsets.UTF_8);
        
        SimpleTokenizer tokenizer = new SimpleTokenizer(c->!Character.isWhitespace(c));
        tokenizer.reset(input);

        long time = System.currentTimeMillis();
        AtomicLong tokenCount = new AtomicLong();
        tokenizer.toSimplifiedIterator().forEach(m->{
            tokenCount.incrementAndGet();
        });
        
        time = System.currentTimeMillis() - time;
        System.out.println(time);
        System.out.println(tokenCount.get());

        input.close();

    }
}
