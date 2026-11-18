package bg.mobileapps.fn2401321025.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ShareTextTest {

    @Test
    public void joinsLinesWithNewlines() {
        assertEquals("a\nb\nc", ShareText.compose("a", "b", "c"));
    }

    @Test
    public void skipsNullAndBlankLines() {
        assertEquals("a\nc", ShareText.compose("a", null, "   ", "c"));
    }

    @Test
    public void trimsLines() {
        assertEquals("a\nb", ShareText.compose("  a ", "b\n"));
    }

    @Test
    public void allEmpty_givesEmptyText() {
        assertEquals("", ShareText.compose(null, ""));
    }
}
