package farn.bookandquill.code;

import net.minecraft.src.*;

public class ItemEditableBook extends Item {
	public ItemEditableBook(int var1) {
		super(var1);
		this.setMaxStackSize(1);
	}

	public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
		if(var3 instanceof EntityPlayerSP) {
			ModLoader.getMinecraftInstance().displayGuiScreen(new GuiScreenBook(var3, var1, false));
		}
		return var1;
	}

	public String getItemNameIS(ItemStack itemStack) {
		String title = this.getItemName();
		if(itemStack.getItemData() != null) {
			NBTTagCompound var2 = itemStack.getItemData();
			String var3 = var2.getString("title");
			if (!var3.isEmpty()) {
				title = var3;
			}

			String author = var2.getString("author");
			if(!author.isEmpty()) {
				return title + " §8(" + String.format("By %1$s", new Object[]{author}) + ")§r";
			}
		}
		return title;
	}
}