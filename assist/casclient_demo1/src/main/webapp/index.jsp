<%--
  Created by IntelliJ IDEA.
  User: XML
  Date: 2018/12/20
  Time: 15:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>                          <%--获取用户名--%>
        <h1>欢饮光临猪大肠</h1> <%=request.getRemoteUser() %><br>

<a href="http://localhost:9100/cas/logout?service=http://www.baidu.com">退出登录</a>
</body>
</html>
