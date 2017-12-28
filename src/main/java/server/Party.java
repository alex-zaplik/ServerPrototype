package server;

import exceptions.FullPartyException;
import game.GameManager;
import game.GameType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The class representing a Party, that is, a group of users that can communicate/play with each other
 *
 * @author Aleksander Lasecki
 */
public class Party implements Runnable {

    /**
     * Maximum number of users connected to the Party
     */
    private final int maxUsers;
    /**
     * Array containing all connected users (null if a slot is empty)
     */
    private ConnectedUser[] users;
    /**
     * Number of free slots in the Party
     */
    private int freeSlots;
    /**
     * Name of the Party
     */
    private String name;

    private boolean joinable = false;

    private GameManager manager;

    /**
     * Class constructor
     *
     * @param maxUsers  Maximum number of users connected to the Party
     * @param name      Name of the Party
     */
    Party(int maxUsers, String name, GameType type) {
        this.maxUsers = maxUsers;
        this.name = name;

        users = new ConnectedUser[maxUsers];
        freeSlots = maxUsers;

        // TODO: Dummy switch
        switch (type) {
            case TEST_GAME:
                manager = new GameManager();
        }

        joinable = true;
    }

    /**
     * Adds the specified user to the Party
     *
     * @param user                  The user to be added
     * @throws FullPartyException   Thrown if the Party was already full
     */
    synchronized void addUser(ConnectedUser user) throws FullPartyException {
        if (freeSlots > 0) {
            for (int i = 0; i < maxUsers; i++) {
                if (users[i] == null) {
                    users[i] = user;
                    freeSlots--;

                    System.out.println("User with ID=" + user.getID() + " has joined " + name);

                    return;
                }
            }
        }

        throw new FullPartyException();
    }

    /**
     * Removes the specified user from the party
     *
     * @param user  The user to be removed
     */
    synchronized void removeUser(ConnectedUser user) {
        // TODO: There might be a bug here

        for (int i = 0; i < maxUsers; i++) {
            if (users[i].equals(user)) {
                users[i] = null;
                freeSlots++;
                break;
            }
        }
    }

    public boolean isJoinable() {
        return joinable;
    }

    /**
     * Returns the maximum number of users connected to the Party
     *
     * @return  Maximum number of users connected to the Party
     */
    int getMaxUsers() {
        return maxUsers;
    }

    /**
     * Returns the number of free slots in the Party
     *
     * @return  Number of free slots in the Party
     */
    int getFreeSlots() {
        return freeSlots;
    }

    /**
     * Returns the name of the Party
     *
     * @return  Name of the Party
     */
    String getName() {
        return name;
    }

    private void initLoop() {
        // TODO: Closing a party when the last user leaves

        boolean gameStarted = false;
        while (!gameStarted) {
            for (ConnectedUser user : users) {
                if (user == null)
                    continue;

                try {
                    Map<String, Object> response = Server.parser.parse(user.receiveMessage(-2));
                    if (response != null && response.containsKey("s_start")) {
                        gameStarted = true;
                        break;
                    }

                    if (response == null)
                        throw new IOException();

                } catch (IOException e) {
                    System.out.println("Disconnecting " + user.getID() + "...");
                    removeUser(user);

                    try {
                        user.getIn().close();
                    } catch (IOException e1) {
                        System.err.println("Unable to close client's socket...");
                    }

                    user.getOut().close();
                }
            }
        }

        joinable = false;

        List<Integer> userIDs = new ArrayList<>();
        for (int u = 0; u < users.length; u++) {
            if (users[u] == null)
                continue;

            userIDs.add(users[u].getID());
        }
        manager.init(userIDs);

        for (int u = 0; u < users.length; u++) {
            if (users[u] == null)
                continue;

            users[u].sendMessage(Server.builder.put("s_game", "Test").put("i_pcount", manager.getPlayerCount()).get());
        }

        boolean allDone = false;
        while (!allDone) {

            allDone = true;

            for (int u = 0; u < users.length; u++) {
                if (users[u] == null)
                    continue;

                try {
                    Map<String, Object> response = Server.parser.parse(users[u].receiveMessage(-2));

                    if (response == null)
                        throw new IOException();

                    users[u].setDoneSetUp(response.containsKey("b_done") && (boolean) response.get("b_done"));
                    if (!users[u].isDoneSetUp()) allDone = false;

                } catch (IOException e) {
                    endParty();

                    System.out.println("Disconnecting " + users[u].getID() + "...");
                    try {
                        users[u].getIn().close();
                    } catch (IOException e1) {
                        System.err.println("Unable to close client's socket...");
                    }
                    users[u].getOut().close();

                    removeUser(users[u]);
                }
            }
        }

        System.out.println("All done");
    }

    private void gameLoop() {
        // TODO: Check if someone won in this condition here
        while (true) {
            for (int u = 0; u < users.length; u++) {
                if (users[u] == null)
                    continue;

                try {
                    // TODO: Clear the buffer here in case someone sent something when they weren't supposed to

                    users[u].sendMessage(Server.builder
                            .put("s_move", "Your move")
                            .get());

                    Map<String, Object> response = Server.parser.parse(users[u].receiveMessage(-1));

                    if (response.containsKey("i_action")) {
                        int action = (int) response.get("i_action");

                        switch (action) {
                            case 0:
                                boolean done = manager.makeMove(users[u].getID(),
                                        (int) response.get("i_fx"), (int) response.get("i_fy"), (int) response.get("i_tx"), (int) response.get("i_ty"));

                                if (!done) u--;

                                break;
                            case 1:
                                // Skipping a move
                                break;
                        }
                    }
                } catch (IOException e) {
                    endParty();

                    System.out.println("Disconnecting " + users[u].getID() + "...");
                    removeUser(users[u]);

                    try {
                        users[u].getIn().close();
                    } catch (IOException e1) {
                        System.err.println("Unable to close client's socket...");
                    }

                    users[u].getOut().close();
                }
            }
        }
    }

    private void endParty() {
        // TODO: End game :(
        System.err.println("This method has not been implemented yet");
    }

    /**
     * Simple chat implementation
     */
    @Override
    public void run() {
        initLoop();
        gameLoop();
        endParty();
    }
}
