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
import java.util.List;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class TestClient {

    /**
     * @param args
     */
    public static void main(String[] args)  throws Exception {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL("http://127.0.0.1:8080/xmlrpc"));
        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);
        String[] source = new String[]{"The source string.","And this is the source's next sentence."};
        //source.add("The source string . ");
        Object[] params = new Object[]{"systemid", source};
        System.out.println(Arrays.toString(source));
        Object[] result =  (Object[])client.execute("translate", params);
        System.out.println(Arrays.toString(result));
    }

}
