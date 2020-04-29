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
import javax.servlet.http.HttpSession;


@WebServlet("/NewAccount")
public class NewAccount extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String email = request.getParameter("email");
		String username = request.getParameter("uname");
		String password = request.getParameter("pword");
		String confPass = request.getParameter("cpword");
		String readTAC = request.getParameter("natAndc");
		PrintWriter out = response.getWriter();
		String next = "/login-sign-up.jsp";
		boolean empty = false;
		System.out.println(username + " " + password + " " + email + " " + confPass + " " + readTAC);	
		if(email == "" || username == "" || password == "" || confPass == "" || readTAC == null) {
			/*if(email == "") {request.setAttribute("signUpError", "\tPlease fill out the email section.");}
			if(username == "") {request.setAttribute("usernameError", "\tPlease fill out the username section.");}
			if(password == "") {request.setAttribute("passwordError", "\tPlease fill out the password section.");}
			if(confPass == "") {request.setAttribute("confpassError", "\tPlease fill out the password confirmation section.");}
			if(readTAC == null) {request.setAttribute("readTACError", "\tPlease read the terms and conditions.");*/

			//Make sure servlet doesn't go through to next code block
			empty = true;
			
			//Send dispatch
			RequestDispatcher dispatch = getServletContext().getRequestDispatcher(next);
			dispatch.forward(request, response);
		}
		if(!empty && password.equals(confPass) && email.contains("@")) {
			//Going through database
			Connection connection = null;
			PreparedStatement st = null;
			ResultSet rs = null;
			
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				connection = DriverManager.getConnection("jdbc:mysql://localhost/sportswebsite?user=root&password=okamoto928");
				st = connection.prepareStatement("SELECT * FROM Users WHERE email=?");
				st.setString(1, email);
				rs = st.executeQuery();
				if(rs.next()) {
					//If username and email are already in the database, ask them to provide different ones
					out.println( "<div>Email already taken, please sign up with a new one.</div>");
					System.out.println("Already taken");
				}
				else {
					//Register user in the database and return a new page with favorites and logout
					PreparedStatement newuser = connection.prepareStatement("INSERT INTO Users (username, passphrase, email, points) VALUES (?, ?, ?, ?)");
					newuser.setString(1, username);
					newuser.setString(2, password);
					newuser.setString(3, email);
					newuser.setInt(4, 0);
					newuser.execute();
                    request.setAttribute("loggedIn", "true");
                    HttpSession session = request.getSession();
                    session.setAttribute("username", username);
                    next = "/profile.jsp";
                        
					request.setAttribute("username", username);
					//Send dispatch
					RequestDispatcher dispatch = getServletContext().getRequestDispatcher(next);
					dispatch.forward(request, response);
					try {
						newuser.close();
					} catch (SQLException sqle) {
						System.out.println(sqle.getMessage());
                    }
				}
				
				
			} catch (SQLException sqle) {
				System.out.println(sqle.getMessage());
			} /*catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/ catch (Exception e ){
				e.printStackTrace();
			}/*finally { {
			
				try {
					connection.close();
					st.close();
					rs.close();
				} catch (SQLException sqle2) {
					System.out.println(sqle2.getMessage());
				}*/
			}
			
		}
	

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
