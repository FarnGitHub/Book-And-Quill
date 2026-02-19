package farn.ender_pearl;

import farn.ender_pearl.item.EnderPearlEntity;
import farn.ender_pearl.item.EnderPearlEntityRenderer;
import farn.ender_pearl.item.ItemEnderPearl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.client.entity.factory.EntityWorldAndPosFactory;
import net.modificationstation.stationapi.api.client.event.render.entity.EntityRendererRegisterEvent;
import net.modificationstation.stationapi.api.client.event.texture.TextureRegisterEvent;
import net.modificationstation.stationapi.api.event.registry.EntityHandlerRegistryEvent;

public class EnderPearlStationAPIClient implements EntityWorldAndPosFactory {

    public static int ENDER_PEARL_TEXTURE = 0;

    private static EnderPearlEntityRenderer renderer = new EnderPearlEntityRenderer(0);

    @EventListener
    public void registerEntityRenderer(EntityRendererRegisterEvent event) {
        event.renderers.put(EnderPearlEntity.class, renderer);
    }

    @EventListener
    public void registerTextures(TextureRegisterEvent event) {
        EnderPearlStationAPI.ENDER_PEARL.setTexture(EnderPearlStationAPI.NAMESPACE.id("item/ender_pearl"));
        ENDER_PEARL_TEXTURE = EnderPearlStationAPI.ENDER_PEARL.getTextureId(0);
        renderer.setTexture(ENDER_PEARL_TEXTURE);
    }

    @EventListener
    public void registerClientEntity(EntityHandlerRegistryEvent event) {
        event.register(EnderPearlStationAPI.NAMESPACE.id("ender_pearl_entity"), this);
    }

    @Override
    public Entity create(World world, double x, double y, double z) {
        return new EnderPearlEntity(world,x,y,z);
    }
}
