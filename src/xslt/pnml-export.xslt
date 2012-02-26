<?xml version="1.0" encoding="UTF-8"?>
<!--
Copyright (C) 2008-2010 Martin Riesz <riesz.martin at gmail.com>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
-->
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="xml" omit-xml-declaration="no" indent="yes"/>

<xsl:template match="/document">
<pnml>
	<net type="VIPschema.xsd">
		<xsl:call-template name="subnet">
			<!-- use this to translate all elements to positive coordinates: -->
			<xsl:with-param name="x"><xsl:value-of select="-left"/></xsl:with-param>
			<xsl:with-param name="y"><xsl:value-of select="-top"/></xsl:with-param>
			<!--instead of following 2 lines: -->
			<!-- <xsl:with-param name="x">0</xsl:with-param>
			<xsl:with-param name="y">0</xsl:with-param>-->
			<xsl:with-param name="label"></xsl:with-param>
		</xsl:call-template>
	</net>
</pnml>
</xsl:template>

<xsl:template name="label">
	<xsl:param name="subnetlabel"/>
	<xsl:choose>
		<xsl:when test="not(string(label))">
			<value>&#160;</value> <!-- empty label -->
		</xsl:when>
		<xsl:when test="not(string($subnetlabel))">
			<value><xsl:value-of select="label"/></value>
		</xsl:when>
		<xsl:otherwise>
			<value><xsl:value-of select="concat($subnetlabel,'.',label)"/></value>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template name="subnet">
	<xsl:param name="x"/>
	<xsl:param name="y"/>
	<xsl:param name="label"/>
	<xsl:for-each select="place">
		<place provider="petrinet.provider.place">
			<xsl:attribute name="id"><xsl:value-of select="id"/></xsl:attribute>
			<name>
				<xsl:call-template name="label">
					<xsl:with-param name="subnetlabel"><xsl:value-of select="$label"/></xsl:with-param>
				</xsl:call-template>
				<graphics>
					<offset x="5" y="33"/>
					<fill color="rgb(255, 255, 255)"/>
					<line color="rgb(0, 0, 0)" shape="line" style="solid" width="1"/>
					<font family="SansSerif" orientation="0" posture="0.0" rotation="0.0" size="10.0" weight="1.0"/>
				</graphics>
			</name>
			<initialMarking>
				<token id="viptool.petrinet.datastructure.Token@1000" provider="petrinet.provider.token">
					<value><xsl:value-of select="tokens"/></value>
				</token>
			</initialMarking>
			<graphics>
				<position>
					<xsl:attribute name="x"><xsl:value-of select="x+$x"/></xsl:attribute>
					<xsl:attribute name="y"><xsl:value-of select="y+$y"/></xsl:attribute>
				</position>
				<dimension x="32" y="32"/>
				<fill color="rgb(255, 255, 255)"/>
				<line color="rgb(0, 0, 0)" shape="curve" style="solid" width="1"/>
			</graphics>
		</place>
	</xsl:for-each>
	<xsl:for-each select="transition">
		<transition provider="petrinet.provider.transition">
			<xsl:attribute name="id"><xsl:value-of select="id"/></xsl:attribute>
			<name>
				<xsl:call-template name="label">
					<xsl:with-param name="subnetlabel"><xsl:value-of select="$label"/></xsl:with-param>
				</xsl:call-template>
				<graphics>
					<offset x="4" y="31"/>
					<fill color="rgb(255, 255, 255)"/>
					<line color="rgb(0, 0, 0)" shape="line" style="solid" width="1"/>
					<font family="SansSerif" orientation="0" posture="0.0" rotation="0.0" size="10.0" weight="1.0"/>
				</graphics>
			</name>
			<graphics>
				<position>
					<xsl:attribute name="x"><xsl:value-of select="x+$x"/></xsl:attribute>
					<xsl:attribute name="y"><xsl:value-of select="y+$y"/></xsl:attribute>
				</position>
				<dimension x="32" y="32"/>
				<fill color="rgb(255, 255, 255)"/>
				<line color="rgb(0, 0, 0)" shape="line" style="solid" width="1"/>
			</graphics>
		</transition>
	</xsl:for-each>
	<xsl:for-each select="arc">
		<arc provider="petrinet.provider.weightededge">
			<xsl:attribute name="id"><xsl:value-of select="id"/></xsl:attribute>
			<xsl:attribute name="source"><xsl:value-of select="realSourceId"/></xsl:attribute>
			<xsl:attribute name="target"><xsl:value-of select="realDestinationId"/></xsl:attribute>
			<inscription>
				<value><xsl:value-of select="multiplicity"/></value>
				<graphics>
					<anchor distance="30.0" segment="0"/>
					<offset x="4" y="-7"/>
					<fill color="rgb(255, 255, 255)"/>
					<line color="rgb(0, 0, 0)" shape="line" style="solid" width="1"/>
					<font family="SansSerif" orientation="1" posture="0.0" rotation="0.0" size="10.0" weight="1.0"/>
				</graphics>
			</inscription>
			<graphics>
				<line color="rgb(0, 0, 0)" shape="line" style="solid" width="1"/>
				<xsl:for-each select="breakPoint">
					<position>
						<xsl:attribute name="x"><xsl:value-of select="x+$x"/></xsl:attribute>
						<xsl:attribute name="y"><xsl:value-of select="y+$y"/></xsl:attribute>
					</position>
				</xsl:for-each>
			</graphics>
		</arc>
	</xsl:for-each>
	<xsl:for-each select="subnet">
		<xsl:call-template name="subnet">
			<xsl:with-param name="x"><xsl:value-of select="x+$x"/></xsl:with-param>
			<xsl:with-param name="y"><xsl:value-of select="y+$y"/></xsl:with-param>
			<xsl:with-param name="label">
				<xsl:value-of select="$label"/>
				<xsl:if test="string(label) and string($label)">.</xsl:if>
				<xsl:value-of select="label"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:for-each>
</xsl:template>

</xsl:transform>