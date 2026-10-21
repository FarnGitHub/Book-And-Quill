package farn.bookandquil.util.text;
import farn.bookandquil.util.text.wrapper.TextWrapper;
import farn.bookandquil.util.text.wrapper.TranslatedLegacyTextWrapper;
import farn.bookandquil.util.text.wrapper.VanillaTextWrapper;
import net.fabricmc.loader.api.FabricLoader;

import java.util.ArrayList;
import java.util.List;

public class TextUtils {
    private static final List<TextLine> DUMMY = new ArrayList<>();

    private static final TextWrapper WRAPPER =
            FabricLoader.getInstance().isModLoaded("translated-legacy") ?
                    new TranslatedLegacyTextWrapper() : new VanillaTextWrapper();

    public static List<TextLine> splits(List<TextLine> lines, String text, int width) {
        if(lines == null) lines = new ArrayList<>();
        else lines.clear();
        int paragraphStart = 0;
        while (paragraphStart <= text.length()) {
            int newline = text.indexOf('\n', paragraphStart);
            if (newline == -1)
                newline = text.length();
            WRAPPER.subSplits(lines, text, width, paragraphStart, newline);
            if (newline == text.length())
                break;
            paragraphStart = newline + 1;
        }
        return lines;
    }

    public static int splitsTotalHeight(String text, int width) {
        return splits(DUMMY, text, width).stream().
                mapToInt(TextUtils::getHeight).sum();
    }

    public static int getWidth(String text) {
        return WRAPPER.getWidth(text);
    }

    public static int getHeight(String text) {
        return WRAPPER.getHeight(text);
    }

    public static boolean isCharAllowed(char c) {
        return WRAPPER.isCharAllowed(c);
    }

    public static int getHeight(TextLine line) {
        return WRAPPER.getHeight(line.text);
    }
}
