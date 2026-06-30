package farn.bookandquil.config;

import net.glasslauncher.mods.gcapi3.api.ConfigEntry;
import net.glasslauncher.mods.gcapi3.api.ConfigRoot;

public class ConfigListener {

    @ConfigRoot(value = "book_and_quill_config", visibleName = "Book And Quill Config")
    public static Inside get = new Inside();

    public static class Inside {
        @ConfigEntry(name="Pause while writing (SP Only)", description = "Pause the game while in the screen")
        public Boolean pauseGame = false;

        @ConfigEntry(name="Classic Book Screen", description = "Use the java edition book and quill screen")
        public Boolean classicBook = false;
    }
}
