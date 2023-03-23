package ru.s5a4ed1sa7.core.asm.api

import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.VarInsnNode

class FieldSetterBuilder(private val classNode: SpecialClassNode, private val name: String) {
    private var desc = ""
    private var isStatic = false
    private var setterMethodName = ""
    fun desc(desc: String): FieldSetterBuilder {
        this.desc = desc
        return this
    }

    fun setStatic(aStatic: Boolean): FieldSetterBuilder {
        isStatic = aStatic
        return this
    }

    fun setterMethodName(name: String): FieldSetterBuilder {
        setterMethodName = name
        return this
    }

    fun build() {
        val fieldNode = if (desc.isEmpty()) classNode.getField(name) else classNode.getField(name, desc)
        if (setterMethodName.isEmpty()) setterMethodName = "set" + name.substring(0, 1).uppercase() + name.substring(1)
        val node = MethodNode(Opcodes.ACC_PUBLIC or if (isStatic) Opcodes.ACC_STATIC else 0, setterMethodName, String.format("(%s)V", fieldNode.desc), null, null)
        val list = node.instructions
        if (!isStatic) list.add(VarInsnNode(Opcodes.ALOAD, 0))
        list.add(VarInsnNode(ASMUtils.getLoadOpcodeFromType(Type.getType(fieldNode.desc)), if (isStatic) 0 else 1))
        list.add(FieldInsnNode(if (isStatic) Opcodes.PUTSTATIC else Opcodes.PUTFIELD, classNode.name, fieldNode.name, fieldNode.desc))
        list.add(InsnNode(Opcodes.RETURN))
        classNode.methods.add(node)
    }
}