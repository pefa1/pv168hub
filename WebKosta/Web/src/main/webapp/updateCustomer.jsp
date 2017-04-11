<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: pefa1
  Date: 8.4.2017
  Time: 8:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%--<link rel="stylesheet" href="styleUpdateCustomer.css">--%>
</head>
<body>
<h2>Updating customer</h2>

<c:if test="${not empty chyba1}">
    <div class="chyba">
        <c:out value="${chyba1}"/>
    </div>
</c:if>

<form action="${pageContext.request.contextPath}/sth/postUpdateCustomer?id=${customer.id}" method="post">
    <table>
        <tr>
            <th>Full Name</th>
            <td><input type="text" name="fullName" value="<c:out value='${param.fullName}'/>" placeholder="${customer.fullName}"/></td>
        </tr>
        <tr>
            <th>Email</th>
            <td><input type="text" name="email" value="<c:out value='${param.email}'/>" placeholder="${customer.email}"/></td>
        </tr>
    </table>
    <input type="Submit" value="Submit" />
</form>

</body>
</html>
