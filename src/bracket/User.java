package bracket;

import java.io.Serializable;

public class User implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6931930006220466624L;
	private int userID;
	private String name;
	
	public User (int userID, String name) {
		this.userID = userID;
		this.name = name;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return String.format("userID: %d, name: %s", userID, name);
	}
}
