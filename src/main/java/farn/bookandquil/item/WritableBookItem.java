package farn.bookandquil.item;

public class WritableBookItem extends AbstractBookItem {
    public WritableBookItem(String id) {
        super(id);
    }

    @Override
    boolean writable() {
        return true;
    }

}
