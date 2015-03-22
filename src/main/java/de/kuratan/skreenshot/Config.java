package de.kuratan.skreenshot;


import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class Config {
    public static Configuration configuration;

    public static int FRAME_DELAY;
    public static String BASE_DIR;

    public static void init(File configFile) {
        if (configuration == null) {
            configuration = new Configuration(configFile);
            loadConfiguration();
        }
    }

    public static void loadConfiguration() {
        FRAME_DELAY = configuration.getInt("FRAME_DELAY", Configuration.CATEGORY_GENERAL, 10, 0, 120, "Delay in Frames before saving Screenshot after changing orientation");
        BASE_DIR = configuration.getString("BASE_DIR", Configuration.CATEGORY_GENERAL, ".", "Parent for the menubackground directory");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    @SubscribeEvent
    public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equalsIgnoreCase(SKreenShot.MODID)) {
            loadConfiguration();
        }
    }
}
