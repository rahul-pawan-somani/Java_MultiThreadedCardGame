package cardsTest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import cards.*;

class CardTest {

    @Test
    void testCardValueRetrieval() {
        Card card = new Card(10);
        assertEquals(10, card.getValue(), "The card value should be retrieved correctly.");
    }

    @Test
    void testCardNegativeValueThrowsException() {
        assertThrows(CardDeckException.class, () -> new Card(-1),
                "Creating a card with a negative value should throw CardDeckException.");
    }
}
