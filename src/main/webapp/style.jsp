<%@ page import="com.hitsoft.mfcs.www.Style"%><%@ page import="java.util.List"%>
<%@ page contentType="text/css;charset=UTF-8" language="java" %>
<%
response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); //HTTP 1.0
response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>
<%
    List<Style.StyleRec> recs = Style.process(request);
    for (Style.StyleRec style: recs ) {
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
