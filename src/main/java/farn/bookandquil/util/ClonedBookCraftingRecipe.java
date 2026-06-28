package farn.bookandquil.util;

import farn.bookandquil.BookAndQuill;
import farn.bookandquil.item.WritableBookItem;
import farn.bookandquil.item.WrittenBookItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.ShapelessRecipe;
import net.modificationstation.stationapi.impl.item.StationNBTSetter;

import java.util.List;

public class ClonedBookCraftingRecipe extends ShapelessRecipe {
    public ClonedBookCraftingRecipe() {
        super(
              copiedBook(),
              List.of(
                      new ItemStack(BookAndQuill.WRITTEN_BOOK),
                      new ItemStack(BookAndQuill.BOOK_AND_QUILL)
              )
        );
    }

    public boolean matches(CraftingInventory inv) {
        return getOriginalBook(inv) != null;
    }

    public ItemStack craft(CraftingInventory inv) {
        ItemStack theBook = getOriginalBook(inv);

        if (theBook != null) {
            ItemStack stack = new ItemStack(BookAndQuill.WRITTEN_BOOK, 1);
            StationNBTSetter.cast(stack).setStationNbt(theBook.getStationNbt().copy());
            stack.getStationNbt().putBoolean("copy", true);
            return stack;
        } else {
            return null;
        }
    }

    public static ItemStack getOriginalBook(CraftingInventory inv) {
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

        return hasWritableBook ? theBook : null;
    }

    private static ItemStack copiedBook() {
        ItemStack stack = new ItemStack(BookAndQuill.WRITTEN_BOOK);
        stack.getStationNbt().putBoolean("copy", true);
        return stack;
    }
}
