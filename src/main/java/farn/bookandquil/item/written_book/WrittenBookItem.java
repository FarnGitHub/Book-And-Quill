package farn.bookandquil.item.written_book;

import farn.bookandquil.BookAndQuil;
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
import net.modificationstation.stationapi.api.client.item.CustomTooltipProvider;
import net.modificationstation.stationapi.api.item.StationItemNbt;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;

public class WrittenBookItem extends TemplateItem implements StationItemNbt, CustomTooltipProvider {
    public WrittenBookItem(Identifier id) {
        super(id);
        this.setMaxCount(1);
    }

    @Environment(EnvType.CLIENT)
    public ItemStack use(ItemStack stack, World world, PlayerEntity user) {
        if(user instanceof ClientPlayerEntity) {
            ((Minecraft) FabricLoader.getInstance().getGameInstance()).setScreen(new BookScreen(user, stack, false));
        }
        return stack;
    }

    @Override
    public NbtCompound getStationNbt() {
        NbtCompound theCompound = new NbtCompound();
        theCompound.putString("title", "The Book");
        theCompound.putString("author", "The Author");
        return null;
    }

    @Override
    public String[] getTooltip(ItemStack stack, String originalTooltip) {
        try {
            String title = originalTooltip;
            if(stack.getStationNbt() != null) {
                NbtCompound var2 = stack.getStationNbt();
                String var3 = var2.getString("title");
                if(var3 != null) {
                    title = var3;
                }
                String var6 = var2.getString("author");
                if(var6 != null) {
                    return title.isEmpty() ? new String[]{originalTooltip} : new String[]{title, "§8" + String.format(BookAndQuil.getTranslatedKeyFormat("book.byAuthor", new Object[]{var6}))};
                }
            }
            return title.isEmpty() ? new String[]{originalTooltip} : new String[]{title};
        } catch (Exception e) {
            return new String[]{originalTooltip};
        }
    }

}
