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
  <h2>Updating book</h2>
  <c:if test="${not empty chyba}">
    <div style="border: solid 1px red; background-color: yellow; padding: 10px">
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
    <input type="Submit" value="Submit" />
  </form>
</head>
<body>

</body>
</html>
