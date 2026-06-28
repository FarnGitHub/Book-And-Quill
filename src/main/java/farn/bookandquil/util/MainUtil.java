package farn.bookandquil.util;

import farn.bookandquil.BookAndQuill;
import farn.bookandquil.config.ConfigListener;
import farn.bookandquil.item.WritableBookItem;
import farn.bookandquil.item.WrittenBookItem;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.modificationstation.stationapi.impl.item.StationNBTSetter;

import java.util.List;

public class MainUtil {
    private static final boolean hasGcapi = FabricLoader.getInstance().isModLoaded("gcapi3");

    public static String translate(String string) {
        return TranslationStorage.getInstance().get("bookandquill." + string);
    }

    public static String translate(String string, Object... var1) {
        return TranslationStorage.getInstance().get("bookandquill." + string, var1);
    }

    @SuppressWarnings("unchecked")
    public static boolean validContent(NbtList listNbt) {
        if(listNbt == null) return false;
        List<NbtElement> list = listNbt.value;

        for(NbtElement content : list)
            if(!(content instanceof NbtString str && str.value.length() <= 256))
                return false;

        return true;
    }

    public static boolean isCopiedBook(ItemStack stack) {
        return isWrittenBook(stack) && stack.getStationNbt().getBoolean("copy");
    }

    public static boolean isWrittenBook(ItemStack stack) {
        return stack != null && stack.getItem() instanceof WrittenBookItem;
    }

    public static boolean shouldPause() {
        return !hasGcapi || ConfigListener.get.pauseGame;
    }

    public static boolean classicBook() {
        return !hasGcapi || ConfigListener.get.classicBook;
    }

    public static ItemStack createClonedBook(CraftingInventory inv) {
        boolean hasWritableBook = false;
        ItemStack theBook = null;

        for (int index = 0; index < inv.size(); ++index) {
            ItemStack curStack = inv.getStack(index);

            if (curStack != null) {
                if (curStack.getItem() instanceof WrittenBookItem) {
                    if (theBook != null)
                        return null;

                    theBook = curStack;
                } else if(curStack.getItem() instanceof WritableBookItem) {
                    if(hasWritableBook)
                        return null;

                    hasWritableBook = true;
                }
            }
        }

        if (theBook != null && hasWritableBook) {
            ItemStack stack = new ItemStack(BookAndQuill.WRITTEN_BOOK, 1);
            StationNBTSetter.cast(stack).setStationNbt(theBook.getStationNbt().copy());
            stack.getStationNbt().putBoolean("copy", true);
            return stack;
        } else {
            return null;
        }
    }
}
