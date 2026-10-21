package farn.bookandquil.util.text.wrapper;

import farn.bookandquil.util.text.TextLine;
import net.minecraft.client.Minecraft;
import net.minecraft.util.CharacterUtils;

import java.text.BreakIterator;
import java.util.List;

public class VanillaTextWrapper implements TextWrapper {

    @Override
    public void subSplits(List<TextLine> lines, String fullText, int width, int start, int end) {
        if(start == end)  {
            lines.add(new TextLine("", start, end));
            return;
        }

        String text = fullText.substring(start, end);
        BreakIterator breaks = BreakIterator.getLineInstance();
        breaks.setText(text);
        int lineStart = 0;
        int lineEnd = 0;

        for(int lBreak = breaks.next(); lBreak != -1; lBreak = breaks.next()) {
            if (lBreak > lineEnd) {
                if (getWidth(text.substring(lineStart, lBreak)) > width) {
                    if (lineEnd > lineStart) {
                        lines.add(new TextLine(text.substring(lineStart, lineEnd), start + lineStart, start + lineEnd));
                        lineStart = lineEnd;
                    }

                    while (getWidth(text.substring(lineStart, lBreak)) > width) {
                        int splitAt = findHardBreak(text, lineStart, lBreak, width);
                        lines.add(new TextLine(text.substring(lineStart, splitAt), start + lineStart, start + lineEnd));
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

    private int findHardBreak(String text, int start, int end, int maxWidth) {
        int lo = start + 1;
        int hi = end;

        while(lo < hi) {
            int mid = lo + hi >>> 1;
            if (getWidth(text.substring(start, mid)) > maxWidth) {
                hi = mid;
            } else {
                lo = mid + 1;
            }
        }

        int splitAt = lo - 1;
        if (splitAt <= start) {
            splitAt = start + 1;
        }

        return splitAt;
    }

    @Override
    public int getWidth(String text) {
        return Minecraft.INSTANCE.textRenderer.getWidth(text);
    }

    @Override
    public int getHeight(String text) {
        return 8;
    }

    @Override
    public boolean isCharAllowed(char c) {
        return CharacterUtils.VALID_CHARACTERS.indexOf(c) != -1;
    }
}
