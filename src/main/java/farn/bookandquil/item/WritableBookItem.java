package farn.bookandquil.item;

import net.modificationstation.stationapi.api.util.Identifier;

public class WritableBookItem extends AbstractBookItem {
    public WritableBookItem(Identifier id) {
        super(id);
    }

    @Override
    boolean writable() {
        return true;
    }

}
