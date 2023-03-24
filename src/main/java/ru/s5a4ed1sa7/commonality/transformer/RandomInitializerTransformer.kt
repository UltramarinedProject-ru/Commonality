package ru.s5a4ed1sa7.commonality.transformer

import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes.INVOKESPECIAL
import org.objectweb.asm.Opcodes.NEW
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.TypeInsnNode
import ru.s5a4ed1sa7.core.asm.api.ASMClassTransformer
import ru.s5a4ed1sa7.core.asm.api.SpecialClassNode
import ru.s5a4ed1sa7.core.asm.api.SpecialInsnList

class RandomInitializerTransformer : ASMClassTransformer {
    override fun transformClass(
        name: String,
        transformedName: String,
        reader: ClassReader,
        classNode: SpecialClassNode
    ): ASMClassTransformer.TransformResult {
        var modified = false
        for (method in classNode.methods) {
            val iter = method.instructions.iterator()
            while (iter.hasNext()) {
                val node = iter.next()
                val netty = classNode.name == "io/netty/util/internal/ThreadLocalRandom"
                if (node.type == AbstractInsnNode.TYPE_INSN) {
                    val typeNode = node as TypeInsnNode
                    if (typeNode.opcode == NEW && typeNode.desc == "java/util/Random") {
                        iter.set(TypeInsnNode(NEW, if(netty) "it/unimi/dsi/util/SplitMix64Random" else "it/unimi/dsi/util/XoRoShiRo128PlusRandom"))
                        modified = modified or true
                    }
                } else if (node.type == AbstractInsnNode.METHOD_INSN) {
                    val methodNode = node as MethodInsnNode
                    if (methodNode.owner == "java/util/Random" && methodNode.name == "<init>" && (methodNode.desc == "()V" || methodNode.desc == "(J)V")) {
                        iter.set(MethodInsnNode(INVOKESPECIAL, if(netty) "it/unimi/dsi/util/SplitMix64Random" else "it/unimi/dsi/util/XoRoShiRo128PlusRandom", methodNode.name, methodNode.desc, false))
                        modified = modified or true
                    }
                } else if (classNode.superName == "java/util/Random") {
                    classNode.superName = if(netty) "it/unimi/dsi/util/SplitMix64Random" else "it/unimi/dsi/util/XoRoShiRo128PlusRandom"
                }
            }
        }
        return if (modified) ASMClassTransformer.TransformResult.MODIFIED else ASMClassTransformer.TransformResult.NOT_MODIFIED
    }
}