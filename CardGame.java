import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CardGame {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Enter the number of players:");
        int numPlayers = Integer.parseInt(reader.readLine());

        System.out.println("Enter the path to the card pack file:");
        String packPath = reader.readLine();

        List<Integer> cardValues = readPack(packPath, numPlayers);
        if (cardValues == null) {
            System.out.println("Invalid pack file.");
            return;
        }

        List<Player> players = new ArrayList<>();
        List<CardDeck> decks = new ArrayList<>();

        // Create decks and players
        for (int i = 0; i < numPlayers; i++) {
            decks.add(new CardDeck());
        }
        for (int i = 0; i < numPlayers; i++) {
            CardDeck leftDeck = decks.get(i);
            CardDeck rightDeck = decks.get((i + 1) % numPlayers);
            players.add(new Player(i + 1, i + 1, leftDeck, rightDeck));
        }

        // Distribute cards to players and decks
        distributeCards(players, decks, cardValues);

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

        // Output final deck states
        for (int i = 0; i < decks.size(); i++) {
            System.out.println("deck" + (i + 1) + " contents: " + decks.get(i).getDeckContents());
        }
    }

    private static List<Integer> readPack(String filePath, int numPlayers) throws IOException {
        File file = new File(filePath);
        BufferedReader reader = new BufferedReader(new FileReader(file));

        List<Integer> cardValues = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            cardValues.add(Integer.parseInt(line));
        }
        reader.close();

        if (cardValues.size() != 8 * numPlayers) {
            return null;
        }
        return cardValues;
    }

    private static void distributeCards(List<Player> players, List<CardDeck> decks, List<Integer> cardValues) {
        int numPlayers = players.size();
        int cardIndex = 0;

        // Distribute cards to players
        for (int i = 0; i < 4 * numPlayers; i++) {
            players.get(i % numPlayers).receiveCard(new Card(cardValues.get(cardIndex++)));
        }

        // Distribute cards to decks
        for (int i = 0; i < cardValues.size() - (4 * numPlayers); i++) {
            decks.get(i % numPlayers).addCard(new Card(cardValues.get(cardIndex++)));
        }
    }
}
