package farn.bookandquil.util.text.wrapper;

import farn.bookandquil.util.text.TextLine;

import java.util.Collections;
import java.util.List;

public interface TextWrapper {

    List<TextLine> splits(List<TextLine> lines, String text, int width);

    int getWidth(String text);

    int getHeight(String text);

    boolean isCharAllowed(char c);

}
