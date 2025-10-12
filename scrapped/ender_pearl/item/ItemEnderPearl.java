package farn.ender_pearl.item;

import farn.ender_pearl.EnderPearlStationAPI;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;

public class ItemEnderPearl extends TemplateItem {
    public ItemEnderPearl(Identifier identifier) {
        super(identifier);

    }

    public ItemStack use(ItemStack stack, World world, PlayerEntity user) {
        --stack.count;
        world.playSound(user, "random.bow", 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
        if(!world.isRemote) {
            world.spawnEntity(new EnderPearlEntity(world, user));
        }
        return stack;
    }


}
