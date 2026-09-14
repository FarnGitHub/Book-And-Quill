package farn.bookandquill.util;

public class TextLine {
    public final String text;
    public final int start;
    public final int end;

    public TextLine(String text, int start, int end) {
        this.text = text;
        this.start = start;
        this.end = end;
    }
}
