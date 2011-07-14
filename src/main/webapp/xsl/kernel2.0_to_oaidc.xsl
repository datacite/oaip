<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/">

    <xsl:output method="xml" omit-xml-declaration="yes" indent="yes" encoding="UTF-8" />

    <xsl:strip-space elements="*"/>

    <xsl:template match="identifier">
        <dc:identifier>
            <xsl:choose>
                <xsl:when test="string-length(@identifierType) &gt; 0">
                    <xsl:value-of select="lower-case(@identifierType)"/>
                    <xsl:text>:</xsl:text>
                </xsl:when>
            </xsl:choose>
            <xsl:value-of select="."/>
        </dc:identifier>
    </xsl:template>

    <xsl:template match="creators">
        <xsl:for-each select="creator">
            <xsl:element name="dc:creator">
                <xsl:value-of select="./creatorName"/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="titles">
        <xsl:for-each select="title">
            <xsl:element name="dc:title">
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="publisher">
        <xsl:for-each select=".">
            <xsl:element name="dc:publisher">
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="publicationYear">
        <xsl:element name="dc:date">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="subjects">
        <xsl:for-each select="subject">
            <xsl:element name="dc:subject">
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="contributors">
        <xsl:for-each select="contributor">
            <xsl:element name="dc:contributor">
                <xsl:value-of select="./contributorName"/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="dates">
        <xsl:variable name="startDate" select="string(date[lower-case(@dateType) = 'startdate'])" />
        <xsl:variable name="endDate" select="string(date[lower-case(@dateType) = 'enddate'])" />

        <xsl:if test="$startDate and $endDate">
            <xsl:element name="dc:coverage">
                <xsl:value-of select="translate($startDate,'-','')"/>
                <xsl:text>-</xsl:text>
                <xsl:value-of select="translate($endDate,'-','')"/>
            </xsl:element>
        </xsl:if>

        <xsl:for-each select="date">
            <xsl:choose>
                <!-- dcterms not supported in oai_dc
                    <xsl:when test="lower-case(@dateType) = 'accepted'">
                        <xsl:element name="dcterms:dateAccepted">
                            <xsl:value-of select="."/>
                        </xsl:element>
                    </xsl:when>
                    <xsl:when test="lower-case(@dateType) = 'available'">
                        <xsl:element name="dcterms:available">
                            <xsl:value-of select="."/>
                        </xsl:element>
                    </xsl:when>
                    <xsl:when test="lower-case(@dateType) = 'created'">
                        <xsl:element name="dcterms:created">
                            <xsl:value-of select="."/>
                        </xsl:element>
                    </xsl:when>
                    <xsl:when test="lower-case(@dateType) = 'copyrighted'">
                        <xsl:element name="dcterms:dateCopyrighted">
                            <xsl:value-of select="."/>
                        </xsl:element>
                    </xsl:when>
                    <xsl:when test="lower-case(@dateType) = 'submitted'">
                        <xsl:element name="dcterms:dateSubmitted">
                            <xsl:value-of select="."/>
                        </xsl:element>
                    </xsl:when>
                    <xsl:when test="lower-case(@dateType) = 'updated'">
                        <xsl:element name="dcterms:modified">
                            <xsl:value-of select="."/>
                        </xsl:element>
                    </xsl:when>
                -->
                <xsl:when test="lower-case(@dateType) = 'issued'">
                    <xsl:element name="dc:date">
                        <xsl:value-of select="."/>
                    </xsl:element>
                </xsl:when>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="language">
        <xsl:element name="dc:language">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="resourceType">
        <xsl:for-each select=".">
            <xsl:if test="normalize-space(@resourceTypeGeneral)">
                <xsl:element name="dc:type">
                    <xsl:value-of select="@resourceTypeGeneral"/>
                </xsl:element>
            </xsl:if>
            <xsl:if test="normalize-space(.)">
                <xsl:element name="dc:type">
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="alternateIdentifiers">
        <xsl:for-each select="alternateIdentifier">
            <xsl:element name="dc:identifier">
                <xsl:choose>
                    <xsl:when test="string-length(@alternateIdentifierType) &gt; 0">
                        <xsl:value-of select="lower-case(@alternateIdentifierType)"/>
                        <xsl:text>:</xsl:text>
                    </xsl:when>
                </xsl:choose>
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="relatedIdentifiers">
        <xsl:for-each select="relatedIdentifier">
            <xsl:element name="dc:relation">
                <xsl:choose>
                    <xsl:when test="string-length(@relatedIdentifierType) &gt; 0">
                        <xsl:value-of select="lower-case(@relatedIdentifierType)"/>
                        <xsl:text>:</xsl:text>
                    </xsl:when>
                </xsl:choose>
                <xsl:value-of select="."/>            
            </xsl:element>
        </xsl:for-each>            
    </xsl:template>

    <xsl:template match="sizes">
        <xsl:for-each select="size">
            <xsl:element name="dc:format">
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="formats">
        <xsl:for-each select="format">
            <xsl:element name="dc:format">
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="rights">
        <xsl:for-each select=".">
            <xsl:element name="dc:rights">
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="descriptions">
        <xsl:for-each select="description">
            <xsl:if test="normalize-space(@descriptionType)">
                <xsl:element name="dc:description">
                    <xsl:value-of select="@descriptionType"/>
                </xsl:element>
            </xsl:if>
            <xsl:if test="normalize-space(.)">
                <xsl:element name="dc:description">
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="resource">
        <xsl:element name="oai_dc:dc">
            <xsl:attribute name="xsi:schemaLocation">http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd</xsl:attribute>
            <xsl:namespace name="dc">http://purl.org/dc/elements/1.1/</xsl:namespace>            
            <xsl:apply-templates select="titles"/>
            <xsl:apply-templates select="creators"/>
            <xsl:apply-templates select="publisher"/>
            <xsl:apply-templates select="publicationYear"/>
            <xsl:apply-templates select="identifier"/>
            <xsl:apply-templates select="alternateIdentifiers"/>
            <xsl:apply-templates select="relatedIdentifiers"/>
            <xsl:apply-templates select="subjects"/>
            <xsl:apply-templates select="descriptions"/>
            <xsl:apply-templates select="contributors"/>
            <xsl:apply-templates select="dates"/>
            <xsl:apply-templates select="language"/>
            <xsl:apply-templates select="resourceType"/>
            <xsl:apply-templates select="sizes"/>
            <xsl:apply-templates select="formats"/>
            <xsl:apply-templates select="rights"/>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>
