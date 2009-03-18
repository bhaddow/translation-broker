/*=======================================================================
 *
 *  Copyright (c) Barry Haddow
 *  All rights reserved
 *
 *  First Published: 2009
 *
 *  $Author$
 *  $Date$
 *  $Revision$
 *  $URL$
 *  ========================================================================*/
package org.statmt.tbroker;


import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.webserver.XmlRpcServlet;

public class TranslationServlet extends XmlRpcServlet {

    @Override
    protected XmlRpcHandlerMapping newXmlRpcHandlerMapping()
            throws XmlRpcException {
            //NOTE:   this is called from Servlet.init()
            return new TranslationHandler();
        
    }
    

}
