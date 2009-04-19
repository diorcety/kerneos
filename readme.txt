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
#  - $Id: readme.txt 2664 2008-10-22 15:07:16Z renaultgu $
#  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
#

// ////////////////////////////////////////////////////////////////////////////
// Before investigations ...
// ////////////////////////////////////////////////////////////////////////////
Before using JASMINe-EoS :

    - Declare a JORAM topic named "jasmine"

    ex (for JOnAS 4.x) :

        <Topic name="jasmine">
          <freeReader/>
          <freeWriter/>
          <jndi name="jasmine"/>
        </Topic>



    - add a JAAS configuration named "eos"

    ex (for JOnAS 4.x) :


        eos {
            // Use LoginModule for EoS authentication
            org.objectweb.jonas.security.auth.spi.JResourceLoginModule required
            resourceName="memrlm_1"
            ;
        };

    ex (for JOnAS 5.x) :


        eos {
            // Use LoginModule for EoS authentication
            org.ow2.jonas.security.auth.spi.JResourceLoginModule required
            resourceName="memrlm_1"
            ;
        };

// ////////////////////////////////////////////////////////////////////////////
// How to use WTP to develop JASMINe-EoS :
// ////////////////////////////////////////////////////////////////////////////


    - First run the following command : mvn -P initWTP to copy some dependencies
      in the WTP webapp lib directory

    - run the app using eclipse on your favorite server (i.e JOnAS ;) )

    - load the main page EoS.html
    
  
////////////////////////////////////////////////////////////////////////////
// How to add a new module to core
////////////////////////////////////////////////////////////////////////////

 	Due to the fact that the core is compiled with the context root "jasmine-eos-core",
 	every new modules must have their compiler context-root with a value like
 	"jasmine-eos-root/<module-name>".
 	
 	Ex. For JasmineEos QuickVisu :
 	
 		extraParameters>
            <parameter>
              <!--============================================================================
                | context root is needed to allow Flex and Java to communicate using Granite |
                ===========================================================================-->
              <name>compiler.context-root</name>
              <values>
                <value>jasmine-eos-core/jasmine-eos</value>
              </values>
            </parameter>
          </extraParameters>
