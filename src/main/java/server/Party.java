package server;

import exceptions.FullPartyException;

public class Party implements Runnable {

    private final int maxUsers;
    private ConnectedUser[] users;
    private int freeSlots;

    public Party(int maxUsers) {
        this.maxUsers = maxUsers;

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

    @Override
    public void run() {
        // TODO: Do stuff here
    }
}
