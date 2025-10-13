package net.minecraft.src;

import bookandquill.code.ItemEditableBook;
import bookandquill.code.ItemWritableBook;
import com.sun.jmx.mbeanserver.ModifiableClassLoaderRepository;
import jdk.internal.module.ModuleLoaderMap;

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

    public mod_BookAndQuill() {
        System.out.println("Example Mod initialized.");
        writableBook = new ItemWritableBook(bookAndQuilID).setItemName("bookandquill.writablebook.name");
        ModLoader.AddLocalization(writableBook.getItemName(), "Book And Quill");
        writableBook.setIconIndex(ModLoader.addOverride("/gui/items.png", "/bookandquill/resource/writingBook.png"));
        writtenBook = new ItemEditableBook(writtenBookID).setItemName("bookandquill.writtenbook.name");
        ModLoader.AddLocalization(writtenBook.getItemName(), "Written Book");
        writtenBook.setIconIndex(ModLoader.addOverride("/gui/items.png", "/bookandquill/resource/writtenBook.png"));
        ModLoader.AddShapelessRecipe(new ItemStack(writableBook, 1), new Object[]{Item.book, new ItemStack(Item.dyePowder, 1, 0), Item.feather});
    }

    @Override
    public String Version() {
        return "1.0.0";
    }

    /* Mod Menu Information */

    public String Name() {
        return "Example Mod";
    }

    public String Description() {
        return "Lorem ipsum dolor sit amet";
    }

    public static void removeTag(NBTTagList list, int base) {
        try {
            Field tagListField = list.getClass().getDeclaredField("a");
            tagListField.setAccessible(true);
            ((List)tagListField.get(list)).remove(base);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}