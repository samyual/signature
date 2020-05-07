package signature

fun main() {
    print("Enter name and surname: ")
    val name = readLine()!!
    print("Enter person's status: ")
    val status = readLine()!!

    println(VisitCard(name, status))
}

class VisitCard(name: String, private val status: String) : Iterable<String> {

    private val tag = Tag(name)

    private val maxWidth by lazy { tag.length.coerceAtLeast(status.length) }

    override fun iterator() = object : Iterator<String> {
        var lineNo = 0
        override fun hasNext() = lineNo in 0..5
        override fun next() = this@VisitCard[lineNo++]
    }

    override fun toString() = fold(StringBuilder()) { builder, line -> builder.appendln(line) }.toString()

    private operator fun get(line: Int) = when (line) {
        0, 5 -> "*".repeat(maxWidth + 6)
        1, 2, 3 -> "*  ${tag[line - 1].center(maxWidth)}  *"
        4 -> "*  ${status.center(maxWidth)}  *"
        else -> throw IndexOutOfBoundsException("Line number is $line, but it must be in range 0..5")
    }

    private fun String.center(width: Int) = if (length == width) this
    else {
        val startPad = (width - length) / 2
        val endPad = width - length - startPad
        " ".repeat(startPad) + this + " ".repeat(endPad)
    }
}

private class Tag(private val text: String) {

    val length: Int by lazy {
        text.fold(0) { len, ch -> len + Font[ch].width } + text.length.dec()
    }

    operator fun get(line: Int) = text.map { Font[it][line] }.joinToString(" ")
}

private object Font {

    operator fun get(ch: Char) = glyphs[ch.toUpperCase()] ?: SPACE

    private val SPACE = Glyph("    ", "    ", "    ")

    private val glyphs = mapOf(
            ' ' to SPACE,
            'A' to Glyph("____", "|__|", "|  |"),
            'B' to Glyph("___ ", "|__]", "|__]"),
            'C' to Glyph("____", "|   ", "|___"),
            'D' to Glyph("___ ", "|  \\", "|__/"),
            'E' to Glyph("____", "|___", "|___"),
            'F' to Glyph("____", "|___", "|   "),
            'G' to Glyph("____", "| __", "|__]"),
            'H' to Glyph("_  _", "|__|", "|  |"),
            'I' to Glyph("_", "|", "|"),
            'J' to Glyph(" _", " |", "_|"),
            'K' to Glyph("_  _", "|_/ ", "| \\_"),
            'L' to Glyph("_   ", "|   ", "|___"),
            'M' to Glyph("_  _", "|\\/|", "|  |"),
            'N' to Glyph("_  _", "|\\ |", "| \\|"),
            'O' to Glyph("____", "|  |", "|__|"),
            'P' to Glyph("___ ", "|__]", "|   "),
            'Q' to Glyph("____", "|  |", "|_\\|"),
            'R' to Glyph("____", "|__/", "|  \\"),
            'S' to Glyph("____", "[__ ", "___]"),
            'T' to Glyph("___", " | ", " | "),
            'U' to Glyph("_  _", "|  |", "|__|"),
            'V' to Glyph("_  _", "|  |", " \\/ "),
            'W' to Glyph("_ _ _", "| | |", "|_|_|"),
            'X' to Glyph("_  _", " \\/ ", "_/\\_"),
            'Y' to Glyph("_   _", " \\_/ ", "  |  "),
            'Z' to Glyph("___ ", "  / ", " /__")
    )
}

private class Glyph(upperLine: String, mediumLine: String, bottomLine: String) {

    init {
        require(upperLine.length in 1..5) { "Length must be in range from 1 to 5" }
        require(upperLine.length == mediumLine.length) { "Length of first and second lines must be equal" }
        require(upperLine.length == bottomLine.length) { "Length of first and third lines must be equal" }
    }

    val width: Int by lazy { lines[0].length }

    private val lines = arrayOf(upperLine, mediumLine, bottomLine)

    operator fun get(i: Int) = lines[i]
}