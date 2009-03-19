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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcHandler;
import org.apache.xmlrpc.XmlRpcRequest;

/**
 * Handles the translation requests.
 * @author bhaddow
 */
public class Translator  implements XmlRpcHandler{
    
    private static final Logger _logger = Logger.getLogger(Translator.class);
   
    private static Translator _instance;
    private TranslationTool _tool;
    
    private Translator(HierarchicalConfiguration config) throws IOException {
        //Create the suport tools
        Map<String,TranslationTool> tools = new HashMap<String,TranslationTool>();
        List  pipeToolsConfig  = config.configurationsAt("pipetool");
        for (Iterator i = pipeToolsConfig.iterator(); i.hasNext(); ) {
            HierarchicalConfiguration h = (HierarchicalConfiguration)i.next();
            String name = h.getString("name");
            String exe = h.getString("command");
            List args = h.getList("arg");
            String[] cmd = new String[1 + args.size()];
            cmd[0] = exe;
            for (int j = 0; j < args.size(); ++j) {
            	cmd[j+1] = args.get(j).toString();
            }
            System.out.println(name + " " + exe + " " + args);
        }
        //System.exit(1);
        //_tool = new PipedTool("en-tok", new String[]{"/home/bhaddow/statmt/repository/experiments/trunk/scripts/tokenizer.perl", "-l", "en"});
    }
    
    /**
     * Call this before using the Translator.
     * @param config
     * @throws IOException
     */
    public static synchronized void init(HierarchicalConfiguration config) throws IOException {
        _instance = new Translator(config);
    }
    
    public static  Translator instance() throws IOException {
        if (_instance == null) {
            throw new RuntimeException("Translator not initialised");
        }
        return _instance;
    }
    
   
    @Override
    public Object execute(XmlRpcRequest request) throws XmlRpcException {
        _logger.debug("Received request: " + request.getMethodName());
        if (request.getParameterCount() != 2) {
            throw new XmlRpcException("Incorrect number of parameters");
        }
        String systemId = request.getParameter(0).toString();
        Object[] sources = (Object[])request.getParameter(1);
        return translate(systemId,sources);
    }

    /**
     * The translate() operations. All objects in the array are interpreted as strings.
     * @param systemId
     * @param sources
     * @return
     */
    private Object[]  translate(String systemId, Object[] sources) {
        _logger.debug("received source sentence: " + Arrays.toString(sources));
        String[] sourceStrings = new String[sources.length];
        for (int i = 0; i < sourceStrings.length; ++i) {
            sourceStrings[i] = sources[i].toString();
        }
        return  _tool.transform(sourceStrings);
    }






   

}
