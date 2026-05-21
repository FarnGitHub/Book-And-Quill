package farn.bookandquill.forge;

import forge.MinecraftForge;

public class ForgeRegisterer {

    public static void registerHook() {
        MinecraftForge.registerCraftingHandler(new BookAndQuillCraftingHandler());
    }
}
