package farn.bookandquill.code;

import net.minecraft.src.*;

public class ItemWritableBook extends Item {
	public ItemWritableBook(int var1) {
		super(var1);
		this.setMaxStackSize(1);
	}

	public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
		if(var3 instanceof EntityPlayerSP) {
			ModLoader.getMinecraftInstance().displayGuiScreen(new GuiScreenBook(var3, var1, true));
		}
		return var1;
	}
}