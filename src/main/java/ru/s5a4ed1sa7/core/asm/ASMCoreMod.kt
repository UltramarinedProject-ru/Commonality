package ru.s5a4ed1sa7.core.asm

import cpw.mods.fml.relauncher.IFMLLoadingPlugin
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.*

@SortingIndex(1001)
@MCVersion("1.7.10")
@TransformerExclusions("ru.s5a4ed1sa7.core.asm", "kotlin", "ru.s5a4ed1sa7.commonality", "it.unimi")
class ASMCoreMod : IFMLLoadingPlugin {
    companion object {
        var isObfEnv = false
    }

    override fun getASMTransformerClass(): Array<String> {
        return arrayOf("ru.s5a4ed1sa7.core.asm.ClassTransformerImpl")
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