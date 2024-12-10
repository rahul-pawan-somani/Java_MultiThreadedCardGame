package cardsTest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import cards.*;

class CardDeckTest {
    @Test
    void testAddAndDrawCard() {
        CardDeck deck = new CardDeck();
        Card card1 = new Card(1);
        Card card2 = new Card(2);

        deck.addCard(card1);
        deck.addCard(card2);

        assertEquals(1, deck.drawCard().getValue(), "The first card should be drawn correctly.");
        assertEquals(2, deck.drawCard().getValue(), "The second card should be drawn correctly.");
    }

    @Test
    void testAddNullCardThrowsException() {
        CardDeck deck = new CardDeck();
        assertThrows(CardDeckException.class, () -> deck.addCard(null),
                "Adding a null card should throw CardDeckException.");
    }

    @Test
    void testDrawFromEmptyDeckReturnsNull() {
        CardDeck deck = new CardDeck();
        assertNull(deck.drawCard(), "Drawing from an empty deck should return null.");
    }

    @Test
    void testDeckContents() {
        CardDeck deck = new CardDeck();
        deck.addCard(new Card(5));
        deck.addCard(new Card(7));

        assertEquals("5 7", deck.getDeckContents(), "Deck contents should match the added cards.");
    }
}
