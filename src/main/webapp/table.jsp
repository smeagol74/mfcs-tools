<%@ page import="com.hitsoft.mfcs.www.TableModel" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    TableModel table = TableModel.getFromRequest(request);
%>
<html>
<head>
    <title>MFCS Table</title>
    <link href="style.jsp?maxWidth=<%=table.maxWidth%>" type="text/css" rel="stylesheet"/>
    <link href="style.css" type="text/css" rel="stylesheet"/>
</head>
<body>
<table class="<%=table.styleClass%>">
    <% for (TableModel.Row row : table.rows) { %>
    <tr>
        <% for (TableModel.Cell cell : row.cells) { %>
        <% if (cell.isVisible) {
            String colSpan = "";
            if (cell.colSpan > 1)
                colSpan = String.format("colspan=\"%d\"", cell.colSpan);
            String rowSpan = "";
            if (cell.rowSpan > 1)
                rowSpan = String.format("rowspan=\"%d\"", cell.rowSpan);
        %>
        <td id="<%=cell.getId()%>" <%=colSpan%> <%=rowSpan%> class="<%=cell.styleClass()%>"><%=cell.data%>
        </td>
        <% } %>
        <% } %>
    </tr>
    <% } %>
</table>
</body>
</html>
