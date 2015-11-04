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
<xsl:stylesheet version="1.0"
                xmlns:jcm="http://jakarta.apache.org/jetspeed/xml/jetspeed-portal-content"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="html" indent="yes"/>

    <xsl:template match="/jcm:content">

        <table border="0" cellspacing="0" cellpadding="0">


            <!--
            <xsl:call-template name="newest-topics"/>
            -->
            <xsl:apply-templates select="/jcm:content/jcm:channel/jcm:item"/>
        </table>
    </xsl:template>

    <xsl:template match="/jcm:content/jcm:channel/jcm:item">

  <xsl:if test="not(position() = 1)">
    <tr width="100%">
      <td width="100%" colspan="2">
        <hr noshade="noshade"><!--breaker--></hr>
      </td>
    </tr>
  </xsl:if>
  
        <tr width="100%">
        <!--
        BEGIN Add the topic icon
        -->

        
        <td  align="left" valign="top"> 
        <xsl:call-template name="topics">
            <xsl:with-param name="topic"><xsl:value-of select="jcm:topic"/></xsl:with-param>
        </xsl:call-template>
     <!--   </td> -->
        <!--
        END Add the topic icon
        -->

     <!--   <td width="100%" align="left" valign="top"> -->
        

        <a href="{jcm:link}">
        <b>
        <xsl:value-of select="jcm:title"/>
        </b>
        </a>
        

        <!--
        Add the quote if any...
        -->

        <xsl:apply-templates select="jcm:quote"/>
        

        <p align="left" clear="left">    
        <xsl:value-of select="jcm:description"/>
        </p>
        </td>
        </tr>

    </xsl:template>

    <xsl:template match="jcm:quote">

        <p align="left">    
        from: 
        <a href="{jcm:link}" target="_new">
        <xsl:value-of select="jcm:author"/>        
        </a>
        </p>
    
        <xsl:apply-templates select="jcm:p"/>

    </xsl:template>

    <xsl:template match="jcm:p">
      <p>
          <i>
              <xsl:value-of select="."/>
          </i>
      </p>
    </xsl:template>
    
    
    <xsl:template name="topics">
        <xsl:param name="topic"/>
        <xsl:variable name="link"          select="/jcm:content/jcm:channel/jcm:topics/jcm:entry[@name=$topic]/jcm:image/jcm:link"/>
        <xsl:variable name="url"           select="/jcm:content/jcm:channel/jcm:topics/jcm:entry[@name=$topic]/jcm:image/jcm:url"/>
        <xsl:variable name="title"         select="/jcm:content/jcm:channel/jcm:topics/jcm:entry[@name=$topic]/jcm:image/jcm:title"/>
        <a href="{$link}">
        <img src="{$url}" border="0" alt="{$title}" align="left" />
        </a>
    </xsl:template>


    <!--
    Get an index of the most recent topics
    -->
    <xsl:template name="newest-topics">

        <tr width="100%">
        <td colspan="2">
        <table>
        <tr width="100%" align="right">
        <td width="100%"><!-- align --> </td>
        
        <xsl:call-template name="get-entry-topic">
            <xsl:with-param name="itemId">0</xsl:with-param>
        </xsl:call-template>
    
        <xsl:call-template name="get-entry-topic">
            <xsl:with-param name="itemId">2</xsl:with-param>
        </xsl:call-template>


        </tr>
        </table>
        </td>
        </tr>
    </xsl:template>
    
    <!--
    Given an id... get the image entry for a specific topic.
    -->
    <xsl:template name="get-entry-topic">
        <xsl:param name="itemId"/>
        <!-- first get the topic name of the iten you requested -->
        
        <xsl:variable name="topic" select="/jcm:content/jcm:channel/jcm:item[$itemId]/jcm:topic"/>

        <td>

        <xsl:call-template name="topics">
            <xsl:with-param name="topic"><xsl:value-of select="$topic"/></xsl:with-param>
        </xsl:call-template>

        </td>
        

    </xsl:template>
    
</xsl:stylesheet>

