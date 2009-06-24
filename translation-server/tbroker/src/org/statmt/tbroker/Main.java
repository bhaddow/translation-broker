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

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
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
        if (args.length != 1) {
            System.err.println("Usage: java " + Main.class.getName() + " config-file");
            System.exit(1);
        }
        
        //configure 
        HierarchicalConfiguration config = new XMLConfiguration(args[0]);
        int port = config.getInt("port");
      
        //Tool chains
        Translator.init(config);
        
        //Start server
        XmlRpcServlet servlet = new TranslationServlet();
        
        ServletWebServer webServer = new ServletWebServer(servlet, port);
        _logger.info("server starting on port " + port);
        webServer.start();
    }

  }

