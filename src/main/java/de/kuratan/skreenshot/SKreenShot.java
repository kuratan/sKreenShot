package de.kuratan.skreenshot;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;


@Mod(modid = SKreenShot.MODID, version = SKreenShot.VERSION, name = "sKreenShot")
public class SKreenShot {
    public static final String MODID = "@MOD_ID@";
    public static final String VERSION = "@VERSION@";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Config.init(event.getSuggestedConfigurationFile());
    }

    @EventHandler
    public void startingServer(FMLServerStartingEvent event) {
        event.registerServerCommand(new MenuBackgroundCommand());
        event.registerServerCommand(new SizedScreenShotCommand());
    }
}
