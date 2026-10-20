package farn.bookandquil.util;

public enum PageFocus {
    FIRST,
    SECOND,
    UNFOCUS;

    public static PageFocus from(int value) {
        return PageFocus.values()[value];
    }
}
