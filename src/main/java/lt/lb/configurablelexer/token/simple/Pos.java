package lt.lb.configurablelexer.token.simple;

/**
 *
 * @author laim0nas100
 */
public class Pos {

    public final int lineIndex, columnIndex;

    public Pos(int lineIndex, int columnIndex) {
        this.lineIndex = lineIndex;
        this.columnIndex = columnIndex;
    }

    @Override
    public String toString() {
        return lineIndex + "," + columnIndex;
    }

}
