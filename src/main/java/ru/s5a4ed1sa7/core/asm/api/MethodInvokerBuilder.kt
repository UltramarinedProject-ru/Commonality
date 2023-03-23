package ru.s5a4ed1sa7.core.asm.api

import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.VarInsnNode

class MethodInvokerBuilder(private val classNode: SpecialClassNode, private val name: String) {
    private var invokerName = ""
    private var desc: MethodDescription? = null
    private var isStatic = false
    private var isSpecial = false
    fun desc(desc: MethodDescription?): MethodInvokerBuilder {
        this.desc = desc
        return this
    }

    fun setStatic(aStatic: Boolean): MethodInvokerBuilder {
        isStatic = aStatic
        return this
    }

    fun setSpecial(special: Boolean): MethodInvokerBuilder {
        require(!(isStatic && special)) { "Method cannot be both and static and special" }
        isSpecial = special
        return this
    }

    fun invokerName(name: String): MethodInvokerBuilder {
        invokerName = name
        return this
    }

    fun build() {
        val methodNode: MethodNode = if (desc == null) classNode.getMethod(name) else classNode.getMethod(name, desc!!)
        if (invokerName.isEmpty()) invokerName = "call" + name.substring(0, 1).uppercase() + name.substring(1)
        if (desc == null) desc = MethodDescription(methodNode)
        val generated = MethodNode(Opcodes.ACC_PUBLIC or if (isStatic) Opcodes.ACC_STATIC else 0, invokerName, methodNode.desc, methodNode.signature, methodNode.exceptions.toTypedArray())
        val list = generated.instructions
        if (!isStatic) list.add(VarInsnNode(Opcodes.ALOAD, 0))
        for (i in 0 until desc!!.argumentsSize) {
            list.add(VarInsnNode(ASMUtils.getLoadOpcodeFromType(desc!!.getArgument(i)), if (isStatic) i else i + 1))
        }
        list.add(MethodInsnNode(if (isStatic) Opcodes.INVOKESTATIC else if (isSpecial) Opcodes.INVOKESPECIAL else Opcodes.INVOKEVIRTUAL, classNode.name, methodNode.name, methodNode.desc, false))
        list.add(InsnNode(ASMUtils.getReturnOpcodeFromType(desc!!.returnType)))
        classNode.methods.add(generated)
    }
}
