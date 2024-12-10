package cards;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CardDeck {
    private final Queue<Card> cards = new LinkedList<>();
    private final Lock lock = new ReentrantLock();

    public void addCard(Card card) {
        if (card == null) {
            throw new CardDeckException("Cannot add a null card to the deck.");
        }
        lock.lock();
        try {
            cards.add(card);
        } finally {
            lock.unlock();
        }
    }

    public Card drawCard() {
        lock.lock();
        try {
            return cards.poll();
        } finally {
            lock.unlock();
        }
    }

    public void addCardToBottom(Card card) {
        if (card == null) {
            throw new CardDeckException("Cannot add a null card to the bottom of the deck.");
        }
        lock.lock();
        try {
            cards.add(card);
        } finally {
            lock.unlock();
        }
    }

    public String getDeckContents() {
        lock.lock();
        try {
            StringBuilder contents = new StringBuilder();
            for (Card card : cards) {
                contents.append(card.getValue()).append(" ");
            }
            return contents.toString().trim();
        } finally {
            lock.unlock();
        }
    }
}
