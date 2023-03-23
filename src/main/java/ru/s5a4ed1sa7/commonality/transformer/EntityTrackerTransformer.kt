package ru.s5a4ed1sa7.commonality.transformer

import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes.*
import ru.s5a4ed1sa7.core.asm.ASMUtil.replace
import ru.s5a4ed1sa7.core.asm.api.ASMClassTransformer
import ru.s5a4ed1sa7.core.asm.api.SpecialClassNode
import ru.s5a4ed1sa7.core.asm.api.SpecialInsnList

class EntityTrackerTransformer : ASMClassTransformer {
    override fun transformClass(
        name: String,
        transformedName: String,
        reader: ClassReader,
        classNode: SpecialClassNode
    ): ASMClassTransformer.TransformResult {
        var modified = false
        val addEntityToTracker = classNode.getMethod("addEntityToTracker")
        addEntityToTracker.findSequenceLast(false, ALOAD, ALOAD, SIPUSH, ICONST_2).ifPresent {
            val list = SpecialInsnList().node(ICONST_1)
            addEntityToTracker.replace(it, list)
            modified = true
        }

        return onModified(modified)
    }
}