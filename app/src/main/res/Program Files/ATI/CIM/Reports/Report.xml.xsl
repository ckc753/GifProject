<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:template match="/">
    <HTML>
      <BODY>
        <center>
        <TABLE BORDER="0" CELLPADDING="2" CELLSPACING="2" WIDTH="100%">
          <xsl:for-each select="monetreport/dcm/primary">
          <TR>
            <TD colspan="2">
              <center><xsl:value-of select="/monetreport/resource/header1"/><br/>
				<xsl:value-of select="/monetreport/resource/header2"/><br/>
				<xsl:value-of select="/monetreport/general/time"/><br/><br/>
              </center>
              <xsl:value-of select="/monetreport/resource/hwinfo"/>
              <table WIDTH="100%"  STYLE="border: 1px solid #000000;">
              <tr><td colspan="2"></td></tr>
              <tr><td><xsl:value-of select="/monetreport/resource/name"/></td><td><xsl:value-of select="name"/></td></tr>
              <tr><td><xsl:value-of select="/monetreport/resource/manufacture"/></td><td><xsl:value-of select="manufacture"/></td></tr>
              <tr><td><xsl:value-of select="/monetreport/resource/chipid"/></td><td><xsl:value-of select="chipid"/></td></tr>
              <tr><td><xsl:value-of select="/monetreport/resource/vendorid"/></td><td><xsl:value-of select="vendorid"/></td></tr>
              <tr><td><xsl:value-of select="/monetreport/resource/classcode"/></td><td><xsl:value-of select="classcode"/></td></tr>
              <tr><td><xsl:value-of select="/monetreport/resource/revisionid"/></td><td><xsl:value-of select="revisionid"/></td></tr>
              <tr><td><xsl:value-of select="/monetreport/resource/ssid"/></td><td><xsl:value-of select="ssid"/></td></tr>
              <tr><td><xsl:value-of select="/monetreport/resource/ssvid"/></td><td><xsl:value-of select="ssvid"/></td></tr>
              <tr><td colspan="2"><a href="#otherdev"><xsl:value-of select="/monetreport/resource/otherhw"/></a></td></tr>
              </table>
            </TD>
          </TR>
          </xsl:for-each>

          <TR><td><xsl:value-of select="/monetreport/resource/existpkg"/>
          <table WIDTH="100%" STYLE="border: 1px solid #000000;">
          <xsl:for-each select="monetreport/exist/package">
            <TR>
              <TD><xsl:value-of select="description"/></TD>
            </TR>
          </xsl:for-each>
          </table>
          </td></TR>

         <xsl:if test="/monetreport/general/download='true'">
          <TR><td><xsl:value-of select="/monetreport/resource/download"/>
          <table WIDTH="100%" STYLE="border: 1px solid #000000;">
          <xsl:for-each select="monetreport/downloaded/package">
            <TR>
              <TD><xsl:value-of select="description"/></TD>
              <TD>
              <xsl:if test="result='Fail'">
              <a href="#errmsg"><xsl:value-of select="/monetreport/resource/fail"/></a>
              </xsl:if>
              <xsl:if test="result='Succeed'">
              <xsl:value-of select="/monetreport/resource/succeed"/>
              </xsl:if>
              </TD>
            </TR>
          </xsl:for-each>
          </table>
          </td></TR>
        </xsl:if>
          
         <xsl:if test="/monetreport/general/action='install'">
          <TR><td><b><xsl:value-of select="/monetreport/resource/pkg2inst"/></b><br/>
          
          <xsl:for-each select="monetreport/install/package">
          <xsl:value-of select="description"/>
          <table WIDTH="100%" STYLE="border: 1px solid #000000;">
            <TR><TD><xsl:value-of select="/monetreport/resource/finalstatus"/></TD>
				<TD><xsl:if test="result='Fail'">
				<a href="#errmsg"><xsl:value-of select="/monetreport/resource/fail"/></a>
				</xsl:if>
          
				<xsl:if test="result='Succeed'">
				<xsl:value-of select="/monetreport/resource/succeed"/>
				</xsl:if></TD>
              </TR>
            <TR><TD><xsl:value-of select="/monetreport/resource/versionofitem"/></TD><TD><xsl:value-of select="version"/></TD></TR>
            <TR><TD><xsl:value-of select="/monetreport/resource/size"/></TD><TD><xsl:value-of select="size"/><xsl:value-of select="/monetreport/resource/bytes"/></TD></TR>
          </table>
          </xsl:for-each>
          
          </td></TR>
        </xsl:if>

         <xsl:if test="/monetreport/general/action='uninst'">
          <TR><td><b><xsl:value-of select="/monetreport/resource/pkg2uninst"/></b><br/>
          <xsl:for-each select="monetreport/uninstall/package">
          <xsl:value-of select="description"/>
          <table WIDTH="100%" STYLE="border: 1px solid #000000;">
            <TR><TD><xsl:value-of select="/monetreport/resource/finalstatus"/></TD>
				<TD><xsl:if test="result='Fail'">
				<a href="#errmsg"><xsl:value-of select="/monetreport/resource/fail"/></a>
				</xsl:if>
				<xsl:if test="result='Succeed'">
				<xsl:value-of select="/monetreport/resource/succeed"/>
				</xsl:if></TD>
              </TR>
            <TR><TD><xsl:value-of select="/monetreport/resource/versionofitem"/></TD><TD><xsl:value-of select="version"/></TD></TR>
            <TR><TD><xsl:value-of select="/monetreport/resource/size"/></TD><TD><xsl:value-of select="size"/><xsl:value-of select="/monetreport/resource/bytes"/></TD></TR>
          </table>
          </xsl:for-each>
          
          </td></TR>
        </xsl:if>
          

          <TR>
            <TD colspan="2"><br/>
            <b><a name="#otherdev"></a><xsl:value-of select="/monetreport/resource/otherdev"/></b>
            </TD>
          </TR>
          <xsl:for-each select="monetreport/dcm/device">
          <TR>
            <TD colspan="2">
              <xsl:value-of select="name"/>
              <table WIDTH="100%"  STYLE="border: 1px solid #000000;">
              <tr><td colspan="2"></td></tr>
              <tr><td><xsl:value-of select="/monetreport/resource/manufacture"/></td><td><xsl:value-of select="manufacture"/></td></tr>
              <tr><td><xsl:value-of select="/monetreport/resource/chipid"/></td><td><xsl:value-of select="chipid"/></td></tr>
              <tr><td><xsl:value-of select="/monetreport/resource/vendorid"/></td><td><xsl:value-of select="vendorid"/></td></tr>
              <tr><td><xsl:value-of select="/monetreport/resource/classcode"/></td><td><xsl:value-of select="classcode"/></td></tr>
              <tr><td><xsl:value-of select="/monetreport/resource/revisionid"/></td><td><xsl:value-of select="revisionid"/></td></tr>
              <tr><td><xsl:value-of select="/monetreport/resource/ssid"/></td><td><xsl:value-of select="ssid"/></td></tr>
              <tr><td><xsl:value-of select="/monetreport/resource/ssvid"/></td><td><xsl:value-of select="ssvid"/></td></tr>
              </table>
            </TD>
          </TR>
          </xsl:for-each>

          <TR>
            <TD colspan="2"><br/>
            <b><a name="#errmsg"></a><xsl:value-of select="/monetreport/resource/errmsg"/></b>
            </TD>
          </TR>
          <TR>
            <TD colspan="2">
              <xsl:value-of select="name"/>
              <table WIDTH="100%"  STYLE="border: 1px solid #000000;">
          	<xsl:for-each select="monetreport/error">
              		<tr><td><xsl:value-of select="msg"/></td></tr>
          	</xsl:for-each>
              </table>
            </TD>
          </TR>


        </TABLE>
        </center>
      </BODY>
    </HTML>
  </xsl:template>
</xsl:stylesheet>
