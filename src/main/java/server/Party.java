package server;

import exceptions.FullPartyException;

import java.io.IOException;

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

    /**
     * Class constructor
     *
     * @param maxUsers  Maximum number of users connected to the Party
     * @param name      Name of the Party
     */
    Party(int maxUsers, String name) {
        this.maxUsers = maxUsers;
        this.name = name;

        users = new ConnectedUser[maxUsers];
        freeSlots = maxUsers;
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
        for (int i = 0; i < maxUsers; i++) {
            if (users[i].equals(user)) {
                users[i] = null;
                freeSlots++;
                break;
            }
        }
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

    /**
     * Simple chat implementation
     */
    @Override
    public void run() {
        // TODO: This is just a ping pong conversation
        while (true) {
            for (ConnectedUser user : users) {
                if (user == null)
                    continue;

                try {
                    if (!user.getIn().ready())
                        continue;

                    String msg = user.getIn().readLine();
                    if (msg != null)
                        for (ConnectedUser u : users) {
                            if (u == null)
                                continue;

                            u.getOut().println(name + "|" + user.getID() + ": " + msg);
                        }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
