package ru.s5a4ed1sa7.commonality.transformer

import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes.INVOKESPECIAL
import org.objectweb.asm.Opcodes.INVOKESTATIC
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import ru.s5a4ed1sa7.core.asm.api.ASMClassTransformer
import ru.s5a4ed1sa7.core.asm.api.SpecialClassNode

class CheckGLErrorTransformer : ASMClassTransformer {
    override fun transformClass(
        name: String,
        transformedName: String,
        reader: ClassReader,
        classNode: SpecialClassNode
    ): ASMClassTransformer.TransformResult {
        var modified = false

        val toRemove = mutableListOf<MethodNode>()
        for (method in classNode.methods) {
            val iter = method.instructions.iterator()
            while (iter.hasNext()) {
                val node = iter.next()
                if(node.type == AbstractInsnNode.METHOD_INSN) {
                    val methodNode = node as MethodInsnNode
                    if ((methodNode.opcode == INVOKESPECIAL || methodNode.opcode == INVOKESTATIC) && methodNode.name == "checkGLError" && methodNode.desc == "(Ljava/lang/String;)V") {
                        repeat(2) { method.instructions.remove(methodNode.previous) }
                        iter.remove()
                        modified = true
                    }
                }
            }

            if (method.name == "checkGLError" && method.desc == "(Ljava/lang/String;)V") toRemove.add(method)
        }

        classNode.methods.removeAll(toRemove)

        return onModified(modified)
    }
}