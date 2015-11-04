<!--

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

-->
<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:output method="html" indent="yes"/>

    <xsl:template match="/content">

        <center>
        <table>
            <xsl:apply-templates select="/content/channel/topics/entry"/>
        </table>
        </center>
            
    </xsl:template>

    <xsl:template match="/content/channel/topics/entry">

        <xsl:variable name="link"          select="./image/link"/>
        <xsl:variable name="url"           select="./image/url"/>
        <xsl:variable name="title"         select="./image/title"/>

        <td>
        <a href="{$link}">
        <img src="{$url}" border="0" alt="{$title}"></img>
        </a>
        </td>

    </xsl:template>
    
</xsl:stylesheet>

