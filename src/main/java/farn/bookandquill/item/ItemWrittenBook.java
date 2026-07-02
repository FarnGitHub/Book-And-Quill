package farn.bookandquill.item;

import net.minecraft.src.ItemStack;

public class ItemWrittenBook extends ItemBookAbstract {
	public ItemWrittenBook(int id) {
		super(id);
	}

	@Override
	boolean isWritable() {
		return false;
	}

	@Override
	public String getItemNameIS(ItemStack stack) {
	 	String finalName = super.getItemNameIS(stack);
		if(stack.getItemData() != null) {
			String title = stack.getItemData().getString("title");
			boolean copy = stack.getItemData().getBoolean("copy");
			if (!title.isEmpty()) {
				finalName = title + (copy ? " (Copy)" : "");
			} else if(copy)
				finalName = finalName + ".copy";
		}
		return finalName;
	}
}