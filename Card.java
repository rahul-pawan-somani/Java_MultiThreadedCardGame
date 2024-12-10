package cards;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Card {
    private final int value;
    private final Lock lock;

    public Card(int value) {
        if (value < 0) {
            throw new CardDeckException("Invalid pack provided. Card value cannot be negative.");
        }
        this.value = value;
        this.lock = new ReentrantLock();
    }

    public int getValue() {
        lock.lock();
        try {
            return value;
        } finally {
            lock.unlock();
        }
    }
}
