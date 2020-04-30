package tournaments;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sql.JDBCBracketStuff;

/**
 * Servlet implementation class UpdateBracket
 */
@WebServlet("/UpdateBracket")
public class UpdateBracket extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateBracket() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String slot1 = request.getParameter("slot1");
		String slot2 = request.getParameter("slot2");
		String bracketID1 = request.getParameter("bracketID");
		String won = request.getParameter("won");
		boolean success = true;
		String error = null;
		if (slot1 != null && slot2 != null && bracketID1 != null && won != null) {
			int slotOne = -1;
			int slotTwo = -1;
			int bracketID = -1;
			try {
				slotOne = Integer.parseInt(slot1);
				slotTwo = Integer.parseInt(slot2);
				bracketID = Integer.parseInt(bracketID1);
				success = true;
				if (!won.equals("yes") && !won.equals("no")) {
					success = false;
					error = "please enter whether won is yes or no";
				} 
			} catch (NumberFormatException e) {
				error = "please enter valid slot numbers and bracket IDs";
			}
			if (success) {
				if (!JDBCBracketStuff.update(slotOne, slotTwo, won.equals("yes"), bracketID)) {
					success = false;
					error = "The backend sucks lolol";
				}
			}
		}
		PrintWriter out = response.getWriter();
		if (success) {
			out.print("Succeeded!");
		} else {
			out.print(error);
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
