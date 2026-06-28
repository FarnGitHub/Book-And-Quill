package farn.bookandquil.util;

public enum PageFocus {
    UNFOCUS(-1),
    FIRST(0),
    SECOND(1);

    public final int focusVal;

    PageFocus(int val) {
        focusVal = val;
    }
}
