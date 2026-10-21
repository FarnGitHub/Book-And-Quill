package farn.bookandquil.util.text.wrapper;

import farn.bookandquil.util.text.TextLine;

import java.util.List;

public interface TextWrapper {

    void subSplits(List<TextLine> lines, String fullText, int width, int start, int end);

    int getWidth(String text);

    int getHeight(String text);

    boolean isCharAllowed(char c);

}
