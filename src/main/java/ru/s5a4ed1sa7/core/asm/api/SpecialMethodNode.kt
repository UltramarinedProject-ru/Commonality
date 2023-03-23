package ru.s5a4ed1sa7.core.asm.api

import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import java.util.*
import java.util.stream.Collectors

class SpecialMethodNode(access: Int, name: String?, desc: String?, signature: String?, exceptions: Array<String?>?) : MethodNode(Opcodes.ASM5, access, name, desc, signature, exceptions) {
    fun findAllOpcodes(opcode: Int): List<AbstractInsnNode> {
        return Arrays.stream(instructions.toArray())
                .filter { node: AbstractInsnNode -> node.opcode == opcode }
                .map { node: AbstractInsnNode -> node }
                .collect(Collectors.toList())
    }

    fun findAllTypes(type: Int): List<AbstractInsnNode> {
        return Arrays.stream(instructions.toArray())
                .filter { node: AbstractInsnNode -> node.type == type }
                .map { node: AbstractInsnNode -> node }
                .collect(Collectors.toList())
    }

    fun findFirstOpcode(opcode: Int): Optional<AbstractInsnNode> {
        return Arrays.stream(instructions.toArray())
                .filter { node: AbstractInsnNode -> node.opcode == opcode }
                .map { node: AbstractInsnNode -> node }
                .findFirst()
    }

    fun findLastOpcode(opcode: Int): Optional<AbstractInsnNode> {
        val list = instructions
        for (i in list.size() - 1 downTo 1) {
            val node = list[i]
            if (node.opcode == opcode) return Optional.of(node)
        }
        return Optional.empty()
    }

    fun findOpcode(opcode: Int, times: Int): Optional<AbstractInsnNode> {
        val list = instructions
        val size = list.size()
        var temp = 0
        for (i in 0 until size) {
            val node = list[i]
            if (node.opcode == opcode && ++temp == times) return Optional.of(node)
        }
        return Optional.empty()
    }

    fun findOpcodeReverse(opcode: Int, times: Int): Optional<Any> {
        val list = instructions
        var temp = 0
        for (i in list.size() downTo 1) {
            val node = list[i]
            if (node.opcode == opcode && ++temp == times) return Optional.of(node)
        }
        return Optional.empty()
    }

    fun findSequence(skipNons: Boolean, vararg opSequence: Int): Optional<AbstractInsnNode> {
        val instructions = instructions
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
        return if (found) Optional.of(insertionPoint!!) else Optional.empty()
    }

    fun findSequenceLast(skipNons: Boolean, vararg opSequence: Int): Optional<AbstractInsnNode> {
        val instructions = instructions
        var seqIndex = 0
        var insertionPoint: AbstractInsnNode? = null
        for (index in 0 until instructions.size()) {
            val ins = instructions[index]
            if (skipNons && ins.opcode == -1) {
                continue
            }
            if (ins.opcode == opSequence[seqIndex]) {
                if (++seqIndex == opSequence.size) {
                    insertionPoint = ins
                    break
                }
            } else if (ins.opcode == opSequence[0]) {
                seqIndex = 1
            } else {
                seqIndex = 0
            }
        }
        return Optional.of(insertionPoint!!)
    }

    @JvmOverloads
    fun findMethodCall(opcode: Int, owner: String, name: String, desc: String, ordinal: Int = 1): Optional<MethodInsnNode> {
        val list = instructions
        val size = list.size()
        var temp = 0
        for (i in 0 until size) {
            val node = list[i]
            if (node.opcode == opcode && node.type == AbstractInsnNode.METHOD_INSN) {
                val methodNode = node as MethodInsnNode
                if (owner == methodNode.owner && name == methodNode.name && desc == methodNode.desc && ++temp == ordinal) {
                    return Optional.of(methodNode)
                }
            }
        }
        return Optional.empty()
    }

    @JvmOverloads
    fun findFieldCall(opcode: Int, owner: String, name: String, desc: String, ordinal: Int = 1): Optional<FieldInsnNode> {
        val list = instructions
        val size = list.size()
        var temp = 0
        for (i in 0 until size) {
            val node = list[i]
            if (node.opcode == opcode && node.type == AbstractInsnNode.FIELD_INSN) {
                val fieldNode = node as FieldInsnNode
                if (owner == fieldNode.owner && name == fieldNode.name && desc == fieldNode.desc && ++temp == ordinal) {
                    return Optional.of(fieldNode)
                }
            }
        }
        return Optional.empty()
    }

    fun removeOpcode(opcode: Int, position: Int) {
        findOpcode(opcode, position).ifPresent {
            val insnNode = SpecialInsnList()
            insnNode.insert(it, insnNode)
            insnNode.remove(it)
        }
    }

    fun remove(abstractInsnNode: AbstractInsnNode) {
        val insnList = SpecialInsnList()
        insnList.insert(abstractInsnNode, insnList)
        insnList.remove(abstractInsnNode)
    }
}