package farn.bookandquil.mixins;

import fr.catcore.translatedlegacy.util.TextUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Locale;
import java.util.function.ToIntFunction;

@Pseudo
@Mixin(TextUtils.class)
public interface TextUtilsAccessor {

    @Accessor("THAI")
    static Locale BQ_THAI() {
        throw new AssertionError();
    }

    @Invoker("isRightToLeftParagraph")
    static boolean BQ_isRightToLeftParagraph(String text) {
        throw new AssertionError();
    }

    @Invoker("findHardBreak")
    static int BQ_findHardBreak(String text, int start, int end, int maxWidth, ToIntFunction<String> widthFunction) {
        throw new AssertionError();
    }

    @Invoker("containsThai")
    static boolean BQ_containsThai(String text) {
        throw new AssertionError();
    }

    @Invoker("trimEnd")
    static String BQ_trimEnd(String text) {
        throw new AssertionError();

    }

}
