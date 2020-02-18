package com.staffbase

data class State(
    val memory: List<Int> = List(DEFAULT_MEMORY_SIZE) { 0 },
    val pointer: Int = 0
) {
    val currentValue: Int get() = memory[pointer]

    companion object {
        const val DEFAULT_MEMORY_SIZE = 65536
    }
}

fun <T> List<T>.withReplaced(index: Int, replacer: (T) -> T) = ArrayList(this).apply {
    set(index, replacer(get(index)))
}

object NoOp : (State, Console) -> State {
    override fun invoke(state: State, console: Console) = state
}

enum class Instruction(
    val character: Char,
    val action: (State, Console) -> State
) : (State, Console) -> State by action {
    IncrementValue(
        character = '+',
        action = { (memory, pointer), _ ->
            State(
                memory = memory.withReplaced(pointer) { it + 1 },
                pointer = pointer
            )
        }
    ),
    DecrementValue(
        character = '-',
        action = { (memory, pointer), _ ->
            State(
                memory = memory.withReplaced(pointer) { it - 1 },
                pointer = pointer
            )
        }
    ),
    MoveRight(
        character = '>',
        action = { state, _ ->
            state.copy(pointer = (state.pointer + 1) % state.memory.size)
        }
    ),
    MoveLeft(
        character = '<',
        action = { state, _ ->
            state.copy(pointer = (state.pointer - 1) % state.memory.size)
        }
    ),
    Input(
        character = ',',
        action = { state, console ->
            state.copy(memory = state.memory.withReplaced(state.pointer) { console.readCharacter() })
        }
    ),
    Output(
        character = '.',
        action = { state, console ->
            state.apply { console.writeCharacter(memory[pointer]) }
        }
    );

    companion object {
        private val ACTION_MAPPING = values().associate { it.character to it.action }

        fun getActionForCharacter(character: Char) = ACTION_MAPPING[character] ?: NoOp
    }
}

interface Console {
    fun readCharacter() = readLine()?.first()?.toInt() ?: 0
    fun writeCharacter(character: Int) = print(character.toChar())
}

object DefaultConsole : Console

fun interpret(commands: String, console: Console = DefaultConsole): List<Int> {
    var state = State()
    val indexes = findLoopIndexes(commands)
    var ip = 0
    while (ip < commands.length) {
        val command = commands[ip]
        when (command) {
            Instruction.IncrementValue.character -> state = Instruction.IncrementValue.action(state, console)
            Instruction.DecrementValue.character -> state = Instruction.DecrementValue.action(state, console)
            Instruction.MoveRight.character -> state = Instruction.MoveRight.action(state, console)
            Instruction.MoveLeft.character -> state = Instruction.MoveLeft.action(state, console)
            Instruction.Input.character -> state = Instruction.Input.action(state, console)
            Instruction.Output.character -> state = Instruction.Output.action(state, console)
            '[' -> if (state.currentValue == 0) {
                ip = indexes[ip]
            }
            ']' -> ip = indexes[ip] - 1
        }
        ++ip
    }
    return state.memory.filter { element -> element != 0 }
}

enum class BrokenLoopType {
    UnexpectedClosing,
    UnclosedLoop
}

class BrokenLoopException(
    val type: BrokenLoopType,
    message: String? = null,
    cause: Throwable? = null
) : RuntimeException(message, cause) {
    override fun toString(): String {
        return "BrokenLoopException: $type"
    }
}

fun findLoopIndexes(commands: String): Array<Int> {
    val loopIndexes = Array(commands.length) { 0 }
    val openings = mutableListOf<Int>()
    for ((idx, command) in commands.withIndex()) {
        if (command == '[') {
            openings.add(0, idx)
        } else if (command == ']') {
            val start = try {
                openings.removeAt(0)
            } catch (ex: IndexOutOfBoundsException) {
                throw BrokenLoopException(type = BrokenLoopType.UnexpectedClosing, cause = ex)
            }
            loopIndexes[start] = idx
            loopIndexes[idx] = start
        }
    }

    if (openings.isNotEmpty()) {
        throw BrokenLoopException(type = BrokenLoopType.UnclosedLoop)
    }

    return loopIndexes
}

fun main() {
    val commands = ",[.-]"
    println(interpret(commands).toList())
}