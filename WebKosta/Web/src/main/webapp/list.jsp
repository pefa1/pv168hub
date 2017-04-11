<%@page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
  <!---<link rel="stylesheet" href="${pageContext.request.contextPath}/styleList.css"/>-->
</head>
<body>

<table border="1">
  <thead>
  <tr>
    <th>Id</th>
    <th>Title</th>
    <th>Author</th>
  </tr>
  </thead>
  <c:forEach items="${books}" var="book">
    <tr>
      <td><c:out value="${book.id}"/></td>
      <td><c:out value="${book.title}"/></td>
      <td><c:out value="${book.author}"/></td>
      <td><form method="post" action="${pageContext.request.contextPath}/sth/deleteBook?id=${book.id}"
                style="margin-bottom: 0;"><input type="submit" value="Delete"></form></td>
      <td><form method="post" action="${pageContext.request.contextPath}/sth/updateBook?id=${book.id}&title=${book.title}&author=${book.author}"
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
<form action="${pageContext.request.contextPath}/sth/addBook" method="post">
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


<table border="1">
  <thead>
  <tr>
    <th>Id</th>
    <th>Full name</th>
    <th>Email</th>
  </tr>
  </thead>
  <c:forEach items="${customers}" var="customer">
    <tr>
      <td><c:out value="${customer.id}"/></td>
      <td><c:out value="${customer.fullName}"/></td>
      <td><c:out value="${customer.email}"/></td>
      <td><form method="post" action="${pageContext.request.contextPath}/sth/deleteCustomer?id=${customer.id}"
                style="margin-bottom: 0;"><input type="submit" value="Smazat"></form></td>
    </tr>
  </c:forEach>
</table>

<h2>Create new customer</h2>
<c:if test="${not empty chyba1}">
  <div style="border: solid 1px red; background-color: yellow; padding: 10px; /*margin-left: 20px*/">
    <c:out value="${chyba1}"/>
  </div>
</c:if>

<form action="${pageContext.request.contextPath}/sth/addCustomer" method="post">
  <table>
    <tr>
      <th>Full name</th>
      <td><input type="text" name="fullName" value="<c:out value='${param.fullName}'/>"/></td>
    </tr>
    <tr>
      <th>Email:</th>
      <td><input type="text" name="email" value="<c:out value='${param.email}'/>"/></td>
    </tr>
  </table>
  <input class="submit-button" type="Submit" value="Submit" />
</form>

<table border="1">
  <thead>
  <tr>
    <th>Id</th>
    <th>Customer id</th>
    <th>Book id</th>
    <th>Start time</th>
    <th>Expected return time</th>
    <th>Return time</th>
  </tr>
  </thead>
  <c:forEach items="${rents}" var="rent">
    <tr>
      <td><c:out value="${rent.id}"/></td>
      <td><c:out value="${rent.customer.id}"/></td>
      <td><c:out value="${rent.book.id}"/></td>
      <td><c:out value="${rent.rentTime}"/></td>
      <td><c:out value="${rent.expectedReturnTime}"/></td>
      <td>
      <c:if test="${not empty rent.returnTime}">
        <c:out value="${rent.returnTime}"/>
      </c:if>
      </td>
      <td><form class="inside-form" method="post" action="${pageContext.request.contextPath}/sth/deleteRent?id=${rent.id}">
        <input class="submit-button-table" type="submit" value="Smazat"></form></td>
      <td><form class="inside-form" method="post" action="${pageContext.request.contextPath}/sth/updateRent?id=${rent.id}&expectedReturnTime=${rent.expectedReturnTime}
            &returnTime=${rent.returnTime}"><input class="submit-button-table" type="submit" value="Update"></form> </td>
      <td><form class="inside-form" method="post" action="${pageContext.request.contextPath}/sth/returnBook?id=${rent.id}">
        <input class="submit-button-table" type="submit" value="Return"></form></td>
    </tr>
  </c:forEach>
</table>

<h2>Create new rent</h2>
<c:if test="${not empty chyba2}">
  <div style="border: solid 1px red; background-color: yellow; padding: 10px; /*margin-left: 20px*/">
    <c:out value="${chyba2}"/>
  </div>
</c:if>

<form action="${pageContext.request.contextPath}/sth/addRent" method="post">
  <table>
    <tr>
      <th>Book</th>
      <td>
        <select name="books-option" size="1">
          <c:forEach items="${books}" var="book">
            <option value="${book.id}"><c:out value="${book.id}"/></option>
          </c:forEach>
        </select>
      </td>
    </tr>

    <tr>
      <th>Customer</th>
      <td>
        <select name="customers-option" size="1">
          <c:forEach items="${customers}" var="customer">
            <option value="${customer.id}"><c:out value="${customer.id}"/></option>
          </c:forEach>
        </select>
      </td>
    </tr>

    <tr>
      <th>Expected Retrun Time</th>
      <td>
        <input type="text" name="expectedReturnTime" value="<c:out value='${param.expectedReturnTime}'/>"/>
      </td>
    </tr>

  </table>
  <input class="submit-button" type="Submit" value="Submit" />
</form>

</body>
</html>