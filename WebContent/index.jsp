<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">	
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<title>Braction Home</title>
		<link rel="stylesheet" href="homepage.css">
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
		<script async src="https://www.googletagmanager.com/gtag/js?id=UA-165111822-1"></script>
		<script>
		  	window.dataLayer = window.dataLayer || [];
		  	function gtag(){dataLayer.push(arguments);}
		  	gtag('js', new Date());  gtag('config', 'UA-165111822-1');
		</script>
	</head>
<body>
	<% 
    	boolean loggedIn = false;
    	if(session.getAttribute("userID") != null)
    	{
    		loggedIn = true;
    		int userID = (Integer)session.getAttribute("userID");
    		//int userIDInt = Integer.parseInt(userID);
    	}

        //boolean loggedIn = false;
        HttpSession s = request.getSession(false);
        String profile = "";
        String createTournament = "";
        String currentUser = (String)s.getAttribute("username");
        String home = "";
        String login = "";
        String logout = "";
        // If the user is signed in, display home, favorites and logout
        if(currentUser != null){
            home = "inline";
            createTournament = "inline";
            login = "none";
            profile = "inline";
            logout = "inline";
        }else{
            home = "inline";
            createTournament = "inline";
            login = "inline";
            profile = "none";
            logout = "none";
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
				<a href="index.jsp" style="display: <%=home %>">
					<input type="submit" class="button home-button" value="Home" id="active">
				</a>
				<a href="createTournament.jsp" style="display: <%= createTournament %>" >
					<input type="submit" class="button create-button" value="Create Tournament">
				</a>
				<form method="GET" action="Profile">
				<!-- User ID -->
					<!-- input type="hidden" name="userID" value="1"> -->
					<a href="profile.jsp" style="display: <%= profile %>">
						<input type="submit" class="button profile-button" value="Profile">
					</a>
				</form>
				<a href="login-sign-up.jsp" style="display: <%= login %>">
					<input type="submit" class="button login-button" value="Login/Sign Up">
				</a>
				<a href="index.jsp">
                    <input type="submit" style="display: <%= logout %>" class="button logout-button" value="Logout" onclick="logout()">
                </a>
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