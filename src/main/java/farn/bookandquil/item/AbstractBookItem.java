package farn.bookandquil.item;

import farn.bookandquil.BookAndQuill;
import farn.bookandquil.gui.screen.BookScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.template.item.TemplateItem;

public abstract class AbstractBookItem extends TemplateItem {
    public AbstractBookItem(String id) {
        super(BookAndQuill.id(id));
        this.setMaxCount(1);
    }

    @Environment(EnvType.CLIENT)
    public ItemStack use(ItemStack stack, World world, PlayerEntity user) {
        if(user instanceof ClientPlayerEntity)
            Minecraft.INSTANCE.setScreen(BookScreen.get(user, stack, writable()));
        return stack;
    }

    abstract boolean writable();
}
