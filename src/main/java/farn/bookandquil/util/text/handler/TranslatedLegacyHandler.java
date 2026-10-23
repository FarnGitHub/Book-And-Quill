package farn.bookandquil.util.text.handler;

import farn.bookandquil.mixins.TextUtilsAccessor;
import fr.catcore.translatedlegacy.font.TextRenderer;
import fr.catcore.translatedlegacy.util.TextUtils;

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
    public Locale locale(String text) {
        return TextUtilsAccessor.BQ_containsThai(text) ? TextUtilsAccessor.BQ_THAI() : Locale.US;
    }
}
