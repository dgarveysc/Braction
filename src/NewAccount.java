import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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
		//Getting parameters
		int userID = -1;
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
			PreparedStatement ps = null;
			ResultSet rs = null;
			ResultSet getUserID = null;
			
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sportswebsite?user=root&password=root");
				st = connection.prepareStatement("SELECT * FROM users WHERE email=? OR username=?");
				st.setString(1, email);
				st.setString(2, username);
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
					//RequestDispatcher dispatch = getServletContext().getRequestDispatcher(next);
					//dispatch.forward(request, response);
					try {
						newuser.close();
					} catch (SQLException sqle) {
						System.out.println(sqle.getMessage());
					}
					
					//Getting the userID
					ps = connection.prepareStatement("SELECT userID FROM users WHERE username=?");
					ps.setString(1, username);
					getUserID = ps.executeQuery();
					if(getUserID.next()) {
						userID = getUserID.getInt("userID");
					}
					
					//Setting session userID
					System.out.println("userID: " + userID);
					HttpSession userSession = request.getSession();
                    userSession.setAttribute("userID", userID);
					//Sending confirmation email 
					//Initializing variables
				    String bractionEmail = "bractionsportswebsite@gmail.com";  
				    String bractionPass = "sportswebsite"; 
				    String recipientAddress = email;

			        //Setting properties
			        Properties properties = System.getProperties();
			        String sender = bractionEmail;
			        String pass = bractionPass;
			        String host = "smtp.gmail.com";
			        properties.put("mail.smtp.starttls.enable", "true");
			        properties.put("mail.smtp.host", host);
			        properties.put("mail.smtp.user", sender);
			        properties.put("mail.smtp.password", pass);
			        properties.put("mail.smtp.port", "587");
			        properties.put("mail.smtp.auth", "true");

			        //Creating the message and session
			        Session session2 = Session.getDefaultInstance(properties);
			        MimeMessage message = new MimeMessage(Session.getDefaultInstance(properties));

			        try {
			            message.setFrom(new InternetAddress(sender));
			            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientAddress));
			            
			            //Subject line
				        String subject = "Welcome to Braction!";
			            message.setSubject(subject);
			            
			            //Writing the body of the email
				        String body = "Thank you for registering for Braction! \nHere's your username: \"" + username + "\" and password: \"" + password + "\"";
			            message.setText(body);
			            
			            //Creating transport
			            Transport transport = session2.getTransport("smtp");
			            transport.connect(host, sender, pass);
			            transport.sendMessage(message, message.getAllRecipients());
			            transport.close();
			        }
			        catch (AddressException ae) {
			            ae.printStackTrace();
			        }
			        catch (MessagingException me) {
			            me.printStackTrace();
			        }

				      out.println("Account created!");
				
			} }
				catch (SQLException sqle) {
				System.out.println(sqle.getMessage());
			} /*catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/ catch (Exception e ){
				e.printStackTrace();
			}
			
			finally { 
			
				try {
					connection.close();
					ps.close();
					st.close();
					rs.close();
					getUserID.close();
				} catch (SQLException sqle2) {
					System.out.println(sqle2.getMessage());
				}
			
			}
		} // TRY BLOCK
			else
			{
				
			
	}// IF STATEMENT
	} // METHOD
	

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
