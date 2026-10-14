package farn.bookandquil.util.text.wrapper;

import farn.bookandquil.util.text.TextLine;

import java.util.Collections;
import java.util.List;

public interface TextWrapper {

    default List<TextLine> getTextLines(List<TextLine> lines, String text, int width) {
        return Collections.emptyList();
    }

    default int getTextLinesCount(String text, int width) {
        return 0;
    }

    default int getWidth(String text) {
        return 16;
    }

    default int getHeight(String text) {
        return 8;
    }

    default boolean isCharAllowed(char c) {
        return false;
    }

    default boolean isRightToLeft(String text) {
        return false;
    }
}
