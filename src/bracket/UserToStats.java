package bracket;

import java.io.Serializable;

public class UserToStats implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2310733488367263368L;
	private User user;
	private int won; // -1 if they haven't played, else 1 or 0
	
	public UserToStats(User user, int won) {
		this.user = user;
		this.won = won;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int isWon() {
		return won;
	}

	public void setWon(int won) {
		this.won = won;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(user.toString());
		sb.append(" ");
		sb.append(String.format("won: %d", won));
		return sb.toString();
	}
	
}
