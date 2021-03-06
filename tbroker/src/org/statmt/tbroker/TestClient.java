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
        	System.out.println("description: " + toolConfig.get("description"));
        	System.out.println("source: "  + toolConfig.get("source"));
        	System.out.println("target: " + toolConfig.get("target"));
        	System.out.println("requires lowercase: " + toolConfig.get("lcinput"));
        	System.out.println("requires tokenised: " + toolConfig.get("tokinput"));
        	System.out.println();
        }
        
        //do some translation
        //String source = "das ist ein haus\ndas haus ist klein ";
        //String[] source = new String[]{"je ne sais pas . ", "j' ai douze ans."};
        String source = "Je ne sais pas. ";
        //source.add("The source string . ");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("systemid", "fr-en");
        params.put("text",source);
        params.put("debug","yes");
        params.put("align", "yes");
        //Object[] params = new Object[]{"fr-en", source};
        System.out.println(params);
        Object[] results = (Object[])client.execute("translate", new Object[]{params});
        for (Object o : results) {
        Map result = (Map)o;
        System.out.println(result);
            Object[] debug = (Object[])result.get("debug");
            if (debug != null) {
                System.out.println("debug:");
                for (Object d: debug) {
                    System.out.println(d);
                }
            }
            Object[] align= (Object[])result.get("align");
            if (align != null) {
                System.out.println("align:");
                for (Object a: align) {
                    System.out.println(a);
                }
            }
        }
    }

}
