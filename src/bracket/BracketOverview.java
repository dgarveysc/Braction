package bracket;

import java.io.Serializable;

public class BracketOverview implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5271141676274380052L;
	private int type;
	private String name;
	private int vacantSpots;
	private String winnerName;
	private String hostName;
	private int id;
	private String gameType;
	
	/**
	 * Active Tournament constructor
	 * @param name
	 */
	public BracketOverview(String name, int id, String hostName, String gameType) {
		this.name = name;
		this.id = id;
		this.vacantSpots = 0;
		this.winnerName = null;
		this.hostName = hostName;
		this.type = 1;
		this.gameType = gameType;
	}
	
	/**
	 * Pending Tournament constructor
	 * @param name
	 * @param vacantSpots
	 * @param id
	 */
	public BracketOverview(String name, int id, int vacantSpots, String hostName, String gameType) {
		this.id = id;
		this.name = name;
		this.vacantSpots = vacantSpots;
		this.winnerName = null;
		this.hostName = hostName;
		this.type = 0;
		this.gameType = gameType;
	}
	
	public BracketOverview(String name, int id, String winnerName, String hostName, String gameType) {
		this.id = id;
		this.name = name;
		this.winnerName = winnerName;
		this.vacantSpots = 0;
		this.hostName = hostName;
		this.type = 2;
		this.gameType = gameType;
	}

	public int getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public int getVacantSpots() {
		return vacantSpots;
	}

	public String getWinnerName() {
		return winnerName;
	}

	public int getId() {
		return id;
	}
	
	public String getHostName() {
		return hostName;
	}
	
	private String getGameType() {
		return gameType;
	}
	
	public String toString() {
		return String.format(
				"Bracket with name %s with id %d hosted by %s has %d vacant spots and winner %s and type %d\n", 
				name, id, hostName, vacantSpots, winnerName, type);
	}
}
