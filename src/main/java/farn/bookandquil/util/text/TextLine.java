package farn.bookandquil.util.text;

public class TextLine {
    public String text;
    public int start;
    public int end;

    public TextLine(String text, int start, int end) {
        this.text = text;
        this.start = start;
        this.end = end;
    }

    public String text() {
        return text;
    }

    public int start() {
        return start;
    }

    public int end() {
        return end;
    }

}
