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

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class TestClient {

    /**
     * @param args
     */
    public static void main(String[] args)  throws Exception {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL("http://localhost:8080/xmlrpc"));
        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);
        
        //figure out what tools are available
        Object[] tools = (Object[])client.execute("list", new Object[]{});
        System.out.println("Available tools:");
        for (Object tool: tools) {
        	Map toolConfig = (Map)tool;
        	System.out.println("name: " + toolConfig.get("name"));
        	System.out.println("requires lowercase: " + toolConfig.get("lcinput"));
        	System.out.println("requires tokenised: " + toolConfig.get("tokinput"));
        	System.out.println();
        }
        
        //do some translation
        String source = "Je ne sais pas..";
        //source.add("The source string . ");
        Map<String, String> params = new HashMap<String, String>();
        params.put("systemid", "fr-en");
        params.put("text",source);
        //Object[] params = new Object[]{"fr-en", source};
        System.out.println(params);
        Map result = (Map)client.execute("translate", new Object[]{params});
        System.out.println(result);
    }

}
