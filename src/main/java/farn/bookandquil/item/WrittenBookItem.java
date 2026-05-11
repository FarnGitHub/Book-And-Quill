package farn.bookandquil.item;

import farn.bookandquil.BookAndQuil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.client.item.CustomTooltipProvider;

import java.util.ArrayList;
import java.util.List;

public class WrittenBookItem extends AbstractBookItem implements CustomTooltipProvider {
    public WrittenBookItem(String id) {
        super(id);
    }

    @Override
    boolean writable() {
        return false;
    }

    @Override
    public String[] getTooltip(ItemStack stack, String itemName) {
        try {
            List<String> list = new ArrayList<>();
            list.add(itemName);
            if(stack.getStationNbt() != null) {
                NbtCompound nbt = stack.getStationNbt();
                String title = nbt.getString("title");
                if(title != null && !title.isEmpty()) {
                    list.set(0, title);
                }

                if(nbt.contains("copy") && nbt.getBoolean("copy")) {
                    list.add("§8Copy");
                } else {
                    String author = nbt.getString("author");
                    if(author != null && !author.isEmpty()) {
                        list.add("§8" + BookAndQuil.translateFormat("book.byAuthor", author));
                    }
                }
            }
            return list.toArray(new String[0]);
        } catch (Exception e) {
            return new String[]{itemName};
        }
    }

}
