package ru.s5a4ed1sa7.core.asm

import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import ru.s5a4ed1sa7.core.asm.api.SpecialMethodNode

object ASMUtil {
    internal fun MethodNode.replace(target: AbstractInsnNode, replaceable: InsnList) {
        this.instructions.insertBefore(target, replaceable)
        this.instructions.remove(target)
    }

    internal fun MethodNode.insertStart(replaceable: InsnList) {
        this.instructions.insert(replaceable)
    }
}