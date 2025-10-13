package bookandquill.code;

import com.sun.jmx.mbeanserver.ModifiableClassLoaderRepository;
import jdk.internal.module.ModuleLoaderMap;
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

	public String getItemName() {
		return "Book And Quill";
	}

	public String getItemNameIS(ItemStack stack) {
		return "Book And Quill";
	}
}