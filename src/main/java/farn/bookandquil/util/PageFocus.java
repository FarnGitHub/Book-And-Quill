package farn.bookandquil.util;

public enum PageFocus {
    UNFOCUS,
    FIRST,
    SECOND;

    public int increment() {
        return this.ordinal() - 1;
    }

    public static PageFocus from(int value) {
        return PageFocus.values()[value];
    }
}
