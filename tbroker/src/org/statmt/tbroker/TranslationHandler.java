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
    
    private Translator _translator;
    private static final Logger _logger = Logger.getLogger(TranslationHandler.class);
    
    public TranslationHandler() throws IOException {
        _translator = new Translator();
    }

    @Override
    public XmlRpcHandler getHandler(String handlerName)
            throws XmlRpcNoSuchHandlerException, XmlRpcException {
        _logger.debug("Request name: " + handlerName);
        if (handlerName.equals("translate")) {
            return _translator;
        } else {
            throw new XmlRpcNoSuchHandlerException("No handler for " + handlerName);
        }
    }

}
