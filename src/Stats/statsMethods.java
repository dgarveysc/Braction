package Stats;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class statsMethods extends Thread {

	public int getUserToGameStatsID1() {
		return userToGameStatsID1;
	}

	public void setUserToGameStatsID1(int userToGameStatsID1) {
		this.userToGameStatsID1 = userToGameStatsID1;
	}

	public int getUserToGameStatsID2() {
		return userToGameStatsID2;
	}

	public void setUserToGameStatsID2(int userToGameStatsID2) {
		this.userToGameStatsID2 = userToGameStatsID2;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	private int userToGameStatsID1;
	private int userToGameStatsID2;
	private boolean won;
	private int round;

	public statsMethods(int userToGameStatsID1, boolean won, int userToGameStatsID2, int round)
	{
		this.userToGameStatsID1 = userToGameStatsID1;
		this.won = won;
		this.userToGameStatsID2 = userToGameStatsID2;
		this.round = round;
	}
	
	public void run()
	{
		addOpponents(userToGameStatsID1, userToGameStatsID2);
		updateStats(userToGameStatsID1, round, won);
	}

	public static boolean addOpponents(int userToGameStatsID1, int userToGameStatsID2) {
		Connection conn = null;
		Statement st = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean success = false;

		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/SportsWebsite?user=root&password=root");
			// get all statIDs corresponding to a user

			ps = conn.prepareStatement("INSERT INTO Opponents(userToGameStatsID1, userToGameStatsID2) VALUES (?,?)",
					Statement.RETURN_GENERATED_KEYS);

			ps.setInt(1, userToGameStatsID1);
			ps.setInt(2, userToGameStatsID2);

			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			return true;

		} catch (SQLException se) {
			System.out.println(se.getMessage());
			return false;
		}
	}

	public static boolean updateStats(int userToGameStatsID, int round, boolean won) {
		// take userToGameStatsID to get userID and statsID

		Connection conn = null;
		Statement st = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		// should count wins, losses, and upcoming!

		int winUserGameStatsID = 0;
		int winUserID = 0;
		int winStatsID = 0;
		int winCurElo = 0;
		int winEloChange = 0;

		int loseUserGameStatsID = 0;
		int loseUserID = 0;
		int loseStatsID = 0;
		int loseCurElo = 0;
		int loseEloChange = 0;

		// get system date and time

		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/SportsWebsite?user=root&password=root");
			st = conn.createStatement();

			// get all statIDs corresponding to a user

			rs = st.executeQuery("SELECT * FROM UserToGameStats WHERE userToGameStatsID=" + userToGameStatsID);

			// get userID and statsID
			rs.next();

			if (won) {
				winUserGameStatsID = userToGameStatsID;
				winUserID = rs.getInt("userID");
				winStatsID = rs.getInt("statsID");
			} else {
				loseUserGameStatsID = userToGameStatsID;
				loseUserID = rs.getInt("userID");
				loseStatsID = rs.getInt("statsID");
			}

			// use opponents table to get opponent's userToGameStatsID to get their userID
			// and statsID
			rs = st.executeQuery("SELECT * FROM Opponents WHERE userToGameStatsID1 = " + userToGameStatsID
					+ " OR userToGameStatsID2 = " + userToGameStatsID);

			rs.next();

			// spaghetti im sorry, only need to compare once for opponents
			if (rs.getInt("userToGameStatsID1") == userToGameStatsID) // ID2 is the other user's
			{
				if (won) {
					loseUserGameStatsID = rs.getInt("userToGameStatsID2");
				} else {
					winUserGameStatsID = rs.getInt("userToGameStatsID2");
				}
			} else { // ID 1 is the other user's
				if (won) {
					loseUserGameStatsID = rs.getInt("userToGameStatsID1");
				} else {
					winUserGameStatsID = rs.getInt("userToGameStatsID1");
				}
			}

			// use userToGameStatsID of other opponent to get their statsID and userID

			if (won) {
				rs = st.executeQuery("SELECT * FROM UserToGameStats WHERE userToGameStatsID= " + loseUserGameStatsID);
				rs.next();
				loseUserID = rs.getInt("userID");
				loseStatsID = rs.getInt("statsID");
			} else {
				rs = st.executeQuery("SELECT * FROM UserToGameStats WHERE userToGameStatsID= " + winUserGameStatsID);
				rs.next();
				winUserID = rs.getInt("userID");
				winStatsID = rs.getInt("statsID");
			}

			/*
			 * System.out.println("winUserGameStatsID = " + winUserGameStatsID);
			 * System.out.println("loseUserGameStatsID = " + winUserGameStatsID);
			 * System.out.println("winUserID = " + winUserID);
			 * System.out.println("loseUserID = " + loseUserID);
			 * System.out.println("winStatsID = " + winStatsID);
			 * System.out.println("loseStatsID = " + loseStatsID);
			 */

			// then look up both userID's to get each player's current rank

			rs = st.executeQuery(
					"SELECT userID, points FROM Users WHERE userID = " + winUserID + " OR userID = " + loseUserID);
			while (rs.next()) {
				if (winUserID == rs.getInt("userID")) {
					winCurElo = rs.getInt("points");
				} else {
					loseCurElo = rs.getInt("points");
				}
			}

			/*
			 * System.out.println("winCurElo = " + winCurElo);
			 * System.out.println("loseCurElo = " + loseCurElo);
			 */

			// get date in string
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			String curDate = dateFormat.format(date);
			System.out.println(curDate);

			int k = 50;

			// now we have ranks so must compute ELO
			Double probForWinUser = (1.0 / (1.0 + Math.pow(10.0, (winCurElo - loseCurElo) / 400.0)));
			Double probForLoseUser = (1.0 / (1.0 + Math.pow(10.0, (loseCurElo - winCurElo) / 400.0)));
			winEloChange = (int) Math.round(k * (1 - probForWinUser));
			loseEloChange = (int) Math.round(k * (0 - probForLoseUser));

			/*
			 * System.out.println("winEloChange = " + winEloChange);
			 * System.out.println("loseEloChange = " + loseEloChange);
			 */

			// now update the statsID table
			st.executeUpdate("UPDATE Stats SET won = TRUE, bracketRound = " + round + ", xp = " + winEloChange
					+ ", gameDate = '" + curDate + "', oppRank = " + loseCurElo + " WHERE statsID = " + winStatsID);

			st.executeUpdate("UPDATE Stats SET won = FALSE, bracketRound = " + round + ", xp = " + loseEloChange
					+ ", gameDate = '" + curDate + "', oppRank = " + winCurElo + " WHERE statsID = " + loseStatsID);

			// now just update ranks of both users

			st.executeUpdate(
					"UPDATE Users " + "SET points = " + (winEloChange + winCurElo) + " WHERE UserID = " + winUserID);

			st.executeUpdate(
					"UPDATE Users " + "SET points = " + (loseEloChange + loseCurElo) + " WHERE UserID = " + loseUserID);

			return true;

		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
			return false;
		} finally {

			// close stuff I guess
		}
	}

	public static int blankStatsRow() {

		int statsID = 0;

		Connection conn = null;
		Statement st = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		// should count wins, losses, and upcoming!

		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/SportsWebsite?user=root&password=root");
			st = conn.createStatement();

			// get all statIDs corresponding to a user

			ps = conn.prepareStatement("INSERT INTO Stats(won, bracketRound) VALUES (?,?)",
					Statement.RETURN_GENERATED_KEYS);

			ps.setNull(1, Types.NULL);
			ps.setInt(2, 1);

			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			if (rs.next()) {
				statsID = rs.getInt(1);
			}
			return statsID;
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		} finally {
			return 0;
		}
	}
}