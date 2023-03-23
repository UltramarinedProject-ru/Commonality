package ru.s5a4ed1sa7.core.asm.api

import org.objectweb.asm.Type
import org.objectweb.asm.tree.MethodNode
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors

class MethodDescription {
    private val arguments: MutableList<Type>
    val returnType: Type

    constructor(returnType: Type, vararg arguments: Type?) {
        this.returnType = returnType
        this.arguments = ArrayList(listOf(*arguments))
    }

    constructor(returnType: String?, vararg arguments: String) {
        this.returnType = Type.getType(returnType)
        this.arguments = ArrayList(Arrays.stream(arguments).map { typeDescriptor: String? -> Type.getType(typeDescriptor) }.collect(Collectors.toList()))
    }

    constructor(methodNode: MethodNode) {
        returnType = Type.getReturnType(methodNode.desc)
        arguments = ArrayList(Arrays.stream(Type.getArgumentTypes(methodNode.desc)).collect(Collectors.toList()))
    }

    fun build(): String {
        val desc = StringBuilder("(")
        for (t in arguments) desc.append(t.descriptor)
        desc.append(")")
        desc.append(returnType.descriptor)
        return desc.toString()
    }

    val argumentsSize: Int get() = arguments.size

    fun getArgument(index: Int): Type = arguments[index]

    private fun addArgument(type: Type) {
        arguments.add(type)
    }

    private fun addArgument(type: String?) {
        addArgument(Type.getType(type))
    }

    fun addArguments(vararg types: Type) {
        Arrays.stream(types).forEach { type: Type -> this.addArgument(type) }
    }

    fun addArguments(vararg types: String) {
        Arrays.stream(types).forEach { type: String? -> this.addArgument(type) }
    }

    private fun addArguments(types: List<Type>) {
        types.forEach(Consumer { type: Type -> this.addArgument(type) })
    }

    fun addArguments(desc: MethodDescription) {
        addArguments(desc.arguments)
    }
}
