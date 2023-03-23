package ru.s5a4ed1sa7.core.asm.api

import org.objectweb.asm.Handle
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*
import java.util.*

class SpecialInsnList : InsnList(), Opcodes {
    private val jumpDepth: Deque<LabelNode> = ArrayDeque()
    fun addNode(node: AbstractInsnNode?): SpecialInsnList {
        this.add(node)
        return this
    }

    private fun `var`(opcode: Int, `var`: Int): SpecialInsnList {
        return addNode(VarInsnNode(opcode, `var`))
    }

    fun iLoad(`var`: Int): SpecialInsnList {
        return `var`(Opcodes.ILOAD, `var`)
    }

    fun lLoad(`var`: Int): SpecialInsnList {
        return `var`(Opcodes.LLOAD, `var`)
    }

    fun fLoad(`var`: Int): SpecialInsnList {
        return `var`(Opcodes.FLOAD, `var`)
    }

    fun dLoad(`var`: Int): SpecialInsnList {
        return `var`(Opcodes.DLOAD, `var`)
    }

    fun aLoad(`var`: Int): SpecialInsnList {
        return `var`(Opcodes.ALOAD, `var`)
    }

    fun iStore(`var`: Int): SpecialInsnList {
        return `var`(Opcodes.ISTORE, `var`)
    }

    fun lStore(`var`: Int): SpecialInsnList {
        return `var`(Opcodes.LSTORE, `var`)
    }

    fun fStore(`var`: Int): SpecialInsnList {
        return `var`(Opcodes.FSTORE, `var`)
    }

    fun dStore(`var`: Int): SpecialInsnList {
        return `var`(Opcodes.DSTORE, `var`)
    }

    fun aStore(`var`: Int): SpecialInsnList {
        return `var`(Opcodes.ASTORE, `var`)
    }

    fun type(opcode: Int, desc: String?): SpecialInsnList {
        return addNode(TypeInsnNode(opcode, desc))
    }

    fun newNode(className: String?): SpecialInsnList {
        return type(Opcodes.NEW, className)
    }

    fun instanceofNode(className: String?): SpecialInsnList {
        return type(Opcodes.INSTANCEOF, className)
    }

    fun checkCast(className: String?): SpecialInsnList {
        return type(Opcodes.CHECKCAST, className)
    }

    fun newAArray(className: String?): SpecialInsnList {
        return type(Opcodes.ANEWARRAY, className)
    }

    fun tableSwitch(min: Int, max: Int, dflt: LabelNode?, vararg labels: LabelNode?): SpecialInsnList {
        return addNode(TableSwitchInsnNode(min, max, dflt, *labels))
    }

    fun multiANewArray(className: String?, dims: Int): SpecialInsnList {
        return addNode(MultiANewArrayInsnNode(className, dims))
    }

    @JvmOverloads
    fun method(opcode: Int, owner: String?, name: String?, desc: String?, itf: Boolean = opcode == Opcodes.INVOKEINTERFACE): SpecialInsnList {
        return addNode(MethodInsnNode(opcode, owner, name, desc, itf))
    }

    fun invokeVirtual(owner: String?, name: String?, desc: String?): SpecialInsnList {
        return method(Opcodes.INVOKEVIRTUAL, owner, name, desc, false)
    }

    fun invokeSpecial(owner: String?, name: String?, desc: String?): SpecialInsnList {
        return method(Opcodes.INVOKESPECIAL, owner, name, desc, false)
    }

    fun invokeStatic(owner: String?, name: String?, desc: String?): SpecialInsnList {
        return method(Opcodes.INVOKESTATIC, owner, name, desc, false)
    }

    fun invokeInterface(owner: String?, name: String?, desc: String?): SpecialInsnList {
        return method(Opcodes.INVOKEINTERFACE, owner, name, desc, true)
    }

    fun lookupSwitch(dflt: LabelNode?, keys: IntArray?, labels: Array<LabelNode?>?): SpecialInsnList {
        return addNode(LookupSwitchInsnNode(dflt, keys, labels))
    }

    fun lineNumber(line: Int, start: LabelNode?): SpecialInsnList {
        return addNode(LineNumberNode(line, start))
    }

    fun ldc(cst: Any?): SpecialInsnList {
        return addNode(LdcInsnNode(cst))
    }

    fun label(node: LabelNode?): SpecialInsnList {
        return addNode(node)
    }

    fun invokeDynamic(name: String?, desc: String?, bsm: Handle?, vararg bsmArgs: Any?): SpecialInsnList {
        return addNode(InvokeDynamicInsnNode(name, desc, bsm, *bsmArgs))
    }

    fun intNode(opcode: Int, operand: Int): SpecialInsnList {
        return addNode(IntInsnNode(opcode, operand))
    }

    fun biPush(number: Byte): SpecialInsnList {
        return intNode(Opcodes.BIPUSH, number.toInt())
    }

    fun siPush(number: Short): SpecialInsnList {
        return intNode(Opcodes.SIPUSH, number.toInt())
    }

    fun primitiveArray(type: Int, length: Int): SpecialInsnList {
        return pushInt(length).intNode(Opcodes.NEWARRAY, type)
    }

    fun pushInt(i: Int): SpecialInsnList {
        if (i.toByte().toInt() == i) return biPush(i.toByte())
        return if (i.toShort().toInt() == i) siPush(i.toShort()) else ldc(i)
    }

    fun node(opcode: Int): SpecialInsnList {
        return addNode(InsnNode(opcode))
    }

    fun nop(): SpecialInsnList {
        return node(Opcodes.NOP)
    }

    fun pushNull(): SpecialInsnList {
        return node(Opcodes.ACONST_NULL)
    }

    fun pushM1(): SpecialInsnList {
        return node(Opcodes.ICONST_M1)
    }

    fun push0(): SpecialInsnList {
        return node(Opcodes.ICONST_0)
    }

    fun push1(): SpecialInsnList {
        return node(Opcodes.ICONST_1)
    }

    fun push2(): SpecialInsnList {
        return node(Opcodes.ICONST_2)
    }

    fun push3(): SpecialInsnList {
        return node(Opcodes.ICONST_3)
    }

    fun push4(): SpecialInsnList {
        return node(Opcodes.ICONST_4)
    }

    fun push5(): SpecialInsnList {
        return node(Opcodes.ICONST_5)
    }

    fun pushL0(): SpecialInsnList {
        return node(Opcodes.LCONST_0)
    }

    fun pushL1(): SpecialInsnList {
        return node(Opcodes.LCONST_1)
    }

    fun pushF0(): SpecialInsnList {
        return node(Opcodes.FCONST_0)
    }

    fun pushF1(): SpecialInsnList {
        return node(Opcodes.FCONST_1)
    }

    fun pushF2(): SpecialInsnList {
        return node(Opcodes.FCONST_2)
    }

    fun pushD0(): SpecialInsnList {
        return node(Opcodes.DCONST_0)
    }

    fun pushD1(): SpecialInsnList {
        return node(Opcodes.DCONST_1)
    }

    fun iALoad(): SpecialInsnList {
        return node(Opcodes.IALOAD)
    }

    fun lALoad(): SpecialInsnList {
        return node(Opcodes.LALOAD)
    }

    fun fALoad(): SpecialInsnList {
        return node(Opcodes.FALOAD)
    }

    fun dALoad(): SpecialInsnList {
        return node(Opcodes.DALOAD)
    }

    fun aALoad(): SpecialInsnList {
        return node(Opcodes.AALOAD)
    }

    fun bALoad(): SpecialInsnList {
        return node(Opcodes.BALOAD)
    }

    fun cALoad(): SpecialInsnList {
        return node(Opcodes.CALOAD)
    }

    fun sALoad(): SpecialInsnList {
        return node(Opcodes.SALOAD)
    }

    fun iAStore(): SpecialInsnList {
        return node(Opcodes.IASTORE)
    }

    fun lAStore(): SpecialInsnList {
        return node(Opcodes.LASTORE)
    }

    fun fAStore(): SpecialInsnList {
        return node(Opcodes.FASTORE)
    }

    fun dAStore(): SpecialInsnList {
        return node(Opcodes.DASTORE)
    }

    fun aAStore(): SpecialInsnList {
        return node(Opcodes.AASTORE)
    }

    fun bAStore(): SpecialInsnList {
        return node(Opcodes.BASTORE)
    }

    fun cAStore(): SpecialInsnList {
        return node(Opcodes.CASTORE)
    }

    fun sAStore(): SpecialInsnList {
        return node(Opcodes.SASTORE)
    }

    fun pop(): SpecialInsnList {
        return node(Opcodes.POP)
    }

    fun pop2(): SpecialInsnList {
        return node(Opcodes.POP2)
    }

    fun dup(): SpecialInsnList {
        return node(Opcodes.DUP)
    }

    fun dup_x1(): SpecialInsnList {
        return node(Opcodes.DUP_X1)
    }

    fun dup_x2(): SpecialInsnList {
        return node(Opcodes.DUP_X2)
    }

    fun dup2(): SpecialInsnList {
        return node(Opcodes.DUP2)
    }

    fun dup2_x1(): SpecialInsnList {
        return node(Opcodes.DUP2_X1)
    }

    fun dup2_x2(): SpecialInsnList {
        return node(Opcodes.DUP2_X2)
    }

    fun swap(): SpecialInsnList {
        return node(Opcodes.SWAP)
    }

    fun iAdd(): SpecialInsnList {
        return node(Opcodes.IADD)
    }

    fun lAdd(): SpecialInsnList {
        return node(Opcodes.LADD)
    }

    fun fAdd(): SpecialInsnList {
        return node(Opcodes.FADD)
    }

    fun dAdd(): SpecialInsnList {
        return node(Opcodes.DADD)
    }

    fun iSub(): SpecialInsnList {
        return node(Opcodes.ISUB)
    }

    fun lSub(): SpecialInsnList {
        return node(Opcodes.LSUB)
    }

    fun fSub(): SpecialInsnList {
        return node(Opcodes.FSUB)
    }

    fun dSub(): SpecialInsnList {
        return node(Opcodes.DSUB)
    }

    fun iMul(): SpecialInsnList {
        return node(Opcodes.IMUL)
    }

    fun lMul(): SpecialInsnList {
        return node(Opcodes.LMUL)
    }

    fun fMul(): SpecialInsnList {
        return node(Opcodes.FMUL)
    }

    fun dMul(): SpecialInsnList {
        return node(Opcodes.DMUL)
    }

    fun iDiv(): SpecialInsnList {
        return node(Opcodes.IDIV)
    }

    fun lDiv(): SpecialInsnList {
        return node(Opcodes.LDIV)
    }

    fun fDiv(): SpecialInsnList {
        return node(Opcodes.FDIV)
    }

    fun dDiv(): SpecialInsnList {
        return node(Opcodes.DDIV)
    }

    fun iRem(): SpecialInsnList {
        return node(Opcodes.IREM)
    }

    fun lRem(): SpecialInsnList {
        return node(Opcodes.LREM)
    }

    fun fRem(): SpecialInsnList {
        return node(Opcodes.FREM)
    }

    fun dRem(): SpecialInsnList {
        return node(Opcodes.DREM)
    }

    fun iNeg(): SpecialInsnList {
        return node(Opcodes.INEG)
    }

    fun lNeg(): SpecialInsnList {
        return node(Opcodes.LNEG)
    }

    fun fNeg(): SpecialInsnList {
        return node(Opcodes.FNEG)
    }

    fun dNeg(): SpecialInsnList {
        return node(Opcodes.DNEG)
    }

    fun iShl(): SpecialInsnList {
        return node(Opcodes.ISHL)
    }

    fun lShl(): SpecialInsnList {
        return node(Opcodes.LSHL)
    }

    fun iShr(): SpecialInsnList {
        return node(Opcodes.ISHR)
    }

    fun lShr(): SpecialInsnList {
        return node(Opcodes.LSHR)
    }

    fun iuShr(): SpecialInsnList {
        return node(Opcodes.IUSHR)
    }

    fun luShr(): SpecialInsnList {
        return node(Opcodes.LUSHR)
    }

    fun iAnd(): SpecialInsnList {
        return node(Opcodes.IAND)
    }

    fun lAnd(): SpecialInsnList {
        return node(Opcodes.LAND)
    }

    fun iOr(): SpecialInsnList {
        return node(Opcodes.IOR)
    }

    fun lOr(): SpecialInsnList {
        return node(Opcodes.LOR)
    }

    fun iXor(): SpecialInsnList {
        return node(Opcodes.IXOR)
    }

    fun lXor(): SpecialInsnList {
        return node(Opcodes.LXOR)
    }

    fun i2l(): SpecialInsnList {
        return node(Opcodes.I2L)
    }

    fun i2f(): SpecialInsnList {
        return node(Opcodes.I2F)
    }

    fun i2d(): SpecialInsnList {
        return node(Opcodes.I2D)
    }

    fun i2b(): SpecialInsnList {
        return node(Opcodes.I2B)
    }

    fun i2s(): SpecialInsnList {
        return node(Opcodes.I2S)
    }

    fun i2c(): SpecialInsnList {
        return node(Opcodes.I2C)
    }

    fun l2i(): SpecialInsnList {
        return node(Opcodes.L2I)
    }

    fun l2f(): SpecialInsnList {
        return node(Opcodes.L2F)
    }

    fun l2d(): SpecialInsnList {
        return node(Opcodes.L2D)
    }

    fun d2l(): SpecialInsnList {
        return node(Opcodes.D2L)
    }

    fun d2f(): SpecialInsnList {
        return node(Opcodes.D2F)
    }

    fun d2i(): SpecialInsnList {
        return node(Opcodes.D2I)
    }

    fun f2l(): SpecialInsnList {
        return node(Opcodes.F2L)
    }

    fun f2i(): SpecialInsnList {
        return node(Opcodes.F2I)
    }

    fun f2d(): SpecialInsnList {
        return node(Opcodes.F2D)
    }

    fun lcmp(): SpecialInsnList {
        return node(Opcodes.LCMP)
    }

    fun fcmpl(): SpecialInsnList {
        return node(Opcodes.FCMPL)
    }

    fun fcmpg(): SpecialInsnList {
        return node(Opcodes.FCMPG)
    }

    fun dcmpl(): SpecialInsnList {
        return node(Opcodes.DCMPL)
    }

    fun dcmpg(): SpecialInsnList {
        return node(Opcodes.DCMPG)
    }

    fun vReturn(): SpecialInsnList {
        return node(Opcodes.RETURN)
    }

    fun iReturn(): SpecialInsnList {
        return node(Opcodes.IRETURN)
    }

    fun dReturn(): SpecialInsnList {
        return node(Opcodes.DRETURN)
    }

    fun fReturn(): SpecialInsnList {
        return node(Opcodes.FRETURN)
    }

    fun lReturn(): SpecialInsnList {
        return node(Opcodes.LRETURN)
    }

    fun aReturn(): SpecialInsnList {
        return node(Opcodes.ARETURN)
    }

    fun arrayLength(): SpecialInsnList {
        return node(Opcodes.ARRAYLENGTH)
    }

    fun aThrow(): SpecialInsnList {
        return node(Opcodes.ATHROW)
    }

    fun monitorEnter(): SpecialInsnList {
        return node(Opcodes.MONITORENTER)
    }

    fun monitorExit(): SpecialInsnList {
        return node(Opcodes.MONITOREXIT)
    }

    fun frameNode(type: Int, nLocal: Int, local: Array<Any?>?, nStack: Int, stack: Array<Any?>?): SpecialInsnList {
        return addNode(FrameNode(type, nLocal, local, nStack, stack))
    }

    fun fieldNode(opcode: Int, owner: String?, name: String?, desc: String?): SpecialInsnList {
        return addNode(FieldInsnNode(opcode, owner, name, desc))
    }

    fun putField(owner: String?, name: String?, desc: String?): SpecialInsnList {
        return fieldNode(Opcodes.PUTFIELD, owner, name, desc)
    }

    fun getField(owner: String?, name: String?, desc: String?): SpecialInsnList {
        return fieldNode(Opcodes.GETFIELD, owner, name, desc)
    }

    fun putStaticField(owner: String?, name: String?, desc: String?): SpecialInsnList {
        return fieldNode(Opcodes.PUTSTATIC, owner, name, desc)
    }

    fun getStaticField(owner: String?, name: String?, desc: String?): SpecialInsnList {
        return fieldNode(Opcodes.GETSTATIC, owner, name, desc)
    }

    fun jump(opcode: Int, node: LabelNode?): SpecialInsnList {
        return addNode(JumpInsnNode(opcode, node))
    }

    fun startIf(opcode: Int): SpecialInsnList {
        check(jumpReverseMap.containsKey(opcode)) { "Unrecognized opcode for jump insn" }
        val labelNode = LabelNode()
        jumpDepth.addLast(labelNode)
        return jump(jumpReverseMap[opcode]!!, labelNode)
    }

    fun startElse(): SpecialInsnList {
        val gotoNode = LabelNode()
        jump(Opcodes.GOTO, gotoNode)
        addNode(jumpDepth.removeLast())
        jumpDepth.addLast(gotoNode)
        return this
    }

    fun endIf(): SpecialInsnList {
        return addNode(jumpDepth.removeLast())
    }

    fun copy(): SpecialInsnList {
        val newList = SpecialInsnList()
        val methodNode = MethodNode()
        super.accept(methodNode)
        Arrays.stream(methodNode.instructions.toArray()).forEach { node: AbstractInsnNode? -> newList.addNode(node) }
        return newList
    }

    companion object {
        private val jumpReverseMap = HashMap<Int, Int>()

        init {
            jumpReverseMap[Opcodes.IF_ACMPEQ] = Opcodes.IF_ACMPNE
            jumpReverseMap[Opcodes.IF_ICMPEQ] = Opcodes.IF_ICMPNE
            jumpReverseMap[Opcodes.IF_ICMPGE] = Opcodes.IF_ICMPLT
            jumpReverseMap[Opcodes.IF_ICMPGT] = Opcodes.IF_ICMPLE
            jumpReverseMap[Opcodes.IFEQ] = Opcodes.IFNE
            jumpReverseMap[Opcodes.IFGE] = Opcodes.IFLT
            jumpReverseMap[Opcodes.IFLE] = Opcodes.IFGT
            jumpReverseMap[Opcodes.IFNULL] = Opcodes.IFNONNULL
            val temp = HashMap<Int, Int>()
            jumpReverseMap.forEach { (k: Int, v: Int) -> temp[v] = k }
            jumpReverseMap.putAll(temp)
            temp.clear()
        }
    }
}
