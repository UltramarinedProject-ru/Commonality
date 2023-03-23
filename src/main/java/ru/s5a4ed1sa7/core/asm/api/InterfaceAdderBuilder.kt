package ru.s5a4ed1sa7.core.asm.api

import net.minecraft.launchwrapper.Launch
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import java.io.IOException
import java.util.*

class InterfaceAdderBuilder(private val classNode: SpecialClassNode, private val className: String) {
    private var getterPrefix = "get"
    private var setterPrefix = "set"
    private var invokePrefix = "invoke"

    fun getter(prefix: String): InterfaceAdderBuilder {
        getterPrefix = prefix
        return this
    }

    fun setter(prefix: String): InterfaceAdderBuilder {
        setterPrefix = prefix
        return this
    }

    operator fun invoke(prefix: String): InterfaceAdderBuilder {
        invokePrefix = prefix
        return this
    }

    fun build() {
        val bytes: ByteArray? = try {
            Launch.classLoader.getClassBytes(className)
        } catch (e: IOException) {
            throw IllegalArgumentException("Class $className didn't found")
        }
        requireNotNull(bytes) { "Class $className didn't found" }
        val classNode = ClassNode()
        val classReader = ClassReader(bytes)
        classReader.accept(classNode, 0)
        require(classNode.access and Opcodes.ACC_INTERFACE != 0) { "Given class isn't interface" }
        for (node in classNode.methods) {
            val methodName = node.name
            if (methodName.startsWith(getterPrefix)) {
                processGetter(node)
            } else if (methodName.startsWith(setterPrefix)) {
                processSetter(node)
            } else if (methodName.startsWith(invokePrefix)) {
                processInvoke(node)
            }
        }
        this.classNode.interfaces.add(className.replace('.', '/'))
    }

    private fun processGetter(node: MethodNode) {
        var fieldName = node.name.substring(getterPrefix.length)
        fieldName = fieldName.substring(0, 1).lowercase(Locale.ROOT) + fieldName.substring(1)
        val args = Type.getArgumentTypes(node.desc)
        val returnType = Type.getReturnType(node.desc)
        require(args.isEmpty()) { "Method " + node.name + " mustn't has arguments" }
        classNode.generateGetterForField(fieldName)
                .desc(returnType.descriptor)
                .getterMethodName(node.name)
                .setStatic(node.access and Opcodes.ACC_STATIC != 0)
                .build()
    }

    private fun processSetter(node: MethodNode) {
        var fieldName = node.name.substring(getterPrefix.length)
        fieldName = fieldName.substring(0, 1).lowercase(Locale.ROOT) + fieldName.substring(1)
        val args = Type.getArgumentTypes(node.desc)
        val returnType = Type.getReturnType(node.desc)
        require(!(returnType !== Type.VOID_TYPE || args.size != 1)) { "Method " + node.name + " must return void" }
        classNode.generateSetterForField(fieldName)
                .desc(args[0].descriptor)
                .setterMethodName(node.name)
                .setStatic(node.access and Opcodes.ACC_STATIC != 0)
                .build()
    }

    private fun processInvoke(node: MethodNode) {
        var methodName = node.name.substring(invokePrefix.length)
        methodName = methodName.substring(0, 1).lowercase(Locale.getDefault()) + methodName.substring(1)
        classNode.generateInvokerForMethod(methodName)
                .invokerName(node.name)
                .desc(MethodDescription(node))
                .setStatic(node.access and Opcodes.ACC_STATIC != 0)
                .build()
    }
}
