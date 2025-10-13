package bookandquill.code;

import net.minecraft.src.*;
import net.sunsetsatellite.itemnbt.IDataItem;

import java.util.List;

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

	public String getItemName() {
		return "Written Book";
	}

	public String getItemNameIS(ItemStack itemStack) {
		String title = "Written Book";
		if(itemStack.getItemData() != null) {
			NBTTagCompound var2 = itemStack.getItemData();
			String var3 = var2.getString("title");
			if (!var3.isEmpty()) {
				title = var3;
			}

			String author = var2.getString("author");
			if(!author.isEmpty()) {
				return title + " " + String.format("by %1$s", new Object[]{author});
			}
		}
		return title;
	}

	/*@Override
	public String getDescription(ItemStack itemStack) {
		String title = itemStack.getItemName();
		if(itemStack.getItemData() != null) {
			NBTTagCompound var2 = itemStack.getItemData();
			String var3 = var2.getString("title");
			if(!var3.isEmpty()) {
				title = var3;
			}

			String var6 = var2.getString("author");
			if(!var6.isEmpty()) {
				return title + " " + "§8" + String.format("by %1$s", new Object[]{var6});
			}
		}

		return title;
	}

	@Override
	public int getDescriptionColor(ItemStack itemStack) {
		return 0;
	}

	@Override
	public int getNameColor(ItemStack itemStack) {
		return 0;
	}*/
}