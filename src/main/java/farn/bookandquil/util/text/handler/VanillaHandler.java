package farn.bookandquil.util.text.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.util.CharacterUtils;

public class VanillaHandler implements Handler {

    @Override
    public int widthOf(String text) {
        return Minecraft.INSTANCE.textRenderer.getWidth(text);
    }

    @Override
    public int heightOf(String text) {
        return 8;
    }

    @Override
    public boolean charAllowed(char c) {
        return CharacterUtils.VALID_CHARACTERS.indexOf(c) != -1;
    }

    @Override
    public void forEachSplit(String text, int width, LineInsert insert) {
        int lStart = 0;
        while (!text.isEmpty()) {
            int lEnd = splitAt(text, width, lStart, text.length());
            insert.apply(lStart, lEnd);
            if (lEnd >= text.length()) break;
            lStart = text.charAt(lEnd) == '\n' ? lEnd + 1 : lEnd;
        }
    }

    public int splitAt(String text, int maxWidth, int start, int end) {
        if(maxWidth < 1) maxWidth = 1;

        int textLength = text.length();
        int lineWidth = 0;
        int index = start;
        int breakAt = -1;
        boolean first = true;

        while (index < textLength) {
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
