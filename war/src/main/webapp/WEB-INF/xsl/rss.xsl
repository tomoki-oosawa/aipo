<!--

    Aipo is a groupware program developed by Aimluck,Inc.
    Copyright (C) 2004-2015 Aimluck,Inc.
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
<?xml version="1.0" encoding="iso-8859-1"?> 

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:downlevel="http://my.netscape.com/rdf/simple/0.9/"
                exclude-result-prefixes="downlevel rdf"
                version="1.0">

     <xsl:output indent="yes" 
                 method="html"
                 omit-xml-declaration="yes"/>                

    <xsl:param name="itemdisplayed" select="number(15)"/>
    <xsl:param name="openinpopup" select="'false'"/>
    <xsl:param name="showdescription" select="'true'"/>
    <xsl:param name="showtitle" select="'true'"/>
    <xsl:param name="showtextinput" select="'true'"/>

    <xsl:template match="/rss">
      <div>
        <xsl:apply-templates select="channel"/>
      </div>
    </xsl:template>

    <xsl:template match="/rdf:RDF">
      <div>
        <xsl:apply-templates select="downlevel:channel"/>
      </div>
    </xsl:template>

    <xsl:template match="channel">
      <xsl:variable name="description" select="description"/>    
      <xsl:if test="$showtitle = 'true' and $description">
        <p>
          <xsl:apply-templates select="image|../image" mode="channel"/>
          <xsl:value-of select="$description"/>            
        </p>
      </xsl:if>
      <ul>
        <xsl:apply-templates select="item[$itemdisplayed&gt;=position()]"/>
      </ul>
      <xsl:if test="$showtextinput = 'true'">
        <xsl:apply-templates select="textinput"/>
      </xsl:if>
    </xsl:template>

    <xsl:template match="downlevel:channel">
      <xsl:variable name="description" select="downlevel:description"/>
      <xsl:if test="$showtitle = 'true' and $description">
        <p>
        <xsl:choose>
         <xsl:when test="count(../downlevel:image)">
          <xsl:apply-templates select="../downlevel:image" mode="channel"/>
          <xsl:value-of select="$description"/>
         </xsl:when>
         <xsl:otherwise>
          <a>
           <xsl:attribute name="href"><xsl:value-of select="downlevel:link"/></xsl:attribute>
           <xsl:if test="$openinpopup = 'true'">
            <xsl:attribute name="target">_blank</xsl:attribute>
           </xsl:if>
           <xsl:value-of select="$description"/>
          </a>
         </xsl:otherwise>
        </xsl:choose>
        </p>
      </xsl:if>
      <ul>
        <xsl:apply-templates select="../downlevel:item[$itemdisplayed&gt;=position()]"/>
      </ul>
      <xsl:if test="$showtextinput = 'true'">
        <xsl:apply-templates select="downlevel:textinput"/>
      </xsl:if>
    </xsl:template>
    
    <xsl:template match="item">
      <xsl:variable name="description" select="description"/>
      <li>
        <a>
          <xsl:attribute name="href"><xsl:value-of select="link"/></xsl:attribute>
          <xsl:if test="$openinpopup = 'true'">
            <xsl:attribute name="target">_blank</xsl:attribute>
          </xsl:if>
          <xsl:value-of select="title"/>
        </a>
        <xsl:if test="$showdescription = 'true' and $description">
          <br/><xsl:value-of select="$description"/>
        </xsl:if>
      </li>
    </xsl:template>
    
    <xsl:template match="downlevel:item">
      <xsl:variable name="description" select="downlevel:description"/>
      <li>
        <a>
         <xsl:attribute name="href"><xsl:value-of select="downlevel:link"/></xsl:attribute>
          <xsl:if test="$openinpopup = 'true'">
            <xsl:attribute name="target">_blank</xsl:attribute>
          </xsl:if>
         <xsl:value-of select="downlevel:title"/>
        </a>
        <xsl:if test="$showdescription = 'true' and $description">
          <br/><xsl:value-of select="$description"/>
        </xsl:if>
      </li>
    </xsl:template>
    
    <xsl:template match="textinput">
      <form action="{link}">
        <xsl:value-of select="description"/><br/>
        <input type="text" name="{name}" value=""/>
        <input type="submit" name="submit" value="{title}"/>
      </form>
    </xsl:template>

    <xsl:template match="downlevel:textinput">
      <form action="{downlevel:link}">
        <xsl:value-of select="downlevel:description"/><br/>
        <input type="text" name="{downlevel:name}" value=""/>
        <input type="submit" name="submit" value="{downlevel:title}"/>
      </form>
    </xsl:template>
    
    <xsl:template match="image" mode="channel">
      <a>
        <xsl:attribute name="href"><xsl:value-of select="link"/></xsl:attribute>
        <xsl:if test="$openinpopup = 'true'">
          <xsl:attribute name="target">_blank</xsl:attribute>
        </xsl:if>
        <xsl:element name="img">
          <xsl:attribute name="align">right</xsl:attribute>
          <xsl:attribute name="border">0</xsl:attribute>
          <xsl:if test="title">
            <xsl:attribute name="alt">
              <xsl:value-of select="title"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="url">
            <xsl:attribute name="src">
              <xsl:value-of select="url"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="width">
            <xsl:attribute name="width">
              <xsl:value-of select="width"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="height">
            <xsl:attribute name="height">
              <xsl:value-of select="height"/>
            </xsl:attribute>
          </xsl:if>
        </xsl:element>
      </a>
    </xsl:template>

    <xsl:template match="downlevel:image" mode="channel">
      <a>
        <xsl:attribute name="href"><xsl:value-of select="downlevel:link"/></xsl:attribute>
        <xsl:if test="$openinpopup = 'true'">
          <xsl:attribute name="target">_blank</xsl:attribute>
        </xsl:if>
        <xsl:element name="img">
          <xsl:attribute name="align">right</xsl:attribute>
          <xsl:attribute name="border">0</xsl:attribute>
          <xsl:if test="downlevel:title">
            <xsl:attribute name="alt">
              <xsl:value-of select="downlevel:title"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="downlevel:url">
            <xsl:attribute name="src">
              <xsl:value-of select="downlevel:url"/>
            </xsl:attribute>
          </xsl:if>
        </xsl:element>
      </a>  
    </xsl:template>

    <!-- We ignore images unless we are inside a channel -->    
    <xsl:template match="image">
    </xsl:template>
    <xsl:template match="downlevel:image">
    </xsl:template>
    
</xsl:stylesheet>



