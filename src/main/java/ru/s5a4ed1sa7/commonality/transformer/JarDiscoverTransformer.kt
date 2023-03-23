package ru.s5a4ed1sa7.commonality.transformer

import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.LabelNode
import ru.s5a4ed1sa7.core.asm.api.ASMClassTransformer
import ru.s5a4ed1sa7.core.asm.api.SpecialClassNode
import ru.s5a4ed1sa7.core.asm.api.SpecialInsnList

class JarDiscoverTransformer : ASMClassTransformer {
    override fun transformClass(
        name: String,
        transformedName: String,
        reader: ClassReader,
        classNode: SpecialClassNode
    ): ASMClassTransformer.TransformResult {
        val discover = classNode.getMethod("discover")
        discover.findSequenceLast(false, IFNULL, ALOAD, INVOKEVIRTUAL, LDC, INVOKEVIRTUAL, IFEQ).ifPresent {
            val list = SpecialInsnList()
            val label = LabelNode()

            list.jump(IFEQ, label)
                .aLoad(8)
                .invokeVirtual("java/util/zip/ZipEntry", "getName", "()Ljava/lang/String;")
                .ldc("META_INF/versions")
                .invokeVirtual("java/lang/String", "startsWith", "(Ljava/lang/String;)Z")

            discover.instructions.insert(it, label)
            discover.instructions.insertBefore(it, list)
        }
        return ASMClassTransformer.TransformResult.MODIFIED
    }
}