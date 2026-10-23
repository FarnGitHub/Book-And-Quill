package farn.bookandquil.util.text;
import farn.bookandquil.util.text.handler.Handler;
import farn.bookandquil.util.text.handler.TranslatedLegacyHandler;
import farn.bookandquil.util.text.handler.VanillaHandler;
import net.fabricmc.loader.api.FabricLoader;

import java.util.ArrayList;
import java.util.List;

public final class TextUtils {

    private static final Handler handler =
            FabricLoader.getInstance().isModLoaded("translated-legacy") ?
                    new TranslatedLegacyHandler() : new VanillaHandler();

    private TextUtils() {
    }

    public static List<TextLine> splits(final List<TextLine> lines, String text, int width) {
        if(lines == null)
            return splits(new ArrayList<>(), text, width);
        lines.clear();
        handler.forEachSplit(text,width,(start,end)->
            lines.add(textLineOf(text,start,end))
        );
        return lines;
    }

    public static int splitsTotalHeight(String text, int width) {
        HeightCounter counter = new HeightCounter();
        handler.forEachSplit(text,width,(start, end)->
            counter.count(text.substring(start,end))
        );
        return counter.total();
    }

    public static int widthOf(String text) {
        return handler.widthOf(text);
    }

    public static int heightOf(String text) {
        return handler.heightOf(text);
    }

    public static boolean charAllowed(char c) {
        return handler.charAllowed(c);
    }

    private static TextLine textLineOf(String text, int start, int end) {
        return new TextLine(text.substring(start, end), start, end);
    }

    private static class HeightCounter {
        int total = 8;

        void count(String text) {
            total += widthOf(text);
        }

        int total() {
            return Math.max(total, 8);
        }
    }
}
