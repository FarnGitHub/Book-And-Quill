package farn.bookandquill;

import farn.bookandquill.mod.forge.BookAndQuillCraftingHandler;
import farn.bookandquill.mod.forge.ClonedBookRecipe;
import farn.bookandquill.item.ItemWritableBook;
import farn.bookandquill.item.ItemWrittenBook;
import farn.bookandquill.mod.guiapi.Config;
import net.minecraft.src.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Field;
import java.util.List;

public class BookAndQuill {
    public static Item writableBook;
    public static Item writtenBook;

    public static final String VERSION = "1.1";
    public static final String NAME = "Book And Quill";
    public static final String DESCRIPTION = "Add Book And Quill to b1.7.3";
    public static final String ICON_PATH = "/assets/bookandquill/icon.png";
    public static boolean hasGuiApi = false;

    private BookAndQuill() {
        throw new AssertionError();
    }

    public static void init() {
        if(!hasClass("mod_ItemNBT")) {
            throw new RuntimeException("BookAndQuill: ItemNBT Mod not found, this mod require it");
        }

        writableBook = new ItemWritableBook(mod_BookAndQuill.bookAndQuilID).setItemName("farn.code.bookandquill.writablebook");
        ModLoader.AddLocalization(writableBook.getItemName() + ".name", "Book And Quill");
        writableBook.setIconIndex(ModLoader.addOverride("/gui/items.png", "/assets/bookandquill/textures/item/writingBook.png"));

        //Written Book Item
        writtenBook = new ItemWrittenBook(mod_BookAndQuill.writtenBookID).setItemName("farn.code.bookandquill.writtenbook");
        ModLoader.AddLocalization(writtenBook.getItemName() + ".name", "Written Book");
        ModLoader.AddLocalization(writtenBook.getItemName() + ".name.copy", "Written Book (Copy)");
        writtenBook.setIconIndex(ModLoader.addOverride("/gui/items.png", "/assets/bookandquill/textures/item/writtenBook.png"));

        //Book And Quill Recipe
        ModLoader.AddShapelessRecipe(new ItemStack(writableBook, 1), Item.book, new ItemStack(Item.dyePowder, 1, 0), Item.feather);

        if(hasClass("forge.ICraftingHandler")) {
            System.out.println("BookAndQuill: forge found, add book cloning recipe");
            //noinspection unchecked
            CraftingManager.getInstance().getRecipeList().add(new ClonedBookRecipe());
            BookAndQuillCraftingHandler.init();
        }

        hasGuiApi = hasClass("GuiApiHelper");
        if(hasGuiApi) {
            Config.INSTANCE.initScreen();
        }
    }

    public static void remove(NBTTagList list, int index) {
        try {
            Field tagListField = list.getClass().getDeclaredField("a");
            tagListField.setAccessible(true);
            ((List<?>)tagListField.get(list)).remove(index);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public static <T extends NBTBase> T copyOf(NBTBase tag) {
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(byteOutput);
        NBTBase.writeTag(tag, outputStream);
        byte[] data = byteOutput.toByteArray();
        //noinspection unchecked
        return (T)NBTBase.readTag(new DataInputStream(new ByteArrayInputStream(data)));
    }

    public static boolean hasClass(String className) {
        try {
            //noinspection ConstantValue
            return Class.forName(className) != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isClassicScreen() {
        return hasGuiApi && Config.INSTANCE.classicScreen.get("");
    }

    public static boolean pauseGame() {
        return hasGuiApi && Config.INSTANCE.pauseGame.get("");
    }

}
