package ru.s5a4ed1sa7.commonality.transformer

import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes.INVOKESTATIC
import ru.s5a4ed1sa7.core.asm.ASMUtil.replace
import ru.s5a4ed1sa7.core.asm.api.ASMClassTransformer
import ru.s5a4ed1sa7.core.asm.api.SpecialClassNode
import ru.s5a4ed1sa7.core.asm.api.SpecialInsnList
import java.lang.RuntimeException

class EventSubscriptionTransformer : ASMClassTransformer {
    override fun transformClass(
        name: String,
        transformedName: String,
        reader: ClassReader,
        classNode: SpecialClassNode
    ): ASMClassTransformer.TransformResult {
        if (true) throw RuntimeException("Член")
        val buildEvents = classNode.getMethod("buildEvents")
        buildEvents.findMethodCall(INVOKESTATIC, "org/objectweb/asm/Type", "getType", "(Ljava.lang.String;)Lorg/objectweb/asm/Type;").ifPresent {
            val list = SpecialInsnList().invokeStatic("org/objectweb/asm/Type", "getObjectType", "(Ljava.lang.String;)Lorg/objectweb/asm/Type;")
            buildEvents.replace(it, list)
        }
        return ASMClassTransformer.TransformResult.MODIFIED
    }
}