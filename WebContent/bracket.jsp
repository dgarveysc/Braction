<%@ page language="java" contentType="text/html; charset=UTF-8" import = "bracket.BracketOverview"
    pageEncoding="UTF-8" import="bracket.Bracket" import="java.util.List" import ="bracket.UserToStats"%>    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Tournament Bracket</title>
	<link rel="stylesheet" href="bracket.css">
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
	<script>
	
	<% 
	int bracketID=Integer.parseInt(request.getParameter("bracketID"));
	Object overview = request.getAttribute("overview");
	Object host = request.getAttribute("isHost");
	boolean pending= true;
	boolean isHost = false;
	BracketOverview b2 = null;
	if (overview != null && host != null) {
		b2 = (BracketOverview)overview;
		isHost = (boolean)host;
		pending = (b2.getType() == 0) || !isHost;
	}	
	%>
	<% if (!pending) {%>
	 $( document ).ready(function() {
         
         
	 });
	 function change(curr, opponent, next, currentSlot, opponentSlot){
  		var c = document.getElementById(curr);//current
  		var n = document.getElementById(next);//next
  		var o = document.getElementById(opponent);//opponent, the person against current	
  		if(n.value==="TBD" && c.value !== "TBD" && o.value!=="TBD"){
  			n.value=c.value;
  			
  			$.ajax({
                 url: 'UpdateBracket',
                 data: {
                     slot1 : currentSlot,
     				 slot2 : opponentSlot,
     				 bracketID: <%=bracketID%>,
     				 won : true
                 },
                 success: function (result) {
                 	if(result == "Succeeded!"){
                 		alert("scuceeded!");
                 	}
                 	else{
                 		alert(result);
                 	}
                 }
     		});
  		} else {
  			alert("That is not a valid action");
  		}
  		
  		
  	}
	<%}%>
	function submitWinner(lastWinner){
		//call servlet with whoever won
		var winner=document.getElementById(lastWinner);
		/*for(UserToStats u:currUsers){
			if(u.getUser.getName==winner) u.setWon(1);//winners get won=1
			else u.setWon(0);//players who didnt win get won=0
		}*/
	}
	
	</script>
</head>
<body>
<%
Bracket bracket=(Bracket)(request.getAttribute("bracketData"));//assuming bracket for eight users in it 
//set bracket id
List<UserToStats> currUsers=bracket.getBracket();
int index=0;
//FIRST WE NEED TO LOAD IN USERNAMES

UserToStats blap = null;
String roundTwoOne="TBD";//first winner of round two
String roundTwoTwo="TBD";//second winner of round two
String roundTwoThree="TBD";
String roundTwoFour="TBD";
String roundThreeOne="TBD";//first winner of round three
String roundThreeTwo="TBD";
String finalWinner="TBD";//final winner
if ((blap = currUsers.get(index)) != null) {
	finalWinner= blap.getUser().getName();
}
index++;
if ((blap = currUsers.get(index)) != null) {
	roundThreeOne= blap.getUser().getName();
}
index++;
if ((blap = currUsers.get(index)) != null) {
	roundThreeTwo= blap.getUser().getName();
}
index++;
if ((blap = currUsers.get(index)) != null) {
	roundTwoOne= blap.getUser().getName();
}
index++;
if ((blap = currUsers.get(index)) != null) {
	roundTwoTwo= blap.getUser().getName();
}
index++;
if ((blap = currUsers.get(index)) != null) {
	roundTwoThree= blap.getUser().getName();
}
index++;
if ((blap = currUsers.get(index)) != null) {
	roundTwoFour= blap.getUser().getName();
}
index++;

String playerOne= "TBD";
if ((blap = currUsers.get(index)) != null) {
	playerOne= blap.getUser().getName();
}
index++;
String playerTwo= "TBD";
if ((blap = currUsers.get(index)) != null) {
	 playerTwo= blap.getUser().getName();
}
index++;
String playerThree= "TBD";
if ((blap = currUsers.get(index)) != null) {
	 playerThree= blap.getUser().getName();
}
index++;
String playerFour= "TBD";
if ((blap = currUsers.get(index)) != null) {
	 playerFour= blap.getUser().getName();
}
index++;
String playerFive= "TBD";
if ((blap = currUsers.get(index)) != null) {
	 playerFive= blap.getUser().getName();
}
index++;
String playerSix= "TBD";
if ((blap = currUsers.get(index)) != null) {
	 playerSix= blap.getUser().getName();
}
index++;
String playerSeven= "TBD";
if ((blap = currUsers.get(index)) != null) {
	 playerSeven= blap.getUser().getName();
}
index++;
String playerEight = "TBD";
if ((blap = currUsers.get(index)) != null) {
	 playerEight= blap.getUser().getName();
}


%>
<div class="back">
	<form method="GET" action="Profile">
			<input type="submit" id="back-button" value="Back to Profile"/>
	</form>
</div>
 <div id="bracketName">
<%
out.print(b2.getName());
%>
</div>
<div id="theGameType">
<%
out.print("This bracket is for " + b2.getGameType());
%>
</div>
<div id="editing message">
<%if (!isHost) {
	
	out.print("Please log in as the host to edit this bracket");
} else if (pending) {
	out.print("Please wait for the bracet to be full before updating it");
}

%>
</div>
<div id="code">
<%
if (b2 != null) {
	out.print("Bracket Code: " + b2.getCode());
}
%>
</div>
<div class="wrapper">
  <div class="item">
    <div class="item-winner">
      <p>
      <input onclick="<% if(!pending) {%> submitWinner('rp41')<%}%>" type="button" value="<%=finalWinner%>" id="r4p1">
      </p>
    </div>
    <div class="item-players">
      <div class="item-player">
        <div class="item">
          <div class="item-winner">
            <p><input onclick="<% if(!pending){ %> change('r3p1','r3p2','r4p1',2,3)<%}%>" type="button" value="<%=roundThreeOne %>" id="r3p1"></p>
          </div>
          <div class="item-players">
            <div class="item-player">
              <div class="item">
                <div class="item-winner">
                  <p><input onclick="<% if(!pending){ %> change('r2p1','r2p2','r3p1',4,5)<%}%>" type="button" value="<%=roundTwoOne%>" id="r2p1"></p>
                </div>
                <div class="item-players">
                  <div class="item-player">
                    <p><input onclick="<% if(!pending){ %> change('r1p1','r1p2','r2p1',8,9)<%}%>" type="button" value="<%=playerOne%>" id="r1p1"></p>
                  </div>
                  <div class="item-player">
                    <p><input onclick="<% if(!pending){ %> change('r1p2','r1p1','r2p1',9,8)<%}%>" type="button" value="<%=playerTwo%>" id="r1p2"></p>
                  </div>
                </div>
              </div>
            </div>
            <div class="item-player">
              <div class="item">
                <div class="item-winner">
                  <p><input onclick="<% if(!pending) {%> change('r2p2','r2p1','r3p1',5,4)<%}%>" type="button" value="<%=roundTwoTwo%>" id="r2p2"></p>
                </div>
                <div class="item-players">
                  <div class="item-player">
                    <p><input onclick="<% if(!pending){ %> change('r1p3','r1p4','r2p2',10,11)<%}%>" type="button" value="<%=playerThree%>" id="r1p3"></p>
                  </div>
                  <div class="item-player">
                    <p><input onclick="<% if(!pending){ %> change('r1p4','r1p3','r2p2',11,10)<%}%>" type="button" value="<%=playerFour%>" id="r1p4"></p>
                  </div>
                </div>
              </div>
            </div> 	
          </div>
        </div>
      </div>   
      <div class="item-player">
        <div class="item">
          <div class="item-winner">
            <p><input onclick="<% if(!pending) {%> change('r3p2','r3p1','r4p1',3,2)<%}%>" type="button" value="<%=roundThreeTwo%>" id="r3p2"></p>
          </div>
          <div class="item-players">
            <div class="item-player">
              <div class="item">
                <div class="item-winner">
                  <p><input onclick="<% if(!pending) {%> change('r2p3','r2p4','r3p2',6,7)<%}%>" type="button" value="<%=roundTwoThree%>" id="r2p3"></p>
                </div>
                <div class="item-players">
                  <div class="item-player">
                    <p><input onclick="<% if(!pending) {%> change('r1p5','r1p6','r2p3',12,13)<%}%>" type="button" value="<%=playerFive%>" id="r1p5"></p>
                  </div>
                  <div class="item-player">
                    <p><input onclick="<% if(!pending) {%> change('r1p6','r1p5','r2p3',13,12)<%}%>" type="button" value="<%=playerSix%>" id="r1p6"></p>
                  </div>
                </div>
              </div>
            </div>
            <div class="item-player">
              <div class="item">
                <div class="item-winner">
                  <p><input onclick="<% if(!pending) {%> change('r2p4','r2p3','r3p2',7,6)<%}%>" type="button" value="<%=roundTwoFour%>" id="r2p4"></p>
                </div>
                <div class="item-players">
                  <div class="item-player">
                    <p><input onclick="<% if(!pending) {%> change('r1p7','r1p8','r2p4',14,15)<%}%>" type="button" value="<%=playerSeven%>" id="r1p7"></p>
                  </div>
                  <div class="item-player">
                    <p><input onclick="<% if(!pending) {%> change('r1p8','r1p7','r2p4',15,14)<%}%>" type="button" value="<%=playerEight%>" id="r1p8"></p>
                  </div>
                </div>
              </div>
            </div> 	
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
</body>
</html>