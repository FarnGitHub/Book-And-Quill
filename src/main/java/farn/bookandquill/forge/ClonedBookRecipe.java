package farn.bookandquill.forge;

import farn.bookandquill.BookAndQuill;
import farn.bookandquill.item.ItemWritableBook;
import farn.bookandquill.item.ItemWrittenBook;
import net.minecraft.src.InventoryCrafting;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ShapelessRecipes;

import java.util.ArrayList;
import java.util.List;

public class ClonedBookRecipe extends ShapelessRecipes {
    public ClonedBookRecipe() {
        super(copiedBook(),copiedBookList());
    }

    public boolean matches(InventoryCrafting inv) {
        return getOriginalBook(inv) != null;
    }

    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack theBook = getOriginalBook(inv);

        if (theBook != null) {
            ItemStack stack = new ItemStack(BookAndQuill.writtenBook, 1);
            stack.setItemData(theBook.getItemData().copy());
            stack.getItemData().setBoolean("copy", true);
            return stack;
        } else {
            return null;
        }
    }

    public static ItemStack getOriginalBook(InventoryCrafting inv) {
        boolean hasWritableBook = false;
        ItemStack theBook = null;

        for (int index = 0; index < inv.getSizeInventory(); ++index) {
            ItemStack curStack = inv.getStackInSlot(index);

            if (curStack != null) {
                if (curStack.getItem() instanceof ItemWrittenBook) {
                    if (theBook != null)
                        return null;

                    theBook = curStack;
                } else if(curStack.getItem() instanceof ItemWritableBook) {
                    if(hasWritableBook)
                        return null;

                    hasWritableBook = true;
                }
            }
        }

        return hasWritableBook ? theBook : null;
    }

    private static ItemStack copiedBook() {
        ItemStack stack = new ItemStack(BookAndQuill.writtenBook);
        stack.getItemData().setBoolean("copy", true);
        return stack;
    }

    private static List<ItemStack> copiedBookList() {
        List<ItemStack> stacks = new ArrayList<>();
        stacks.add(new ItemStack(BookAndQuill.writtenBook));
        stacks.add( new ItemStack(BookAndQuill.writableBook));
        return stacks;
    }

}
