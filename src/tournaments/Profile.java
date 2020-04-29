package tournaments;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bracket.BracketOverview;
import sql.JDBCBracketStuff;

/**
 * Servlet implementation class Profile
 */
@WebServlet("/Profile")
public class Profile extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Profile() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session =request.getSession(false);
		int userID = -1;
		int gameType = -1;
		
		boolean success = true;
		Object ui = session.getAttribute("userID");
		String userid = null;
		System.out.println("printing userid from session");
		System.out.println((Integer)ui);
		if (ui == null) {
			success = false;
		} else {
			try {
				userID = (Integer)ui;
				userid = Integer.toString(userID);
			} catch (NumberFormatException e) {
				success = false;
			}
		}

		List<Stack<BracketOverview>> b = null;
		if (success && userID != -1) {
			b = JDBCBracketStuff.getUserOverview(userID);
			if (b == null)  {
				success = false;
			}
		} else {
			success = false;
		}
		if (success) {
			stats(request, userid);
			request.setAttribute("userBrackets", b);
			RequestDispatcher dispatch = getServletContext().getRequestDispatcher("/profile.jsp");
			dispatch.forward(request, response);
		} else {
			
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	private void stats(HttpServletRequest request, String testID) {
		if (JDBCBracketStuff.conn == null) {
			JDBCBracketStuff.initConnection();
		}
		Statement st = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSet rs2 = null;

		Set<Integer> statIDs = null;
		
		Boolean noGames = false;

		//should count wins, losses, and upcoming!

		try {
			
			st = JDBCBracketStuff.conn.createStatement();

			//get all statIDs corresponding to a user
			rs = st.executeQuery("SELECT * FROM UserToGameStats"
					+ " WHERE userID = " + testID);

			statIDs = new HashSet<Integer>();
			while (rs.next()) { //now put all statIDs into vector
				//ASSUME statID always exists
				statIDs.add(rs.getInt("statsID"));
				System.out.println("Adding statsID = " + rs.getInt("statsID"));
			}
			
			if(statIDs.isEmpty())
			{
				noGames = true;
			}
				
			
			//get table of all statsID for user
			rs2 = st.executeQuery("SELECT * FROM Stats"
					+ " WHERE won IS NOT NULL");

			Integer numWins = 0;
			Integer numLosses = 0;
			Integer numPlayed = 0;
			Integer numBWins = 0; //bracket wins
			Integer sumRounds = 0; //sum of all rounds played. divide by numPlayed to get avgRound
			Double avgRound = 0.0;
			Integer numEarned = 0;
			Integer sumOppRank = 0;
			Double avgOppRank = 0.0;
			Date gameDate = null;

			System.out.println("Got here 1");
			
			//List of EloChanges
			List<EloDate> eloChanges = new Vector<EloDate>();


			//no divide by zero error to break website psl

			while(rs2.next()) //go thru statsID table for user
			{
				//if the user is not there, skip it
				if(!statIDs.contains(rs2.getInt("statsID")))
				{
					continue;
				}


				boolean won = rs2.getBoolean("won");
				Integer oppRank = rs2.getInt("oppRank");
				System.out.println("OppRank = " + oppRank);


				if(won) //update wins and rank of opponents you beat
				{
					numWins++;
				}
				else {
					numLosses++;
				}

				sumOppRank += oppRank;
				numPlayed++;

				//rounds span from 1-3, add total rounds
				Integer round = rs2.getInt("bracketRound");
				sumRounds += round;

				if(round == 3 && won) //condition to check if won bracket
				{
					numBWins++;
				}


				Integer eloChange = rs2.getInt("xp");

				//now parse date
				Date dt = rs2.getDate("gameDate");

				//create EloDate object
				EloDate ed = new EloDate(dt, eloChange, oppRank, won);

				eloChanges.add(ed);

			}
			if(numPlayed == 0 || noGames) // NO GAMES PLAYED
			{
				request.setAttribute("numPlayed", 0);
				request.setAttribute("elo", 1000);
				
				//throw new Exception("No games played");
				
			}
			System.out.println("Got here 2");

			//VERIFY THAT THE USERS TESTID IS VALID, CATCH EXCEPTION
			String userIDString = String.valueOf(testID);


			//now order eloChange list by date
			eloChanges.sort(new EloComparator());


			//FileWriter fw = new FileWriter(userIDString + ".txt");
			//String stringPath = "/";
			//String absolutePath = getServletContext().getRealPath(stringPath);
			//System.out.println(absolutePath);
			PrintWriter pw = new PrintWriter("C:\\Users\\sokam\\eclipse-workspace\\Braction_FinalProject\\src\\Stats\\" + userIDString + ".txt");

			int curElo = 1000;


			Vector<Integer> oppRkWins = new Vector<Integer>();
			Vector<Integer> oppRkLosses = new Vector<Integer>();

			System.out.println("Got here 3");

			//now output list to file, of scores not changes or oppRank:
			pw.print("1000\n");
			for(int i = 0; i < eloChanges.size(); i++)
			{
				System.out.println("curElo printing is: " + curElo);
				curElo += eloChanges.get(i).getElo();
				System.out.println("after curElo printing is: " + curElo);
				if(i == eloChanges.size()-1)
				{
					pw.print(curElo);
				}
				else {
					pw.print(curElo + "\n");
				}

				// if won, add oppRkWins // else oppRkLosses
				if(eloChanges.get(i).getWon())
				{
					oppRkWins.add(eloChanges.get(i).getOppRank());
				}
				else {
					oppRkLosses.add(eloChanges.get(i).getOppRank());
				}

			}
			pw.flush();
			System.out.println("Got here 4");

			
			
			// now run Python script thru matPlotLib
			ProcessBuilder pb = new ProcessBuilder("python", "C:\\Users\\sokam\\eclipse-workspace\\Braction_FinalProject\\src\\Stats\\plot.py", userIDString);
			Process p = pb.start();
				// matPlotLib will output fiveID.png, twentyID.png, and allID.png, for rank change graphs
			// wait until done then continue with other stuff
			InputStream in = p.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String pyInput = br.readLine(); //should buffer
			System.out.println("PyInput is " + pyInput);

			if(!pyInput.contentEquals("Success"))  // figure out how to handle
			{
				System.out.println("PYTHON SCRIPT DID NOT WORK");
			}

			System.out.println("Got here 5");

			
			// process data and output avg opp rank over winning games
			Double wRank5 = 0.0;
			Double wRank20 = 0.0;
			Double wRankAll = 0.0;

			// same over losing games
			Double lRank5 = 0.0;
			Double lRank20 = 0.0;
			Double lRankAll = 0.0;

			//compute median, 25%, 75% for winning rank
			for(int i = oppRkWins.size()-1; i>= 0; i--)
			{
				Integer curRk = oppRkWins.get(i);
				if(i >= oppRkWins.size()-5) //rank5
				{
					wRank5 += curRk; wRank20 += curRk; wRankAll += curRk;
				}
				else if(i >= oppRkWins.size()-20) //rank5
				{
					wRank20 += curRk;
					wRankAll += curRk;
				}
				else {
					wRankAll += curRk;
				}
			}
			//compute averages


			//do same for losing rank
			for(int i = oppRkLosses.size()-1; i>= 0; i--)
			{
				Integer curRk = oppRkLosses.get(i);
				if(i >= oppRkLosses.size()-5) //rank5
				{
					lRank5 += curRk; lRank20 += curRk; lRankAll += curRk;
				}
				else if(i >= oppRkLosses.size()-20) //rank5
				{
					lRank20 += curRk;
					lRankAll += curRk;
				}
				else {
					lRankAll += curRk;
				}
			}
			
			// logic here is if less games are played than specified, the average reflects that.
			if(numWins != null && numWins <5 && numWins != 0)
			{
				wRank5 = wRank5 / numWins;
				wRank20 = wRank5 / numWins;
			}
			else {
				wRank5 = wRank5 / 5.0;
				if(numWins != null && numWins < 20 && numWins != 0)
				{
					wRank20 = wRank20 / numWins;				
				}
				else {
					wRank20 = wRank20/20.0;
				}
			}
			
			if(numWins != 0)
			{
				wRankAll = wRankAll / numWins;
			}
			
			
			// same for Losses
			if(numLosses != null && numLosses <5 && numLosses != 0)
			{
				lRank5 = lRank5 / numLosses;
				lRank20 = lRank5 / numLosses;
			}
			else {
				lRank5 = lRank5 / 5.0;
				if(numLosses < 20 && numLosses != null && numLosses != 0)
				{
					lRank20 = lRank20 / numLosses;				
				}
				else {
					lRank20 = lRank20/20.0;
				}
			}
			
			if(numLosses != 0)
			{
				lRankAll = lRankAll / numLosses;
			}

			Collections.sort(oppRkWins);
			Collections.sort(oppRkLosses);

			Integer win25 = 0;
			Integer win50 = 0;
			Integer win75 = 0;
			Integer lose25 = 0;
			Integer lose50 = 0;
			Integer lose75 = 0;

			if(oppRkWins.size() > 0)
			{
				win25 = oppRkWins.get((oppRkWins.size()-1)/4);
				win50 = oppRkWins.get(oppRkWins.size()/2);
				win75 = oppRkWins.get(oppRkWins.size()*3/4);
			}
			if(oppRkLosses.size() > 0)
			{
				lose25 = oppRkLosses.get((oppRkLosses.size()-1)/4);
				lose50 = oppRkLosses.get(oppRkLosses.size()/2);
				lose75 = oppRkLosses.get(oppRkLosses.size()*3/4);
			}

			//compute average opponent rank
			if(numPlayed != 0)
			{
				avgOppRank = ((double) sumOppRank) / ((double) numPlayed);
				avgRound = ((double) sumRounds) / ((double) numPlayed);
			}
			
			request.setAttribute("numPlayed", numPlayed);
			request.setAttribute("numWins", numWins);
			request.setAttribute("numLosses", numLosses);

			request.setAttribute("avgOppW5", wRank5);
			request.setAttribute("avgOppW20", wRank20);
			request.setAttribute("avgOppWAll", wRankAll);
			
			request.setAttribute("avgOppL5", lRank5);
			request.setAttribute("avgOppL20", lRank20);
			request.setAttribute("avgOppLAll", lRankAll);

			
			request.setAttribute("win25", win25);
			request.setAttribute("win50", win50);
			request.setAttribute("win75", win75);
			request.setAttribute("lose25", lose25);
			request.setAttribute("lose50", lose50);
			request.setAttribute("lose75", lose75);
			
			
			request.setAttribute("avgOppRank", avgOppRank);
			request.setAttribute("avgRound", avgRound);
			request.setAttribute("numBWins", numBWins);
			request.setAttribute("elo", curElo);
			
			// here System printing
			System.out.println("numPlayed" + numPlayed);
			System.out.println("numWins" + numWins);
			System.out.println("numLosses" + numLosses);

			System.out.println("avgOppW5" + wRank5);
			System.out.println("avgOppW20" + wRank20);
			System.out.println("avgOppWAll" + wRankAll);
			
			System.out.println("avgOppL5" + lRank5);
			System.out.println("avgOppL20" + lRank20);
			System.out.println("avgOppLAll" + lRankAll);

			
			System.out.println("win25" + win25);
			System.out.println("win50" + win50);
			System.out.println("win75" + win75);
			System.out.println("lose25" + lose25);
			System.out.println("lose50" + lose50);
			System.out.println("lose75" + lose75);
			
			
			System.out.println("avgOppRank" + avgOppRank);
			System.out.println("avgRound" + avgRound);
			System.out.println("numBWins" + numBWins);
			System.out.println("elo" + curElo);


			if(numPlayed != 0)
			{
				request.setAttribute("img5", "five"+testID + ".png");
				request.setAttribute("img20", "twenty"+testID + ".png");
				request.setAttribute("imgAll", "all"+testID + ".png");
			}
		
			
		} catch (SQLException sqle) {
			//however we error handle

			System.out.println ("SQLException: " + sqle.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
	}

}

class EloDate{

	public Date getGameDate() {
		return gameDate;
	}
	public void setGameDate(Date gameDate) {
		this.gameDate = gameDate;
	}
	public Integer getElo() {
		return elo;
	}
	public void setElo(Integer elo) {
		this.elo = elo;
	}

	public Integer getOppRank() {
		return oppRank;
	}
	public void setOppRank(Integer oppRank) {
		this.oppRank = oppRank;
	}
	public EloDate(Date gameDate, Integer elo, Integer oppRank, Boolean won) {
		this.gameDate = gameDate;
		this.elo = elo;
		this.oppRank = oppRank;
		this.won = won;
	}

	Date gameDate;

	Integer oppRank;

	boolean won;

	public boolean getWon() {
		return won;
	}
	public void setWon(boolean won) {
		this.won = won;
	}

	Integer elo;

}


class EloComparator implements Comparator<EloDate> {


	public int compare(EloDate arg0, EloDate arg1) {
		return arg0.getGameDate().compareTo(arg1.getGameDate());
	}

}
