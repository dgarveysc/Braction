package Stats;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import sql.JDBCBracketStuff;

/**
 * Servlet implementation class StatsListerner
 */
@WebServlet("/StatsListerner")
public class StatsListerner extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StatsListerner() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session =request.getSession(false);
		int userID = -1;
		System.out.println("Stats listener called");
		boolean success = true;
		Object ui = session.getAttribute("userID");
		String userid = null;
		if (ui == null) {
			success = false;
		} else {
			try {
				userID = (int)ui;//Integer.parseInt((String)ui);
				//userid = (String) ui;
			} catch (NumberFormatException e) {
				success = false;
			}
		}
		int currElo = Integer.parseInt(request.getParameter("currElo"));
		int newElo;
		while ((newElo = JDBCBracketStuff.getEloOfUser(userID)) == -1 || newElo == currElo) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		PrintWriter out = response.getWriter();
		out.print(newElo);
		out.flush();
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
