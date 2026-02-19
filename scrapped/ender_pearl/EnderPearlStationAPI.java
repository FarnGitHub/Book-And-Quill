package farn.ender_pearl;

import farn.ender_pearl.item.EnderPearlEntity;
import farn.ender_pearl.item.ItemEnderPearl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.item.Item;

import net.modificationstation.stationapi.api.event.entity.EntityRegister;
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.server.event.entity.TrackEntityEvent;
import net.modificationstation.stationapi.api.util.Namespace;
import net.modificationstation.stationapi.api.util.Null;
import org.apache.logging.log4j.Logger;

public class EnderPearlStationAPI {

    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @Entrypoint.Logger
    public static Logger LOGGER = Null.get();

    public static Item ENDER_PEARL;

    @EventListener
    public void registerItems(ItemRegistryEvent event) {
        ENDER_PEARL = new ItemEnderPearl(NAMESPACE.id("ender_pearl")).setTranslationKey(NAMESPACE, "enderpearl").setMaxCount(16);
    }

    @EventListener
    public void registerEntity(EntityRegister event) {
        event.register(EnderPearlEntity.class, "Ender Pearl Entity");
    }

}
