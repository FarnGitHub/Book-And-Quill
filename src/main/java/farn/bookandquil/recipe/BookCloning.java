package farn.bookandquil.recipe;

import farn.bookandquil.BookAndQuil;
import farn.bookandquil.item.WritableBookItem;
import farn.bookandquil.item.WrittenBookItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.impl.item.StationNBTSetter;

public class BookCloning {

    /**
     * Returns an Item that is the result of this recipe
     */
    public static ItemStack getCraftingResult(CraftingInventory inv)
    {
        boolean hasWritableBook = false;
        ItemStack theBook = null;

        for (int index = 0; index < inv.size(); ++index)
        {
            ItemStack curStack = inv.getStack(index);

            if (curStack != null)
            {
                if (curStack.getItem() instanceof WrittenBookItem)
                {
                    if (theBook != null)
                    {
                        return null;
                    }

                    theBook = curStack;
                } else if(curStack.getItem() instanceof WritableBookItem) {
                    if(hasWritableBook) {
                        return null;
                    }

                    hasWritableBook = true;
                }
            }
        }

        if (theBook != null && hasWritableBook)
        {
            ItemStack stack = new ItemStack(BookAndQuil.WRITTEN_BOOK, 1);
            StationNBTSetter.cast(stack).setStationNbt(theBook.getStationNbt().copy());
            stack.getStationNbt().putBoolean("copy", true);
            return stack;
        } else {
            return null;
        }
    }
}
