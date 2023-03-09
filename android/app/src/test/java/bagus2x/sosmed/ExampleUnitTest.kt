package bagus2x.sosmed

import org.junit.Test

import org.junit.Assert.*
import java.util.regex.Pattern

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val MENTION: Pattern = Pattern.compile("(\\s|\\A)@(\\w+)")
        println(MENTION.matcher("@bagus").matches())
        assertEquals(true, MENTION.matcher("@bagus").matches())
    }
}
