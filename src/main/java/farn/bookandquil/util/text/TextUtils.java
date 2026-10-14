package farn.bookandquil.util.text;
import farn.bookandquil.util.text.wrapper.TextWrapper;
import farn.bookandquil.util.text.wrapper.TranslatedLegacyTextWrapper;
import farn.bookandquil.util.text.wrapper.VanillaTextWrapper;
import net.fabricmc.loader.api.FabricLoader;

import java.util.List;

public class TextUtils {
    static TextWrapper WRAPPER =
            FabricLoader.getInstance().isModLoaded("translated-legacy") ?
                    new TranslatedLegacyTextWrapper() : new VanillaTextWrapper();

    public static List<TextLine> getTextLines(List<TextLine> lines, String text, int width) {
        return WRAPPER.getTextLines(lines, text, width);
    }

    public static int getTextLinesCount(String text, int width) {
        return WRAPPER.getTextLinesCount(text, width);
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

    public static boolean isRightToLeft(String text) {
        return WRAPPER.isRightToLeft(text);
    }
}
