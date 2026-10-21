package farn.bookandquil.util.text.wrapper;

import farn.bookandquil.mixins.TextUtilsAccessor;
import farn.bookandquil.util.text.TextLine;
import fr.catcore.translatedlegacy.font.TextRenderer;
import fr.catcore.translatedlegacy.util.TextUtils;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TranslatedLegacyTextWrapper implements TextWrapper{

    public List<TextLine> splits(List<TextLine> lines, String text, int width) {
        if(lines == null)
            lines = new ArrayList<>();
        else
            lines.clear();

        int paragraphStart = 0;
        while(paragraphStart <= text.length()) {
            int newline = text.indexOf('\n', paragraphStart);
            if (newline == -1)
                newline = text.length();

            wrapParagraph(lines, text, width, paragraphStart, newline);

            if (newline == text.length())
                break;
            if (paragraphStart == newline)
                lines.add(new TextLine("", newline, newline));

            paragraphStart = newline + 1;
        }

        return lines;
    }

    public void wrapParagraph(List<TextLine> lines, String fullText, int maxWidth, int firstCharIndex, int finalEnd) {
        if(firstCharIndex == finalEnd) return;

        String text = fullText.substring(firstCharIndex, finalEnd);
        BreakIterator breaks = BreakIterator.getLineInstance(TextUtilsAccessor.BQ_containsThai(text) ? TextUtilsAccessor.BQ_THAI() : Locale.ROOT);
        breaks.setText(text);
        int lineStart = 0;
        int lineEnd = 0;

        for(int end = breaks.next(); end != -1; end = breaks.next()) {
            end = TextUtils.toClusterBoundary(text, end);
            if (end > lineEnd) {
                if (getWidth(TextUtilsAccessor.BQ_trimEnd(text.substring(lineStart, end))) <= maxWidth) {
                    lineEnd = end;
                } else {
                    if (lineEnd > lineStart) {
                        String trimmed = text.substring(lineStart, lineEnd);
                        lines.add(new TextLine(trimmed, firstCharIndex + lineStart, firstCharIndex + lineEnd));
                        lineStart = lineEnd;
                    }

                    while(getWidth(TextUtilsAccessor.BQ_trimEnd(text.substring(lineStart, end))) > maxWidth) {
                        int splitAt = TextUtilsAccessor.BQ_findHardBreak(text, lineStart, end, maxWidth, this::getWidth);
                        String trimmed = text.substring(lineStart, splitAt);
                        lines.add(new TextLine(trimmed, firstCharIndex + lineStart, firstCharIndex + lineEnd));
                        lineStart = splitAt;
                    }

                    lineEnd = end;
                }
            }
        }

        if (lineStart < text.length()) {
            String theline = text.substring(lineStart);
            int trueEnd = Math.min(firstCharIndex + lineStart + theline.length(), finalEnd);
            lines.add(new TextLine(theline, firstCharIndex + lineStart, trueEnd));
        }
    }

    @Override
    public int getHeight(String text) {
        return TextRenderer.getTextHeight(text);
    }

    @Override
    public int getWidth(String text) {
        return TextRenderer.getTextWidth(text);
    }

    @Override
    public boolean isCharAllowed(char c) {
        return TextUtils.isCharacterAllowed(c) == 0;
    }
}
