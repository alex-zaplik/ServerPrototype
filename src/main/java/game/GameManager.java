package game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameManager {

	private Map<Integer, Integer> userToPlayer = new HashMap<>();

	private TestModel model;

	public GameManager() {

	}

	public void init(List<Integer> userIDs) {
		// TODO: model.getPlayers().length != userIDs.size()

		model = new TestModel(userIDs.size());

		for (int p = 0; p < model.getPlayers().length; p++) {
			userToPlayer.put(userIDs.get(p), model.getPlayers()[p].getID());
		}
	}

	public boolean makeMove(int userID, int fx, int fy, int tx, int ty) {
		System.out.println("GameManager: " + userID + " is making a move");
		return model.makeMove(userToPlayer.get(userID), fx, fy, tx, ty);
	}

	public int getPlayerCount() {
		return model.getPlayers().length;
	}
}
