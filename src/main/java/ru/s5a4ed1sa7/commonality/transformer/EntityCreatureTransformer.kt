package ru.s5a4ed1sa7.commonality.transformer

import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.FieldNode
import ru.s5a4ed1sa7.core.asm.ASMCoreMod.Companion.isObfEnv
import ru.s5a4ed1sa7.core.asm.api.ASMClassTransformer
import ru.s5a4ed1sa7.core.asm.api.SpecialClassNode
import ru.s5a4ed1sa7.core.asm.api.SpecialInsnList

class EntityCreatureTransformer : ASMClassTransformer {
    override fun transformClass(
        name: String,
        transformedName: String,
        reader: ClassReader,
        classNode: SpecialClassNode
    ): ASMClassTransformer.TransformResult {
        var modified = false

        classNode.fields.add(FieldNode(ACC_PRIVATE, "lastPathCountedTick", "I", null, 0))

        val updateEntityActionState = classNode.getMethodObf("updateEntityActionState", "func_70626_be")

        val getServer = if(isObfEnv) "func_71276_C" else "getServer"
        val getTickCounter = if(isObfEnv) "func_71259_af" else "getTickCounter"

        updateEntityActionState.findSequenceLast(true, GETFIELD, FLOAD, ICONST_1, ICONST_0, ICONST_0, ICONST_1, INVOKEVIRTUAL, PUTFIELD).ifPresent {
            val list = SpecialInsnList()
                .aLoad(0)
                .invokeStatic("net/minecraft/server/MinecraftServer", getServer, "()Lnet/minecraft/server/MinecraftServer;")
                .invokeVirtual("net/minecraft/server/MinecraftServer", getTickCounter, "()I")
                .putField(classNode.name, "lastPathCountedTick", "I")

            updateEntityActionState.instructions.insert(it, list)
            modified = modified or true
        }

        updateEntityActionState.findSequenceLast(true, ICONST_1, ICONST_0, ICONST_0, ICONST_1, INVOKEVIRTUAL, PUTFIELD, GOTO).ifPresent {
            val start = SpecialInsnList()
                .invokeStatic("net/minecraft/server/MinecraftServer", getServer, "()Lnet/minecraft/server/MinecraftServer;")
                .invokeVirtual("net/minecraft/server/MinecraftServer", getTickCounter, "()I")
                .aLoad(0)
                .getField(classNode.name, "lastPathCountedTick", "I")
                .iSub()
                .biPush(10)
                .startIf(IF_ICMPGT)

            val startIf = it.previous.previous.previous.previous.previous.previous.previous.previous.previous.previous.previous.previous.previous.previous
            updateEntityActionState.instructions.insert(startIf, start)

            start.aLoad(0)
                .invokeStatic("net/minecraft/server/MinecraftServer", getServer, "()Lnet/minecraft/server/MinecraftServer;")
                .invokeVirtual("net/minecraft/server/MinecraftServer", getTickCounter, "()I")
                .putField(classNode.name, "lastPathCountedTick", "I")
                .endIf()
            updateEntityActionState.instructions.insert(it.previous, start)
            modified = modified or true
        }

        return onModified(modified)
    }
}