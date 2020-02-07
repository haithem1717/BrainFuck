package com.staffbase

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.Scanner


internal class InterpreterTest {
    @Test
    fun `can crawl through array & increase values`() {
        val values = IntArray(10)
        fun interpret(commands: String): Sequence<Int> {
            var count = 0
            commands.forEach {
                when (it) {
                    Instruction.IncrementValue.character -> values[count] += 1
                    Instruction.DecrementValue.character -> values[count] -= 1
                    Instruction.MoveRight.character -> count += 1
                    Instruction.MoveLeft.character -> count -= 1
                }
            }
             return values.asSequence().filter { element ->(element != 0)  }
        }
        val commands = "++>+>+++<+<-"
        val testSequence = sequenceOf(1,2,3)
        assertEquals(testSequence.toList(), interpret(commands).toList())
    }

    @Test
    fun `can perform output`() {
        val values = arrayOf(97,98,99)
        val commands = "."
        commands.forEach {
            when (it){
                    Instruction.Output.character -> println(commands[0])
            }
        }
        assertEquals(97, values[0])
    }
}
