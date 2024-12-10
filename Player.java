package cards;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
to make Multhithreadning code more flexible , efficient and reduce amount of errors,
we are using lock and reentrantlock interfaces
to give code specific conditions when our code is going to run which player without breaking parallel execution.
*/

public class Player implements Runnable {
    private final int id;
    private final int preferredValue;
    private final List<Card> hand = new ArrayList<>();
    private final CardDeck leftDeck;
    private final CardDeck rightDeck;
    private final Lock lock = new ReentrantLock();
    private final String outputFile;
    private static volatile boolean gameWon = false;
    private boolean hasWon = false;

    public Player(int id, int preferredValue, CardDeck leftDeck, CardDeck rightDeck) {
        if (id <= 0) {
            throw new PlayerException("Player ID cannot be negative.");
        }
        this.id = id;
        this.preferredValue = preferredValue;
        this.leftDeck = leftDeck;
        this.rightDeck = rightDeck;
        this.outputFile = "output/player" + id + "_output.txt";
    }

    public int getId() {
        return id;
    }

    public boolean isHasWon() {
        return hasWon;
    }

    public void receiveCard(Card card) {
        if (card == null) {
            throw new PlayerException("Cannot receive a null card.");
        }
        lock.lock();
        try {
            hand.add(card);
        } finally {
            lock.unlock();
        }
    }

    private boolean checkWinningCondition() {
        lock.lock();
        try {
            int firstValue = hand.get(0).getValue();
            for (Card card : hand) {
                if (card.getValue() != firstValue) {
                    return false;
                }
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    private void writeToFile(String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true))) {
            writer.write(content);
            writer.newLine();
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    private void logInitialHand() {
        lock.lock();
        try {
            StringBuilder initialHand = new StringBuilder("player " + id + " initial hand ");
            for (Card card : hand) {
                initialHand.append(card.getValue()).append(" ");
            }
            writeToFile(initialHand.toString().trim());
        } finally {
            lock.unlock();
        }
    }

    private void playTurn() {
        lock.lock();
        try {
            Card drawnCard = leftDeck.drawCard();
            if (drawnCard != null) {
                hand.add(drawnCard);

                // Randomly pick a non-preferred card to discard
                Random random = new Random();
                Card discard = hand.stream()
                        .filter(card -> card.getValue() != preferredValue)
                        .skip(random
                                .nextInt((int) hand.stream().filter(card -> card.getValue() != preferredValue).count()))
                        .findFirst()
                        .orElse(hand.get(0));

                hand.remove(discard);
                rightDeck.addCardToBottom(discard);

                // Log actions
                String log = String.format("player %d draws a %d from deck %d", id, drawnCard.getValue(), id);
                writeToFile(log);

                log = String.format("player %d discards a %d to deck %d", id, discard.getValue(),
                        (id % hand.size()) + 1);
                writeToFile(log);

                StringBuilder currentHand = new StringBuilder("player " + id + " current hand ");
                for (Card card : hand) {
                    currentHand.append(card.getValue()).append(" ");
                }
                writeToFile(currentHand.toString().trim());
            }
        } finally {
            lock.unlock();
        }
    }

    private void logExit(String reason) {
        writeToFile(reason);
        writeToFile("player " + id + " exits");

        StringBuilder finalHand = new StringBuilder("player " + id + " final hand: ");
        for (Card card : hand) {
            finalHand.append(card.getValue()).append(" ");
        }
        writeToFile(finalHand.toString().trim());
    }

    @Override
    public void run() {
        logInitialHand();

        if (checkWinningCondition()) {
            hasWon = true;
            gameWon = true;
            System.out.println("Player " + id + " wins");
            logExit("player " + id + " wins");
        } else {
            while (!gameWon) {
                playTurn();
                if (checkWinningCondition()) {
                    hasWon = true;
                    gameWon = true;
                    System.out.println("Player " + id + " wins");
                    logExit("player " + id + " wins");
                }
            }
        }
    }

    public void notifyWin(int winnerId) {
        if (!hasWon) {
            String message = "player " + winnerId + " has informed player " + id + " that player " + winnerId
                    + " has won";
            writeToFile(message);
            logExit(message);
        }
    }
}
