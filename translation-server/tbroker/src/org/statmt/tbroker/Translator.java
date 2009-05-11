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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.HierarchicalConfiguration;
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
    private Map<String,ToolChain> _toolChains = new HashMap<String, ToolChain>();
    
    private Translator(HierarchicalConfiguration config) throws IOException {
        //Create the  tools
        Map<String,TranslationTool> tools = new HashMap<String,TranslationTool>();
        List  pipeToolsConfig  = config.configurationsAt("pipetool");
        for (Iterator i = pipeToolsConfig.iterator(); i.hasNext(); ) {
            HierarchicalConfiguration h = (HierarchicalConfiguration)i.next();
            String name = h.getString("name");
            String exe = h.getString("command");
            boolean debug = h.getBoolean("debug",false);
            String debugInitComplete = h.getString("debug-init-complete","");
            List args = h.getList("arg");
            String[] cmd = new String[1 + args.size()];
            cmd[0] = exe;
            for (int j = 0; j < args.size(); ++j) {
            	cmd[j+1] = args.get(j).toString();
            }
            tools.put(name,new PipedTool(name,cmd,debug,debugInitComplete));
        }
        
        //create tool chains
        List toolChainsConfig = config.configurationsAt("toolchain");
        for (Iterator i = toolChainsConfig.iterator(); i.hasNext();) {
            HierarchicalConfiguration h = (HierarchicalConfiguration)i.next();
            String name = h.getString("name");
            String description = h.getString("description");
            List toolsInChain = h.getList("tool");
            boolean tokenisedInput = h.getBoolean("tokinput",false);
            boolean lowercasedInput = h.getBoolean("lcinput",false);
            ToolChain toolChain = new ToolChain(name,description,lowercasedInput,tokenisedInput);
            for (Iterator j = toolsInChain.iterator(); j.hasNext();) {
                String toolName = j.next().toString();
                TranslationTool tool = tools.get(toolName);
                if (tool == null) {
                    throw new RuntimeException("Error: missing tool: "+ toolName);
                }
                _logger.info("Adding tool " + tool.getName() + " to tool chain " + toolChain.getName());
                toolChain.addTool(tool);
            }
            _toolChains.put(name,toolChain);
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
    	String name = request.getMethodName();
        _logger.debug("Received request: " + name);
        
        if (name.equals("translate")) {
        	if (request.getParameterCount() != 1) {
                throw new XmlRpcException("Incorrect number of parameters");
            }
	        Map params = (Map)request.getParameter(0);
	        TranslationJob job = new TranslationJob(params);
	        translate(job);
	        return job.getResult();
        } else if (name.equals("list")) {
        	return list();
        } else {
        	throw new XmlRpcException("Unknown method: " + name);
        }
    }

    /**
     * The translate() operations. All objects in the array are interpreted as strings.
     * @param systemId
     * @param sources
     * @return
     */
    private void  translate(TranslationJob job) throws XmlRpcException  {
        _logger.debug("received source sentence: " + job.getText());
        ToolChain tool = _toolChains.get(job.getSystemId());
        if (tool == null) {
        	throw new XmlRpcException("Unknown system id: " + job.getSystemId());
        }
        tool.transform(job);
    }
    
    /**
     * Supply list of tools.
     * @throws XmlRpcException
     */
    private Map[] list() throws XmlRpcException {
    	Map[] tools = new Map[_toolChains.size()];
    	int j = 0;
    	for (Iterator<String> i = _toolChains.keySet().iterator(); i.hasNext();) {
    		String name = i.next();
    		ToolChain toolChain = _toolChains.get(name);
    		Map toolConfig = new HashMap();
    		toolConfig.put("name", name);
    		toolConfig.put("description", toolChain.getDescription());
    		toolConfig.put("tokinput", toolChain.tokenisedInput());
    		toolConfig.put("lcinput",toolChain.lowercasedInput());
    		tools[j++] = toolConfig;
    	}
    	return tools;
    }






   

}
