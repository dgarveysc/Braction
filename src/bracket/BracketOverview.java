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
	
	/**
	 * Active Tournament constructor
	 * @param name
	 */
	public BracketOverview(String name, int id, String hostName) {
		this.name = name;
		this.id = id;
		this.vacantSpots = 0;
		this.winnerName = null;
		this.hostName = hostName;
		this.type = 1;
	}
	
	/**
	 * Pending Tournament constructor
	 * @param name
	 * @param vacantSpots
	 * @param id
	 */
	public BracketOverview(String name, int vacantSpots, int id, String hostName) {
		this.id = id;
		this.name = name;
		this.vacantSpots = vacantSpots;
		this.winnerName = null;
		this.hostName = hostName;
		this.type = 0;
	}
	
	public BracketOverview(String name, int id, String winnerName, String hostName) {
		this.id = id;
		this.name = name;
		this.winnerName = winnerName;
		this.vacantSpots = 0;
		this.hostName = hostName;
		this.type = 2;
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
	
	public String toString() {
		return String.format(
				"Bracket with name %s with id %d hosted by %s has %d vacant spots and winner %s\n", 
				name, id, hostName, vacantSpots, winnerName);
	}
}
