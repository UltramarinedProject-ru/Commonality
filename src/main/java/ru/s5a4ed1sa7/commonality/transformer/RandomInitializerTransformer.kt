package ru.s5a4ed1sa7.commonality.transformer

import org.objectweb.asm.ClassReader
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
                if(node.type == AbstractInsnNode.METHOD_INSN) {
                    val methodNode = node as MethodInsnNode
                    if (methodNode.owner == "java/util/Random" && methodNode.name == "<init>" && methodNode.desc == "()V") {
                        val list = SpecialInsnList()
                            .addNode(TypeInsnNode(NEW, "ru/s5a4ed1sa7/commonality/util/ThermiteRandom"))
                            .dup()
                            .invokeSpecial("ru/s5a4ed1sa7/commonality/util/ThermiteRandom", methodNode.name, methodNode.desc)
                            .checkCast("java/util/Random")

                        //TODO Remove trash in bytecode
                        repeat(2) { method.instructions.remove(methodNode.previous) }

                        method.instructions.insertBefore(methodNode, list)
                        iter.remove()
                        modified = true
                    }
                }
            }
        }
        return if (modified) ASMClassTransformer.TransformResult.MODIFIED else ASMClassTransformer.TransformResult.NOT_MODIFIED
    }
}