<%@ page language="Java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<!DOCTYPE html>
<html>

<head>
    <meta charset='utf-8'>
    <meta http-equiv='X-UA-Compatible' content='IE=edge'>
    <title>Create or Join a Tournament</title>
    <meta name='viewport' content='width=device-width, initial-scale=1'>
     <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css"
        integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"
        integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6"
        crossorigin="anonymous"></script>
    <script async src="https://www.googletagmanager.com/gtag/js?id=UA-165111822-1"></script>
	<script>
	  window.dataLayer = window.dataLayer || [];
	  function gtag(){dataLayer.push(arguments);}
	  gtag('js', new Date());  gtag('config', 'UA-165111822-1');
	</script>
    <link rel="stylesheet" type="text/css" href="sty.css">
   
    <script>
        function createTournament() {
            // First, ensure the form is completely filled out
            var v = valNT();
            if (v == false) {
                return false;
            }

            // Using jQuery
            $.ajax({
                url: 'CreateTournament',
                data: {
                    tournamentName: document.createT.tName.value,
                    gameType: document.createT.gameType.value,

                },
                //Making the tournament ID code show up on screen
                success: function (result) {
  					var r = JSON.parse(result);
                    $("#tCode").html("<div class=\"code\"> Your code: " + r[0] + "</div>");
                }

            });
            return false;
        }

        function joinTournament() {
            // First, ensure the form is completely filled out
            var v = valTCode();
            if (v == false) {
                return false;
            }
            // Using jQuery
            $.ajax({
                url: 'JoinTournament',
                data: {
                    tournamentID: document.joinT.tCOde.value
                },
                success: function (result) {
                	if(result[0] != 'A' && result[0] != 'Y'){
                		$("#tCOdee").html("<div> Success! Please navigate to your <a class=\"link\" href=\"Profile\"> profile page </a> to view the tournament! </div>");
                	}else{
                        $("#tCOdee").html("<div>" + result + " Please try a different code. </div>");
                	}
                }

            });
            return false;


        }
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
	String createActive = "";
	String loginActive = "";
	// If the user is signed in, display home, favorites and logout
	if(currentUser != null){
	    home = "inline";
	    createTournament = "inline";
	    login = "none";
	    profile = "inline";
	    logout = "inline";
	    createActive = "active";
	}else{
		loginActive="active";
		RequestDispatcher dispatch = getServletContext().getRequestDispatcher("/login-sign-up.jsp");
		dispatch.forward(request, response);
	} 
	


/*boolean loggedIn = false;
		HttpSession s = request.getSession();
		String profile = "";
		String username = (String)s.getAttribute("username");
		if(username != null){
			profile = "<form method=\"GET\" action=\"Profile\"> <!-- User ID --> <input type=\"hidden\" name=\"userID\" value=\"1\"><a href=\"profile.jsp\"><input type=\"submit\" class=\"button profile-button\" value=\"Profile\"></a></form><a href=\"#\" class=\"button create-button link\">Create Tournament</a><button class=\"button create-button\" value=\"Logout\" onclick=\"logout()\">Logout</button>";
		}else{
			
		}*/

	
	%>

        <div class="row">


            <div class="col">

                <div class="logo">
                    <a href="index.jsp">
                        <img src="logo.png" alt="logo">
                    </a>
                </div>
            </div>
            <div class="col">
                <div class="nav-area">
	                <a href="index.jsp" style="display: <%=home %>">
						<input type="submit" class="button home-button" value="Home">
					</a>
					<a href="createTournament.jsp" style="display: <%= createTournament %>" >
						<input type="submit" class="button create-button" value="Create Tournament" id="<%= createActive %>">
					</a>
					<form method="GET" action="Profile">
					<!-- User ID -->
						<!-- input type="hidden" name="userID" value="1"> -->
						<a href="profile.jsp" style="display: <%= profile %>">
							<input type="submit" class="button profile-button" value="Profile">
						</a>
					</form>
					<a href="login-sign-up.jsp" style="display: <%= login %>">
						<input type="submit" class="button login-button" value="Login/Sign Up" id="<%= loginActive %>">
					</a>
					<a href="index.jsp">
	                    <input type="submit" style="display: <%= logout %>" class="button logout-button" value="Logout" onclick="logout()">
	                </a>
                </div>
            </div>
        </div>
	<div class="container">
        <hr width="100%" style="padding-bottom: 5%">

        <div class="row main">
            <div class="col text">
                <form onsubmit="return createTournament()" class="tournament" action="" method="POST" name="createT">
                    <h1 class="header">Create a Tournament!</h1>
                    <hr class="whole-break">
                    <ol type="1">
                        <li class="item-text">
                            Select the game you want to play with your friends <br>

                            <select onclick="removeError(this.id)" class="dropdown" name="gameType" id="gameType">
                                <option value="">Select your game</option>
                                <option value="1">NBA 2K20</option>
                                <option value="2">FIFA 20</option>
                                <option value="3">Madden 20</option>
                            </select>
                            <div id="gameTypee"></div>
                            <hr class="whole-break">
                        </li>
                        <li class="item-text">
                            Give your tournament a name!
                            <input onclick="removeError(this.id)" type="text" class="text-inpt" style="width:100%"
                                id="tName" name="tName" maxlength="20">
                            <div id="tNamee"></div>
                            <hr class="whole-break">
                        </li>

                        <li>
                            <div class="item-text">
                                Generate your unique TourneyCode <br>
                            </div>

                            <input class="sub" type="submit" value="Generate Your TourneyCode!">
                            <div class="loginError" id="tCode">

                            </div>
                            <hr class="whole-break">
                        </li>
                        <li class="item-text">Share your TourneyCode with your friends! <br>
                           
                        </li>
                    </ol>
                </form>
            </div>
            <div class="col text">
                <form onsubmit="return joinTournament()" class="tournament" action="" method="POST" name="joinT">
                    <h1 class="header">
                        Already have a TourneyCode?
                    </h1>
                    <h3 class="header">Join your friend's tournament below. </h3>
                    <p class="item-text">
                        Enter the TourneyCode below:
                    </p>
                    <input onclick="removeError(this.id)" class="text-inpt" type="text" id="tCOde" name="tCOde">
                    <div class="loginError" id="tCOdee"></div>
                    <br>
                    <div class="tc">


                        <input onclick="removeError(this.id)" type="checkbox" id="nttAndc" name="nttAndc" value="true">
                        <label class="small-item-text" for="nttAndc"> By joining this tournament,
                            I have read and agree to all terms and conditions of Braction.</label>
                        <div id="nttAndce"></div>
                    </div>
                    <br>
                    <input class="sub" type="submit" value="Join Tournament!">
                </form>
            </div>
        </div>


    </div>


    <script src="myScript.js"></script>
</body>

</html>
