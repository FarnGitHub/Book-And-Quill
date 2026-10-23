package farn.bookandquil.util.text;
import farn.bookandquil.util.text.handler.Handler;
import farn.bookandquil.util.text.handler.TranslatedLegacyHandler;
import farn.bookandquil.util.text.handler.VanillaHandler;
import net.fabricmc.loader.api.FabricLoader;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

public final class TextUtils {

    private static final Handler handler =
            FabricLoader.getInstance().isModLoaded("translated-legacy") ?
                    new TranslatedLegacyHandler() : new VanillaHandler();

    private TextUtils() {
    }

    public static List<TextLine> splits(List<TextLine> lines, String text, int width) {
        if(lines == null) lines = new ArrayList<>();
        else lines.clear();

        int lStart = 0;
        BreakIterator breaks = breakIterator(text);
        while (true) {
            int lEnd = splitAt(breaks, text, width, lStart, text.length());
            lines.add(new TextLine(text.substring(lStart, lEnd), lStart, lEnd));
            if (lEnd >= text.length()) break;
            lStart = text.charAt(lEnd) == '\n' ? lEnd + 1 : lEnd;
        }
        return lines;
    }

    public static int splitsTotalHeight(String text, int width) {
        int total = 0;
        int lStart = 0;
        BreakIterator breaks = breakIterator(text);
        while (true) {
            int lEnd = splitAt(breaks, text, width, lStart, text.length());
            total += heightOf(text.substring(lStart, lEnd));
            if (lEnd >= text.length()) break;
            lStart = text.charAt(lEnd) == '\n' ? lEnd + 1 : lEnd;
        }
        return Math.max(total, 8);
    }

    private static int splitAt(BreakIterator breaks, String text, int maxWidth, int start, int end) {
        if(maxWidth < 1) maxWidth = 1;

        int textLength = text.length();
        int lineWidth = 0;
        int index = start;
        int breakAt = -1;
        boolean first = true;
        int boundary = breaks.following(start);

        while (index < textLength) {
            while (boundary != BreakIterator.DONE && boundary < index)
                boundary = breaks.next();

            if (boundary == index) {
                int cut = index;
                while(cut > start && text.charAt(cut - 1) == ' ') cut--;
                if (cut > start) breakAt = cut;
                boundary = breaks.next();
            }

            char c = text.charAt(index);
            if(c == '\n') {
                return index;
            } else {
                if (c == ' ') breakAt = index;
                if (lineWidth != 0) first = false;
                lineWidth += widthOf(String.valueOf(c));
                index++;
            }

            if (lineWidth > maxWidth) {
                if (first) index++;
                break;
            }
        }

        if(index >= end) index = end;
        boolean needBreak = index != textLength && breakAt != -1 && breakAt < index;
        return needBreak ? breakAt : index;
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

    public static BreakIterator breakIterator(String text) {
        BreakIterator breaks = BreakIterator.getLineInstance(handler.locale(text));
        breaks.setText(text);
        return breaks;
    }
}
