package ru.s5a4ed1sa7.core.asm.api.impl

import org.objectweb.asm.ClassReader
import ru.s5a4ed1sa7.core.asm.api.ASMClassTransformer
import ru.s5a4ed1sa7.core.asm.api.ASMClassTransformer.TransformResult
import ru.s5a4ed1sa7.core.asm.api.SpecialClassNode

class AddInterfaceTransformer(private val toAdd: Array<String>) : ASMClassTransformer {
    override fun transformClass(name: String, transformedName: String, reader: ClassReader, classNode: SpecialClassNode): TransformResult {
        toAdd.forEach {
            classNode.addInterface(it).build()
        }
        return TransformResult.MODIFIED
    }
}