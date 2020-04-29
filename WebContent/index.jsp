<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">	
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<title>Braction Home</title>
		<link rel="stylesheet" href="homepage.css">
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
	</head>
<body>
	<% 
		boolean loggedIn = false;
		HttpSession s = request.getSession();
		String username = (String)s.getAttribute("username");
		if(username != null){
			loggedIn = true;
		}
		String profile = "";
		if(loggedIn){
			profile = "<form method=\"GET\" action=\"Profile\"> <!-- User ID --> <input type=\"hidden\" name=\"userID\" value=\"1\"><a href=\"profile.jsp\"><input type=\"submit\" class=\"button profile-button\" value=\"Profile\"></a></form><a href=\"createTournament.jsp\" class=\"button create-button\">Create Tournament</a><button class=\"button create-button\" value=\"Logout\" onclick=\"logout()\">Logout</button>";
			
		}else{
			profile = "<a href=\"createTournament.jsp\" ><input type=\"submit\" class=\"button create-button\" value=\"Create Tournament\"></a><a href=\"login-sign-up.jsp\"><input type=\"submit\" class=\"button login-button\" value=\"Login/Sign Up\"></a>";

		}
	%>
	<header class="homepage-header">
		<div class="wrapper">
        	<div class="logo">
				<a href="index.jsp">
					<img src="logo.png" alt="logo">
				</a>
			</div>
			<div class="nav-area">
				<a href="#">
						<input type="submit" class="button home-button" value="Home" id="active">
				</a>
				<div>
					<%= profile %>
				</div>
			</div>
		</div>	
		<div class="welcome-text">
			<h1>Bringing the competition to you.</h1>
			<a href="createTournament.jsp">Start Creating Tournaments</a>
		</div>
	</header>
	<script src="myScript.js"></script>
</body>
</html>