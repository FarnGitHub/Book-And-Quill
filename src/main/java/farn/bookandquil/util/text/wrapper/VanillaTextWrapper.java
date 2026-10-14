package farn.bookandquil.util.text.wrapper;

import farn.bookandquil.util.text.TextLine;
import net.minecraft.client.Minecraft;
import net.minecraft.util.CharacterUtils;

import java.util.ArrayList;
import java.util.List;

public class VanillaTextWrapper implements TextWrapper {

    public List<TextLine> getTextLines(List<TextLine> lines, String text, int width) {
        if(lines == null)
            lines = new ArrayList<>();
        else
            lines.clear();

        int paragraphStart = 0;

        while (paragraphStart <= text.length()) {
            int newline = text.indexOf('\n', paragraphStart);

            if (newline == -1) {
                newline = text.length();
            }

            addWrappedLines(text, paragraphStart, newline, width, lines);

            if (newline == text.length()) {
                break;
            }

            if (paragraphStart == newline) {
                lines.add(new TextLine("", newline, newline));
            }

            paragraphStart = newline + 1;
        }

        return lines;
    }

    public int getTextLinesCount(String text, int width) {
        int count = 0;

        int paragraphStart = 0;

        while (paragraphStart <= text.length()) {
            int newline = text.indexOf('\n', paragraphStart);

            if (newline == -1) {
                newline = text.length();
            }

            count = addWrappedCounts(text, paragraphStart, newline, width, count);

            if (newline == text.length()) {
                break;
            }

            if (paragraphStart == newline) {
                count += getHeight(text);
            }

            paragraphStart = newline + 1;
        }

        return count;
    }

    public void addWrappedLines(String original, int start, int end, int width, List<TextLine> lines) {

        if (start == end) {
            return;
        }

        String text = original.substring(start, end);
        String[] words = text.split(" ");

        int wordIndex = 0;
        int searchIndex = start;

        while (wordIndex < words.length) {
            String word = words[wordIndex++];

            int wordStart = searchIndex;

            while (wordStart < end && original.charAt(wordStart) == ' ')
                wordStart++;

            searchIndex = wordStart + word.length();

            String line = word + " ";
            int lineStart = wordStart;

            while (wordIndex < words.length) {
                String nextWord = words[wordIndex];

                if (getWidth(line + nextWord) < width) {
                    line += nextWord + " ";
                    wordIndex++;

                    int nextStart = searchIndex;

                    while(nextStart < end && original.charAt(nextStart) == ' ')
                        nextStart++;

                    searchIndex = nextStart + nextWord.length();
                } else {
                    break;
                }
            }

            while (getWidth(line) > width) {
                int chars = 0;

                while (chars < line.length() && getWidth(line.substring(0, chars + 1)) <= width) {
                    chars++;
                }

                if (chars <= 0)
                    break;

                String part = line.substring(0, chars);

                if (!part.trim().isEmpty()) {
                    int partEnd = Math.min(lineStart + part.length(), end);
                    lines.add(new TextLine(part, lineStart, partEnd));
                }

                line = line.substring(chars);
                lineStart += chars;
            }

            if (!line.trim().isEmpty()) {
                int lineEnd = Math.min(lineStart + line.length(), end);
                lines.add(new TextLine(line, lineStart, lineEnd));
            }
        }
    }

    public int addWrappedCounts(String original, int start, int end, int width, int ogCount) {

        if (start == end) {
            return ogCount;
        }

        String text = original.substring(start, end);
        String[] words = text.split(" ");

        int wordIndex = 0;
        int searchIndex = start;

        while (wordIndex < words.length) {
            String word = words[wordIndex++];

            int wordStart = searchIndex;

            while (wordStart < end && original.charAt(wordStart) == ' ')
                wordStart++;

            searchIndex = wordStart + word.length();

            String line = word + " ";

            while (wordIndex < words.length) {
                String nextWord = words[wordIndex];

                if (getWidth(line + nextWord) < width) {
                    line += nextWord + " ";
                    wordIndex++;

                    int nextStart = searchIndex;

                    while (nextStart < end && original.charAt(nextStart) == ' ')
                        nextStart++;

                    searchIndex = nextStart + nextWord.length();
                } else {
                    break;
                }
            }

            while (getWidth(line) > width) {
                int chars = 0;

                while (chars < line.length() && getWidth(line.substring(0, chars + 1)) <= width) {
                    chars++;
                }

                if (chars <= 0)
                    break;

                String part = line.substring(0, chars);

                String trimmedPart = part.trim();
                if (!trimmedPart.isEmpty()) {
                    ogCount += getHeight(trimmedPart);
                }

                line = line.substring(chars);
            }

            String trimmedPart = line.trim();
            if (!trimmedPart.isEmpty()) {
                ogCount += getHeight(trimmedPart);
            }
        }

        return ogCount;
    }

    public int getWidth(String text) {
        return Minecraft.INSTANCE.textRenderer.getWidth(text);
    }

    public int getHeight(String text) {
        return 8;
    }

    public boolean isCharAllowed(char c) {
        return CharacterUtils.VALID_CHARACTERS.indexOf(c) != -1;
    }

    public boolean isRightToLeft(String text) {
        return false;
    }
}
