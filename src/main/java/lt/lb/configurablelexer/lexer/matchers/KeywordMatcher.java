package lt.lb.configurablelexer.lexer.matchers;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author laim0nas100
 */
public class KeywordMatcher extends BreakingSerStringMatcher {

    protected boolean ignoreCase = false;
    protected String keyword = "";

    public KeywordMatcher() {
    }
    
    public KeywordMatcher(String val, boolean ignoreCase, boolean breaking) {
        this.keyword = val;
        this.ignoreCase = ignoreCase;
        this.breaking = breaking;
    }
    
    public KeywordMatcher(String val, boolean breaking) {
        this(val,false,breaking);
    }
    
    public KeywordMatcher(String val) {
        this(val,false,false);
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public Match match(String str, int offset, int localLength) {
        int index = -1;
        int realLen = offset + localLength;
        if (ignoreCase) {
            index = StringUtils.indexOfIgnoreCase(str, getKeyword(), offset);
        } else {
            index = StringUtils.indexOf(str, getKeyword(), offset);
        }
        if (index == -1 || index + getKeyword().length() > realLen) {
            return Match.noMatch();
        }
        int len = getKeyword().length();
        if (index == offset && len == localLength) {
            return makeMatch();
        }else{
            int to = index + len;
            return makeMatch(index, to);
        }
    }

    @Override
    public boolean canBeBreaking() {
        return breaking;
    }

    @Override
    public int minSize() {
        return keyword.length();
    }

    @Override
    public String stringValues() {
        return super.stringValues()+", keyword="+keyword+", ignoreCase="+ignoreCase;
    }

    

}
