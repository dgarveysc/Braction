

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
<<<<<<< HEAD
import javax.servlet.http.HttpSession;

=======

/**
 * Servlet implementation class Login
 */
>>>>>>> a134dde7798aaab16915f46d0a66717f4c0b0891
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
<<<<<<< HEAD
				String username = request.getParameter("uname"); System.out.println(username);
				String password = request.getParameter("pword"); System.out.println(password);
				String next = "/login-sign-up.jsp";
				PrintWriter out = response.getWriter();
=======
		// TODO Auto-generated method stub
				String username = request.getParameter("Username"); System.out.println(username);
				String password = request.getParameter("Password"); System.out.println(password);
				//String next = "/loginsingup.html";
				PrintWriter out = response.getWriter();
				response.setContentType("text/html"); out.println("<html>");
>>>>>>> a134dde7798aaab16915f46d0a66717f4c0b0891
				Connection connection = null;
				PreparedStatement st = null;
				ResultSet rs = null;
				
				try {
					Class.forName("com.mysql.cj.jdbc.Driver");
					connection = DriverManager.getConnection("jdbc:mysql://localhost/sportswebsite?user=root&password=okamoto928");
					st = connection.prepareStatement("SELECT * FROM Users WHERE username=? AND passphrase=?");
					st.setString(1, username);
					st.setString(2, password);
					rs = st.executeQuery();
					if(rs.next()) {
<<<<<<< HEAD
						int userID = Integer.parseInt(rs.getString(1));
                        // Using sessions 
                        HttpSession session = request.getSession();
                        session.setAttribute("username", username);
                        session.setAttribute("userID", userID); System.out.println(userID);
                        next = "/profile.jsp";
                        
                        
                        //Send dispatch
						request.setAttribute("loggedIn", "true");
						//request.setAttribute("username", username);
=======
						out.println("<p>Logged in!</p>");
						//Send dispatch
						/*request.setAttribute("loggedIn", "true");
						request.setAttribute("username", username);
>>>>>>> a134dde7798aaab16915f46d0a66717f4c0b0891
						RequestDispatcher dispatch = getServletContext().getRequestDispatcher(next);
						dispatch.forward(request, response);

					}
					else {
<<<<<<< HEAD
						System.out.println("b");
						out.println("<div>Username and password combination is not registered.</div>");
						//If username and password aren't in database, throw an error
						//request.setAttribute("loginError", "Username and password combination is not registered.");
						//next = "/login-sign-up.jsp";
						
						//Send dispatch
						//RequestDispatcher dispatch = getServletContext().getRequestDispatcher(next);
						//dispatch.forward(request, response);
=======
						out.println("<p>Login error</p>");
						//If username and password aren't in database, throw an error
						/*request.setAttribute("loginError", "\tUsername and password combination is not registered.");
						next = "/LoginSignup.jsp";
						
						//Send dispatch
						RequestDispatcher dispatch = getServletContext().getRequestDispatcher(next);
						dispatch.forward(request, response);*/
>>>>>>> a134dde7798aaab16915f46d0a66717f4c0b0891
					}
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
				} /*finally {
					try {
						connection.close();
						st.close();
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
<<<<<<< HEAD
				}*/
    }
    
=======
				}
				out.println("</html>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
>>>>>>> a134dde7798aaab16915f46d0a66717f4c0b0891
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
<<<<<<< HEAD
}
=======

}
>>>>>>> a134dde7798aaab16915f46d0a66717f4c0b0891
