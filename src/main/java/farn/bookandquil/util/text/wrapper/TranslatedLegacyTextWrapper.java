package farn.bookandquil.util.text.wrapper;

import farn.bookandquil.mixins.TextUtilsAccessor;
import farn.bookandquil.util.text.TextLine;
import fr.catcore.translatedlegacy.font.TextRenderer;
import fr.catcore.translatedlegacy.util.TextUtils;

import java.text.BreakIterator;
import java.util.List;
import java.util.Locale;

public class TranslatedLegacyTextWrapper implements TextWrapper{

    @Override
    public void subSplits(List<TextLine> lines, String fullText, int width, int start, int end) {
        if(start == end)  {
            lines.add(new TextLine(" ", start, end));
            return;
        }

        String text = fullText.substring(start, end);
        BreakIterator breaks = BreakIterator.getLineInstance(TextUtilsAccessor.BQ_containsThai(text) ? TextUtilsAccessor.BQ_THAI() : Locale.ROOT);
        breaks.setText(text);
        int lineStart = 0;
        int lineEnd = 0;

        for(int lBreak = breaks.next(); lBreak != -1; lBreak = breaks.next()) {
            lBreak = TextUtils.toClusterBoundary(text, lBreak);
            if (lBreak > lineEnd) {
                if (getWidth(text.substring(lineStart, lBreak)) > width) {
                    if (lineEnd > lineStart) {
                        lines.add(new TextLine(text.substring(lineStart, lineEnd), start + lineStart, start + lineEnd));
                        lineStart = lineEnd;
                    }

                    while (getWidth(text.substring(lineStart, lBreak)) > width) {
                        int splitAt = TextUtilsAccessor.BQ_findHardBreak(text, lineStart, lBreak, width, this::getWidth);
                        lines.add(new TextLine(text.substring(lineStart, splitAt), start + lineStart, start + splitAt));
                        lineStart = splitAt;
                    }

                }
                lineEnd = lBreak;
            }
        }

        if (lineStart < text.length()) {
            String lastLine = text.substring(lineStart);
            int lastEnd = Math.min(start + lineStart + lastLine.length(), end);
            lines.add(new TextLine(lastLine, start + lineStart, lastEnd));
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
