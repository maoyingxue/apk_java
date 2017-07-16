<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<form method="post" action="http://localhost:8080/QuanDi/m/user/login.do">
	deviceId<input type="text" name="deviceId" value="8"><br>
	phoneNumber<input type="text" name="phoneNumber" value="8"><br>
	pwdMd5<input type="text" name="pwdMd5" value="8"><br> 
	appId<input type="text" name="appId" value="1"><br> 
	userId<input type="text" name="userId"><br> 
	<input type="submit">
</form>
</body>
</html>