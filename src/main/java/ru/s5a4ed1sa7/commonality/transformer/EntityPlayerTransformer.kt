package ru.s5a4ed1sa7.commonality.transformer

import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes.IF_ICMPLT
import ru.s5a4ed1sa7.core.asm.ASMUtil.insertStart
import ru.s5a4ed1sa7.core.asm.api.ASMClassTransformer
import ru.s5a4ed1sa7.core.asm.api.SpecialClassNode
import ru.s5a4ed1sa7.core.asm.api.SpecialInsnList

class EntityPlayerTransformer : ASMClassTransformer {
    override fun transformClass(
        name: String,
        transformedName: String,
        reader: ClassReader,
        classNode: SpecialClassNode
    ): ASMClassTransformer.TransformResult {
        val addExperienceLevel = classNode.getMethodObf("addExperienceLevel", "func_82242_a")

        val list = SpecialInsnList()
            .iLoad(1)
            .biPush(0)
            .startIf(IF_ICMPLT)
            .iLoad(1)
            .aLoad(0)
            .invokeStatic("ru/s5a4ed1sa7/commonality/util/MinecraftUtil", "calculateXPForPlayer", "(ILnet/minecraft/entity/player/EntityPlayer;)V")
            .vReturn()
            .endIf()

        addExperienceLevel.insertStart(list)

        return ASMClassTransformer.TransformResult.MODIFIED
    }
}