package fhtw.quattuor.server;

import fhtw.quattuor.common.model.Player;
import fhtw.quattuor.common.serialization.PlayerSerializer;

import java.io.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerPlayerService {
    private final List<Player> players = new CopyOnWriteArrayList<>();
    private PlayerSerializer serializer = new PlayerSerializer();

    public void registerOrUpdate(Player player) {
        if (player == null || player.getUsername() == null || player.getUsername().isBlank()) {
            return;
        }

        // Replace player in list if he exits
        for (Player p : players) {
            if (p.getUsername().equals(player.getUsername())) {
                players.set(players.indexOf(p), player);
                return;
            }
        }

        players.add(player);
    }

    public void safePlayersToDisk() {
        String jsonPlayers = serializer.serializePlayers(players);
        File jsonFile = new File("src/main/resources/fhtw/quattuor/server/players.json");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFile))) {
            writer.write(jsonPlayers);
        } catch (IOException e) {
            System.err.println("Error writing players to disk: " + e.getMessage());
        }
    }

    public void loadPlayersFromDisk() {
        File jsonFile = new File("src/main/resources/fhtw/quattuor/server/players.json");
        String jsonPlayers = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(jsonFile))) {
            jsonPlayers = reader.readLine();
        } catch (IOException e) {
            System.err.println("Error reading players from disk: " + e.getMessage());
            return;
        }

        List<Player> loadedPlayers = serializer.deserializePlayers(jsonPlayers);
        this.players.clear();
        this.players.addAll(loadedPlayers);
    }

    public List<Player> getPlayers() {
        return List.copyOf(players);
    }

    public void printPlayerUsernames() {
        System.out.println("Current saved usernames:");
        for (Player p : players) {
            System.out.println(p.getUsername());
        }
        System.out.println("-------------------");
    }

    public boolean existsUsername(String username) {
        if (username == null) return false;
        for (Player p : players) {
            if (username.equals(p.getUsername()))
                return true;
        }
        return false;
    }

    public Player findByUsername(String username) {
        if (username == null) return null;
        for (Player p : players) {
            if (username.equals(p.getUsername()))
                return p;
        }
        return null;
    }

    public Player authenticate(String username, String password) {
        Player p = findByUsername(username);
        if (p == null) {
            return null;
        }
        if (p.getPassword() == null) {
            return null;
        }
        if (p.getPassword().equals(password)) {
            return p;
        }
        return null;
    }


    public boolean register(String username, String password) {
        if (username == null || username.isBlank())
            return false;
        if (password == null || password.isBlank())
            return false;
        if (existsUsername(username))
            return false;

        players.add(new Player(username, password));
        return true;
    }
}
