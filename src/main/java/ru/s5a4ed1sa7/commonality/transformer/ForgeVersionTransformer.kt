package ru.s5a4ed1sa7.commonality.transformer

import org.objectweb.asm.ClassReader
import ru.s5a4ed1sa7.core.asm.api.ASMClassTransformer
import ru.s5a4ed1sa7.core.asm.api.SpecialClassNode
import ru.s5a4ed1sa7.core.asm.api.SpecialInsnList

class ForgeVersionTransformer : ASMClassTransformer {
    override fun transformClass(
        name: String,
        transformedName: String,
        reader: ClassReader,
        classNode: SpecialClassNode
    ): ASMClassTransformer.TransformResult {
        classNode.getMethod("startVersionCheck").instructions = SpecialInsnList().vReturn()
        return ASMClassTransformer.TransformResult.MODIFIED
    }
}