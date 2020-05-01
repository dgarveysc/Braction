package Friends;

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
 * Servlet implementation class AddFriend
 */
@WebServlet("/AddFriend")
public class AddFriend extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddFriend() {
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
		int result = -1;
		if (success) {
			String friendUsername = request.getParameter("friendUsername");
			
			result = JDBCBracketStuff.addFriend(userID, friendUsername);
			System.out.println(result);
		}
		PrintWriter out = response.getWriter();
		out.print(result);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
