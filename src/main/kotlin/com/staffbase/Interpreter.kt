package com.staffbase
enum class Instruction(val character : Char){
    IncrementValue('+'),
    DecrementValue('-'),
    MoveRight('>'),
    MoveLeft('<'),
    Input(','),
    Output('.')
}

fun interpret(commands: String): Sequence<Int> {
    val values = IntArray(10)
    var count = 0
    commands.forEach {
        when (it) {
            Instruction.IncrementValue.character -> values[count] += 1
            Instruction.DecrementValue.character -> values[count] -= 1
            Instruction.MoveRight.character -> count += 1
            Instruction.MoveLeft.character -> count -= 1
            Instruction.Input.character -> values[0] = readLine()?.get(0)?.toInt()!!
            Instruction.Output.character -> println(commands[0])
        }
    }
    return values.asSequence().filter { element ->(element != 0)  }
}

/*fun insert(commands: String): Int {
    val values = IntArray(10)
    commands.forEach {
        when (it){
            Instruction.Input.character -> values[0] = readLine()?.get(0)?.toInt()!!
        }
    }
    return values[0]
}*/
fun main() {
    val commands = ",>,"
    println(interpret(commands).toList())
}