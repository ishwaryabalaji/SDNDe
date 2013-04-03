<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.List"%>
<%
    response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
%>

<html>
<head>
<link type="text/css" rel="stylesheet" href="../css/SDNClient.css" />
<script language="JavaScript" type="text/javascript"
	src="../js/jquery_min.js"></script>
<script language="JavaScript" type="text/javascript"
	src="../js/jquery_ui.js"></script>
<link rel="stylesheet"
	href="http://code.jquery.com/ui/1.9.1/themes/base/jquery-ui.css" />
<script language="JavaScript" type="text/javascript"
	src="../js/jWebSocket.js"></script>
</head>

<body>
	<div>

		<div>
			<div class="heading">SDN Client Debugger</div>
			<div class="components">
			<div class="select">
				<select id="selectbox1">
					<option value="">Switch-1 (S1) &hellip;</option>
					<option value="aye">Switch-2 (S2)</option>
					<option value="eh">Switch-3 (S3)</option>
					<option value="ooh">Switch-4 (S4)</option>
					<option value="whoop">Switch-5 (S5)</option>
				</select>
			</div>
			<ul id="menu-css">
				<li><a href="#">Backtrace</a></li>
				<li><a href="#">Forwardtrace</a></li>
				<li><a href="#">Watch</a></li>
				<li><a href="#">Breakpoint</a></li>
				<li><a href="#">Continue</a></li>
			</ul>
			<div id="text" class="output">Debugger Outputs</div><textarea></textarea><br /><br />
			</div>
		</div>
	</div>
	<div class="fl">
	<div style="margin-bottom: 5px;">
		<input type="button" class="button" value="Start">
		<input type="button" class="button" value ="Stop">
	</div>
	<div>
		<input type="button" class="button" style="width: 140px;" value="View / Edit Topology">
	</div>
	</div>
	<!--div id="mainContent">
		<input type="text" id ="txtbox" value=""/>
		<input type="button" onclick="sendt()"/>
		</div-->
</body>

<script>
	if (jws.browserSupportsWebSockets()) {
		jWebSocketClient = new jws.jWebSocketJSONClient();
		// Optionally enable GUI controls here
	} else {
		// Optionally disable GUI controls here
		var lMsg = jws.MSG_WS_NOT_SUPPORTED;
		alert(lMsg);
	}

	var lURL = "ws://localhost:8788";
	var gUsername = "root";
	var lPassword = gUsername;

	console.log("Connecting to " + lURL + " and logging in as '" + gUsername
			+ "'...");

	var lRes = jWebSocketClient
			.logon(
					lURL,
					gUsername,
					lPassword,
					{
						// OnOpen callback
						OnOpen : function(aEvent) {
							console
									.log("<font style='color:#888'>jWebSocket connection established.</font>");
						},
						// OnMessage callback
						OnMessage : function(aEvent, aToken) {
							console.log("<font style='color:#888'>jWebSocket '"
									+ aToken.type
									+ "' token received, full message: '"
									+ aEvent.data + "'</font>");
						},
						// OnClose callback
						OnClose : function(aEvent) {
							console
									.log("<font style='color:#888'>jWebSocket connection closed.</font>");
						}
					});

	function sendt() {
		var lRes = jWebSocketClient.sendToken($("#txtbox").val() // broadcast this message
		);
	}

	/*if( jWebSocketClient ) {
	 jWebSocketClient.close();
	 }*/
</script>
</html>

