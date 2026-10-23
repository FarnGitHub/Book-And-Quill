package farn.bookandquil.util.text.handler;

public interface Handler {

    int widthOf(String text);

    int heightOf(String text);

    boolean charAllowed(char c);

    void forEachSplit(String text, int width, LineInsert insert);

    @FunctionalInterface
    interface LineInsert {
        void apply(int start, int end);
    }

}
