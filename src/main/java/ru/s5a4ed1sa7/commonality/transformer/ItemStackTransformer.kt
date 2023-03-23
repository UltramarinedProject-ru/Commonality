package ru.s5a4ed1sa7.commonality.transformer

import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes.GETFIELD
import ru.s5a4ed1sa7.core.asm.ASMCoreMod.Companion.isObfEnv
import ru.s5a4ed1sa7.core.asm.api.ASMClassTransformer
import ru.s5a4ed1sa7.core.asm.api.SpecialClassNode
import ru.s5a4ed1sa7.core.asm.api.SpecialInsnList

class ItemStackTransformer : ASMClassTransformer {
    override fun transformClass(
        name: String,
        transformedName: String,
        reader: ClassReader,
        classNode: SpecialClassNode
    ): ASMClassTransformer.TransformResult {
        var modified = false
        val writeToNBT = classNode.getMethodObf("writeToNBT", "func_77955_b")
        writeToNBT.findFieldCall(GETFIELD, "net/minecraft/item/ItemStack", "stackTagCompound", "Lnet/minecraft/nbt/NBTTagCompound;", 2).ifPresent {
            val list = SpecialInsnList().invokeVirtual("net/minecraft/nbt/NBTTagCompound", if(isObfEnv) "func_74737_b" else "copy", "()Lnet/minecraft/nbt/NBTBase;")
            writeToNBT.instructions.insert(it, list)
            modified = true
        }
        return onModified(modified)
    }
}