package net.cloudopt.next.logging.test

/**
 * For formatting text
 */
class Format(openToken: String, closeToken: String) {

    private val openToken = openToken

    private val closeToken = closeToken

    /**
     * Replace the placeholders in the string text, consisting of openToken and closeToken,
     * with the values in the args array.
     * @param text String
     * @param args Array<out Any>
     * @return String?
     */
    fun format(text: String, vararg args: Any): String {
        if (args.isEmpty()) {
            return text
        }
        var argsIndex = 0
        val src = text.toCharArray()
        var offset = 0
        // search open token
        var start = text.indexOf(openToken, offset)
        if (start == -1) {
            return text
        }
        val builder = StringBuilder()
        val expression: StringBuilder = StringBuilder()
        while (start > -1) {
            if (start > 0 && src[start - 1] == '\\') {
                // this open token is escaped. remove the backslash and continue.
                builder.append(src, offset, start - offset - 1).append(openToken)
                offset = start + openToken.length
            } else {
                builder.append(src, offset, start - offset)
                offset = start + openToken.length
                var end = text.indexOf(closeToken, offset)
                while (end > -1) {
                    if (end > offset && src[end - 1] == '\\') {
                        // this close token is escaped. remove the backslash and continue.
                        expression.append(src, offset, end - offset - 1).append(closeToken)
                        offset = end + closeToken.length
                        end = text.indexOf(closeToken, offset)
                    } else {
                        expression.append(src, offset, end - offset)
                        offset = end + closeToken.length
                        break
                    }
                }
                if (end == -1) {
                    // close token was not found.
                    builder.append(src, start, src.size - start)
                    offset = src.size
                } else {
                    val value = if (argsIndex <= args.size - 1) args[argsIndex].toString() else expression.toString()
                    builder.append(value)
                    offset = end + closeToken.length
                    argsIndex++
                }
            }
            start = text.indexOf(openToken, offset)
        }
        if (offset < src.size) {
            builder.append(src, offset, src.size - offset)
        }
        return builder.toString()
    }
}