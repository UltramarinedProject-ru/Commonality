package ru.s5a4ed1sa7.core.asm.api

import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.VarInsnNode
import java.util.*

class FieldGetterBuilder(private val classNode: SpecialClassNode, private val name: String) {
    private var desc = ""
    private var isStatic = false
    private var getterMethodName = ""

    fun desc(desc: String): FieldGetterBuilder {
        this.desc = desc
        return this
    }

    fun setStatic(static: Boolean): FieldGetterBuilder {
        this.isStatic = static
        return this
    }

    fun getterMethodName(name: String): FieldGetterBuilder {
        this.getterMethodName = name
        return this
    }

    fun build() {
        val fieldNode = if(desc.isEmpty()) classNode.getField(name) else classNode.getField(name, desc)
        if(getterMethodName.isEmpty()) getterMethodName = "get" + name.substring(0, 1).uppercase(Locale.ROOT) + name.substring(1)
        val node = MethodNode(ACC_PUBLIC or if (isStatic) ACC_STATIC else 0, getterMethodName, "()" + fieldNode.desc, null, null)
        val list = node.instructions
        if (!isStatic) list.add(VarInsnNode(ALOAD, 0))
        list.add(FieldInsnNode(if (isStatic) GETSTATIC else GETFIELD, classNode.name, fieldNode.name, fieldNode.desc))
        list.add(InsnNode(ASMUtils.getReturnOpcodeFromType(Type.getType(fieldNode.desc))))
        classNode.methods.add(node)
    }
}