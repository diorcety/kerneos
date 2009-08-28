#
#  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
#  - JASMINe
#  - Copyright (C) 2008 Bull S.A.S.
#  - Contact: jasmine@ow2.org
#  -
#  - This library is free software; you can redistribute it and/or
#  - modify it under the terms of the GNU Lesser General Public
#  - License as published by the Free Software Foundation; either
#  - version 2.1 of the License, or any later version.
#  -
#  - This library is distributed in the hope that it will be useful,
#  - but WITHOUT ANY WARRANTY; without even the implied warranty of
#  - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
#  - Lesser General Public License for more details.
#  -
#  - You should have received a copy of the GNU Lesser General Public
#  - License along with this library; if not, write to the Free Software
#  - Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
#  - USA
#  -
#  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
#  - $Id$
#  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
#

// ////////////////////////////////////////////////////////////////////////////
// Before investigations ...
// ////////////////////////////////////////////////////////////////////////////
Before using Kerneos :

    ex (for JOnAS 5.x) :
	
	This is the entry if you want to use the log in composant.

        eos {
            // Use LoginModule for EoS authentication
            org.ow2.jonas.security.auth.spi.JResourceLoginModule required
            resourceName="memrlm_1"
            ;
        };
        
        
	However, Kerneos is actually based on the JavaEE authentication. Just set
	it in the web.xml file. The default configuration uses the tomcat role name,
	and the BASIC type.

// ////////////////////////////////////////////////////////////////////////////
// How to use WTP to develop Kerneos :
// ////////////////////////////////////////////////////////////////////////////


    - First run the following command : mvn -P initWTP to copy some dependencies
      in the WTP webapp lib directory

    - set a JOnAS 5 server in your Eclipse environment

    - load the main page : http://localhost:9000/kerneos/Kerneos.html
    
  
////////////////////////////////////////////////////////////////////////////
// How to add a new module to core
////////////////////////////////////////////////////////////////////////////

 	Note :
 	------
	All the modules added on a same core should have been compiled with the same
	context root, the same as were the application is deployed.

	
	Add modules :
	-------------
	Kerneos simply relies on a configuration file : kerneos-config.xml
	This file must be added in the META-INF directory of the war archive of your
	application. See the example below :

	<modules xmlns="org.ow2.jasmine.kerneos:KerneosConfig"
	  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	
	<!-- Examples on how to set this file. An XSD file to come. -->
	
	  <module swfFile="test.swf" loadOnStartup="false" loadMaximized="false">
	    <name>TestModule</name>
	    <description>Does absolutely nothing, it even doesn't exist</description>
	    <services>
	      <service id="testService1" destination="test1"/>
	      <service id="testService2" destination="test2"/>
	      <service id="testService3" destination="test2"/>
	    </services>
	  </module>

	</modules>
	
	In this example, one module is added on Kerneos. The name of the corresponding
	swf file must be set, as well as a name for the module and a description. If the
	module needs services to reach a java server side, just add them here.
	
	Don't forget to add entries for these services in WEB-INF/flex/services-config.xml
	of your webapp.
	
	For the Kerneos services (loginService and moduleService) the channel ref must be
	set to "my-graniteamf-kerneos". This channel is overloaded when Kerneos is loaded. 
	
	
	Nota : 
	------
	
	Instead of specify the swfFile attribute, you can specify the url one as the following 
	example : 
	
      <module url="http://www.yahoo.fr" loadOnStartup="true" loadMaximized="false">
	    <name>Yahoo</name>
	    <description>Yahoo search engine.</description>
      </module>    
	  
	The goal is to be allowed to load any web page in as a module.
	
	#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#
	#|																					   |#
	#| HOWEVER, each time you move from a tab that owns an url, when you come back on it,  |# 
	#| the url is reloaded. 															   |#
	#|																					   |#
	#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#
