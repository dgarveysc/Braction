package tournaments;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bracket.Bracket;
import sql.JDBCBracketStuff;

/**
 * Servlet implementation class DisplayBracket
 */
@WebServlet("/DisplayBracket")
public class DisplayBracket extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DisplayBracket() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String bracketId = request.getParameter("bracketID");
		boolean success = true;
		Bracket b = null;
		int bracketID = -1;
		if (bracketId == null) {
			success = false;
		} else {
			bracketID = Integer.parseInt(bracketId);
			b = JDBCBracketStuff.getBracket(bracketID);
			if (b == null) {
				success = false;
			}
		}
		
		if (success) {
			HttpSession session =request.getSession(false);
			boolean isHost = false;
			if (session != null) {
				Object ui = session.getAttribute("userID");
				if (ui != null) {
					int userID = Integer.parseInt((String)ui);
					isHost = JDBCBracketStuff.isHost(bracketID, userID);
				}
			}
			request.setAttribute("bracketData", b);
			request.setAttribute("isHost", isHost);
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
