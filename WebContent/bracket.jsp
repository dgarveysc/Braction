<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="bracket.Bracket" import="java.util.List" import ="bracket.UserToStats"%>    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Tournament Bracket</title>
	<link rel="stylesheet" href="bracket.css">
	<script>
	function change(curr, opponent, next, currentSlot, opponentSlot){
		var c = document.getElementById(curr);//current
		var n = document.getElementById(next);//next
		var o = document.getElementById(opponent);//opponent, the person against current	
		
		if(n.value=="TBD") {//if player wins
			n.value=c.value;//move current player to the next round
			var xhttp=new XMLHttpRequest();
			xhttp.open("POST","DisplayBracket.java"+currentSlot+true+bracketID);//call to servlet
			xhttp.send();
		}
	}
	
	function submitWinner(lastWinner){
		//call servlet with whoever won
		var winner=document.getElementById(lastWinner);
		for(UserToStats u:currUsers){
			if(u.getUser.getName==winner) u.setWon(1);//winners get won=1
			else u.setWon(0);//players who didnt win get won=0
		}
	}
	
	</script>
</head>
<body>
<%
Bracket bracket=(Bracket)(request.getAttribute("bracketData"));//assuming bracket for eight users in it 
int bracketID=Integer.parseInt(request.getParameter("bracketID"));//set bracket id
List<UserToStats> currUsers=bracket.getBracket();
int index=0;
//FIRST WE NEED TO LOAD IN USERNAMES
String playerOne=currUsers.get(index).getUser.getName();
index++;
String playerTwo=currUsers.get(index).getUser.getName();
index++;
String playerThree=currUsers.get(index).getUser.getName();
index++;
String playerFour=currUsers.get(index).getUser.getName();
index++;
String playerFive=currUsers.get(index).getUser.getName();
index++;
String playerSix=currUsers.get(index).getUser.getName();
index++;
String playerSeven=currUsers.get(index).getUser.getName();
index++;
String playerEight=currUsers.get(index).getUser.getName();
String roundTwoOne="TBD";//first winner of round two
String roundTwoTwo="TBD";//second winner of round two
String roundTwoThree="TBD";
String roundTwoFour="TBD";
String roundThreeOne="TBD";//first winner of round three
String roundThreeTwo="TBD";
String finalWinner="TBD";//final winner
%>
<div class="wrapper">
  <div class="item">
    <div class="item-winner">
      <p>
      <input onclick="submitWinner('rp41')" type="button" value=<%=finalWinner%> id="r4p1">
      </p>
    </div>
    <div class="item-players">
      <div class="item-player">
        <div class="item">
          <div class="item-winner">
            <p><input onclick="change('r3p1','r3p2','r4p1',2,3)" type="button" value=<%=roundThreeOne%> id="r3p1"></p>
          </div>
          <div class="item-players">
            <div class="item-player">
              <div class="item">
                <div class="item-winner">
                  <p><input onclick="change('r2p1','r2p2','r3p1',4,5)" type="button" value=<%=roundTwoOne%> id="r2p1"></p>
                </div>
                <div class="item-players">
                  <div class="item-player">
                    <p><input onclick="change('r1p1','r1p2','r2p1',8,9)" type="button" value=<%=playerOne%> id="r1p1"></p>
                  </div>
                  <div class="item-player">
                    <p><input onclick="change('r1p2','r1p1','r2p1',9,8)" type="button" value=<%=playerTwo%> id="r1p2"></p>
                  </div>
                </div>
              </div>
            </div>
            <div class="item-player">
              <div class="item">
                <div class="item-winner">
                  <p><input onclick="change('r2p2','r2p1','r3p1',5,4)" type="button" value=<%=roundTwoTwo%> id="r2p2"></p>
                </div>
                <div class="item-players">
                  <div class="item-player">
                    <p><input onclick="change('r1p3','r1p4','r2p2',10,11)" type="button" value=<%=playerThree%> id="r1p3"></p>
                  </div>
                  <div class="item-player">
                    <p><input onclick="change('r1p4','r1p3','r2p2',11,10)" type="button" value=<%=playerFour%> id="r1p4"></p>
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
            <p><input onclick="change('r3p2','r3p1','r4p1',3,2)" type="button" value=<%=roundThreeTwo%> id="r3p2"></p>
          </div>
          <div class="item-players">
            <div class="item-player">
              <div class="item">
                <div class="item-winner">
                  <p><input onclick="change('r2p3','r2p4','r3p2',6,7)" type="button" value=<%=roundTwoThree%> id="r2p3"></p>
                </div>
                <div class="item-players">
                  <div class="item-player">
                    <p><input onclick="change('r1p5','r1p6','r2p3',12,13)" type="button" value=<%=playerFive%> id="r1p5"></p>
                  </div>
                  <div class="item-player">
                    <p><input onclick="change('r1p6','r1p5','r2p3',13,12)" type="button" value=<%=playerSix%> id="r1p6"></p>
                  </div>
                </div>
              </div>
            </div>
            <div class="item-player">
              <div class="item">
                <div class="item-winner">
                  <p><input onclick="change('r2p4','r2p3','r3p2',7,6)" type="button" value=<%=roundTwoFour%> id="r2p4"></p>
                </div>
                <div class="item-players">
                  <div class="item-player">
                    <p><input onclick="change('r1p7','r1p8','r2p4',14,15)" type="button" value=<%=playerSeven%> id="r1p7"></p>
                  </div>
                  <div class="item-player">
                    <p><input onclick="change('r1p8','r1p7','r2p4',15,14)" type="button" value=<%=playerEight%> id="r1p8"></p>
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