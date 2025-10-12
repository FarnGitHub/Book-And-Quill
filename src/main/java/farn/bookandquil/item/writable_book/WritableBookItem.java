package farn.bookandquil.item.writable_book;

import farn.bookandquil.item.gui.BookScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;

public class WritableBookItem extends TemplateItem {
    public WritableBookItem(Identifier id) {
        super(id);
        this.setMaxCount(1);
    }

    @Environment(EnvType.CLIENT)
    public ItemStack use(ItemStack stack, World world, PlayerEntity user) {
        if(user instanceof ClientPlayerEntity) {
            ((Minecraft)FabricLoader.getInstance().getGameInstance()).setScreen(new BookScreen(user, stack, true));
        }
        return stack;
    }

}
