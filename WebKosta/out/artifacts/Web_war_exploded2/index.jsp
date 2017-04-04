<%@ page import="cz.muni.fi.web.BookServlet" %>
<%--
  Created by IntelliJ IDEA.
  User: Pepa
  Date: 03.04.2017
  Time: 21:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<body>
<%
    String redirectURL = BookServlet.URL_MAPPING;
    response.sendRedirect(redirectURL);
%>
</body>
</html>