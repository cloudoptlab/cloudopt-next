import kotlinx.coroutines.runBlocking
import net.cloudopt.next.polyglot.R
import net.cloudopt.next.polyglot.javascript
import net.cloudopt.next.polyglot.python
import net.cloudopt.next.polyglot.ruby
import net.cloudopt.next.core.Worker.await
import kotlin.test.Test

class Test {

    @Test
    fun testPython() {
        python(mutableMapOf("name" to "Andy")) {
            "import polyglot \n" +
                    "print(polyglot.import_value('name') + ': Hello Python!') \n"
        }
    }

    @Test
    fun testPythonFile() {
        val value = python(mutableMapOf("name" to "Andy")) {
            "test.py"
        }
        val x = value?.asInt()
        println(x)
    }

    @Test
    fun awaitPythonFile() = runBlocking {
        val x = await<Int> { promise ->
            val value = python(mutableMapOf("name" to "Andy")) {
                "test.py"
            }
            promise.complete(value?.asInt())
        }
        println(x)
    }

    @Test
    fun testJavascript() {
        javascript {
            "console.log('Hello JavaScript')"
        }
    }

    @Test
    fun testRuby() {
        ruby {
            "print 'Hello Ruby \n'"
        }
    }

    @Test
    fun testR() {
        R {
            "print('Hello R')"
        }
    }

}
