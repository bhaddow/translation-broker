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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
    public  static final String PIPE = "APIPENOTFACTOR";
    public static final int MAX_LENGTH = 600; //max sentence length in chars
    
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
        long startTime = System.currentTimeMillis() ;
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(_url);
            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);
            Map<String,String> params = new HashMap<String,String>();
            String text = job.getText();
            //Moses can't handle pipes!!!
            text = text.replaceAll("\\|",PIPE);
            if (text.length() > MAX_LENGTH) {
                _logger.warn("Line length " + text.length() + " exceeds limit. Truncating");
                text = text.substring(0,MAX_LENGTH);
            }
            params.put(TranslationJob.FIELD_TEXT,text);
            List<Map> alignments = job.getAlignments();
            if (alignments != null) {
                params.put(TranslationJob.FIELD_ALIGN, "true");
            }
            Map result = (Map)client.execute("translate", new Object[]{params});
            text = result.get(TranslationJob.FIELD_TEXT).toString();
            text = text.replaceAll(PIPE,"|");
            job.setText(text);
            if (alignments != null) {
                Object[] returnedAlignments = (Object[])result.get(TranslationJob.FIELD_ALIGN);
                if (returnedAlignments == null) {
                    _logger.warn("Alignments expected but missing");
                } else {
                    for (Object a : returnedAlignments) {
                        alignments.add((Map)a);
                    }
                }
            }
        }  catch (XmlRpcException e) {
             _logger.error("Moses server translation failed", e);
             throw new RuntimeException(e);
        }
        long totalTime = System.currentTimeMillis() - startTime;
        job.setTiming(getName(), totalTime);
    }

}
