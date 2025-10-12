package farn.bookandquil;

import farn.bookandquil.item.writable_book.WritableBookItem;
import farn.bookandquil.item.writable_book.WritableBookServerPacket;
import farn.bookandquil.item.written_book.WrittenBookItem;
import farn.bookandquil.item.written_book.WrittenBookServerPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.modificationstation.stationapi.api.client.event.texture.TextureRegisterEvent;
import net.modificationstation.stationapi.api.event.network.packet.PacketRegisterEvent;
import net.modificationstation.stationapi.api.event.recipe.RecipeRegisterEvent;
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent;
import net.modificationstation.stationapi.api.recipe.CraftingRegistry;
import net.modificationstation.stationapi.api.registry.PacketTypeRegistry;
import net.modificationstation.stationapi.api.registry.Registry;
import net.modificationstation.stationapi.api.util.Namespace;
import net.modificationstation.stationapi.api.util.Null;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import org.apache.logging.log4j.Logger;

public class BookAndQuil {
    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @Entrypoint.Logger
    public static Logger LOGGER = Null.get();

    public static Item BOOK_AND_QUILL;
    public static Item WRITTEN_BOOK;


    @EventListener
    public void registerItems(ItemRegistryEvent event) {
        BOOK_AND_QUILL = new WritableBookItem(NAMESPACE.id("writable_book")).setTranslationKey(NAMESPACE, "writable_book");
        WRITTEN_BOOK = new WrittenBookItem(NAMESPACE.id("written_book")).setTranslationKey(NAMESPACE, "written_book");
        LOGGER.info(BOOK_AND_QUILL.getTranslationKey());
    }

    @EventListener
    public void registerPacket(PacketRegisterEvent event) {
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("writable_book_packet"), WritableBookServerPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("written_book_packet"), WrittenBookServerPacket.TYPE);
    }

    @Environment(EnvType.CLIENT)
    @EventListener
    public void registerTextures(TextureRegisterEvent event) {
        BOOK_AND_QUILL.setTexture(NAMESPACE.id("item/writingBook"));
        WRITTEN_BOOK.setTexture(NAMESPACE.id("item/writtenBook"));
    }

    @EventListener
    public void registerRecipes(RecipeRegisterEvent event) {
        RecipeRegisterEvent.Vanilla type = RecipeRegisterEvent.Vanilla.fromType(event.recipeId);

        if (type == RecipeRegisterEvent.Vanilla.CRAFTING_SHAPED) {
            CraftingRegistry.addShapelessRecipe(new ItemStack(BOOK_AND_QUILL, 1), new Object[]{Item.BOOK, new ItemStack(Item.DYE, 1, 0), Item.FEATHER});
        }
    }

    public static String getTranslatedKey(String string) {
        return TranslationStorage.getInstance().get("bookscreen.bookandquill." + string);
    }

    public static String getTranslatedKeyFormat(String string, Object... var1) {
        return TranslationStorage.getInstance().get("bookscreen.bookandquill." + string, var1);
    }

    public static boolean hasSomeWriting(NbtCompound var0) {
        if(var0 == null) {
            return false;
        } else if(!var0.contains("pages")) {
            return false;
        } else {
            NbtList var1 = (NbtList)var0.getList("pages");

            for(int var2 = 0; var2 < var1.size(); ++var2) {
                NbtString var3 = (NbtString)var1.get(var2);
                if(var3.value == null) {
                    return false;
                }

                if(var3.value.length() > 256) {
                    return false;
                }
            }

            return true;
        }
    }

}
