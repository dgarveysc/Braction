

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
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class NewAccount
 */
@WebServlet("/NewAccount")
public class NewAccount extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public NewAccount() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Getting parameters
		String email = request.getParameter("email");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String confPass = request.getParameter("confirmPass");
		String readTAC = request.getParameter("natAndc");
		
		//Setting up the printwriter
		PrintWriter out = response.getWriter();
		response.setContentType("text/html"); out.println("<html>");
		
		
		//Redirected page on successful account creation 
		String next = "/LoginSignup.jsp";
		boolean empty = false;
		
		//Checking the inputs
		System.out.println(username + " " + password + " " + email + " " + confPass + " " + readTAC);
		
		//Checking if the fields are filled out
		if(email == "" || username == "" || password == "" || confPass == "" || readTAC == null) {
			/**if(email == "") {request.setAttribute("signUpError", "\tPlease fill out the email section.");}
			if(username == "") {request.setAttribute("usernameError", "\tPlease fill out the username section.");}
			if(password == "") {request.setAttribute("passwordError", "\tPlease fill out the password section.");}
			if(confPass == "") {request.setAttribute("confpassError", "\tPlease fill out the password confirmation section.");}
			if(readTAC == null) {request.setAttribute("readTACError", "\tPlease read the terms and conditions.");}**/
			out.println("<p>Error occurred when trying to create account</p>");


			//Make sure servlet doesn't go through to next code block
			empty = true;
			
			//Send dispatch
			/**RequestDispatcher dispatch = getServletContext().getRequestDispatcher(next);
			dispatch.forward(request, response);**/
		}
		if(!empty && password.equals(confPass) && email.contains("@")) {
			//Going through database
			Connection connection = null;
			PreparedStatement st = null;
			ResultSet rs = null;
			
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hw4?user=root&password=root");
				st = connection.prepareStatement("SELECT * FROM userinfo WHERE email=?");
				st.setString(1, email);
				rs = st.executeQuery();
				if(rs.next()) {
					//If username and email are already in the database, ask them to provide different ones
					//request.setAttribute("signUpError", "\tEmail already taken, please sign up with a new one.");
					out.println("Email already taken");
				}
				else {
					//Register user in the database and return a new page with favorites and logout
					PreparedStatement newuser = connection.prepareStatement("INSERT INTO userinfo VALUES (?, ?, ?)");
					newuser.setString(1, email);
					newuser.setString(2, username);
					newuser.setString(3, password);
					newuser.execute();
					/**request.setAttribute("loggedIn", "true");
					request.setAttribute("username", username);
					next = "/homePage.jsp";**/
					try {
						newuser.close();
					} catch (SQLException sqle) {
						System.out.println(sqle.getMessage());
					}
					
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
			        Session session = Session.getDefaultInstance(properties);
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
			            Transport transport = session.getTransport("smtp");
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
				}
				
				//Send dispatch
				/**RequestDispatcher dispatch = getServletContext().getRequestDispatcher(next);
				dispatch.forward(request, response);**/
				
			} catch (SQLException sqle) {
				System.out.println(sqle.getMessage());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					connection.close();
					st.close();
					rs.close();
				} catch (SQLException sqle2) {
					System.out.println(sqle2.getMessage());
				}
			}
			//Check if the email is taken
			
		}
		else {
			/**if(email.contains("@")) {
				//Password doesn't match confirmed password
				request.setAttribute("confpassError", "\tPlease make sure that the passwords match.");
			}
			else {
				//Password doesn't match confirmed password
				request.setAttribute("signUpError", "\tPlease enter a valid email address.");
			}
			
			//Send dispatch
			RequestDispatcher dispatch = getServletContext().getRequestDispatcher(next);
			dispatch.forward(request, response);**/
			out.println("<p>Error occurred when trying to create account</p>");
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
