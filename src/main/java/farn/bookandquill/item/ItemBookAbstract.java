package farn.bookandquill.item;

import farn.bookandquill.gui.GuiBook;
import net.minecraft.src.*;

public abstract class ItemBookAbstract extends Item {

    public ItemBookAbstract(int id) {
        super(id);
        this.setMaxStackSize(1);
    }

    abstract boolean isWritable();

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        ModLoader.OpenGUI(player, GuiBook.get(player, stack, isWritable()));
        return stack;
    }
}
