package bracket;

public class BracketOverview {
	
	private int type;
	private String name;
	private int vacantSpots;
	private String winnerName;
	private int id;
	
	/**
	 * Active Tournament constructor
	 * @param name
	 */
	public BracketOverview(String name, int id) {
		this.name = name;
		this.id = id;
		this.vacantSpots = 0;
		this.winnerName = null;
		this.type = 1;
	}
	
	/**
	 * Pending Tournament constructor
	 * @param name
	 * @param vacantSpots
	 * @param id
	 */
	public BracketOverview(String name, int vacantSpots, int id) {
		this.id = id;
		this.name = name;
		this.vacantSpots = vacantSpots;
		this.winnerName = null;
		this.type = 0;
	}
	
	public BracketOverview(String name, int id, String winnerName) {
		this.id = id;
		this.name = name;
		this.winnerName = winnerName;
		this.vacantSpots = 0;
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
}
