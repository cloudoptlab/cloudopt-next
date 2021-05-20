package net.cloudopt.next.polyglot

import net.cloudopt.next.core.Resourcer
import org.graalvm.polyglot.*

/**
 * Evaluates a source object by using the language specified in the source.
 * The result is accessible as value and never returns null.
 *
 * @param members Sets the value of a member using an identifier
 * @param autoContextClose If autoContextClose is turned on, the context
 * object will be closed automatically when the execution of the code is
 * finished
 * @param customContext The context objects was created by yourself
 * @param block Code in the specified language, if there is only one line
 * of text and it ends in .py this file will be executed automatically
 *
 * @return Value Represents a polyglot value that can be accessed using a
 * set of language agnostic operations. Polyglot values represent values
 * from host or guest language. Polyglot values are bound to a context.
 * If the context is closed then all value operations throw an
 * IllegalStateException.
 */
fun python(
    members: MutableMap<String, Any> = mutableMapOf(),
    autoContextClose: Boolean = true,
    customContext: Context? = null,
    block: () -> String
): Value? {
    return runOnGraal(members, autoContextClose, customContext, "python", block.invoke())
}

/**
 * Evaluates a source object by using the language specified in the source.
 * The result is accessible as value and never returns null.
 *
 * @param members Sets the value of a member using an identifier
 * @param autoContextClose If autoContextClose is turned on, the context
 * object will be closed automatically when the execution of the code is
 * finished
 * @param customContext The context objects was created by yourself
 * @param block Code in the specified language, if there is only one line
 * of text and it ends in .js this file will be executed automatically
 *
 * @return Value Represents a polyglot value that can be accessed using a
 * set of language agnostic operations. Polyglot values represent values
 * from host or guest language. Polyglot values are bound to a context.
 * If the context is closed then all value operations throw an
 * IllegalStateException.
 */
fun javascript(
    members: MutableMap<String, Any> = mutableMapOf(),
    autoContextClose: Boolean = true,
    customContext: Context? = null,
    block: () -> String
) {
    runOnGraal(members, autoContextClose, customContext, "js", block.invoke())
}

/**
 * Evaluates a source object by using the language specified in the source.
 * The result is accessible as value and never returns null.
 *
 * @param members Sets the value of a member using an identifier
 * @param autoContextClose If autoContextClose is turned on, the context
 * object will be closed automatically when the execution of the code is
 * finished
 * @param customContext The context objects was created by yourself
 * @param block Code in the specified language, if there is only one line
 * of text and it ends in .r this file will be executed automatically
 *
 * @return Value Represents a polyglot value that can be accessed using a
 * set of language agnostic operations. Polyglot values represent values
 * from host or guest language. Polyglot values are bound to a context.
 * If the context is closed then all value operations throw an
 * IllegalStateException.
 */
fun R(
    members: MutableMap<String, Any> = mutableMapOf(),
    autoContextClose: Boolean = true,
    customContext: Context? = null,
    block: () -> String
) {
    runOnGraal(members, autoContextClose, customContext, "R", block.invoke())
}


/**
 * Evaluates a source object by using the language specified in the source.
 * The result is accessible as value and never returns null.
 *
 * @param members Sets the value of a member using an identifier
 * @param autoContextClose If autoContextClose is turned on, the context
 * object will be closed automatically when the execution of the code is
 * finished
 * @param customContext The context objects was created by yourself
 * @param block Code in the specified language, if there is only one line
 * of text and it ends in .ruby this file will be executed automatically
 *
 * @return Value Represents a polyglot value that can be accessed using a
 * set of language agnostic operations. Polyglot values represent values
 * from host or guest language. Polyglot values are bound to a context.
 * If the context is closed then all value operations throw an
 * IllegalStateException.
 */
fun ruby(
    members: MutableMap<String, Any> = mutableMapOf(),
    autoContextClose: Boolean = true,
    customContext: Context? = null,
    block: () -> String
) {
    runOnGraal(members, autoContextClose, customContext, "ruby", block.invoke())
}

/**
 * Evaluates a source object by using the language specified in the source.
 * The result is accessible as value and never returns null.
 *
 * @param members Sets the value of a member using an identifier
 * @param autoContextClose If autoContextClose is turned on, the context
 * object will be closed automatically when the execution of the code is
 * finished
 * @param customContext The context objects was created by yourself
 * @param language Specify the language name
 * @param block Code in the specified language, if there is only one line
 * of text and it ends in .py this file will be executed automatically
 *
 * @return Value Represents a polyglot value that can be accessed using a
 * set of language agnostic operations. Polyglot values represent values
 * from host or guest language. Polyglot values are bound to a context.
 * If the context is closed then all value operations throw an
 * IllegalStateException.
 */
private fun runOnGraal(
    members: MutableMap<String, Any>,
    autoContextClose: Boolean,
    customContext: Context?,
    language: String,
    block: String
): Value? {
    val context: Context = customContext ?: Context.newBuilder()
        .allowIO(true)
        .allowPolyglotAccess(PolyglotAccess.ALL)
        .allowEnvironmentAccess(EnvironmentAccess.INHERIT)
        .allowNativeAccess(true)
        .build()
    members.keys.forEach { key ->
        context.polyglotBindings.putMember(key, members[key])
    }
    val lineSeparator = System.getProperty("line.separator", "\n")
    val blockArray = block.split(lineSeparator)
    val source = if (blockArray.size < 2
        && (blockArray[0].contains(".py")
                || blockArray[0].contains(".js")
                || blockArray[0].contains(".r")
                || blockArray[0].contains(".ruby")
                )
    ) {
        Source.newBuilder(language, Resourcer.getUrl(block)).cached(true).build()
    } else {
        Source.create(language, block)
    }

    val value = context.eval(source)

    if (autoContextClose) {
        context.close()
    }

    return value
}