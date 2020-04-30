package sql;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.Vector;

import Friends.Friend;
import Stats.statsMethods;
import bracket.Bracket;
import bracket.BracketOverview;
import bracket.User;
import bracket.UserToStats;

public class JDBCBracketStuff {
	
	public static Connection conn;
	private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";

    private static final String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER;
    private static SecureRandom random = new SecureRandom();
    
    public static final String CONNECTION_STRING = "jdbc:mysql://localhost/SportsWebsite?user=root&password=root";
    
    public static void initConnection() {
		if (conn != null) {
			System.out.println("[WARN] Connection has already been established");
		} else {
			try {
				System.out.println("connection established");
				Class.forName("com.mysql.cj.jdbc.Driver");
				conn = DriverManager.getConnection(CONNECTION_STRING);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static int createStats(int bracketRound) {
		if (conn == null) {
			JDBCBracketStuff.initConnection();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		int statsID = -1;
		try {
			ps = conn.prepareStatement("INSERT INTO Stats (bracketRound) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, Integer.toString(bracketRound));
			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			
			if (rs.next()) {
				statsID = rs.getInt(1);
				System.out.printf("pk of stats is %d\n", statsID);
			} else {
				System.out.println("AAAHHHHH");
			}
		} catch (SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
			return -1;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		return statsID;
	}
	
	public static String generateRandomString(int length) {
		
        if (length < 1) throw new IllegalArgumentException();

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {

			// 0-62 (exclusive), random returns 0-61
            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);

            // debug
            //System.out.format("%d\t:\t%c%n", rndCharAt, rndChar);

            sb.append(rndChar);

        }

        return sb.toString();

    }
	
	private static String generateBracketCode() {
		if (conn == null) {
			JDBCBracketStuff.initConnection();
		}
		boolean success = false;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String randomS = null;
		while (!success) {
			randomS = generateRandomString(20);
			try {
			ps = conn.prepareStatement("SELECT bracketID FROM Bracket WHERE bracketCode=?");
			ps.setString(1, randomS);
			rs = ps.executeQuery();
			if (rs.next()) {
				System.out.printf("repeated code %s found in bracket id %s\n", randomS, rs.getString("bracketID"));
			} else {
				System.out.printf("Unique code %s found!\n", randomS);
				success = true;
			}
			System.out.println("------");
			} catch (SQLException sqle) {
				System.out.println ("SQLException: " + sqle.getMessage());
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
					if (ps != null) {
						ps.close();
					}
				} catch (SQLException sqle) {
					System.out.println("sqle: " + sqle.getMessage());
				}
			}
		}
		return randomS;
	}
	
	private static int addToUserToStats(int userID, int round) {
		if (conn == null) {
			JDBCBracketStuff.initConnection();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		int userToStatsID = -1;
		int statsID = createStats(round);
		if( statsID == -1) {
			return -1;
		}
		try {
			ps = conn.prepareStatement("INSERT INTO UserToGameStats (userID, statsID) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, Integer.toString(userID));
			ps.setString(2, Integer.toString(statsID));
			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			
			if (rs.next()) {
				userToStatsID = rs.getInt(1);
				System.out.printf("pk of userToStatsID is %d\n", userToStatsID);
			} else {
				System.out.println("AAAHHHHH");
			} 
		} catch (SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
			userToStatsID = -1;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		return userToStatsID;
	}
	
	private static boolean userExists(int userID) {
		if (conn == null) {
			JDBCBracketStuff.initConnection();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean success = false;
		try {
			ps = conn.prepareStatement("SELECT userID, username FROM Users WHERE userID=?");
			ps.setString(1, Integer.toString(userID));
			rs = ps.executeQuery();
			if (rs.next()) {
				System.out.printf("Valid user found of name %s and userID %s\n", rs.getString("username"), rs.getString("userID"));
				success = true;
			} else {
				System.out.printf("User with id %s not found!\n", userID);
			}
			System.out.println("------");
		} catch (SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		return success;
	}
	
	public static int blankStatsRow() {
		if (JDBCBracketStuff.conn == null) {
			JDBCBracketStuff.initConnection();
		}
		int statsID = 0;

		Statement st = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		// should count wins, losses, and upcoming!
		try {
			// get all statIDs corresponding to a user

			ps = JDBCBracketStuff.conn.prepareStatement("INSERT INTO Stats(won, bracketRound) VALUES (?,?)",
					Statement.RETURN_GENERATED_KEYS);

			ps.setNull(1, Types.NULL);
			ps.setInt(2, 1);

			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			if (rs.next()) {
				statsID = rs.getInt(1);
			}
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (st != null) {
					st.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		return statsID;
	}
	
	public static BracketIdCodePair createBracket(int userID, String bracketName, int gameType) {
		if (conn == null) {
			JDBCBracketStuff.initConnection();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		BracketIdCodePair b = null;
		if (!userExists(userID)) {
			return null;
		}
		try {
			
			int userToStatsID = addToUserToStats(userID, 1);

			String randomS = generateBracketCode();

			ps = conn.prepareStatement("INSERT INTO Bracket (bracketName, bracketCode, gameType, bracketS8) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, bracketName);
			ps.setString(2, randomS);
			ps.setString(3, Integer.toString(gameType));
			ps.setString(4, Integer.toString(userToStatsID));
			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			int bracketID = -1;
			if (rs.next()) {
				bracketID = rs.getInt(1);
				System.out.printf("pk of bracket is %d\n", bracketID);
				connectUserToBracket(userID, bracketID);
			}
			
			b = new BracketIdCodePair(bracketID, randomS);
			
		} catch (SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		return b;
	}
	
	private static int addUserToBracket(int userID, int bracketID) {
		if (conn == null) {
			JDBCBracketStuff.initConnection();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		int success = -1;
		if (!userInBracket(userID, bracketID)) {
			try {
				// rs = st.executeQuery("SELECT * from Student where fname='" + name + "'");
				ps = conn.prepareStatement("SELECT bracketS8, bracketS9, bracketS10, bracketS11, bracketS12, bracketS13, bracketS14, bracketS15 FROM Bracket WHERE bracketID=?");
				ps.setString(1, Integer.toString(bracketID));
				rs = ps.executeQuery();
				if (rs.next()) {
					for (int i = 1; i <= 8; i++) {
						String temp = rs.getString(i);
						if (temp == null) {
							System.out.printf("empty spot found at slot %d\n", i+7);
							int userToStatsID = addToUserToStats(userID, 1);
							if (userToStatsID != -1) {
								String statement = String.format("UPDATE Bracket SET bracketS%d=%d WHERE bracketID=?", i+7, userToStatsID);
								rs.close();
								ps.close();
								System.out.printf("executing statement %s inside addUserToBracket method\n", statement);
								ps = conn.prepareStatement(statement);
								ps.setString(1, Integer.toString(bracketID));
								ps.executeUpdate();
								connectUserToBracket(userID, bracketID);
								success = 1;
								break;
							}
						}
					}
					
				} else {
					System.out.printf("problem");
				}
				System.out.println("------");
			} catch (SQLException sqle) {
				System.out.println ("SQLException: " + sqle.getMessage());
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
					if (ps != null) {
						ps.close();
					}
				} catch (SQLException sqle) {
					System.out.println("sqle: " + sqle.getMessage());
				}
			}
			return success;
		} else {
			return -2;
		}
	}
	
	private static void connectUserToBracket(int userID, int bracketID) {
		if (conn == null) {
			JDBCBracketStuff.initConnection();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
		ps = conn.prepareStatement("INSERT INTO UserToBracket (userID, bracketID) VALUES (?, ?)");
		ps.setString(1, Integer.toString(userID));
		ps.setString(2, Integer.toString(bracketID));
		ps.executeUpdate();
		} catch (SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
	}
	
	/**
	 * @param userID the userID of the user add
	 * @param bracketCode the code the bracket to add
	 * @return adds a user to a bracket given a bracket code. returns -1 if some unknown error
	 * occurs and returns -2 if the user is already in the bracket and returns -3 if the user does not
	 * exist. otherwise returns the bracketID of the brackets the user was added to
	 */
	public static int addUserToBracket(int userID, String bracketCode) {
		if (conn == null) {
			JDBCBracketStuff.initConnection();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		int bracketID = -1;
		if (!userExists(userID)) {
			return -3;
		}
		try {
			// rs = st.executeQuery("SELECT * from Student where fname='" + name + "'");
			ps = conn.prepareStatement("SELECT bracketID FROM Bracket WHERE bracketCode=?");
			ps.setString(1, bracketCode);
			rs = ps.executeQuery();
			if (rs.next()) {
				bracketID = Integer.parseInt(rs.getString("bracketID"));
				System.out.printf("Valid bracket with ID %d found from code %s\n", bracketID, bracketCode);
				int connect = addUserToBracket(userID, bracketID);
				if (connect < 0) {
					bracketID = connect;
				}
			} else {
				System.out.printf("no bracket with code %s was found\n", bracketCode);
			}
			System.out.println("------");
			rs.close();
			ps.close();

			
			
		} catch (SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		return bracketID;
	}
	
	/**
	 * @param statsID
	 * @return -1 if the game has not been played yet, otherwise 1 represents win and 0 represents 
	 * loss
	 */
	private static int didWin(int statsID) {
		if (conn == null) {
			JDBCBracketStuff.initConnection();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		int win = -1;
		try {
			ps = conn.prepareStatement("SELECT won FROM Stats WHERE statsID=?");
			ps.setString(1, Integer.toString(statsID));
			rs = ps.executeQuery();
			if (rs.next()) {
				String won = rs.getString(1);
				if (won != null)
					win = (won.equalsIgnoreCase("1")) ? 1 : 0;
			} 
		} catch (SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		return win;
	}
	
	private static User getUser(int userID) {
		if (conn == null) {
			JDBCBracketStuff.initConnection();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		User u = null;
		try {
			ps = conn.prepareStatement("SELECT username FROM Users WHERE userID=?");
			ps.setString(1, Integer.toString(userID));
			rs = ps.executeQuery();
			if (rs.next()) {
				u = new User(userID, rs.getString(1));				
			} 
		} catch (SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		return u;
	}
	
	public static int getEloOfUser(int userID) {
		if (conn == null) {
			JDBCBracketStuff.initConnection();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		int elo = -1;
		try {
			ps = conn.prepareStatement("SELECT elo FROM Users WHERE userID=?");
			ps.setString(1, Integer.toString(userID));
			rs = ps.executeQuery();
			if (rs.next()) {
				String Elo = rs.getString(1);
				if (Elo != null) {
					elo = Integer.parseInt(Elo);
				}
			} 
		} catch (SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		return elo;
	}
	
	private static UserToStats getUserToStats(String userToStatsID) {
		if (conn == null) {
			JDBCBracketStuff.initConnection();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		UserToStats u = null;
		try {
			ps = conn.prepareStatement("SELECT userID, statsID FROM UserToGameStats WHERE userToGameStatsID=?");
			ps.setString(1, userToStatsID);
			rs = ps.executeQuery();
			if (rs.next()) {
				int userID = Integer.parseInt(rs.getString(1));
				int statsID = Integer.parseInt(rs.getString(2));
				rs.close();
				ps.close();
				u = new UserToStats(getUser(userID), didWin(statsID));
			} 
		} catch (SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		return u;
	}
	
	public static Bracket getBracket(int bracketID) {
		if (conn == null) {
			JDBCBracketStuff.initConnection();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<UserToStats> lst = new ArrayList<>();
		try {
			StringBuilder sb = new StringBuilder("bracketS1");
			for (int i = 2; i <= 15; i++) {
				sb.append(String.format(", bracketS%d", i));
			}
			String query = String.format("SELECT %s FROM Bracket WHERE bracketID=?", sb.toString());
			System.out.printf("Executing Query: %s\n", query);
			ps = conn.prepareStatement(query);
			ps.setString(1, Integer.toString(bracketID));
			rs = ps.executeQuery();
			if (rs.next()) {
				for (int i = 0; i < 15; i++) {
					lst.add(getUserToStats(rs.getString(i+1)));
				}
			} 
		} catch (SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		return new Bracket(lst);
	}
	
	public static boolean userInBracket(int userID, int bracketID) {
		if (conn == null) {
			JDBCBracketStuff.initConnection();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean isIn = false;
		try {
		ps = conn.prepareStatement("SELECT userToBracketID FROM UserToBracket WHERE userID=? AND bracketID=?");
		ps.setString(1, Integer.toString(userID));
		ps.setString(2, Integer.toString(bracketID));
		rs = ps.executeQuery();
		if (rs.next()) {
			isIn = true;
		}
		} catch (SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		return isIn;
	}
	
	private static int userIDofUserToStats(int userToStatsID) {
		if (conn == null) {
			JDBCBracketStuff.initConnection();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		int userID = -1;
		try {
			ps = conn.prepareStatement("SELECT userID FROM UserToGameStats WHERE userToGameStatsID=?");
			ps.setString(1, Integer.toString(userToStatsID));
			rs = ps.executeQuery();
			if (rs.next()) {
				userID = Integer.parseInt(rs.getString(1));
			} 
		} catch (SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		return userID;
	}
	
	private static int getRound(int statsID) {
		if (conn == null) {
			JDBCBracketStuff.initConnection();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		int bracketRound = -1;
		try {
			ps = conn.prepareStatement("SELECT bracketRound FROM Stats WHERE statsID=?");
			ps.setString(1, Integer.toString(statsID));
			rs = ps.executeQuery();
			if (rs.next()) {
				bracketRound = Integer.parseInt(rs.getString("bracketRound"));
			} 
		} catch (SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		return bracketRound;
	}
	
	/**
	 * 
	 * @param slot1 the slot number of one player
	 * @param slot2 the slot number of the opponent
	 * @param slot1Won whether the player in slot1 won or lost
	 * @param bracketID the bracket ID in which this update is happening
	 * @return returns true if the update success, and false if it does not
	 */
	public static boolean update(int slot1, int slot2, boolean slot1Won, int bracketID) {
		if (conn == null) {
			JDBCBracketStuff.initConnection();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean success = false;
		try {
			if (Math.abs(slot1-slot2) > 1 || Math.min(slot1, slot2) % 2 == 1 || Math.max(slot1, slot2) % 2 == 0) {
				success = false;
			} else {
				int newSlot = Math.min(slot1, slot2)/2;
				// rs = st.executeQuery("SELECT * from Student where fname='" + name + "'");
				String query = String.format("SELECT bracketS%d, bracketS%d FROM Bracket WHERE bracketID=?", slot1, slot2);
				System.out.printf("executing query %s\n", query);
				ps = conn.prepareStatement(query);
				ps.setString(1, Integer.toString(bracketID));
				
				rs = ps.executeQuery();
				int winnerID = -1; 
				int uID1 = -1;
				int uID2 = -1;
				if (rs.next()) {
					uID1 = Integer.parseInt(rs.getString(1));
					uID2 = Integer.parseInt(rs.getString(2));
					System.out.printf("Valid usersToStats found with ID %s and %s\n", uID1, uID2);
					winnerID = (slot1Won) ? uID1 : uID2;
				} else {
					System.out.printf("problem");
				}
				System.out.println("------");
				rs.close();
				ps.close();
				
				int round = (newSlot == 1) ? 3 : 2;
				int userID = userIDofUserToStats(winnerID);
				if (userID != -1) {
					int userToStatsID = addToUserToStats(userID, round);
					query = String.format("UPDATE Bracket Set bracketS%d=? WHERE bracketID=?", newSlot);
					System.out.printf("executing query %s\n", query);
					ps = conn.prepareStatement(query);
					ps.setString(1, Integer.toString(userToStatsID));
					ps.setString(2, Integer.toString(bracketID));
					ps.executeUpdate();
					System.out.println("Running the stats Update");
					if (userExists(uID1) && userExists(uID2)) {
						statsMethods s = new statsMethods(uID1, slot1Won, uID2, round);
						s.start();
						success = true;
					}
					
				}				
				
			}
			
		} catch (SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		return success;
	}
	
	public static boolean isHost(int bracketID, int userID) {
		if (conn == null) {
			JDBCBracketStuff.initConnection();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean isHost = false;
		try {
			ps = conn.prepareStatement("SELECT bracketS8 FROM Bracket WHERE bracketID=?");
			ps.setString(1, Integer.toString(bracketID));
			rs = ps.executeQuery();
			if (rs.next()) {
				isHost = (userID == userIDofUserToStats(Integer.parseInt(rs.getString(1))));				
			}
		} catch (SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		return isHost;
	}
	
	private static BracketOverview getBracketOverview(int bracketID) {
		if (conn == null) {
			JDBCBracketStuff.initConnection();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		BracketOverview b = null;
		try {
			StringBuilder sb = new StringBuilder("bracketS1");
			for (int i = 2; i <= 15; i++) {
				sb.append(String.format(", bracketS%d", i));
			}
			String query = String.format("SELECT bracketName, gameType, %s FROM Bracket WHERE bracketID=?", sb.toString());
			System.out.printf("Executing query: %s\n", query);
			ps = conn.prepareStatement(query);
			System.out.println("BracketID: " + bracketID);
			ps.setString(1, Integer.toString(bracketID));
			rs = ps.executeQuery();
			if (rs.next()) {
				String bracketName = rs.getString(1);
				int gameType = Integer.parseInt(rs.getString(2));
				String hostName = getUserToStats(rs.getString(10)).getUser().getName();
				int vacantSpots = 0;
				boolean done = rs.getString(3) != null;
				for (int i = 8; i <= 15; i++) {
					String newString = rs.getString(i+2);
					if (rs.wasNull()) {//.getString(i+2) == null) {
						vacantSpots++;
					}
				}
				if (vacantSpots > 0) {
					b = new BracketOverview(bracketName, bracketID, vacantSpots, hostName);
				} else if (done) {
					b = new BracketOverview(bracketName, bracketID, getUser(Integer.parseInt(rs.getString(3))).getName(), hostName);
				} else {
					b = new BracketOverview(bracketName, bracketID, hostName);
				}
				
			} 
		} catch (SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		return b;
	}
	
	public static List<Stack<BracketOverview>> getUserOverview(int userID) {
		if (conn == null) {
			JDBCBracketStuff.initConnection();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Stack<BracketOverview>> tournaments = new LinkedList<>();
		tournaments.add(new Stack<BracketOverview>());
		tournaments.add(new Stack<BracketOverview>());
		tournaments.add(new Stack<BracketOverview>());		
		try {
			ps = conn.prepareStatement("SELECT bracketID FROM UserToBracket WHERE userID=?");
			ps.setString(1, Integer.toString(userID));
			rs = ps.executeQuery();
			while (rs.next()) {
				int bracketID = Integer.parseInt(rs.getString(1));
				BracketOverview b = getBracketOverview(bracketID);
				if (b != null) {
					System.out.println(b);
					tournaments.get(b.getType()).push(b);
				}
				else
				{
					System.out.println("Bracket was null");
				}
			} 
		} catch (SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		System.out.println("Tournaments: " + tournaments.get(0).size());
		return tournaments;
	}
	
	private static Friend getFriend(int friendID, int status) {
		if (conn == null) {
			JDBCBracketStuff.initConnection();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		Friend u = null;
		try {
			ps = conn.prepareStatement("SELECT username, elo FROM Users WHERE userID=?");
			ps.setString(1, Integer.toString(friendID));
			rs = ps.executeQuery();
			if (rs.next()) {
				u = new Friend(friendID, rs.getString(1), rs.getInt(2), status);		
			} 
		} catch (SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		return u;
	}
	
	public static Vector<Queue<Friend>> getFriends(int userID) {
		if (conn == null) {
			JDBCBracketStuff.initConnection();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		Vector<Queue<Friend>> friends = new Vector<>();
		friends.add(new LinkedList<Friend>());
		friends.add(new LinkedList<Friend>());
		friends.add(new LinkedList<Friend>());		
		try {
			ps = conn.prepareStatement("SELECT userID2, acceptedStatus FROM friendID WHERE userID=?");
			ps.setString(1, Integer.toString(userID));
			rs = ps.executeQuery();
			while (rs.next()) {
				int userID2 = rs.getInt(1);
				int acceptedStatus = rs.getInt(2);
				Friend f = getFriend(userID2, acceptedStatus);
				if (f != null) {
					System.out.println(f);
					switch(f.getStatus()) {
					case 2:
						friends.get(2).add(f); // are friends
						break;
					case 10:
						friends.get(1).add(f); // pending friends rq
					case 3:
						friends.get(0).add(f); // received friend rq
					}
				}
				else
				{
					System.out.println("Bracket was null");
				}
			} 
		} catch (SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		return friends;
	}
	
	/**
	 * 
	 * @param uID1
	 * @param uID2
	 * @return 1 if is friend, 0 if not, -1 if fail
	 */
	public static int isFriend(int uID1, int uID2) {
		if (conn == null) {
			JDBCBracketStuff.initConnection();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		Friend u = null;
		int result = -1;
		try {
			ps = conn.prepareStatement("SELECT friendID FROM Friends WHERE userID1=? AND userID2=? AND acceptedStatus=2");
			ps.setString(1, Integer.toString(uID1));
			ps.setString(2, Integer.toString(uID2));
			rs = ps.executeQuery();
			if (rs.next()) {
				result = 1;
				System.out.println("friends");
			} else {
				System.out.println("not friends");
				result = 0;
			}
		} catch (SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		if (result == -1)
				System.out.println("checking if they are friends failed");
		return result;
	}
	
	private static boolean validateFriendRequest(int uid1, int uid2) {
		if (conn == null) {
			JDBCBracketStuff.initConnection();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean success = false;
		try {
			ps = conn.prepareStatement("SELECT friendID FROM Friends WHERE (userID1=? AND userID2=?) OR (userID1=? AND userID2=?)");
			ps.setString(1, Integer.toString(uid1));
			ps.setString(2, Integer.toString(uid2));
			ps.setString(3, Integer.toString(uid2));
			ps.setString(4, Integer.toString(uid1));
			rs = ps.executeQuery();
			while (rs.next()) {
				int friendID = rs.getInt(1);
				ps = conn.prepareStatement("UPDATE Friends Set acceptedStatus=2 WHERE friendID=?");
				ps.setString(1, Integer.toString(friendID));
				ps.executeUpdate();
			}
			success = true;
		} catch (SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		return success;
	}
	
	private static int getUserID(String username) {
		if (conn == null) {
			JDBCBracketStuff.initConnection();
		}
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		int userID= -1;
		try {
			System.out.println("adding stats");
			ps = conn.prepareStatement("SELECT userID FROM Users WHERE userID=?");
			ps.setString(1, username);
			rs = ps.executeQuery();
			if (rs.next()) {
				userID = rs.getInt(1);
				if (rs.wasNull()) {
					userID = -1;
				}
			} 
		} catch (SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		return userID;
	}
	
	/**
	 * 
	 * @param requestID
	 * @param receiveID
	 * @return -1 is failed, 0 is user does not exist, 1 is friend already exists, 2 is waiting on receive, 3 is success
	 */
	public static int addFriend(int requestID, String friendName) {
		if (conn == null) {
			JDBCBracketStuff.initConnection();
		}
		int receiveID = getUserID(friendName);
		PreparedStatement ps = null;
		ResultSet rs = null;
		if (receiveID == -1) {
			return 0;
		} else if (isFriend(requestID, receiveID) == 1) {
			return 1;
		}
		int result = -1;
		try {
			System.out.println("adding stats");
			ps = conn.prepareStatement("SELECT friendID, acceptedStatus FROM Friends WHERE userID1=? AND userID2=?");
			ps.setString(1, Integer.toString(requestID));
			ps.setString(2, Integer.toString(receiveID));
			rs = ps.executeQuery();
			if (rs.next()) {
				System.out.println("something found");
				int friendID = rs.getInt(1);
				int acceptedStatus = rs.getInt(2);
				if (acceptedStatus == 10) {
					result = 2;
				} else if (acceptedStatus == 3) {
					validateFriendRequest(requestID, receiveID);
					result = 3;
				}
			} else {
				ps.close();
				ps = conn.prepareStatement("INSERT INTO Friends (userID1, userID2, acceptedStatus) VALUES (?,?, 10) , (?,?,3)");
				ps.setString(1, Integer.toString(requestID));
				ps.setString(2, Integer.toString(receiveID));
				ps.setString(3, Integer.toString(receiveID));
				ps.setString(4, Integer.toString(requestID));
				ps.executeUpdate();
				System.out.println("added firend for first time");
				result = 2;
			}
		} catch (SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
		return result;
	}
	
	public static void main(String[] args) {
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/SportsWebsite?user=root&password=root");
			BracketIdCodePair b = JDBCBracketStuff.createBracket(1, "godgamers", 1);
			System.out.println("bracketCreated");
			int godGamerID = 1;
			int bobTheIdiotID = 2;
			int result = JDBCBracketStuff.addUserToBracket(godGamerID, b.getBracketCode());
			System.out.printf("result from readding godGamer was %d\n", result);
			result = JDBCBracketStuff.addUserToBracket(bobTheIdiotID, b.getBracketCode());
			System.out.printf("result from adding bob was %d\n", result);
			boolean result2 = JDBCBracketStuff.update(8, 9, true, b.getBracketID());
			System.out.printf("updated with result %b\n", result2);
			System.out.println("bracket data:");
			System.out.println(getBracket(b.getBracketID()));
			System.out.println("Brakcet Overview");
			System.out.println(getBracketOverview(b.getBracketID()));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
