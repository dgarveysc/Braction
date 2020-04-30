<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" 
import="bracket.BracketOverview" import="java.util.List" import="java.util.Stack" import="java.util.Vector" import="java.util.Queue" import="Friends.Friend"%>
<!Doctype HTML>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">
        <link rel="stylesheet" href="profile.css">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>
        <script src="myScript.js"></script>

        <title>Braction Home</title>

        <style>
        	html {
	  			overflow-y: scroll;
			}
			
            .profile-header .main-wrapper{
                padding-top: 12%;
                width: 70%;
                margin: auto;
            }

            .mt-2 {
                padding-top: 2%;
                color:rgb(150, 180, 206);
                margin-bottom: 2%;
            }

            .radio-text {
                color: rgb(170, 193, 212);
            }
            
            .addError {
			    color: red;
			    font-size: 1em;
            }

            /*.add-friend-content {
                padding-top: 2%;
                display: flex;
            }*/

            #friend-input {
                width: 40%;
                height: 50%;
                background: hsl(225, 20%, 12%);
            }
            
            #add-button {
            	margin-left: 4%;
            	width: 50%;
            }
        </style>

        <script type="text/javascript">
        	/* Start Tab JS */
            $(document).ready(function(){
                $('a[data-toggle="tab"]').on('shown.bs.tab', function(e){
                    var activeTab = $(e.target).text(); // Get the name of active tab
                    var previousTab = $(e.relatedTarget).text(); // Get the name of previous tab
                    $(".active-tab span").html(activeTab);
                    $(".previous-tab span").html(previousTab);
                });
            });
        	
        	
            function acceptFriend(friendU) {
        		$.ajax({
                    url: 'AddFriend',
                    data: {
                        friendUsername: friendU
                    },
                    success: function (result) {
                    	if(result == 3){
                    		$("#addResponse").text("You are now friends with " + friendU + ".");
                    		return false;
                    	}else if(result == 0){
                            $("#addResponse").text("Username does not exist.");
                            error = true;
                            return false;
                    	}else if(result == 1) {
                    		$("#addResponse").text("User is already a friend.");
                    		error = true;
                    		return false;
                    	}
                    	else if(result == 2) {
                    		$("#addResponse").text("Friend request sent to " + friendU + ".");
                    		return false;
                    	}
                    	else if(result == 5) {
                    		$("#addResponse").text("Cannot add yourself.");
                    		return false;
                    	}
                    	else if(result == -1) {
                    		$("#addResponse").text("Request failed.");
                    		return false;
                    	}
                    }
        		});
        	}
        	
        	function cancelRequest(friendId) {
        		$.ajax({
                    url: 'CancelRequest',
                    data: {
                        friendID: friendId
                    },
                    success: function (result) {
                    	if(result == 3){
                    		$("#addResponse").text("Request canceled.");
                    		return false;
                    	}else if(result == 0){
                            $("#addResponse").text("Cannot remove yourself.");
                            error = true;
                            return false;
                    	}else if(result == 1) {
                    		$("#addResponse").text("Cannot remove user that doesn't exist.");
                    		error = true;
                    		return false;
                    	}
                    	else if(result == 2) {
                    		$("#addResponse").text("Request already accepted.");
                    		return false;
                    	}
                    	else if(result == -1) {
                    		$("#addResponse").text("Request failed.");
                    		return false;
                    	}
                    }
        		});
        	}
        	
            function addFriend() {
                // First, ensure the form is completely filled out
                /*var v = valTCode();
                if (v == false) {
                    return false;
                }*/
                var error = false;
                // Using jQuery
                $.ajax({
                    url: 'AddFriend',
                    data: {
                        friendUsername: document.addForm.friendInput.value
                    },
                    success: function (result) {
                    	if(result == 3){
                    		$("#addResponse").text("You are now friends with " + document.addForm.friendInput.value + ".");
                    		return false;
                    	}else if(result == 0){
                            $("#addResponse").text("Username does not exist.");
                            error = true;
                            return false;
                    	}else if(result == 1) {
                    		$("#addResponse").text("User is already a friend.");
                    		error = true;
                    		return false;
                    	}
                    	else if(result == 2) {
                    		$("#addResponse").text("Friend request sent to " + document.addForm.friendInput.value + ".");
                    		return false;
                    	}
                    	else if(result == 5) {
                    		$("#addResponse").text("Cannot add yourself.");
                    		return false;
                    	}
                    	else if(result == -1) {
                    		$("#addResponse").text("Request failed.");
                    		return false;
                    	}
                    }

                });
                return false;
            }
            
            $( document ).ready(function() {
                console.log( "ready!" );
                
                refresh = function() {
                	$.ajax({
                        url: 'StatsListerner',
                        data: {
                            currElo: $("#curElo").text()
                        },
                        success: function (result) {
                        	alert("Your Elo changed to " + result);
                        	$("#curElo").text(result);
                        	refresh();
                        }

                    });
                }
                
                $(function(){
                    refresh();
                });
            
            });

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
 
        <header class="profile-header">
                <div class="logo">
                    <a href="index.jsp">
                        <img src="logo.png" alt="logo">
                    </a>
                </div> <!-- end logo -->
                <div class="nav-area">
				<a href="index.jsp" style="display: <%=home %>">
					<input type="submit" class="button home-button" value="Home">
				</a>
				<a href="createTournament.jsp" style="display: <%= createTournament %>" >
					<input type="submit" class="button create-button" value="Create Tournament">
				</a>
				<form method="GET" action="Profile">
				<!-- User ID -->
					<!-- input type="hidden" name="userID" value="1"> -->
					<a href="profile.jsp" style="display: <%= profile %>">
						<input type="submit" class="button profile-button" value="Profile" id="active">
					</a>
				</form>
				<a href="login-sign-up.jsp" style="display: <%= login %>">
					<input type="submit" class="button login-button" value="Login/Sign Up">
				</a>
				<a href="index.jsp">
                    <input type="submit" style="display: <%= logout %>" class="button logout-button" value="Logout" onclick="logout()">
                </a>
				</div>
            	<!--Tabs-->
                <div class="main-wrapper">
                    <ul id="myTab" class="nav nav-tabs">
                        <li class="nav-item">
                            <a href="#tournaments" class="nav-link active" data-toggle="tab" style="color:rgb(149, 159, 180)">My Tournaments</a>
                        </li>
                        <li class="nav-item">
                            <a href="#friends" class="nav-link" data-toggle="tab" style="color:rgb(149, 159, 180)">Friends List</a>
                        </li>
                        <li class="nav-item">
                            <a href="#stats" class="nav-link" data-toggle="tab" style="color:rgb(149, 159, 180)">Statistics</a>
                        </li>
                    </ul>
                    <div class="tab-content">
                        <div class="tab-pane fade show active" id="tournaments">
                            <div class="tournament-table">
                                <div class="button-group">
                                    <a href="createTournament.jsp" class="btn btn-light">Create/Join Tournament</a>
                                </div>
                                <% 
                                    	Object lists = request.getAttribute("userBrackets");
                                    	if(lists != null)
                                    	{
                                    		List<Stack<BracketOverview>> stackList = (List<Stack<BracketOverview>>)lists;
                                    		Stack<BracketOverview> pending = stackList.get(0);
                                    		Stack<BracketOverview> active = stackList.get(1);
                                    		Stack<BracketOverview> finished = stackList.get(2);
                           		 %>
                                <div class="table-content">
                                    <h4 class="mt-2">Active Tournaments</h4>
                                    
                                            		
                                    <form action="" method="post" name="activeform">
                                    <table>
                                        <thead>
                                        <tr>
                                            <th class="table-heading">Tournament</th>
                                            <th class="table-heading">Host</th>
                                            <th class="table-heading">Game Type</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                           	<% 
	                                               while(!active.isEmpty())
	                                               {
	                                                   BracketOverview nextBracket = active.pop();
	                                                   String name = nextBracket.getName();
	                                                   String hostName = nextBracket.getHostName();
	                                                   int id = nextBracket.getId();
	                                                   String gameTypeActive = nextBracket.getGameType();
                                            %>
                                        <tr>
                                                <td class="item-text"><a href="DisplayBracket?bracketID=<%=id%>"><%= name %></a></td>
                                                <td class="item-text"><%= hostName %></td>
                                                <td class="item-text"><%= gameTypeActive %></td>
                                        </tr>
                                            <%
                                                }
                                            %>
                                        </tbody>
                                    </table>
                                    </form>
                                </div>
                                <h4 class="mt-2">Pending Tournaments</h4>
                                <form action="" method="post" name="pendingform">
                                    <table>
                                        <thead>
                                        <tr>
                                            <th class="table-heading">Tournament</th>
                                            <th class="table-heading">Host</th>
                                            <th class="table-heading">Game Type</th>
                                            <th class="table-heading">Vacant Spots</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        	<% 
                                               while(!pending.isEmpty())
                                               {
                                                   BracketOverview nextBracket = pending.pop();
                                                   String name = nextBracket.getName();
                                                   String hostName = nextBracket.getHostName();
                                                   int vacantSpots = nextBracket.getVacantSpots();
                                                   int id = nextBracket.getId();
                                                   String gameTypePending = nextBracket.getGameType();
                                            %>
                                            <tr>
                                            	<td class="item-text"><a href="DisplayBracket?bracketID=<%=id%>"><%= name %></a></td>
                                                <td class="item-text"><%= hostName %></td>
                                            	<td class="item-text"><%= gameTypePending %></td>
                                                <td class="item-text"><%= vacantSpots %></td>
                                            </tr>
                                            <%
                                                }
                                            %>
                                        </tbody>
                                    </table>
                                </form>
                                <h4 class="mt-2">Finished Tournaments</h4>
                                <form action="" method="post" name="finishedform">
                                    <table>
                                        <thead>
                                        <tr>
                                            <th class="table-heading">Tournament</th>
                                            <th class="table-heading">Host</th>
                                            <th class="table-heading">Game Type</th>
                                            <th class="table-heading">Winner</th>
                                         </tr>
                                        </thead>
                                        <tbody>
                                        	<% 
                                               while(!finished.isEmpty())
                                               {
                                                   BracketOverview nextBracket = finished.pop();
                                                   String name = nextBracket.getName();
                                                   String hostName = nextBracket.getHostName();
                                                   String winner = nextBracket.getWinnerName();
                                                   int id = nextBracket.getId();
                                                   String gameTypeFinished = nextBracket.getGameType();
                                            %>
                                            <tr>
                                                <td class="item-text"><a href="DisplayBracket?bracketID=<%=id%>"><%= name %></a></td>
                                                <td class="item-text"><%= hostName %></td>
                                                <td class="item-text"><%= gameTypeFinished %></td>
                                                <td class="item-text"><%= winner %></td>
                                            </tr>
                                            <%
                                                }
                                            %>
                                        </tbody>
                                    </table>
                                </form>
                                <%
                                	} // end if
                                %>
                            </div>
                        </div>
                        <div class="tab-pane fade" id="friends">
                            <div class="friends-table">
                            <!-- start form for adding friend -->
                                <div class="add-friend-content">
                                <% 
                                    	Object friendLists = request.getAttribute("friends");
                            			String error = "not null";
                            			if(friendLists == null)
                            			{
                            				error = "error! null!";
                            			}
                                    	if(friendLists != null)
                                    	{
                                    		Vector<Queue<Friend>> friendQueues = (Vector<Queue<Friend>>)friendLists;
                                    		Queue<Friend> received = friendQueues.get(0);
                                    		error = ((Integer)received.size()).toString();
                                    		Queue<Friend> pending = friendQueues.get(1);
                                    		error += ((Integer)pending.size()).toString();
                                    		Queue<Friend> ourFriends = friendQueues.get(2);
                                    		error += ((Integer)ourFriends.size()).toString();
                           		 %>
									<form method="post" action="AddFriendServlet" class="add-form" name="addForm">
										<input class="form-control" type="text" name="friendInput" placeholder="Enter username">
								    	<button type="button" class="btn btn-light" id="add-button" onclick="addFriend()">Add Friend</button>
									</form>
									<div id="addResponse" class="addError"></div>   
									<div><%= error %></div>       
								</div>
                                <!-- End form for adding friend -->
                                <div class="table-content">
                                    <h4 class="mt-2">Friends</h4>
                                    <!--Editable Table-->
                                    <form action="" method="post" name="friends-list-form">
                                        <table>
                                            <thead>
                                            
                                            <tr>
                                                <th class="table-heading">Username</th>
                                                <th class="table-heading">Ranking</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                                <tr>
                                                <% 
	                                               while(!ourFriends.isEmpty())
	                                               {
	                                                   Friend nextFriend = ourFriends.remove(); // make sure this works how i think
	                                                   String friendName = nextFriend.getName();
	                                                   int ranking = nextFriend.getElo(); // change so that we compute ranking
                                           		 %>
                                                    <td class="item-text" id="friend-username"><%= friendName %></td>
                                                    <td class="item-text"><%= ranking %></td>
                                                </tr>
	                                            <%
	                                                }
	                                            %>
                                            </tbody>
                                        </table>
                                    </form>
                                    <!-- Start of Friend Requests-->
                                    <h4 class="mt-2">Friend Requests</h4>
                                    <!--Editable Table-->
                                    <form action="" method="post" name="friend-requests-form">
                                        <table>
                                            <thead>
                                            <tr>
                                                <th class="table-heading" id="friend-username">Username</th>
                                                <th class="table-heading">Action</th>
                                                <th class="table-heading">ELO</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            	<% 
	                                               while(!received.isEmpty())
	                                               {
	                                                   Friend nextFriend = received.remove(); // make sure this works how i think
	                                                   String requesterName = nextFriend.getName();
	                                                   int ELO = nextFriend.getElo(); 
                                           		 %>
                                                <tr>
                                                    <td class="item-text" id="friend-username"><%= requesterName %></td>
                                                    <td><button type="button" class="btn btn-light" id="accept-button" onclick="acceptFriend('<%=requesterName%>')">Accept</button></td>
                                                    <td class="item-text"><%= ELO %></td>
                                                </tr>
                                                <%
	                                               }
                                                %>
                                            </tbody>
                                        </table>
                                    </form>
                                    <!-- End of Friend Requests-->
                                    <!-- Start of Pending Requests-->
                                    <h4 class="mt-2">Pending Requests</h4>
                                    <!--Editable Table-->
                                    <form action="" method="post" name="pending-requests-form">
                                        <table>
                                            <thead>
                                            <tr>
                                                <th class="table-heading">Username</th>
                                                <th class="table-heading">Action</th>
                                                <th class="table-heading"></th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            	<% 
	                                               while(!pending.isEmpty())
	                                               {
	                                                   Friend nextFriend = pending.remove(); // make sure this works how i think
	                                                   String sentFriendName = nextFriend.getName();
	                                                   int friendId = nextFriend.getId();
                                           		 %>
                                                <tr>
                                                    <td class="item-text" id="friend-username"><%= sentFriendName %></td>
                                                    <td><button type="button" class="btn btn-danger" id="cancel-button" onclick="cancelRequest(<%= friendId %>)">Cancel Request</button></td>
                                                    <td class="item-text">Pending</td>
                                                </tr>
                                                <%
	                                               }
                                                %>
                                            </tbody>
                                        </table>
                                    </form>
                                    <!-- End of Pending Requests-->
                                    <%
                                   		} // end if statement
                                	%>
                                </div>
                                
                            </div> <!-- End Friends Table -->
                        </div> <!--End Friends Tab-->
                        <div class="tab-pane fade" id="stats">
                            <div class="stats-table">
                            <%
                           		Object checkNull = request.getAttribute("numPlayed");
	                            int numPlayed = 0;
	                    		int numWins = 0;
	                    		int numLosses = 0;
	                    		double avgOppW5 = 0.0;
	                    		double avgOppW20 = 0;
	                    		double avgOppWAll = 0;
	                    		double avgOppL5 = 0;
	                    		double avgOppL20 = 0;
	                    		double avgOppLAll = 0;
	                    		int win25 = 0;
	                    		int win50 = 0;
	                    		int win75 = 0;
	                    		int lose25 = 0;
	                    		int lose50 = 0;
	                    		int lose75 = 0;
	                    		double avgOppRank = 0;
	                    		double avgRound = 0;
	                    		int numBWins = 0;
	                    		int elo = 1000;
	                    		boolean gamePlayed = false;
	                    		String img5 = "";
	                    		String img20 = "";
	                    		String imgAll = "";
	                    		String displayUser = "";
	                    		if(session.getAttribute("username") != null)
	                    		{
	                    			displayUser = (String)session.getAttribute("username");
	                    		}
	                    		if(checkNull != null && ((Integer)checkNull != 0))
                            	{
	                    			
                            		numPlayed = (Integer)checkNull;
                            		
                            		// int
		                            //numPlayed = (int)request.getAttribute("numPlayed");
		            				numWins = (Integer)request.getAttribute("numWins");
		            				numLosses = (Integer)request.getAttribute("numLosses");
		            	
		            				// doubles
		            				avgOppW5 = (Double)request.getAttribute("avgOppW5");
		            				avgOppW20 = (Double)request.getAttribute("avgOppW20");
		            				avgOppWAll = (Double)request.getAttribute("avgOppWAll");
		            				
		            				// doubles 
		            				avgOppL5 = (Double)request.getAttribute("avgOppL5");
		            				avgOppL20 = (Double)request.getAttribute("avgOppL20");
		            				avgOppLAll = (Double)request.getAttribute("avgOppLAll");
		            	
		            				// int
		            				win25 = (Integer)request.getAttribute("win25");
		            				win50 = (Integer)request.getAttribute("win50");
		            				win75 = (Integer)request.getAttribute("win75");
		            				lose25 = (Integer)request.getAttribute("lose25");
		            				lose50 = (Integer)request.getAttribute("lose50");
		            				lose75 = (Integer)request.getAttribute("lose75");
		            				
		            				// double
		            				avgOppRank = (Double)request.getAttribute("avgOppRank");
		            				avgRound = (Double)request.getAttribute("avgRound");
		            				//int
		            				numBWins = (Integer)request.getAttribute("numBWins");
		            				// int
		            				elo = (Integer)request.getAttribute("elo");
		            				img5 = (String)request.getAttribute("img5");
		            				img20 = (String)request.getAttribute("img20");
		            				imgAll = (String)request.getAttribute("imgAll");
		            				gamePlayed = true;
                            	}
                            
                            %>
                                <h4 class="mt-2"><%=displayUser%>'s Stats</h4>
                                <p class="stat-heading"><strong>ELO: </strong></p>
                                <p id="curElo" class="stat"><%= elo %></p>
                                <!-- Start Images Row -->
	                                <div class="row" id="graph-row" align="center">
										<div class="col-4">
											<p class="graph-heading"><strong>Past 5 Games:</strong></p>
											<%
												if(gamePlayed)
												{
													out.println("<img src=\"" + img5 + "\" alt=\"Graph of last 5 games\" class=\"graph\">");
												}
											%>
										</div>
										<div class="col-4">
											<p class="graph-heading"><strong>Past 20 Games:</strong></p>
											<%
												if(gamePlayed)
												{
													out.println("<img src=\"" + img20 + "\" alt=\"Graph of last 20 games\" class=\"graph\">");
												}
											%>
										</div>
										<div class="col-4">
											<p class="graph-heading"><strong>All Games:</strong></p>
											<%
												if(gamePlayed)
												{
													out.println("<img src=\"" + imgAll + "\" alt=\"Graph of all games\" class=\"graph\">");
												}
											%>
										</div>
									</div>
								<!-- End Images Row -->
								<!-- Start Other Stats Row -->
									<div class="row" id="other-stats-row" align="center">
										<div class="col-4" id="winCol">
											<p class="stat-heading"><strong>Win Opponent Rank: </strong></p><br/>
                                			<p id="medWinOppRank" class="stat">Median: <%= win50 %></p><br/>
                                			<p id="winOppRank25" class="stat">25th Percentile: <%= win25 %></p><br/>
                                			<p id="winOppRank75" class="stat">75th Percentile: <%= win75 %></p><br/> 			
										</div>
										<!-- Start Pie Chart -->
										<div class="col-4" id="pieChartCol">
											<div id="piechart"></div>
												<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
												<script type="text/javascript">
													// Load google charts
													google.charts.load('current', {'packages':['corechart']});
													google.charts.setOnLoadCallback(drawChart);
													
													// Draw the chart and set the chart values
													function drawChart() {
													  var data = google.visualization.arrayToDataTable([
													  ['Total', 'Percentage'],
													  ['Wins', <%= numWins %>], // win value
													  ['Losses', <%= numLosses %>] // loss value
													]);
													
													  // Optional; add a title and set the width and height of the chart
													  var options = {'title':'Wins and Losses', 'width':150, 'height':200, 
															  legend:
															  {
																  position: 'none', 
																  textStyle: {color: 'white', fontSize: 10}},
															  titleTextStyle: 
															  { 
																  color: '#ffffff',
																  fontSize: 14,
																  bold: true 
															  },
															  colors: ['#db0052', 'fe7c86'], backgroundColor: { fill:'transparent' }};
													 
													  // Display the chart inside the <div> element with id="piechart"
													  var chart = new google.visualization.PieChart(document.getElementById('piechart'));
													  chart.draw(data, options);
													}
												</script>
										</div>
										<!-- End Pie Chart -->
										<div class="col-4" id="lossCol">
											<p class="stat-heading"><strong>Loss Opponent Rank: </strong></p><br/>
                                			<p id="medLossOppRank" class="stat">Median: <%= lose50 %></p><br/>
                                			<p id="lossOppRank25" class="stat">25th Percentile: <%= lose25 %></p><br/>
                                			<p id="lossOppRank75" class="stat">75th Percentile: <%= lose75 %></p><br/>
										</div>
									</div>
									<!-- End Other Stats Row -->
									<!-- Start Totals Stats Row-->
									<div class="row" id="totals-row" align="center">
										<div class="col-4" id="totalWinsCol">
											<p class="stat-heading"><strong>Total Wins: </strong></p>
                        					<p id="numWins" class="stat"><%= numWins %></p><br/>
										</div>
										<div class="col-4" id="totalLossCol">
											<p class="stat-heading"><strong>Total Losses: </strong></p>
                        					<p id="numLoss" class="stat"><%= numLosses %></p><br/>
										</div>
										<div class="col-4" id="totalGamesCol">
											<p class="stat-heading"><strong>Total Games: </strong></p>
                        					<p id="numWins" class="stat"><%= numPlayed %></p><br/>
										</div>
										
									</div>
									<!-- End Totals Stats Row -->
									<!-- Start Random Stats Row -->
									<div class="row" id="random-stats-row" align="center">
										<div class="col-4" id="avgRoundCol">
											<p class="stat-heading"><strong>Average Round: </strong></p>
                        					<p id="avgRound" class="stat"><%= avgRound %></p><br/>
										</div>
										<div class="col-4" id="extra">
										</div>
									</div>
									<!--  End Random Stats Row -->
									<%
									
									/*<div class="col-6" id="totalCol">
									<p class="stat-heading"><strong>Total Wins: </strong></p>
                        			<p id="numWins" class="stat">12</p><br/>
                        			<p class="stat-heading"><strong>Total Losses: </strong></p>
                        			<p id="numLoss" class="stat">12</p><br/>
                        			<p class="stat-heading"><strong>Total Games: </strong></p>
                        			<p id="numWins" class="stat">1234</p><br/>
                        			<p class="stat-heading"><strong>Average Round: </strong></p>
                        			<p id="avgRound" class="stat">12</p><br/>
								</div>*/
									
									%>
                            </div>
                        </div>
                    </div> <!--tab content div-->
                    <hr>
                </div> <!--Main Wrapper-->                   
              <!--End Tabs-->          
        </header>
    </body>
</html>