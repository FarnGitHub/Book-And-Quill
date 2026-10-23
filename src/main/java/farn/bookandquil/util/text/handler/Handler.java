package farn.bookandquil.util.text.handler;

import java.util.Locale;

public interface Handler {

    int widthOf(String text);

    int heightOf(String text);

    boolean charAllowed(char c);

    Locale locale(String text);

}
