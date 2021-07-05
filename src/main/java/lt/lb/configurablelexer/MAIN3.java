/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.configurablelexer;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import lt.lb.configurablelexer.parse.TokenMatcher;
import lt.lb.configurablelexer.parse.TokenMatchers;

/**
 *
 * @author laim0nas100
 */
public class MAIN3 {
    public static final String OPERATOR_AND = "and";
    public static final String OPERATOR_OR = "or";
    public static final String OPERATOR_NOT = "not";

    public static final String OPERATOR_WILD_QUESTION = "?";
    public static final String OPERATOR_WILD_STAR = "*";
    public static final String OPERATOR_WILD_QUESTION_ESC = "\\?";
    public static final String OPERATOR_WILD_STAR_ESC = "\\*";

    public static final TokenMatcher and = TokenMatchers.exact(OPERATOR_AND);
    public static final TokenMatcher or = TokenMatchers.exact(OPERATOR_OR);
    public static final TokenMatcher not = TokenMatchers.exact(OPERATOR_NOT);

    public static final TokenMatcher wildStar = TokenMatchers.exact(OPERATOR_WILD_STAR).named("wild_star");
    public static final TokenMatcher wildQuestion = TokenMatchers.exact(OPERATOR_WILD_QUESTION).named("wild_question");
    public static final TokenMatcher wildStarEsc = TokenMatchers.exact(OPERATOR_WILD_STAR_ESC).named("wild_star_esc");
    public static final TokenMatcher wildQuestionEsc = TokenMatchers.exact(OPERATOR_WILD_QUESTION_ESC).named("wild_question_escape");
    public static final TokenMatcher literal = TokenMatchers.literalType();

    public static final TokenMatcher concatable = TokenMatchers.or(literal, wildStarEsc, wildQuestionEsc).named("concatable");
    public static final TokenMatcher wildCard = TokenMatchers.or(wildStar, wildQuestion).named("wild_card");
    public static final TokenMatcher wildCard_word = TokenMatchers.concat(wildCard, concatable);
    public static final TokenMatcher word_wildCard = TokenMatchers.concat(concatable, wildCard);
    public static final TokenMatcher wildCard_word_wildcard = TokenMatchers.concat(wildCard, concatable, wildCard);
    public static final TokenMatcher gate = TokenMatchers.or(and, or, not).named("gate");

    static final List<TokenMatcher> asList = Arrays.asList(concatable,
            wildCard_word_wildcard, word_wildCard, wildCard_word, wildCard,
            wildQuestion, wildStar,
            gate, and, or, not
    );

    static final Pattern REPLACE_REPEATING_WILDCARD = Pattern.compile("(\\*+\\?+)|(\\?+\\*+)|(\\*)+");
    public static void main(String[] args) {
        
    }
}
