package cards;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CardGame {
    private static List<Integer> readPack(String filePath, int numberOfPlayers) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new CardGameException("Pack file not found: " + filePath);
        }

        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<Integer> cardValues = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null) {
            try {
                int value = Integer.parseInt(line);
                if (value < 0) {
                    reader.close();
                    throw new CardGameException("Card value cannot be negative.");
                }
                cardValues.add(value);
            } catch (NumberFormatException e) {
                throw new CardGameException("Invalid card value: " + line);
            }
        }

        reader.close();

        if (cardValues.size() != 8 * numberOfPlayers) {
            throw new CardGameException("Pack file must have exactly 8*n cards.");
        }
        return cardValues;
    }

    public static void validatePackAndDistribution(List<Integer> cardValues, int numberOfPlayers) {
        // Count occurrences of each card value
        Map<Integer, Long> valueCounts = cardValues.stream()
                .collect(Collectors.groupingBy(value -> value, Collectors.counting()));

        // Check if any value appears at least 4 times
        boolean hasWinningSet = valueCounts.values().stream().anyMatch(count -> count >= 4);
        if (!hasWinningSet) {
            throw new CardGameException(
                    "Invalid card pack: The distribution of cards does not allow any player to win.");
        }

        // Simulate distribution to players to check for winnability
        int cardsPerPlayer = 4;
        int totalCardsForPlayers = numberOfPlayers * cardsPerPlayer;
        List<Integer> playerCards = cardValues.subList(0, totalCardsForPlayers);

        Map<Integer, Long> playerCardCounts = playerCards.stream()
                .collect(Collectors.groupingBy(value -> value, Collectors.counting()));

        boolean isDistributionWinnable = playerCardCounts.values().stream().anyMatch(count -> count >= 4);
        if (!isDistributionWinnable) {
            throw new CardGameException(
                    "Invalid card pack: The distribution of cards does not allow any player to win.");
        }
    }

    private static void dealCards(List<Player> players, List<CardDeck> decks, List<Integer> cardValues) {
        if (players == null || decks == null || cardValues == null) {
            throw new CardGameException("Players, decks, or card values cannot be null.");
        }
        int numberOfPlayers = players.size();
        int cardIndex = 0;

        // Deal cards to players
        for (int i = 0; i < 4 * numberOfPlayers; i++) {
            players.get(i % numberOfPlayers).receiveCard(new Card(cardValues.get(cardIndex++)));
        }

        // Deal cards to decks
        for (int i = 0; i < cardValues.size() - (4 * numberOfPlayers); i++) {
            decks.get(i % numberOfPlayers).addCard(new Card(cardValues.get(cardIndex++)));
        }
    }

    public static void writeFinalDeckState(CardDeck deck, int deckNumber) {
        String fileName = "output/deck" + deckNumber + "_output.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("deck" + deckNumber + " contents: " + deck.getDeckContents());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Failed to write final state for deck " + deckNumber + " to file: " + fileName);
        }
    }

    public static void clearOutputFolder(String outputPath) {
        File outputFolder = new File(outputPath);
        if (outputFolder.exists() && outputFolder.isDirectory()) {
            File[] files = outputFolder.listFiles();
            if (files != null) { // Check if listFiles() returned null
                for (File file : files) {
                    if (file.isFile()) {
                        try {
                            new FileWriter(file, false).close(); // Empty the file
                        } catch (IOException e) {
                            System.err.println("Failed to clear file: " + file.getName());
                        }
                    }
                }
            }
        } else {
            System.err.println("Output folder does not exist or is not a directory: " + outputPath);
        }
    }

    public static void main(String[] args) throws IOException {
        clearOutputFolder("output");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        List<Player> players = new ArrayList<>();
        List<CardDeck> decks = new ArrayList<>();

        // taking input on the number of players and the pack which has the card number
        System.out.println();
        System.out.print("Enter the number of players:");
        int numberOfPlayers = Integer.parseInt(reader.readLine());
        if (numberOfPlayers <= 0) {
            throw new CardGameException("Number of players must be a positive integer.");
        }

        System.out.print("Enter the path to the card pack file:");
        String packPath = reader.readLine();
        System.out.println();

        List<Integer> cardValues = readPack(packPath, numberOfPlayers);

        // Validate the card pack for a winning condition
        validatePackAndDistribution(cardValues, numberOfPlayers);

        // Shuffling the pack
        Collections.shuffle(cardValues);

        // Create decks and players
        for (int i = 0; i < numberOfPlayers; i++) {
            decks.add(new CardDeck());
        }
        for (int i = 0; i < numberOfPlayers; i++) {
            CardDeck leftDeck = decks.get(i);
            CardDeck rightDeck = decks.get((i + 1) % numberOfPlayers);
            players.add(new Player(i + 1, i + 1, leftDeck, rightDeck));
        }

        // Deal cards to players and decks
        dealCards(players, decks, cardValues);

        // Start game threads
        List<Thread> threads = new ArrayList<>();
        for (Player player : players) {
            Thread thread = new Thread(player);
            threads.add(thread);
            thread.start();
        }

        // Wait for game to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Notify other players if someone has won
        for (Player player : players) {
            if (player.isHasWon()) {
                for (Player otherPlayer : players) {
                    if (player != otherPlayer) {
                        otherPlayer.notifyWin(player.getId());
                    }
                }
            }
        }

        // Output final deck states to files
        for (int i = 0; i < decks.size(); i++) {
            writeFinalDeckState(decks.get(i), i + 1);
        }
    }
}
