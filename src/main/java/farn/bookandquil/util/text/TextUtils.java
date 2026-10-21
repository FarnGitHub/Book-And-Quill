package farn.bookandquil.util.text;
import farn.bookandquil.util.text.wrapper.TextWrapper;
import farn.bookandquil.util.text.wrapper.TranslatedLegacyTextWrapper;
import farn.bookandquil.util.text.wrapper.VanillaTextWrapper;
import net.fabricmc.loader.api.FabricLoader;

import java.util.List;

public class TextUtils {

    private static final TextWrapper WRAPPER =
            FabricLoader.getInstance().isModLoaded("translated-legacy") ?
                    new TranslatedLegacyTextWrapper() : new VanillaTextWrapper();

    public static List<TextLine> splits(List<TextLine> lines, String text, int width) {
        return WRAPPER.splits(lines, text, width);
    }

    public static int splitsTotalHeight(String text, int width) {
        return WRAPPER.splits(null, text, width).stream().
                mapToInt(line -> getHeight(line.text)).sum();
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
}
