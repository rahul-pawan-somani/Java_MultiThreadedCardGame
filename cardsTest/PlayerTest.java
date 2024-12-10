package cardsTest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import cards.*;

class PlayerTest {

    @Test
    void testPlayerInitialization() {
        CardDeck leftDeck = new CardDeck();
        CardDeck rightDeck = new CardDeck();
        Player player = new Player(1, 1, leftDeck, rightDeck);

        assertEquals(1, player.getId(), "Player ID should be initialized correctly.");
    }

    @Test
    void testReceiveCard() {
        Player player = new Player(1, 1, new CardDeck(), new CardDeck());
        player.receiveCard(new Card(10));

        assertTrue(player.isHasWon(), "Player should have won if all cards are the same.");
    }
}
