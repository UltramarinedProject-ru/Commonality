package me.eigenraven.lwjgl3ify;

import me.eigenraven.lwjgl3ify.api.ConfigUtils;
import me.eigenraven.lwjgl3ify.core.Config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(
        modid = "lwjgl3ify",
        name = "Lwjgl3ify",
        version = "1.0",
        acceptedMinecraftVersions = "[1.7.10]",
        acceptableRemoteVersions = "*"
)
public class Lwjgl3ify {
    public static Logger LOG = LogManager.getLogger("LWJGL3IFY");
    @SidedProxy(clientSide = "me.eigenraven.lwjgl3ify.ClientProxy", serverSide = "me.eigenraven.lwjgl3ify.CommonProxy")
    public static CommonProxy PROXY;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        PROXY.runCompatHooks();
        LOG.info("Lwjgl3ify preInit - Java version {}", System.getProperty("java.specification.version"));

        // Test that ConfigUtils works as expected
        ConfigUtils utils = new ConfigUtils(LOG);
        if (!utils.isLwjgl3ifyLoaded()) {
            throw new IllegalStateException();
        }
        if (!utils.isConfigLoaded()) {
            throw new IllegalStateException();
        }
        if (utils.getExtensibleEnums() != Config.getExtensibleEnums()) {
            throw new IllegalStateException();
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        PROXY.registerF3Handler();
    }
}
