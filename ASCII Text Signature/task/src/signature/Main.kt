package signature

import java.io.File
import java.util.*
import kotlin.properties.Delegates

fun main() {
    print("Enter name and surname: ")
    val name = readLine()!!
    print("Enter person's status: ")
    val status = readLine()!!

    println(Badge(name, status))
}

class Badge(name: String, status: String) : Iterable<String> {

    private companion object {
        const val ROMAN = "/home/samyual/Загрузки/roman.txt"
        const val MEDIUM = "/home/samyual/Загрузки/medium.txt"
    }

    private val nameTag = Tag(name, Font(ROMAN))
    private val statusTag = Tag(status, Font(MEDIUM))
    private val width by lazy { nameTag.length.coerceAtLeast(statusTag.length) }

    override fun toString() = fold(StringBuilder()) { builder, line -> builder.appendln(line) }.toString()

    override fun iterator() = object : Iterator<String> {
        val lines = lines().toList()
        var current = 0
        override fun hasNext() = current in lines.indices
        override fun next() = lines[current++]
    }

    private fun lines() = sequence {
        val firstAndLast = "8".repeat(width + 8)
        yield(firstAndLast)
        nameTag.forEach {
            yield("88  ${it.center(width)}  88")
        }
        statusTag.forEach {
            yield("88  ${it.center(width)}  88")
        }
        yield(firstAndLast)
    }

    private fun String.center(width: Int): String {
        val startPad = (width - this.length) / 2
        val endPad = width - this.length - startPad
        return " ".repeat(startPad) + this + " ".repeat(endPad)
    }
}

private class Tag(private val text: String, private val font: Font) : Iterable<String> {

    val length: Int by lazy { text.fold(0) { len, ch -> len + font[ch].width } }

    private val height = font.height

    operator fun get(line: Int) = text.map { font[it][line] }.joinToString("")

    override fun iterator() = object : Iterator<String> {
        var index = 0
        override fun hasNext() = index < height
        override fun next() = this@Tag[index++]
    }
}

private class Font(private val fileName: String) {

    var height by Delegates.notNull<Int>()
        private set

    private val glyphs = mutableMapOf<Char, Glyph>()

    init {
        Scanner(File(fileName)).use { file ->
            height = file.nextInt()
            val glyphNumber = file.nextInt()
            repeat(glyphNumber) {
                val character = file.next()
                val width = file.nextInt()
                file.nextLine()
                glyphs[character[0]] = Glyph(Array(height) { file.nextLine() })
            }
        }
        // add whitespace using 'a' glyph width
        glyphs[' '] = Glyph(Array(height) { " ".repeat(glyphs['a']!!.width) })
    }

    operator fun get(ch: Char): Glyph = glyphs[ch] ?: throw IllegalStateException("Illegal letter $ch in file $fileName")
}

private class Glyph(private val lines: Array<String>) {

    init {
        require(lines.distinctBy { it.length }.size == 1) { "All lines in glyph must be same length" }
    }

    val width: Int by lazy { lines[0].length }

    operator fun get(i: Int) = lines[i]
}
