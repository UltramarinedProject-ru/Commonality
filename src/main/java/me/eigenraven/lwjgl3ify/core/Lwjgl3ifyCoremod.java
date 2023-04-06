package me.eigenraven.lwjgl3ify.core;

import java.awt.*;
import java.util.Map;

import net.minecraft.launchwrapper.LaunchClassLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.Platform;
import org.lwjglx.Sys;

import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.TransformerExclusions({ "org.lwjglx", "org.lwjgl", "org.lwjgl.input", "org.lwjglx.input" })
@IFMLLoadingPlugin.SortingIndex(Integer.MAX_VALUE - 2)
public class Lwjgl3ifyCoremod implements IFMLLoadingPlugin {

    public static final Logger LOGGER = LogManager.getLogger("LWJGL3IFY");

    public Lwjgl3ifyCoremod() {
        Config.loadConfig();
        try {
            LaunchClassLoader launchLoader = (LaunchClassLoader) getClass().getClassLoader();
            // Packages that used to be in rt.jar
            launchLoader.addClassLoaderExclusion("com.sun");
            launchLoader.addClassLoaderExclusion("com.oracle");
            launchLoader.addClassLoaderExclusion("javax");
            launchLoader.addClassLoaderExclusion("jdk");
            launchLoader.addClassLoaderExclusion("org.ietf.jgss");
            launchLoader.addClassLoaderExclusion("org.jcp.xml.dsig.internal");
            launchLoader.addClassLoaderExclusion("org.omg");
            launchLoader.addClassLoaderExclusion("org.w3c.dom");
            launchLoader.addClassLoaderExclusion("org.xml.sax");
            launchLoader.addClassLoaderExclusion("org.hotswap.agent");
            launchLoader.addClassLoaderExclusion("org.lwjglx.debug");
        } catch (ClassCastException e) {
            LOGGER.warn("Unsupported launch class loader type " + getClass().getClassLoader().getClass(), e);
        }
        // Ensure javax.script.ScriptEngineManager gets loaded
        try {
            Class.forName("javax.script.ScriptEngineManager");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (FMLLaunchHandler.side().isClient()) {
            Sys.initialize();
        }
    }

    @Override
    public String[] getASMTransformerClass() {
        LOGGER.info("Registering lwjgl3ify redirect transformer");

        return new String[] { LwjglRedirectTransformer.class.getName()};
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
