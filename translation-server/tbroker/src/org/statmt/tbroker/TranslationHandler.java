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

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcHandler;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcNoSuchHandlerException;

public class TranslationHandler implements XmlRpcHandlerMapping {
    
    private static final Logger _logger = Logger.getLogger(TranslationHandler.class);
    
   

    @Override
    public XmlRpcHandler getHandler(String handlerName)
            throws XmlRpcNoSuchHandlerException, XmlRpcException {
        _logger.debug("Request name: " + handlerName);
        try {
            if (handlerName.equals("translate")) {
                    return Translator.instance();
            } else {
                throw new XmlRpcNoSuchHandlerException("No handler for " + handlerName);
            }
        }  catch (IOException e) {
            throw new XmlRpcException("Failed to create handler",e);
        }
        
    }

}
