package ru.s5a4ed1sa7.core.asm.api

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper
import cpw.mods.fml.common.patcher.ClassPatchManager
import net.minecraft.launchwrapper.LaunchClassLoader
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import ru.s5a4ed1sa7.core.asm.ClassTransformerImpl
import java.io.IOException

class ComputeFramesClassWriter : ClassWriter(COMPUTE_FRAMES) {
    override fun getCommonSuperClass(type1: String, type2: String): String {
        if (type1 == type2) return type1
        if (type1 == JAVA_LANG_OBJECT || type2 == JAVA_LANG_OBJECT) return JAVA_LANG_OBJECT
        val node1 = getClassNode(type1)
        val node2 = getClassNode(type2)
        if (node1 != null && node1.access and Opcodes.ACC_INTERFACE != 0 || node2 != null && node2.access and Opcodes.ACC_INTERFACE != 0) return JAVA_LANG_OBJECT
        val sup1 = getSuperTypesStack(type1, node1)
                ?: return JAVA_LANG_OBJECT
        val sup2 = getSuperTypesStack(type2, node2)
                ?: return JAVA_LANG_OBJECT
        if (sup2.contains(type1)) return type1
        if (sup1.contains(type2)) return type2
        if (sup1.isEmpty() || sup2.isEmpty()) return JAVA_LANG_OBJECT
        for (s1 in sup1) {
            if (sup2.contains(s1)) return s1
        }
        return JAVA_LANG_OBJECT
    }

    companion object {
        private val CLASSLOADER = ComputeFramesClassWriter::class.java.classLoader as LaunchClassLoader
        private const val JAVA_LANG_OBJECT = "java/lang/Object"
        private fun getClassNode(name: String): ClassReader? {
            return try {
                val classBytes = ClassPatchManager.INSTANCE.getPatchedResource(if (ClassTransformerImpl.IS_DEV) name else FMLDeobfuscatingRemapper.INSTANCE.unmap(name), FMLDeobfuscatingRemapper.INSTANCE.map(name), CLASSLOADER)
                if (classBytes != null) ClassReader(classBytes) else null
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }

        private fun getSuperTypesStack(type: String, node: ClassReader?): List<String>? {
            return try {
                if (node == null) {
                    val cls1 = Class.forName(type.replace('/', '.'), false, CLASSLOADER)
                    if (cls1.isInterface) null else getSuperTypesStack(cls1)
                } else {
                    getSuperTypesStack(node)
                }
            } catch (e: ClassNotFoundException) {
                emptyList()
            }
        }

        private fun getSuperTypesStack(node: ClassReader): List<String> {
            val superName = FMLDeobfuscatingRemapper.INSTANCE.map(node.superName)
            if (superName == JAVA_LANG_OBJECT) return emptyList()
            val list: MutableList<String> = ArrayList(4)
            list.add(superName)
            getSuperTypesStack(list, superName)
            return list
        }

        private fun getSuperTypesStack(cls: Class<*>): List<String> {
            var cls = cls
            if (cls.superclass == Any::class.java) return emptyList()
            val list: MutableList<String> = ArrayList(4)
            while (cls.superclass.also { cls = it } != Any::class.java) list.add(cls.name.replace('.', '/'))
            return list
        }

        private fun getSuperTypesStack(list: MutableList<String>, name: String) {
            val node = getClassNode(name)
            if (node != null) {
                val superName = FMLDeobfuscatingRemapper.INSTANCE.map(node.superName)
                if (superName != JAVA_LANG_OBJECT) {
                    list.add(superName)
                    getSuperTypesStack(list, superName)
                }
            } else {
                try {
                    var cls = Class.forName(name.replace('/', '.'), false, CLASSLOADER)
                    while (cls != Any::class.java) {
                        list.add(cls.name.replace('.', '/'))
                        cls = cls.superclass
                    }
                } catch (ignored: ClassNotFoundException) {
                    //will be used incomplete hierarchy
                }
            }
        }
    }
}
