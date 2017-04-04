<%@page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>

<table border="1">
  <thead>
  <tr>
    <th>Title</th>
    <th>Author</th>
  </tr>
  </thead>
  <c:forEach items="${books}" var="book">
    <tr>
      <td><c:out value="${book.title}"/></td>
      <td><c:out value="${book.author}"/></td>
      <td><form method="post" action="${pageContext.request.contextPath}/book/delete?id=${book.id}"
                style="margin-bottom: 0;"><input type="submit" value="Delete"></form></td>
      <td><form method="post" action="${pageContext.request.contextPath}/book/update?id=${book.id}&title=${book.title}&author=${book.author}"
                style="margin-bottom: 0;"><input type="submit" value="Update"></form></td>
    </tr>
  </c:forEach>
</table>

<h2>Create new book</h2>
<c:if test="${not empty chyba}">
  <div style="border: solid 1px red; background-color: yellow; padding: 10px">
    <c:out value="${chyba}"/>
  </div>
</c:if>
<form action="${pageContext.request.contextPath}/book/create" method="post">
  <table>
    <tr>
      <th>Title of the book:</th>
      <td><input type="text" name="title" value="<c:out value='${param.title}'/>"/></td>
    </tr>
    <tr>
      <th>Author of the book:</th>
      <td><input type="text" name="author" value="<c:out value='${param.author}'/>"/></td>
    </tr>
  </table>
  <input type="Submit" value="Submit" />
</form>

</body>
</html>