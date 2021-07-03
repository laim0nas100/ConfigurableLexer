package lt.lb.configurablelexer.parse;

import java.util.Iterator;
import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.ConfTokenizer;

/**
 *
 * @author laim0nas100
 */
public interface MatchedTokenProducer {
    
    public static class MatchedTokenProducerException extends Exception {

        public MatchedTokenProducerException(String message) {
            super(message);
        }

    }
    
    public MatchedTokenProducer withTokenizer(ConfTokenizer<ConfToken> tokenizer);
    
}
