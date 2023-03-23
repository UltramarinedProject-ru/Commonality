package ru.s5a4ed1sa7.core.asm.api

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*
import org.objectweb.asm.util.TraceClassVisitor
import java.io.PrintWriter
import java.util.*

object ASMUtils {
    fun findFirstOpCode(instructions: InsnList, opcode: Int): AbstractInsnNode? {
        for (index in 0 until instructions.size()) {
            if (instructions[index].opcode == opcode) {
                return instructions[index]
            }
        }
        return null
    }

    fun findLastOpCode(instructions: InsnList, opcode: Int): AbstractInsnNode? {
        for (index in instructions.size() - 1 downTo 1) {
            if (instructions[index].opcode == opcode) {
                return instructions[index]
            }
        }
        return null
    }

    fun findLastType(instructions: InsnList, type: Int, skip: Int): AbstractInsnNode? {
        var skip = skip
        for (index in instructions.size() - 1 downTo 1) {
            if (instructions[index].type == type) {
                if (--skip < 0) {
                    return instructions[index]
                }
            }
        }
        return null
    }

    fun findNextOpCode(fromInstruction: AbstractInsnNode, opcode: Int): AbstractInsnNode? {
        var nextInsn = fromInstruction
        do {
            if (nextInsn.opcode == opcode) {
                return nextInsn
            }
        } while (nextInsn.next.also { nextInsn = it } != null)
        return null
    }

    fun findSequenceLast(instructions: InsnList, skipNons: Boolean, vararg opSequence: Int): AbstractInsnNode? {
        var seqIndex = 0
        var insertionPoint: AbstractInsnNode? = null
        for (index in 0 until instructions.size()) {
            // Get the instruction
            val ins = instructions[index]
            if (skipNons && ins.opcode == -1) {
                continue
            }

            // Does it match the sequence?
            if (ins.opcode == opSequence[seqIndex]) {
                // Has the full sequence been found?
                if (++seqIndex == opSequence.size) {
                    // Found the full sequence
                    insertionPoint = ins
                    break
                }
            } else if (ins.opcode == opSequence[0]) {
                // Restart sequence
                seqIndex = 1
            } else {
                // Reset sequence
                seqIndex = 0
            }
        }
        return insertionPoint
    }

    fun findSequence(instructions: InsnList, skipNons: Boolean, vararg opSequence: Int): AbstractInsnNode? {
        var seqIndex = 0
        var insertionPoint: AbstractInsnNode? = null
        var found = false
        for (index in 0 until instructions.size()) {
            val ins = instructions[index]
            if (skipNons && ins.opcode == -1) {
                continue
            }
            if (ins.opcode == opSequence[seqIndex]) {
                if (seqIndex == 0) {
                    insertionPoint = ins
                }
                if (++seqIndex == opSequence.size) {
                    found = true
                    break
                }
            } else if (ins.opcode == opSequence[0]) {
                insertionPoint = ins
                seqIndex = 1
            } else {
                seqIndex = 0
            }
        }
        return if (found) insertionPoint else null
    }

    @JvmOverloads
    fun readClass(basicClass: ByteArray?, flags: Int = 0): ClassNode {
        val reader = ClassReader(basicClass)
        val node = ClassNode()
        reader.accept(node, flags)
        return node
    }

    fun findMethod(classNode: ClassNode, vararg names: String?): MethodNode? {
        val list = listOf(*names)
        var methodNode: MethodNode? = null
        for (node in classNode.methods) {
            if (list.contains(node.name)) {
                methodNode = node
                break
            }
        }
        return methodNode
    }

    @JvmOverloads
    fun writeClass(node: ClassNode, flags: Int = ClassWriter.COMPUTE_FRAMES or ClassWriter.COMPUTE_MAXS): ByteArray {
        val writer = ComputeFramesClassWriter()
        node.accept(writer)
        return writer.toByteArray()
    }

    fun findMethodDesc(classNode: ClassNode, name: String, desc: String): MethodNode? {
        var methodNode: MethodNode? = null
        for (node in classNode.methods) {
            if (name == node.name && desc == node.desc) {
                methodNode = node
                break
            }
        }
        return methodNode
    }

    @JvmOverloads
    fun log(classNode: ClassNode, writer: PrintWriter? = PrintWriter(System.out)) {
        val visitor = TraceClassVisitor(writer)
        classNode.accept(visitor)
    }

    fun findAllOpcodes(list: InsnList, opcode: Int): List<AbstractInsnNode> {
        val nodes: MutableList<AbstractInsnNode> = ArrayList()
        for (node in list.toArray()) {
            if (node.opcode == opcode) nodes.add(node)
        }
        return nodes
    }

    fun generateGetter(name: String?, desc: String, opcode: Int, owner: String?, fieldName: String?, retOpcode: Int): MethodNode {
        val node = MethodNode(Opcodes.ASM5, Opcodes.ACC_PUBLIC, name, "()$desc", null, null)
        val list = node.instructions
        list.add(VarInsnNode(Opcodes.ALOAD, 0))
        list.add(FieldInsnNode(opcode, owner, fieldName, desc))
        list.add(InsnNode(retOpcode))
        return node
    }

    fun copy(list: InsnList): InsnList {
        val node = MethodNode()
        list.accept(node)
        return node.instructions
    }

    fun getReturnOpcodeFromType(type: Type): Int {
        return when (type.sort) {
            Type.INT, Type.BYTE, Type.CHAR, Type.SHORT, Type.BOOLEAN -> Opcodes.IRETURN
            Type.VOID -> Opcodes.RETURN
            Type.FLOAT -> Opcodes.FRETURN
            Type.LONG -> Opcodes.LRETURN
            Type.DOUBLE -> Opcodes.DRETURN
            else -> Opcodes.ARETURN
        }
    }

    fun getLoadOpcodeFromType(type: Type): Int {
        return when (type.sort) {
            Type.INT, Type.BYTE, Type.CHAR, Type.SHORT, Type.BOOLEAN -> Opcodes.ILOAD
            Type.FLOAT -> Opcodes.FLOAD
            Type.LONG -> Opcodes.LLOAD
            Type.DOUBLE -> Opcodes.DLOAD
            else -> Opcodes.ALOAD
        }
    }

    fun getStoreOpcodeFromType(type: Type): Int {
        return when (type.sort) {
            Type.INT, Type.BYTE, Type.CHAR, Type.SHORT, Type.BOOLEAN -> Opcodes.ISTORE
            Type.FLOAT -> Opcodes.FSTORE
            Type.LONG -> Opcodes.LSTORE
            Type.DOUBLE -> Opcodes.DSTORE
            else -> Opcodes.ASTORE
        }
    }
}
