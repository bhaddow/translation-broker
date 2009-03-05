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

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.webserver.ServletWebServer;
import org.apache.xmlrpc.webserver.XmlRpcServlet;

public class Main {
    
    private static final Logger _logger = Logger.getLogger(Main.class);

    /**
     * Set up the translation server.
     * @param args
     */
    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        //configure
        int port = 8080;
        
        //Start tools
        
        
        //Tool chains
        
        //Start server
        XmlRpcServlet servlet = new TranslationServlet();
        
        ServletWebServer webServer = new ServletWebServer(servlet, port);
        _logger.info("server starting");
        webServer.start();
    }

  }

