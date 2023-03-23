package ru.s5a4ed1sa7.commonality.transformer

import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes.*
import ru.s5a4ed1sa7.core.asm.ASMCoreMod.Companion.isObfEnv
import ru.s5a4ed1sa7.core.asm.api.ASMClassTransformer
import ru.s5a4ed1sa7.core.asm.api.SpecialClassNode
import ru.s5a4ed1sa7.core.asm.api.SpecialInsnList
import ru.s5a4ed1sa7.core.asm.api.SpecialMethodNode

class MinecraftTransformer : ASMClassTransformer {
    override fun transformClass(
        name: String,
        transformedName: String,
        reader: ClassReader,
        classNode: SpecialClassNode
    ): ASMClassTransformer.TransformResult {
        var modified = false

        val memoryReserve = classNode.getFieldObf("memoryReserve", "field_71444_a")
        classNode.fields.remove(memoryReserve)

        val clinit = classNode.getMethod("<clinit>")
        clinit.findFieldCall(PUTSTATIC, "net/minecraft/client/Minecraft", if (isObfEnv) "field_71444_a" else "memoryReserve", "[B").ifPresent {
            clinit.instructions.remove(it.previous)
            clinit.instructions.remove(it.previous)
            clinit.instructions.remove(it)
            modified = modified or true
        }

        val freeMemory = classNode.getMethodObf("freeMemory", "func_71398_f")
        classNode.methods.remove(freeMemory)

        val freeMemoryNew = SpecialMethodNode(ACC_PUBLIC,  if (isObfEnv) "func_71398_f" else "freeMemory", "()V", null, null)
        freeMemoryNew.instructions = SpecialInsnList()
            .aLoad(0)
            .getField("net/minecraft/client/Minecraft", "renderGlobal", "Lnet/minecraft/client/renderer/RenderGlobal;")
            .invokeVirtual("net/minecraft/client/renderer/RenderGlobal", "deleteAllDisplayLists", "()V")
            .vReturn()

        classNode.methods.add(freeMemoryNew)

        val getLimitFramerate = classNode.getMethodObf("getLimitFramerate", "func_90020_K")
        getLimitFramerate.findSequence(false, BIPUSH).ifPresent {
            val list = SpecialInsnList().biPush(60)
            getLimitFramerate.instructions.insertBefore(it, list)
            getLimitFramerate.instructions.remove(it)
            modified = modified or true
        }

        return onModified(modified)
    }
}