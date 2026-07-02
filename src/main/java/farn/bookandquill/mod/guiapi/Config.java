package farn.bookandquill.mod.guiapi;

import net.minecraft.src.ModSettingScreen;
import net.minecraft.src.SettingBoolean;
import net.minecraft.src.WidgetBoolean;

public class Config {
    public SettingBoolean pauseGame = new SettingBoolean("pauseGame", false);
    public SettingBoolean classicScreen = new SettingBoolean("classicScreen", false);
    public static final Config INSTANCE = new Config();

    public void initScreen() {
        ModSettingScreen screen = new ModSettingScreen("Book And Quill");
        screen.append(new WidgetBoolean(pauseGame, "Pause Game"));
        screen.append(new WidgetBoolean(classicScreen, "Classic Screen"));
    }
}
