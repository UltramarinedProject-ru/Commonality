package ru.s5a4ed1sa7.core.asm.api

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.util.TraceClassVisitor
import ru.s5a4ed1sa7.core.asm.ClassTransformerImpl.Companion.IS_DEV
import java.io.PrintWriter

class SpecialClassNode : ClassNode(Opcodes.ASM5) {
    fun generateGetterForField(name: String): FieldGetterBuilder = FieldGetterBuilder(this, name)
    fun generateSetterForField(name: String): FieldSetterBuilder = FieldSetterBuilder(this, name)
    fun generateInvokerForMethod(name: String): MethodInvokerBuilder = MethodInvokerBuilder(this, name)
    fun addInterface(className: String): InterfaceAdderBuilder = InterfaceAdderBuilder(this, className)

    fun getField(name: String): FieldNode {
        for (node in fields) {
            if (node.name == name) {
                return node
            }
        }
        throw IllegalArgumentException("Field not found")
    }
    fun getFieldObf(name: String, obfName: String): FieldNode = this.getField(if (IS_DEV) name else obfName)
    fun getField(name: String, desc: String): FieldNode {
        for (node in fields) {
            if (node.name == name && node.desc == desc) {
                return node
            }
        }
        throw IllegalArgumentException("Field not found")
    }
    fun getFieldObf(name: String, obfName: String, desc: String): FieldNode = this.getField(if (IS_DEV) name else obfName, desc)
    fun getMethod(name: String): SpecialMethodNode {
        for (node in methods) {
            if (node.name == name) {
                return node as SpecialMethodNode
            }
        }
        throw IllegalArgumentException("Method with name $name not found")
    }
    fun getMethodObf(name: String, obfName: String): SpecialMethodNode = this.getMethod(if (IS_DEV) name else obfName)
    fun getMethod(name: String, desc: String): SpecialMethodNode {
        for (node in methods) {
            if (node.name == name && node.desc == desc) {
                return node as SpecialMethodNode
            }
        }
        throw IllegalArgumentException("Method with name $name not found")
    }

    fun getMethodObf(name: String, obfName: String, desc: String): SpecialMethodNode = this.getMethod(if (IS_DEV) name else obfName, desc)
    fun getMethod(name: String, desc: MethodDescription): SpecialMethodNode = this.getMethod(name, desc.build())
    fun getMethodObf(name: String, obfName: String, desc: MethodDescription): SpecialMethodNode = this.getMethodObf(name, obfName, desc.build())

    @JvmOverloads
    fun trace(writer: PrintWriter? = PrintWriter(System.out)) {
        val visitor = TraceClassVisitor(writer)
        accept(visitor)
    }

    override fun visitMethod(access: Int, name: String?, desc: String?, signature: String?, exceptions: Array<String?>?): MethodVisitor? {
        val specialMethodNode = SpecialMethodNode(access, name, desc, signature, exceptions)
        methods.add(specialMethodNode)
        return specialMethodNode
    }
}