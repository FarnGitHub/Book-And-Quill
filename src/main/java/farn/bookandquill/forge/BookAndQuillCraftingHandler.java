package farn.bookandquill.forge;

import farn.bookandquill.item.ItemWrittenBook;
import forge.ICraftingHandler;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;

public class BookAndQuillCraftingHandler implements ICraftingHandler {
    @Override
    public void onTakenFromCrafting(EntityPlayer player, ItemStack output, IInventory matrix) {
        if(isCopyBook(output)) {
            for(int i = 0; i < matrix.getSizeInventory(); i++) {
                ItemStack stack = matrix.getStackInSlot(i);
                if(isWrittenBook(stack)) ++stack.stackSize;
            }
        }
    }

    private boolean isCopyBook(ItemStack stack) {
        return isWrittenBook(stack) && stack.getItemData().getBoolean("copy");
    }

    private boolean isWrittenBook(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemWrittenBook;
    }
}
