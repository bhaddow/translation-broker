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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/**
 * Translate using the moses server.
 * @author bhaddow
 */
public class MosesServerTool extends TranslationTool {
    
    private static final Logger _logger = Logger.getLogger(MosesServerTool.class);
    private static final String PIPE = "APIPENOTFACTOR";
    
    private URL _url;

    public MosesServerTool(String name, HierarchicalConfiguration config) throws MalformedURLException {
        super(name);
        String host = config.getString("host");
        int port = config.getInteger("port",8080);
        _url = new URL("http://" +host + ":" + port + "/RPC2");
        _logger.info("Created moses server tool with URL; " + _url);
    }

    @Override
    public void transform(TranslationJob job) {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(_url);
            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);
            Map<String,String> params = new HashMap<String,String>();
            String text = job.getText();
            //Moses can't handle pipes!!!
            text = text.replaceAll("\\|",PIPE);
            params.put("text",text);
            Map result = (Map)client.execute("translate", new Object[]{params});
            text = result.get("text").toString();
            text.replaceAll(PIPE,"|");
            job.setText(text);
        }  catch (XmlRpcException e) {
             //TODO: Handle this better
             throw new RuntimeException(e);
        }
    }

}
