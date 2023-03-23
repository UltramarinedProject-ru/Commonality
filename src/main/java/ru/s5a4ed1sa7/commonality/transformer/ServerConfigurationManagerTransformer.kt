package ru.s5a4ed1sa7.commonality.transformer

import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes.INVOKESTATIC
import org.objectweb.asm.Opcodes.INVOKEVIRTUAL
import ru.s5a4ed1sa7.core.asm.ASMCoreMod.Companion.isObfEnv
import ru.s5a4ed1sa7.core.asm.ASMUtil.replace
import ru.s5a4ed1sa7.core.asm.api.ASMClassTransformer
import ru.s5a4ed1sa7.core.asm.api.SpecialClassNode
import ru.s5a4ed1sa7.core.asm.api.SpecialInsnList

class ServerConfigurationManagerTransformer : ASMClassTransformer {
    override fun transformClass(
        name: String,
        transformedName: String,
        reader: ClassReader,
        classNode: SpecialClassNode
    ): ASMClassTransformer.TransformResult {
        var modified = false

        val createPlayerForUser = classNode.getMethodObf("createPlayerForUser", "func_148545_a")
        createPlayerForUser.findMethodCall(INVOKESTATIC, "net/minecraft/entity/player/EntityPlayer", "func_146094_a", "(Lcom/mojang/authlib/GameProfile;)Ljava/util/UUID;").ifPresent {
            val list = SpecialInsnList().aLoad(1).invokeVirtual("com/mojang/authlib/GameProfile", "getId", "()Ljava/util/UUID;")
            createPlayerForUser.replace(it, list)
            modified = modified or true
        }
        createPlayerForUser.findMethodCall(INVOKEVIRTUAL, "net/minecraft/entity/player/EntityPlayerMP", if (isObfEnv) "func_80006_f" else "getUniqueID", "()Ljava/util/UUID;").ifPresent {
            val list = SpecialInsnList().invokeVirtual("net/minecraft/entity/player/EntityPlayerMP",  if (isObfEnv) "func_146103_bH" else "getGameProfile", "()Lcom/mojang/authlib/GameProfile;").invokeVirtual("com/mojang/authlib/GameProfile", "getId", "()Ljava/util/UUID;")
            createPlayerForUser.replace(it, list)
            modified = modified or true
        }

        return onModified(modified)
    }
}