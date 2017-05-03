<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core_1_1" %>
<%--
  Created by IntelliJ IDEA.
  User: pefa1
  Date: 11.4.2017
  Time: 15:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styleList.css"/>
</head>
<body>

<h2>Updating Rent</h2>

<form action="${pageContext.request.contextPath}/sth/postUpdateRent?id=${rent.id}" method="post">
    <table>
        <tr>
            <th>Expected Return Time</th>
            <td><input type="text" name="expectedReturnTime" value="<c:out value='${param.expectedReturnTime}'/>" placeholder="${rent.expectedReturnTime}"/></td>
        </tr>
    </table>
    <input class="submit-button" type="Submit" value="Submit" />
</form>

</body>
</html>
