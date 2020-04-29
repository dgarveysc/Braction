package Friends;

public class Friend {
	
	private int id;
	private String name;
	private int elo;
	private int status;
	
	public Friend(int id, String name, int elo, int status) {
		this.id = id;
		this.name = name;
		this.elo = elo;
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getElo() {
		return elo;
	}
	
	public int getStatus() {
		return status;
	}
	
	public String toString() {
		return String.format("Friend with id %d anem %s else %d and status %d", id, name, elo, status);
	}
}
