package farn.bookandquil.util;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class TextUtils {

    public static List<TextLine> getTextLines(String text, int width) {
        List<TextLine> lines = new ArrayList<>();

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

    public static int getTextLinesCount(String text, int width) {
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
                count += 8;
            }

            paragraphStart = newline + 1;
        }

        return count;
    }

    public static void addWrappedLines(String original, int start, int end, int width, List<TextLine> lines) {

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

    public static int addWrappedCounts(String original, int start, int end, int width, int ogCount) {

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
                    ogCount += 8;
                }

                line = line.substring(chars);
            }

            if (!line.trim().isEmpty()) {
                ogCount += 8;
            }
        }

        return ogCount;
    }

    private static int getWidth(String text) {
        return Minecraft.INSTANCE.textRenderer.getWidth(text);
    }
}
