<%--

    Aipo is a groupware program developed by TOWN, Inc.
    Copyright (C) 2004-2015 TOWN, Inc.
    http://www.aipo.com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="org.apache.jetspeed.portal.portlets.GenericMVCPortlet" %>

<%
String portletId = request.getParameter(GenericMVCPortlet.PORTLET_ID);
String docUrl = request.getParameter(GenericMVCPortlet.DOC_URL);
docUrl = URLDecoder.decode(docUrl);
%>

<script type="text/javascript">
var timerId;
var waitCount = 0;
function handleLoadDocOnLoad(portletId)
{
	// get frame
	var frame = window.frames[portletId];
	if (!frame) frame = document.body.firstChild;

	// get delayed content
	var delayedContent = '';
	if (frame.contentDocument)
	{
		delayedContent = frame.contentDocument.body.innerHTML;
	}
	else if (frame.document)
	{
		delayedContent = frame.document.body.innerHTML;
	}

	// handle content
	if (timerId) window.clearTimeout(timerId);
	if (delayedContent.length > 0)
	{
		top.handleOnLoad(portletId, delayedContent);
	}
	else	// content not loaded yet: Mozilla bug
	{
		// window.status = 'frame.contentDocument missing; waitCount=' + (waitCount++);
		// alert('delayedContent missing; waitCount=' + (waitCount++));
		timerId = window.setTimeout('handleLoadDocOnLoad("' + portletId + '")', 200);
	}
}
</script>

<frameset rows="1" onLoad="handleLoadDocOnLoad('<%= portletId %>')">
<frame name="<%= portletId %>" src="<%= docUrl %>"></frame>
</frameset>
