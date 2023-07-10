package ru.s5a4ed1sa7.core.asm

import net.minecraft.launchwrapper.IClassTransformer
import net.minecraft.launchwrapper.Launch
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.util.TraceClassVisitor
import ru.s5a4ed1sa7.commonality.transformer.*
import ru.s5a4ed1sa7.core.asm.api.ASMClassTransformer
import ru.s5a4ed1sa7.core.asm.api.ComputeFramesClassWriter
import ru.s5a4ed1sa7.core.asm.api.SpecialClassNode
import java.io.File
import java.io.IOException
import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption


class ClassTransformerImpl : IClassTransformer {
    companion object {
        @JvmField
        val IS_DEV = Launch.blackboard["fml.deobfuscatedEnvironment"] as? Boolean ?: true

        private const val dumpASM = true
        private const val dumpClass = true

        private var asmDir = if (dumpASM) {
            val dir = Paths.get("asm_dump")
            val asmDir = dir.resolve("asm")
            if(Files.exists(asmDir)) {
                Files.walk(asmDir).sorted(reverseOrder()).map(Path::toFile).forEach(File::delete)
            }
            Files.createDirectories(asmDir)
            asmDir
        } else null

        private var classDir = if(dumpClass) {
            val dir = Paths.get("asm_dump")
            val classDir = dir.resolve("class")
            if(Files.exists(classDir)) {
                Files.walk(classDir).sorted(reverseOrder()).map(Path::toFile).forEach(File::delete)
            }
            Files.createDirectories(classDir)
            classDir
        } else null
    }


    @Suppress("UNUSED")
    constructor() {
        //TODO - Startup error's fix
        registerSpecialTransformer(JarDiscoverTransformer(), "cpw.mods.fml.common.discovery.JarDiscoverer")
        registerSpecialTransformer(ForgeVersionTransformer(), "net.minecraftforge.common.ForgeVersion")
        registerSpecialTransformer(EventSubscriptionTransformer(), "cpw.mods.fml.common.asm.transformers.EventSubscriptionTransformer")
        //TODO - Optimized random (faster generation, ticking, increase performance )
        registerGlobalTransformer(RandomInitializerTransformer())
        //TODO - Fix ConcurrentModificationException
        registerSpecialTransformer(ItemStackTransformer(), "net.minecraft.item.ItemStack")
        //TODO - Increase update player tick rate
        registerSpecialTransformer(EntityTrackerTransformer(), "net.minecraft.entity.EntityTracker")
        //TODO - Honest xp consuming
        registerSpecialTransformer(EntityPlayerTransformer(), "net.minecraft.entity.player.EntityPlayer")
        //TODO - Fix double-logging
        registerSpecialTransformer(ServerConfigurationManagerTransformer(), "net.minecraft.server.management.ServerConfigurationManager")
        //TODO - Increase gui frame-time to 60, and memory fixing
        registerSpecialTransformer(MinecraftTransformer(), "net.minecraft.client.Minecraft")
        //TODO - Fix pathfinding at entity (optimize)
        registerSpecialTransformer(EntityCreatureTransformer(), "net.minecraft.entity.EntityCreature")
        //TODO - Remove checking OpenGL Error's because it's so laggy (fps increase at 25-40%)
        registerSpecialTransformer(CheckGLErrorTransformer(), arrayOf("net.minecraft.client.Minecraft", "cpw.mods.fml.client.SplashProgress"))
    }

    private val globalTransformers: MutableList<ASMClassTransformer> = mutableListOf()
    private val specialTransformers: MutableMap<String, MutableList<ASMClassTransformer>> = mutableMapOf()

    private fun registerGlobalTransformer(transformer: ASMClassTransformer) {
        globalTransformers.add(transformer)
    }

    private fun registerSpecialTransformer(transformer: ASMClassTransformer, name: String) {
        specialTransformers.computeIfAbsent(name) { ArrayList(1) }.add(transformer)
    }

    private fun registerSpecialTransformer(transformer: ASMClassTransformer, names: Array<String>) {
        names.forEach { registerSpecialTransformer(transformer, it) }
    }

    override fun transform(name: String, transformedName: String, basicClass: ByteArray?): ByteArray? {
        if (basicClass == null) return null
        val classNode = SpecialClassNode()
        val classReader = ClassReader(basicClass)
        classReader.accept(classNode, 0)
        var flags = 0
        for (transformer in specialTransformers.getOrDefault(transformedName, emptyList())) flags = flags or transformer.transformClass(name, transformedName, classReader, classNode).ordinal
        for (transformer in globalTransformers) flags = flags or transformer.transformClass(name, transformedName, classReader, classNode).ordinal
        val shouldComputeFrames = classNode.version and 0xFFFF > Opcodes.V1_6
        if (flags == 0) return basicClass
        val writer = if (shouldComputeFrames) ComputeFramesClassWriter() else ClassWriter(if (flags == 1) 0 else 1)
        classNode.accept(writer)
        val b = writer.toByteArray()
        if (dumpClass && classDir != null) {
            try {
                Files.write(classDir!!.resolve("$transformedName.class"), b, StandardOpenOption.CREATE)
            } catch (_: IOException) {}
        }
        if (dumpASM && asmDir != null) {
            try {
                val traceClassVisitor = TraceClassVisitor(PrintWriter(asmDir!!.resolve("$transformedName.asm").toFile()))
                val cr = ClassReader(b)
                cr.accept(traceClassVisitor, 0)
            } catch (_: IOException) {}
        }
        return b
    }
}