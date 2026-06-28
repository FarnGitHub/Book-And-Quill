package farn.bookandquil;

import farn.bookandquil.item.WritableBookItem;
import farn.bookandquil.packet.BookContentC2SPacket;
import farn.bookandquil.item.WrittenBookItem;
import farn.bookandquil.packet.SigningBookC2SPacket;
import farn.bookandquil.util.MainUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.event.texture.TextureRegisterEvent;
import net.modificationstation.stationapi.api.event.container.slot.ItemUsedInCraftingEvent;
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

@SuppressWarnings("unused")
public class BookAndQuill {
    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @Entrypoint.Logger
    public static Logger LOGGER = Null.get();

    public static Item BOOK_AND_QUILL;
    public static Item WRITTEN_BOOK;

    @EventListener
    public void registerItems(ItemRegistryEvent event) {
        BOOK_AND_QUILL = new WritableBookItem("writable_book").setTranslationKey(NAMESPACE, "writable_book");
        WRITTEN_BOOK = new WrittenBookItem("written_book").setTranslationKey(NAMESPACE, "written_book");
    }

    @EventListener
    public void registerPacket(PacketRegisterEvent event) {
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("writable_book_packet"), BookContentC2SPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("written_book_packet"), SigningBookC2SPacket.TYPE);
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
            CraftingRegistry.addShapelessRecipe(new ItemStack(BOOK_AND_QUILL, 1), Item.BOOK, new ItemStack(Item.DYE, 1, 0), Item.FEATHER);
        }
    }

    @EventListener
    public void afterCrafting(ItemUsedInCraftingEvent event) {
        if(MainUtil.isCopiedBook(event.itemCrafted) && MainUtil.isWrittenBook(event.itemUsed)) {
            event.craftingMatrix.setStack(event.itemOrdinal, event.itemUsed);
        }
    }

}
