package cardsTest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import cards.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class CardGameTest {
    @Test
    void testValidCardPack() {
        List<Integer> cardValues = Arrays.asList(1, 1, 1, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        int numberOfPlayers = 2;

        assertDoesNotThrow(() -> CardGame.validatePackAndDistribution(cardValues, numberOfPlayers),
                "A valid card pack should not throw an exception.");
    }

    @Test
    void testInvalidCardPackNoWinningSet() {
        List<Integer> cardValues = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        int numberOfPlayers = 2;

        Exception exception = assertThrows(CardGameException.class,
                () -> CardGame.validatePackAndDistribution(cardValues, numberOfPlayers),
                "An invalid card pack without a winning set should throw an exception.");

        assertEquals("Invalid card pack: The distribution of cards does not allow any player to win.",
                exception.getMessage());
    }

    @Test
    void testEmptyCardPack() {
        List<Integer> cardValues = Collections.emptyList();
        int numberOfPlayers = 2;

        Exception exception = assertThrows(CardGameException.class,
                () -> CardGame.validatePackAndDistribution(cardValues, numberOfPlayers),
                "An empty card pack should throw an exception.");

        assertEquals("Pack file must have exactly 8*n cards.", exception.getMessage());
    }
}
