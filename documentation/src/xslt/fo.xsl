<?xml version="1.0"?>
<!--
 - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
 - JASMINe
 - Copyright (C) 2010-2011 Bull S.A.S.
 - Contact: jasmine@ow2.org
 -
 - This library is free software; you can redistribute it and/or
 - modify it under the terms of the GNU Lesser General Public
 - License as published by the Free Software Foundation; either
 - version 2.1 of the License, or any later version.
 -
 - This library is distributed in the hope that it will be useful,
 - but WITHOUT ANY WARRANTY; without even the implied warranty of
 - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 - Lesser General Public License for more details.
 -
 - You should have received a copy of the GNU Lesser General Public
 - License along with this library; if not, write to the Free Software
 - Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 - USA
 -
 -
 - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
 - $Id$
 - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
-->
<!DOCTYPE 
  xsl:stylesheet [
  <!ENTITY admon_gfx_path "${basedir}/src/resources/images/">
  <!ENTITY img.src.path "${basedir}/src/resources/images/">
  ]
  >
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:fo="http://www.w3.org/1999/XSL/Format"
 version="1.0">
 
 <xsl:import href="urn:docbkx:stylesheet"/>
 <xsl:import href="titlepages-jasmine.xsl"/>
 
 <!--###################################################
  Header
  ################################################### -->
 
 <!-- More space in the center header for long text -->
 <xsl:attribute-set name="header.content.properties">
  <xsl:attribute name="font-family">
   <xsl:value-of select="$body.font.family"/>
  </xsl:attribute>
  <xsl:attribute name="margin-left">-5em</xsl:attribute>
  <xsl:attribute name="margin-right">-5em</xsl:attribute>
 </xsl:attribute-set>
 
 <!--###################################################
  Custom Footer
  ################################################### -->
 
 <!-- This footer prints the version number on the left side -->
 <xsl:template name="footer.content">
  <xsl:param name="pageclass" select="''"/>
  <xsl:param name="sequence" select="''"/>
  <xsl:param name="position" select="''"/>
  <xsl:param name="gentext-key" select="''"/>
  
  <xsl:variable name="Version">
   <xsl:choose>
    <xsl:when test="//releaseinfo">
     <xsl:text>JASMINe </xsl:text>
     <xsl:value-of select="//releaseinfo"/>
    </xsl:when>
    <xsl:otherwise>
     <!-- nop -->
    </xsl:otherwise>
   </xsl:choose>
  </xsl:variable>
  
  <xsl:choose>
   <xsl:when test="$sequence='blank'">
    <xsl:choose>
     <xsl:when test="$double.sided != 0 and $position = 'left'">
      <xsl:value-of select="$Version"/>
     </xsl:when>
     
     <xsl:when test="$double.sided = 0 and $position = 'center'">
      <!-- nop -->
     </xsl:when>
     
     <xsl:otherwise>
      <fo:page-number/>
     </xsl:otherwise>
    </xsl:choose>
   </xsl:when>
   
   <xsl:when test="$pageclass='titlepage'">
    <!-- nop: other titlepage sequences have no footer -->
   </xsl:when>
   
   <xsl:when test="$double.sided != 0 and $sequence = 'even' and $position='left'">
    <fo:page-number/>
   </xsl:when>
   
   <xsl:when test="$double.sided != 0 and $sequence = 'odd' and $position='right'">
    <fo:page-number/>
   </xsl:when>
   
   <xsl:when test="$double.sided = 0 and $position='right'">
    <fo:page-number/>
   </xsl:when>
   
   <xsl:when test="$double.sided != 0 and $sequence = 'odd' and $position='left'">
    <xsl:value-of select="$Version"/>
   </xsl:when>
   
   <xsl:when test="$double.sided != 0 and $sequence = 'even' and $position='right'">
    <xsl:value-of select="$Version"/>
   </xsl:when>
   
   <xsl:when test="$double.sided = 0 and $position='left'">
    <xsl:value-of select="$Version"/>
   </xsl:when>
   
   <xsl:otherwise>
    <!-- nop -->
   </xsl:otherwise>
  </xsl:choose>
 </xsl:template>
 
 <xsl:param name="paper.type" select="'A4'"/>
 
 <!--###################################################
  Fonts & Styles
  ################################################### -->
 
 <!-- Default font settings -->
 <xsl:param name="title.font.family">sans-serif</xsl:param>
 <xsl:param name="body.font.family">sans-serif</xsl:param>
 <xsl:param name="sans.font.family">sans-serif</xsl:param>
 <xsl:param name="dingbat.font.family">sans-serif</xsl:param>
 <xsl:param name="monospace.font.family">monospace</xsl:param>
 <xsl:param name="symbol.font.family">Symbol,ZapfDingbats</xsl:param>
 
 <!-- Left aligned text and no hyphenation -->
 <xsl:param name="alignment">justify</xsl:param>
 <xsl:param name="hyphenate">false</xsl:param>
 
 <xsl:param name="img.src.path" select="'&img.src.path;'"/>
 <xsl:param name="keep.relative.image.uris" select="1"/>
 <!--  use graphics in admonitions -->
 <xsl:param name="admon.graphics" select="1"/>
 <xsl:param name="admon.graphics.path" select="'&admon_gfx_path;'"/>
 <!-- don't use graphics for callout -->
 <xsl:param name="callout.graphics" select="0"/>
 <!-- depth to which recursive sections should appear in the TOC -->
 <xsl:param name="toc.section.depth">2</xsl:param>
 <!--  chapters will be numbered -->
 <xsl:param name="chapter.autolabel" select="1"/>
 <!--  sections will be numbered -->
 <xsl:param name="section.autolabel" select="1"/>
 <!--  section numbers will include the chapter number -->
 <xsl:param name="section.label.includes.component.label" select="1"/>
 <!-- ProgramListing/Screen has a background color -->
 <xsl:param name="shade.verbatim">1</xsl:param>
 <xsl:attribute-set name="shade.verbatim.style">
  <xsl:attribute name="background-color">#fffbe0</xsl:attribute>
 </xsl:attribute-set>
 <!-- Reduce size of program listing font and add a border -->
 <xsl:attribute-set name="verbatim.properties">
  <xsl:attribute name="space-before.minimum">1em</xsl:attribute>
  <xsl:attribute name="space-before.optimum">1em</xsl:attribute>
  <xsl:attribute name="space-before.maximum">1em</xsl:attribute>
  <xsl:attribute name="font-size">7pt</xsl:attribute>
  <xsl:attribute name="border-width">1px</xsl:attribute>
  <xsl:attribute name="border-style">dashed</xsl:attribute>
  <xsl:attribute name="border-color">#9999cc</xsl:attribute>
  <xsl:attribute name="padding-top">0.5em</xsl:attribute>
  <xsl:attribute name="padding-left">0.5em</xsl:attribute>
  <xsl:attribute name="padding-right">0.5em</xsl:attribute>
  <xsl:attribute name="padding-bottom">0.5em</xsl:attribute>
  <xsl:attribute name="margin-left">0.5em</xsl:attribute>
  <xsl:attribute name="margin-right">0.5em</xsl:attribute>
 </xsl:attribute-set>
 <!-- Allow to wrap long lines for program listing -->
 <xsl:param name="hyphenate.verbatim" select="0"/>
 <xsl:attribute-set name="monospace.verbatim.properties">
  <xsl:attribute name="wrap-option">wrap</xsl:attribute>
  <xsl:attribute name="hyphenation-character">\</xsl:attribute>
 </xsl:attribute-set>
 <!-- for getting bookmarks in pdf document -->
 <xsl:param name="fop1.extensions" select="1"/>
 
</xsl:stylesheet>
