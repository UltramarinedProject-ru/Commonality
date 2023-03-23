package ru.s5a4ed1sa7.commonality.util

import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import ru.s5a4ed1sa7.core.annotation.ASMApi
import java.util.*

@ASMApi
object MinecraftUtil {
    @JvmStatic
    @ASMApi
    fun calculateXPForPlayer(level: Int, entityPlayer: EntityPlayer) {
        var xp = countXPCostForLevel(entityPlayer.experienceLevel)
        xp += ((countXPCostForLevel(entityPlayer.experienceLevel + 1) - xp) * entityPlayer.experience).toInt()
        xp -= countXPCostForLevel(-level)
        entityPlayer.experienceLevel = 0
        entityPlayer.experience = 0.0F
        entityPlayer.experienceTotal = 0
        if (xp > 0) entityPlayer.addExperience(xp)
    }

    private fun countXPCostForLevel(level: Int): Int {
        return when {
            level < 17 -> {
                17 * level
            }
            level < 30 -> {
                var cost = 17 * level
                for (i in 0 until level - 15) cost += i * 3
                cost + level / 18
            }
            else -> {
                var cost = 826
                for (i in 0 until level - 30) cost += 62 + i * 7
                cost
            }
        }
    }


    @JvmStatic
    @ASMApi
    fun canLeavesStay(world: World, bx: Int, by: Int, bz: Int, searchDistance: Int): Boolean {
        return LeavesPathFinder(world, bx, by, bz, searchDistance).canLeavesStay()
    }

    fun Item.toItemStack(count: Int): ItemStack {
        return ItemStack(this, count)
    }

    private class LeavesPathFinder(private val world: World, private val bx: Int, private val by: Int, private val bz: Int, private val distance: Int) {
        fun canLeavesStay(): Boolean {
            require(distance <= 7) { "distance should be less then 8, given: $distance" }
            if (!world.checkChunksExist(bx - distance - 1, by - distance - 1, bz - distance - 1, bx + distance + 1, by + distance + 1, bz + distance + 1)) return true
            Arrays.fill(AREA, EMPTY.toByte())
            setVal(0, 0, 0, LEAVES)
            return recursivePathFind(0, 0, 0, 0)
        }

        private fun recursivePathFind(x: Int, y: Int, z: Int, depth: Int): Boolean {
            var current = getVal(x, y, z)
            if (current == EMPTY) {
                val block: Block = world.getBlock(bx + x, by + y, bz + z)
                current = if (block.canSustainLeaves(world, bx + x, by + y, bz + z)) WOOD else if (block.isLeaves(world, bx + x, by + y, bz + z)) LEAVES else WALL
                setVal(x, y, z, current)
            }
            if (current == WOOD) return true else if (current <= depth) return false
            setVal(x, y, z, depth)
            val newDepth = depth + 1
            if (newDepth > distance) return false
            if (recursivePathFind(x + 1, y, z, newDepth)) return true
            if (recursivePathFind(x, y + 1, z, newDepth)) return true
            if (recursivePathFind(x, y, z + 1, newDepth)) return true
            if (recursivePathFind(x - 1, y, z, newDepth)) return true
            if (recursivePathFind(x, y - 1, z, newDepth)) return true
            return recursivePathFind(x, y, z - 1, newDepth)
        }

        companion object {
            private const val SIZE: Byte = 16
            private const val SIZE_HALF = SIZE / 2
            private val AREA = ByteArray(SIZE * SIZE * SIZE)
            private const val EMPTY = 0
            private const val WOOD = -1
            private const val LEAVES = Byte.MAX_VALUE.toInt()
            private const val WALL = -2
            private fun getVal(x: Int, y: Int, z: Int): Int {
                return AREA[x + SIZE_HALF shl 8 or (y + SIZE_HALF shl 4) or z + SIZE_HALF].toInt()
            }

            private fun setVal(x: Int, y: Int, z: Int, newVal: Int) {
                AREA[x + SIZE_HALF shl 8 or (y + SIZE_HALF shl 4) or z + SIZE_HALF] = newVal.toByte()
            }
        }
    }
}