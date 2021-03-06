<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Pepa
  Date: 05.04.2017
  Time: 0:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/styleList.css"/>
</head>
<body>
<h2>Updating book</h2>

<c:if test="${not empty chyba}">
  <div class="chyba">
    <c:out value="${chyba}"/>
  </div>
</c:if>

<form action="${pageContext.request.contextPath}/sth/postUpdateBook?id=${book.id}" method="post">
  <table>
    <tr>
      <th>Title of the book:</th>
      <td><input type="text" name="title" value="<c:out value='${param.title}'/>" placeholder="${book.title}"/></td>
    </tr>
    <tr>
      <th>Author of the book:</th>
      <td><input type="text" name="author" value="<c:out value='${param.author}'/>" placeholder="${book.author}"/></td>
    </tr>
  </table>
  <input class="submit-button" type="Submit" value="Submit" />
</form>

</body>
</html>
