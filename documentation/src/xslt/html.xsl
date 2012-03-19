<?xml version="1.0"?>
<!--
  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
  - Kerneos
  - Copyright (C) 2010 Bull S.A.S.
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
<!DOCTYPE xsl:stylesheet [
<!ENTITY admon_gfx_path "images/">
<!ENTITY img.src.path "images/" >
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version='1.0'>
  <xsl:import href="urn:docbkx:stylesheet"/>
  
  <xsl:output method="html" encoding="UTF-8" indent="yes" />
  <!--  graphics -->
  <xsl:param name="img.src.path" select="'&img.src.path;'" />
  <!--  use graphics in admonitions -->
  <xsl:param name="admon.graphics" select="1" />
  <xsl:param name="admon.graphics.path" select="'&admon_gfx_path;'" />
  <xsl:param name="callout.graphics.path" select="'&admon_gfx_path;callouts/'" />
  <!--  chapters will be numbered -->
  <xsl:param name="chapter.autolabel" select="1" />
  <!--  sections will be numbered -->
  <xsl:param name="section.autolabel" select="1" />
  <!--  section numbers will include the chapter number -->
  <xsl:param name="section.label.includes.component.label" select="1" />
  <!--  parts will be numbered (Uppercase roman numeration )  -->
  <xsl:param name="part.autolabel" select="'I'"></xsl:param>
  <!--  component labels include the part label  -->
  <xsl:param name="component.label.includes.part.label" select="1"></xsl:param>
  <!--  stylesheet to use in the generated HTML  -->
  <xsl:param name="html.stylesheet">jasmine.css</xsl:param>
  <!--  empty paragraphs will be inserted in several contexts -->
  <xsl:param name="spacing.paras" select="'1'"></xsl:param>
  <!-- depth to which recursive sections should appear in the TOC -->
  <xsl:param name="toc.section.depth">2</xsl:param>
  <xsl:param name="simplesect.in.toc" select="0"></xsl:param>
  <!--
    - fix the build for thoses who couldn't build
    - the doc anymore -->
  <xsl:param name="language">en</xsl:param>
  <xsl:param name="annotation.support" select="1"></xsl:param>
  
  <xsl:param name="generate.toc">
    book      toc
    chapter   toc
  </xsl:param>
  
</xsl:stylesheet>
