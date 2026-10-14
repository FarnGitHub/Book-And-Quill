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

    public List<TextLine> getTextLines(String text, int width) {
        List<TextLine> lines = new ArrayList<>();
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

    public int getTextLinesCount(String text, int width) {
        int totalHeight = 0;
        int paragraphStart = 0;
        while(paragraphStart <= text.length()) {
            int newline = text.indexOf('\n', paragraphStart);
            if (newline == -1)
                newline = text.length();

            totalHeight += paragraphTotalHeight(text, width, paragraphStart, newline);
            if (newline == text.length())
                break;

            if (paragraphStart == newline)
                totalHeight += getHeight(" ");

            paragraphStart = newline + 1;
        }

        if(totalHeight < 8)
            totalHeight = 8;

        return totalHeight;
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

        if (lines.size() > 1 && TextUtilsAccessor.BQ_isRightToLeftParagraph(text)) {
            lines.forEach((line) -> line.text = '\u200f' + line.text);
        }
    }

    public int paragraphTotalHeight(String fullText, int maxWidth, int paragraphStart, int finalSplit) {
        if(paragraphStart == finalSplit) return 0;

        String text = fullText.substring(paragraphStart, finalSplit);
        int totalHeight = 0;
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
                        totalHeight += getHeight(trimmed);
                        lineStart = lineEnd;
                    }

                    while(getWidth(TextUtilsAccessor.BQ_trimEnd(text.substring(lineStart, end))) > maxWidth) {
                        int splitAt = TextUtilsAccessor.BQ_findHardBreak(text, lineStart, end, maxWidth, this::getWidth);
                        String trimmed = text.substring(lineStart, splitAt);
                        totalHeight += getHeight(trimmed);
                        lineStart = splitAt;
                    }

                    lineEnd = end;
                }
            }
        }

        if (lineStart < text.length()) {
            totalHeight += getHeight(text.substring(lineStart));
        }

        return totalHeight;
    }


    public int getHeight(String text) {
        return TextRenderer.getTextHeight(text);
    }

    public int getWidth(String text) {
        return TextRenderer.getTextWidth(text);
    }

    public boolean isRightToLeft(String text) {
        return TextUtilsAccessor.BQ_isRightToLeftParagraph(text);
    }

    public boolean isCharAllowed(char c) {
        return TextUtils.isCharacterAllowed(c) == 0;
    }
}
