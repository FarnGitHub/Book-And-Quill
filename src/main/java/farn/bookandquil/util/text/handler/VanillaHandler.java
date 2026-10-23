package farn.bookandquil.util.text.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.util.CharacterUtils;

import java.util.Locale;

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
    public Locale locale(String text) {
        return Locale.US;
    }
}
