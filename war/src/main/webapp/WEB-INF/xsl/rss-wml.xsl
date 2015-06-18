<?xml version="1.0" encoding="iso-8859-1"?>
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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:downlevel="http://my.netscape.com/rdf/simple/0.9/"
                exclude-result-prefixes="downlevel rdf"
                version="1.0">

  <xsl:output indent="yes" 
              method="xml"
             omit-xml-declaration="yes"/>

  <xsl:param name="itemdisplayed" select="number(5)"/>
  <xsl:param name="showdescription" select="'false'"/>
  <xsl:param name="showtitle" select="'false'"/>
    
  <xsl:template match="/rss">
    <xsl:apply-templates select="channel"/>
  </xsl:template>

  <xsl:template match="/rdf:RDF">
    <xsl:apply-templates select="downlevel:channel"/>
  </xsl:template>
    
  <xsl:template match="channel">
      <xsl:variable name="description" select="description"/>    
      <card id="channel">
      <p><xsl:apply-templates select="title"/>
      <xsl:if test="$showtitle = 'true' and $description">
        <br/><xsl:apply-templates select="$description" />
      </xsl:if></p>
      <xsl:apply-templates select="item[$itemdisplayed&gt;=position()]"/>
      </card>
  </xsl:template>

  <xsl:template match="item">
      <xsl:variable name="description" select="description"/>    
    <p><a href="{link}"><xsl:apply-templates select="title"/></a>
    <xsl:if test="$showdescription = 'true' and $description">
      <br/><xsl:apply-templates select="$description"/>
    </xsl:if></p>
  </xsl:template>
    
  <xsl:template match="downlevel:channel">
    <xsl:variable name="description" select="downlevel:description"/>
    <card id="channel">
    <p><xsl:apply-templates select="downlevel:title"/>
    <xsl:if test="$showtitle = 'true' and $description">
      <br/><xsl:apply-templates select="$description"/>
    </xsl:if></p>
    <xsl:apply-templates select="../downlevel:item[$itemdisplayed&gt;=position()]"/>
    </card>
  </xsl:template>

  <xsl:template match="downlevel:item">
    <xsl:variable name="description" select="downlevel:description"/>
    <p><a href="{downlevel:link}"><xsl:apply-templates select="downlevel:title"/></a>
    <xsl:if test="$showdescription = 'true' and $description">
      <br/><xsl:apply-templates select="$description"/>
    </xsl:if></p>
  </xsl:template>

  <xsl:template match="text()">
    <xsl:call-template name="dollar-cleaner">
      <xsl:with-param name="chars">
        <xsl:value-of select="."/>            
        </xsl:with-param>
      </xsl:call-template>
  </xsl:template>

  <xsl:template name="dollar-cleaner">
    <xsl:param name="chars"></xsl:param>
    <xsl:choose>
    <xsl:when test="contains($chars,'$') and 
                    not(starts-with(substring-after($chars,'$'), '$'))" >
      <xsl:value-of select="substring-before($chars,'$')"
            />$$<xsl:call-template
                  name="dollar-cleaner">
                  <xsl:with-param name="chars">
                    <xsl:value-of select="substring-after($chars,'$')" />
                  </xsl:with-param>
                </xsl:call-template>
    </xsl:when>
    <xsl:otherwise><xsl:value-of select="$chars" /></xsl:otherwise>
    </xsl:choose> 
  </xsl:template>
    
</xsl:stylesheet>
