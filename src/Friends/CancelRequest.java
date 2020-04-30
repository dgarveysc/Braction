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
 * Servlet implementation class CancelRequest
 */
@WebServlet("/CancelRequest")
public class CancelRequest extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CancelRequest() {
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
				userID = (Integer)ui;
			} catch (NumberFormatException e) {
				success = false;
			}
		}
		int result = -1;
		if (success) {
			String friendID = request.getParameter("friendID");
			if (friendID != null) {
				result = JDBCBracketStuff.cancelFriends(userID, Integer.parseInt(friendID));
			} else {
				result = 1;
			}
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
