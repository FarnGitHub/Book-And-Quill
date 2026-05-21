package farn.bookandquill;

import farn.bookandquill.forge.ForgeRegisterer;
import farn.bookandquill.item.ItemWritableBook;
import farn.bookandquill.item.ItemWrittenBook;
import net.minecraft.src.*;

import java.lang.reflect.Field;
import java.util.List;

public class BookAndQuill {
    public static Item writableBook;
    public static Item writtenBook;

    public static final String VERSION = "1.1";
    public static final String NAME = "Book And Quill";
    public static final String DESCRIPTION = "Add Book And Quill to b1.7.3";
    public static final String ICON_PATH = "farn/bookandquill/resource/icon.png";

    public static boolean canDoBookCopy = false;

    private BookAndQuill() {
        throw new AssertionError();
    }

    public static void init() {
        try {
            Class.forName("mod_ItemNBT");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("BookAndQuill: ItemNBT Mod not found, this mod require it");
        }

        writableBook = new ItemWritableBook(mod_BookAndQuill.bookAndQuilID).setItemName("farn.code.bookandquill.writablebook");
        ModLoader.AddLocalization(writableBook.getItemName() + ".name", "Book And Quill");
        writableBook.setIconIndex(ModLoader.addOverride("/gui/items.png", "/farn/bookandquill/resource/writingBook.png"));

        //Written Book Item
        writtenBook = new ItemWrittenBook(mod_BookAndQuill.writtenBookID).setItemName("farn.code.bookandquill.writtenbook");
        ModLoader.AddLocalization(writtenBook.getItemName() + ".name", "Written Book");
        writtenBook.setIconIndex(ModLoader.addOverride("/gui/items.png", "/farn/bookandquill/resource/writtenBook.png"));

        //Book And Quill Recipe
        ModLoader.AddShapelessRecipe(new ItemStack(writableBook, 1), Item.book, new ItemStack(Item.dyePowder, 1, 0), Item.feather);
    }

    public static void removeFromNbtList(NBTTagList list, int index) {
        try {
            Field tagListField = list.getClass().getDeclaredField("a");
            tagListField.setAccessible(true);
            ((List<?>)tagListField.get(list)).remove(index);
        } catch (Exception e) {
            throw new RuntimeException();
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
        switch (tag.getType()) {
            case 0:
                return new NBTTagEnd();
            case 1:
                return new NBTTagByte(((NBTTagByte) tag).byteValue);
            case 2:
                return new NBTTagShort(((NBTTagShort) tag).shortValue);
            case 3:
                return new NBTTagInt(((NBTTagInt) tag).intValue);
            case 4:
                return new NBTTagLong(((NBTTagLong) tag).longValue);
            case 5:
                return new NBTTagFloat(((NBTTagFloat) tag).floatValue);
            case 6:
                return new NBTTagDouble(((NBTTagDouble) tag).doubleValue);
            case 7:
                byte[] data = ((NBTTagByteArray) tag).byteArray;
                return new NBTTagByteArray(data.clone());
            case 8:
                NBTTagString str = (NBTTagString) tag;
                return new NBTTagString(str.stringValue);
            case 9:
                return copyNbtList((NBTTagList) tag);
            case 10:
                return ((NBTTagCompound) tag).copy();
        }
        throw new RuntimeException("Invalid tag type");
    }

    public static ItemStack getCopyableBook(InventoryCrafting inv)
    {
        boolean hasWritableBook = false;
        ItemStack theBook = null;

        for (int index = 0; index < inv.getSizeInventory(); ++index)
        {
            ItemStack curStack = inv.getStackInSlot(index);

            if (curStack != null)
            {
                if (curStack.getItem() instanceof ItemWrittenBook)
                {
                    if (theBook != null)
                    {
                        return null;
                    }

                    theBook = curStack;
                } else if(curStack.getItem() instanceof ItemWritableBook) {
                    if(hasWritableBook) {
                        return null;
                    }

                    hasWritableBook = true;
                }
            }
        }

        if (theBook != null && hasWritableBook)
        {
            ItemStack stack = new ItemStack(BookAndQuill.writtenBook, 1);
            stack.setItemData(theBook.getItemData().copy());
            stack.getItemData().setBoolean("copy", true);
            return stack;
        } else {
            return null;
        }
    }

    static {
        try {
            Class.forName("forge.ICraftingHandler");
            Class.forName("xyz.wagyourtail.unimined.jarmodagent.JarModAgent");
            canDoBookCopy = true;
        } catch (ClassNotFoundException e) {
            canDoBookCopy = false;
            System.out.println("BookAndQuill: JarModAgent or forge not found, disable book copy");
        }

        if(canDoBookCopy)
            ForgeRegisterer.registerHook();
    }

}
