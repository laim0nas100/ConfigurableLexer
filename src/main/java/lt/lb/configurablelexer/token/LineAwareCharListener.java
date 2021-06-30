package lt.lb.configurablelexer.token;

/**
 *
 * @author laim0nas100
 */
public class LineAwareCharListener implements CharListener {

    protected int line = 0;
    protected int column = 0;
    protected int lastChar = -1;

    @Override
    public void reset() {
        line = 0;
        column = 0;
        lastChar = -1;
    }


    @Override
    public void listen(boolean isTokenChar, int c) {
        if (lastChar == '\n') {// reaction one token char later assuming we break on new line
            line++;
            column = 0;
        }
        lastChar = c;
        column++;

    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public int getLastChar() {
        return lastChar;
    }
    
    

}
