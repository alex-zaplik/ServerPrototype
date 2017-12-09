package server;

import exceptions.CreatingPartyFailedException;
import exceptions.FullPartyException;
import message.builder.IMessageBuilder;
import message.builder.JSONMessageBuilder;
import message.parser.IMessageParser;
import message.parser.JSONMessageParser;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Server {

    private ServerSocket sSocket;
    private List<Party> parties;

    private IMessageParser parser;
    private IMessageBuilder builder;

    private volatile int lastID = 0;

    private Runnable emptySocket = new Runnable() {
        @Override
        public void run() {
            if (sSocket != null && parties != null) {
                while (true) {
                    ConnectedUser user;
                    try {
                        user = new ConnectedUser(sSocket.accept(), getID());
                        setUpConnection(user);
//                        parties.addUser(user);
//                        user.getOut().println("Added to parties");
//                        System.out.println("Connected with client!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    } catch (FullPartyException e) {
//                        user.getOut().println("Party was full");
//                    }
                }
            }
        }
    };

    private synchronized int getID() {
        return lastID++;
    }

    private void setUpConnection(ConnectedUser user) throws IOException {
        sendPartyList(user);
        String response = user.getIn().readLine();

        if (response != null) {
            Map<String, Object> responseMap = parser.parse(response);
            int action = (int) responseMap.get("i_action");

            switch (action) {
                case 0:
                    try {
                        Party p = createParty(responseMap);

                        if (p == null)
                            throw new CreatingPartyFailedException();

                        p.addUser(user);
                    } catch (CreatingPartyFailedException | FullPartyException e) {
                        // TODO: Try again
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    try {
                        joinParty(user, responseMap);
                    } catch (FullPartyException e) {
                        // TODO: Try again
                        e.printStackTrace();
                    }
                    break;
                default:
                    throw new IOException("Unsupported action");
            }
        }
    }

    private void sendPartyList(ConnectedUser user) {
        if (parties.size() == 0) {
            user.getOut().println(
                    builder.put("s_msg", "No parties available")
                    .get()
            );
        }

        for (int i = parties.size() - 1; i >= 0; i--) {

            user.getOut().println(
                    builder.put("i_size", parties.size())
                    .put("s_name", parties.get(i).getName())
                    .put("i_max", parties.get(i).getMaxUsers())
                    .put("i_left", parties.get(i).getFreeSlots())
                    .get()
            );
        }
    }

    private synchronized Party createParty(Map<String, Object> settings) throws CreatingPartyFailedException, IOException {
        Party party;
        String name = (String) settings.get("s_name");

        int max = (int) settings.get("i_max");

        for (Party p : parties)
            if (p.getName().equals(name))
                throw new CreatingPartyFailedException();

        party = new Party(max, name);
        parties.add(party);
        new Thread(party).start();

        return party;
    }

    private synchronized void joinParty(ConnectedUser user, Map<String, Object> settings) throws FullPartyException, IOException {
        Party party = null;
        String name = (String) settings.get("s_name");

        for (Party p : parties)
            if (p.getName().equals(name))
                party = p;

        if (party != null) {
            party.addUser(user);
        } else {
            throw new FullPartyException();
        }
    }

    private void init() {
        parser = new JSONMessageParser();
        builder = new JSONMessageBuilder();

        parties = new ArrayList<>();

        // TODO: Remove testing parties
        Party p1 = new Party(10,"Test1");
        parties.add(p1);
        new Thread(p1).start();
        Party p2 = new Party(15,"Test2");
        parties.add(p2);
        new Thread(p2).start();

        try {
            sSocket = new ServerSocket(4444);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Wait for users
        new Thread(emptySocket).start();
    }

    public static void serverMain(String[] args) {
        System.out.println("Starting the server...");

        Server server = new Server();
        server.init();
    }
}
