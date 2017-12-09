package server;

import exceptions.FullPartyException;

import java.io.IOException;

public class Party implements Runnable {

    private final int maxUsers;
    private ConnectedUser[] users;
    private int freeSlots;
    private String name;

    public Party(int maxUsers, String name) {
        this.maxUsers = maxUsers;
        this.name = name;

        users = new ConnectedUser[maxUsers];
        freeSlots = maxUsers;
    }

    public synchronized void addUser(ConnectedUser user) throws FullPartyException {
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

    public synchronized void removeUser(ConnectedUser user) {
        for (int i = 0; i < maxUsers; i++) {
            if (users[i].equals(user)) {
                users[i] = null;
                freeSlots++;
                break;
            }
        }
    }

    public int getMaxUsers() {
        return maxUsers;
    }

    public int getFreeSlots() {
        return freeSlots;
    }

    public String getName() {
        return name;
    }

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
