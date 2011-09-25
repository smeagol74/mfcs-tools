<%@ page import="com.hitsoft.mfcs.www.Style" %>
<%@ page import="com.hitsoft.mfcs.www.TableModel" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    response.setHeader("Cache-Control", "no-cache"); //HTTP 1.1
    response.setHeader("Pragma", "no-cache"); //HTTP 1.0
    response.setDateHeader("Expires", 0); //prevents caching at the proxy server
%>
<%
    TableModel table = TableModel.getFromRequest(request);
%>
<html>
<head>
    <title><%=table.title%>
    </title>
    <style type="text/css">
        table {
            border-collapse: collapse;
        }

        td {
            padding: 4px;
        }

        td.th {
            text-align: center;
            font-weight: bold;
        }

        <%
            for (Style.StyleRec style: Style.process(request) ) {
        %>
        <%=style.name%>
        {
        <% for (String key: style.values.keySet()) { %>
        <%=key%>
        :
        <%=style.values.get(key)%>
        ;
        <%  } %>
        }
        <%  } %>
    </style>
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
<br/>
<table>
    <% for (TableModel.Row row : table.legend) { %>
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
        <td id="<%=cell.getId()%>" <%=colSpan%> <%=rowSpan%> class="<%=cell.style%>"
            style="<%=cell.rawStyle%>"><%=cell.data%>
        </td>
        <% } %>
        <% } %>
    </tr>
    <% } %>
</table>
</body>
</html>
