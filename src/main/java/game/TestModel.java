package game;

public class TestModel {

	private Player[] players;

	public TestModel(int playerCount) {
		players = new Player[playerCount];

		for (int i = 0; i < playerCount; i++) {
			players[i] = new Player();
		}
	}

	public boolean makeMove(int playerID, int fx, int fy, int tx, int ty) {
		System.out.println("TestModel: " + playerID + " is making a move");
		return true;
	}

	public Player[] getPlayers() {
		return players;
	}
}
