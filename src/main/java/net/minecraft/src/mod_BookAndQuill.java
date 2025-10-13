package net.minecraft.src;

import farn.bookandquill.code.ItemEditableBook;
import farn.bookandquill.code.ItemWritableBook;

import java.lang.reflect.Field;
import java.util.List;

@SuppressWarnings("unused")
public class mod_BookAndQuill extends BaseMod {
    public static Item writableBook;
    public static Item writtenBook;

    @MLProp(name="Book And Quill Item ID", info="ID")
    public static int bookAndQuilID = 30000;

    @MLProp(name="Written Book Item ID", info="ID")
    public static int writtenBookID = 29999;

    //initialization
    public mod_BookAndQuill() {
        //Book And Quill Item
        writableBook = new ItemWritableBook(bookAndQuilID).setItemName("farn.code.bookandquill.writablebook");
        ModLoader.AddLocalization(writableBook.getItemName() + ".name", "Book And Quill");
        writableBook.setIconIndex(ModLoader.addOverride("/gui/items.png", "/farn/bookandquill/resource/writingBook.png"));

        //Written Book Item
        writtenBook = new ItemEditableBook(writtenBookID).setItemName("farn.code.bookandquill.writtenbook");
        ModLoader.AddLocalization(writtenBook.getItemName() + ".name", "Written Book");
        writtenBook.setIconIndex(ModLoader.addOverride("/gui/items.png", "/farn/bookandquill/resource/writtenBook.png"));

        //Book And Quill Recipe
        ModLoader.AddShapelessRecipe(new ItemStack(writableBook, 1), new Object[]{Item.book, new ItemStack(Item.dyePowder, 1, 0), Item.feather});
    }

    @Override
    public String Version() {
        return "1.0.0";
    }

    //ModMenu Stuff start here
    public String Name() {
        return "Book And Quill";
    }

    public String Description() {
        return "Add Book And Quill to b1.7.3";
    }

    public String Icon() {
        return "farn/bookandquill/resource/icon.png";
    }
    //ModMenu Stuff end here

    //Nbt stuff
    public static void nbtListRemoveTag(NBTTagList list, int index) {
        try {
            Field tagListField = list.getClass().getDeclaredField("a");
            tagListField.setAccessible(true);
            ((List)tagListField.get(list)).remove(index);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static NBTTagList copyNbtList(NBTTagList original) {
        if (original == null) return null;

        NBTTagList copy = new NBTTagList();
        for (int i = 0; i < original.tagCount(); i++) {
            NBTBase tag = original.tagAt(i);
            copy.setTag(copyNbt(tag));
        }
        return copy;
    }

    private static NBTBase copyNbt(NBTBase tag) {
        if (tag instanceof NBTTagString) {
            NBTTagString str = (NBTTagString) tag;
            return new NBTTagString(str.stringValue);
        } else if (tag instanceof NBTTagCompound) {
            return ((NBTTagCompound) tag).copy();
        } else if (tag instanceof NBTTagList) {
            return copyNbtList((NBTTagList) tag);
        } else if (tag instanceof NBTTagByte) {
            return new NBTTagByte(((NBTTagByte) tag).byteValue);
        } else if (tag instanceof NBTTagShort) {
            return new NBTTagShort(((NBTTagShort) tag).shortValue);
        } else if (tag instanceof NBTTagInt) {
            return new NBTTagInt(((NBTTagInt) tag).intValue);
        } else if (tag instanceof NBTTagLong) {
            return new NBTTagLong(((NBTTagLong) tag).longValue);
        } else if (tag instanceof NBTTagFloat) {
            return new NBTTagFloat(((NBTTagFloat) tag).floatValue);
        } else if (tag instanceof NBTTagDouble) {
            return new NBTTagDouble(((NBTTagDouble) tag).doubleValue);
        } else if (tag instanceof NBTTagByteArray) {
            byte[] data = ((NBTTagByteArray) tag).byteArray;
            return new NBTTagByteArray(data.clone());
        } else {
            // Fallback: unknown tag type (shouldn't happen)
            return tag;
        }
    }
}