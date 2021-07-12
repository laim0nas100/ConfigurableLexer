package lt.lb.configurablelexer.parse;

import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.ConfTokenizer;
import lt.lb.configurablelexer.utils.BufferedIterator;

/**
 *
 * @author laim0nas100
 */
public interface MatchedTokenProducer<T extends ConfToken> extends BufferedIterator<MatchedTokens<T>> {

    public static class MatchedTokenProducerException extends Exception {

        public MatchedTokenProducerException(String message) {
            super(message);
        }

    }

    public MatchedTokenProducer withTokenizer(ConfTokenizer<T> tokenizer);

}
