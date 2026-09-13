package farn.bookandquil.config;

import farn.bookandquil.BookAndQuill;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.item.ItemStack;
import paulevs.bhcreative.listeners.VanillaTabListener;
import paulevs.bhcreative.registry.TabRegistryEvent;

public class BHCreativeListener {

    @EventListener
    public void registerCreativeTab(TabRegistryEvent event) {
        VanillaTabListener.tabItems.addItem(new ItemStack(BookAndQuill.BOOK_AND_QUILL));
        VanillaTabListener.tabItems.addItem(new ItemStack(BookAndQuill.WRITTEN_BOOK));
    }
}
