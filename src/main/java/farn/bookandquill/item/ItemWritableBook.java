package farn.bookandquill.item;

public class ItemWritableBook extends ItemBookAbstract {
	public ItemWritableBook(int id) {
		super(id);
	}

	@Override
	boolean isWritable() {
		return true;
	}
}