package farn.bookandquil.util.text.handler;

import farn.bookandquil.mixins.TextUtilsAccessor;
import fr.catcore.translatedlegacy.font.TextRenderer;
import fr.catcore.translatedlegacy.util.TextUtils;

import java.text.BreakIterator;
import java.util.Locale;

public class TranslatedLegacyHandler implements Handler {

    @Override
    public int heightOf(String text) {
        return TextRenderer.getTextHeight(text);
    }

    @Override
    public int widthOf(String text) {
        return TextRenderer.getTextWidth(text);
    }

    @Override
    public boolean charAllowed(char c) {
        return TextUtils.isCharacterAllowed(c) == 0;
    }

    @Override
    public void forEachSplit(String text, int width, LineInsert insert) {
        int lStart = 0;
        BreakIterator breaks = BreakIterator.getLineInstance(TextUtilsAccessor.BQ_containsThai(text) ? TextUtilsAccessor.BQ_THAI() : Locale.US);
        breaks.setText(text);
        while (!text.isEmpty()) {
            int lEnd = splitAt(breaks, text, width, lStart, text.length());
            insert.apply(lStart, lEnd);
            if (lEnd >= text.length()) break;
            lStart = text.charAt(lEnd) == '\n' ? lEnd + 1 : lEnd;
        }
    }

    public int splitAt(BreakIterator breaks, String text, int maxWidth, int start, int end) {
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
            switch (c) {
                case '\n':
                    return index;
                case '§':
                    index += (index < end - 1) ? 2 : 1;
                    break;
                case ' ':
                    breakAt = index;
                default:
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
        return breakAt != -1 && breakAt < index ? breakAt : index;
    }
}
