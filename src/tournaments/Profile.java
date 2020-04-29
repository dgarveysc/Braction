package tournaments;

import java.io.IOException;
import java.util.List;
import java.util.Stack;

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
		if (ui == null) {
			success = false;
		} else {
			try {
				userID = Integer.parseInt((String)ui);
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

}
