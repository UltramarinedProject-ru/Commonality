package ru.s5a4ed1sa7.core.asm

import cpw.mods.fml.relauncher.FMLLaunchHandler
import cpw.mods.fml.relauncher.IFMLLoadingPlugin
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.*
import me.eigenraven.lwjgl3ify.core.Config
import org.lwjgl.system.Configuration
import org.lwjgl.system.Platform
import org.lwjglx.Sys
import java.awt.Toolkit

@SortingIndex(1001)
@MCVersion("1.7.10")
@TransformerExclusions("ru.s5a4ed1sa7.core.asm", "kotlin", "ru.s5a4ed1sa7.commonality", "it.unimi", "org.lwjglx", "org.lwjgl", "org.lwjgl.input", "org.lwjglx.input")
class ASMCoreMod : IFMLLoadingPlugin {
    constructor() {
        Config.loadConfig()
        if (FMLLaunchHandler.side().isClient) {
            if (Platform.get() === Platform.MACOSX) {
                Configuration.GLFW_LIBRARY_NAME.set("glfw_async")
                Configuration.GLFW_CHECK_THREAD0.set(false)
                Toolkit.getDefaultToolkit() // Initialize AWT before GLFW
            }
            Sys.initialize()
        }
    }

    companion object {
        var isObfEnv = false
    }

    override fun getASMTransformerClass(): Array<String> {
        return arrayOf("ru.s5a4ed1sa7.core.asm.ClassTransformerImpl", "me.eigenraven.lwjgl3ify.core.LwjglRedirectTransformer")
    }

    override fun getModContainerClass(): String? {
        return null
    }

    override fun getSetupClass(): String? {
        return null
    }

    override fun injectData(data: MutableMap<String, Any>) {
        isObfEnv = data["runtimeDeobfuscationEnabled"] as Boolean
    }

    override fun getAccessTransformerClass(): String? {
        return null
    }
}