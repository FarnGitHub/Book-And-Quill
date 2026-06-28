package farn.bookandquil.item;

public class WritableBookItem extends AbstractBookItem {
    public WritableBookItem(String id) {
        super(id);
    }

    @Override
    public boolean writable() {
        return true;
    }

}
