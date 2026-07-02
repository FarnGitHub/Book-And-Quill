package farn.bookandquill.mod.forge;

import farn.bookandquill.item.ItemWrittenBook;
import forge.ICraftingHandler;
import forge.MinecraftForge;
import net.minecraft.src.*;

public class BookAndQuillCraftingHandler implements ICraftingHandler {
    public void onTakenFromCrafting(EntityPlayer entityPlayer, ItemStack output, IInventory matrix) {
        if(matrix != null && isCopyBook(output)) {
            for(int i = 0; i < matrix.getSizeInventory(); i++) {
                ItemStack stack = matrix.getStackInSlot(i);
                if(isWrittenBook(stack)) ++stack.stackSize;
            }
        }
    }

    private static boolean isCopyBook(ItemStack stack) {
        return isWrittenBook(stack) && stack.getItemData().getBoolean("copy");
    }

    private static boolean isWrittenBook(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemWrittenBook;
    }

    public static void init() {
        MinecraftForge.registerCraftingHandler(new BookAndQuillCraftingHandler());
    }
}
