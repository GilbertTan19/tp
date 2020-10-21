package quickcache.model.flashcard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static quickcache.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class DifficultyTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Difficulty(null));
    }

    @Test
    public void constructor_invalidDifficultyName1_throwsIllegalArgumentException() {
        String invalidDifficultyName = "";
        assertThrows(IllegalArgumentException.class, () -> new Difficulty(invalidDifficultyName));
    }

    @Test
    public void constructor_invalidDifficultyName2_throwsIllegalArgumentException() {
        String invalidDifficultyName = "invalidDifficultyName";
        assertThrows(IllegalArgumentException.class, () -> new Difficulty(invalidDifficultyName));
    }

    @Test
    public void isValidDifficultyName() {
        // null tag name
        assertThrows(NullPointerException.class, () -> Difficulty.isValidDifficultyName(null));
    }

    @Test
    public void testToString() {
        String difficultyLevel = "LOW";
        Difficulty difficulty = new Difficulty(difficultyLevel);
        assertEquals(difficulty.toString(), "[" + difficultyLevel + "]");
    }
}
