package com.staffbase

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertEquals
import kotlin.test.expect

class TestConsole(private val input: MutableList<Int>) : Console {
    var output = ""
        private set

    constructor(input: String = "") : this(input.map(Char::toInt).toMutableList())

    override fun writeCharacter(character: Int) {
        output += character.toChar()
    }

    override fun readCharacter(): Int {
        return if (input.isEmpty()) 0 else input.removeAt(0)
    }
}

class InterpreterTest {
    @Test
    fun `can crawl through array & increase values`() {
        val tc = TestConsole()
        val script = "++>+>+++<+<-"
        assertEquals(listOf(1, 2, 3), interpret(script, tc))
        assertEquals(expected = "", actual = tc.output)
    }

    @ParameterizedTest
    @CsvSource(
        "11, 2",
        "45, 9",
        "55, 10",
        "99, 18"
    )
    fun `add two entered digits`(input: String, output: String) {
        val tc = TestConsole(input = input)
        val script = """
            ++++++
            [>++++++++<-]
            >>,>,<<
            [>->-<<-]
            >>
            [<+>-]
            >++++++++++<<[->+>-[>+>>]>[+[-<+>]>+>>]<<<<<<]>>[-]>>>++++++++++<[->-[>+>>]>[+[-
            <+>]>+>>]<<<<<]>[-]>>[>++++++[-<++++++++>]<.<<+>+>[-]]<[<[->-<]++++++[->++++++++
            <]>.[-]]<<++++++[-<++++++++>]<.[-]<<[-<+>]
        """.trimIndent()
        interpret(script, tc)
        assertEquals(expected = output, actual = tc.output)
    }

    @Test
    fun `can perform output`() {
        val tc = TestConsole(input = "hello")
        val script = ",[.,]"
        val memory = interpret(script, tc)
        assertEquals(expected = listOf(), actual = memory)
        assertEquals(expected = "hello", actual = tc.output)
    }
    @Test
    fun `check the loop`(){
        val temporary = ">+[++]"

    }

    @ParameterizedTest
    @ValueSource(strings = ["]", "["])
    fun `broken loops should be detected correctly`(script: String) {
        assertThrows<BrokenLoopException> {
            findLoopIndexes(script)
        }
    }
}
