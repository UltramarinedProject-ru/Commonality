package ru.s5a4ed1sa7.commonality.transformer

import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes.BIPUSH
import ru.s5a4ed1sa7.core.asm.api.ASMClassTransformer
import ru.s5a4ed1sa7.core.asm.api.SpecialClassNode
import ru.s5a4ed1sa7.core.asm.api.SpecialInsnList

class MinecraftTransformer : ASMClassTransformer {
    override fun transformClass(
        name: String,
        transformedName: String,
        reader: ClassReader,
        classNode: SpecialClassNode
    ): ASMClassTransformer.TransformResult {
        var modified = false

        val getLimitFramerate = classNode.getMethodObf("getLimitFramerate", "func_90020_K")
        getLimitFramerate.findSequence(false, BIPUSH).ifPresent {
            val list = SpecialInsnList().biPush(60)
            getLimitFramerate.instructions.insertBefore(it, list)
            getLimitFramerate.instructions.remove(it)
        }

        return onModified(modified)
    }
}