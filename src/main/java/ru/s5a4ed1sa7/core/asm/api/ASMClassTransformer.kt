package ru.s5a4ed1sa7.core.asm.api

import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes

interface ASMClassTransformer : Opcodes {
    fun transformClass(name: String, transformedName: String, reader: ClassReader, classNode: SpecialClassNode): TransformResult = TransformResult.NOT_MODIFIED

    fun onModified(modified: Boolean): TransformResult {
        return if (modified) TransformResult.MODIFIED else TransformResult.NOT_MODIFIED
    }

    enum class TransformResult {
        NOT_MODIFIED,
        MODIFIED,
        MODIFIED_STACK
    }
}