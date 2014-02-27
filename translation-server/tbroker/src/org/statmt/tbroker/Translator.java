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
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcHandler;
import org.apache.xmlrpc.XmlRpcRequest;

/**
 * Handles the translation requests and configures the tool chains..
 * @author bhaddow
 */
public class Translator  implements XmlRpcHandler{
    
    private static final Logger _logger = Logger.getLogger(Translator.class);
   
   
    
    private static Translator _instance;
    private Map<String,ToolChain> _toolChains = new HashMap<String, ToolChain>();
    
    private Translator(HierarchicalConfiguration config) throws IOException {
        //Create the  tools
        Map<String,TranslationTool> tools = new HashMap<String,TranslationTool>();
       
        //piped moses tools
        if (!config.configurationsAt("moses-pipes").isEmpty()) {
	        SubnodeConfiguration subconf = config.configurationAt("moses-pipes");
	        String exe = subconf.getString("command");
	        int verbosity = subconf.getInt("verbosity",0);
	        int stack = subconf.getInt("stack",200);
	        String initCompleteMsg = subconf.getString("init-end-msg","");
	        String jobCompleteMsg  = subconf.getString("end-msg","");
	        List mconfs = subconf.configurationsAt("moses-pipe");
	        for (Iterator i = mconfs.iterator(); i.hasNext();) {
	            HierarchicalConfiguration h = (HierarchicalConfiguration)i.next();
	            String name = h.getString("name");
	            String model = h.getString("model");
	            verbosity = h.getInt("verbosity",verbosity);
	            stack = h.getInt("stack", stack);
	            String cmd[] = new String[]{exe, "-f", model, "-s", stack+"", "-v", verbosity+""};
	            boolean debug = (verbosity > 0);
	            PipedTool tool = new PipedTool(name,cmd,debug,initCompleteMsg, jobCompleteMsg);
                tool.setSubstitutePipes(true);
	            tools.put(name,tool);
	        }
        }
        
        if (!config.configurationAt("moses-servers").isEmpty()) {
            SubnodeConfiguration subconf = config.configurationAt("moses-servers");
            List mconfs = subconf.configurationsAt("moses-server");
            for (Iterator i = mconfs.iterator(); i.hasNext(); ) {
                HierarchicalConfiguration h = (HierarchicalConfiguration)i.next();
                String name = h.getString("name");
                tools.put(name, new MosesServerTool(name,h));
            }
        }
         
        //Process input and remember state in job object
        if (!config.configurationsAt("stripctm").isEmpty()) {
	        SubnodeConfiguration subconf = config.configurationAt("stripctm");
	        String exe = subconf.getString("command");
	        List tconfs = subconf.configurationsAt("stripctm");
	        for (Iterator i = tconfs.iterator(); i.hasNext();) {
	            HierarchicalConfiguration h = (HierarchicalConfiguration)i.next();
	            String name = h.getString("name");
	            String cmd[] = new String[]{exe};
	            tools.put(name,new PipedToolState(name,cmd,"save"));
	        }
        }
        //Process output and restore state from job object
        if (!config.configurationsAt("restorectm").isEmpty()) {
	        SubnodeConfiguration subconf = config.configurationAt("restorectm");
	        String exe = subconf.getString("command");
	        List tconfs = subconf.configurationsAt("restorectm");
	        for (Iterator i = tconfs.iterator(); i.hasNext();) {
	            HierarchicalConfiguration h = (HierarchicalConfiguration)i.next();
	            String name = h.getString("name");
	            String cmd[] = new String[]{exe};
	            tools.put(name,new PipedToolState(name,cmd,"restore"));
	        }
        }

        //tokenisers
        if (!config.configurationsAt("tokenisers").isEmpty()) {
	        SubnodeConfiguration subconf = config.configurationAt("tokenisers");
	        String exe = subconf.getString("command");
	        List tconfs = subconf.configurationsAt("tokeniser");
	        for (Iterator i = tconfs.iterator(); i.hasNext();) {
	            HierarchicalConfiguration h = (HierarchicalConfiguration)i.next();
	            String name = h.getString("name");
	            String language = h.getString("language");
	            String cmd[] = new String[]{exe, "-l",language};
	            tools.put(name,new PipedTool(name,cmd));
	        }
        }
        
      //detokenisers
        if (!config.configurationsAt("detokenisers").isEmpty()) {
        	SubnodeConfiguration subconf = config.configurationAt("detokenisers");
        	String exe = subconf.getString("command");
	        List dconfs = subconf.configurationsAt("detokeniser");
	        for (Iterator i = dconfs.iterator(); i.hasNext();) {
	            HierarchicalConfiguration h = (HierarchicalConfiguration)i.next();
	            String name = h.getString("name");
	            String language = h.getString("language");
	            String cmd[] = new String[]{exe, "-l",language};
	            tools.put(name,new PipedTool(name,cmd));
	        }
        }
        
        //lowercasers
        if (!config.configurationsAt("lowercasers").isEmpty()) {
        	SubnodeConfiguration subconf = config.configurationAt("lowercasers");
	        String exe = subconf.getString("command");
	        List lconfs = subconf.configurationsAt("lowercaser");
	        for (Iterator i = lconfs.iterator(); i.hasNext();) {
	            HierarchicalConfiguration h = (HierarchicalConfiguration)i.next();
	            String name = h.getString("name");
	            String cmd[] = new String[]{exe};
	            tools.put(name,new PipedTool(name,cmd));
	        }
        }
        
        //desegmenters
        if (!config.configurationsAt("desegmenters").isEmpty()) {
        	SubnodeConfiguration subconf = config.configurationAt("desegmenters");
	        String exe = subconf.getString("command");
	        List lconfs = subconf.configurationsAt("desegmenter");
	        for (Iterator i = lconfs.iterator(); i.hasNext();) {
	            HierarchicalConfiguration h = (HierarchicalConfiguration)i.next();
	            String name = h.getString("name");
	            String cmd[] = new String[]{exe};
	            tools.put(name,new PipedTool(name,cmd));
	        }
        }
        
 
        //recasers
        if (!config.configurationsAt("recasers").isEmpty()) {
        	SubnodeConfiguration subconf = config.configurationAt("recasers");
        	String exe = subconf.getString("command");
	        String moses = subconf.getString("moses");
	        List rconfs = subconf.configurationsAt("recaser");
	        for (Iterator i = rconfs.iterator(); i.hasNext();) {
	            HierarchicalConfiguration h = (HierarchicalConfiguration)i.next();
	            String name = h.getString("name");
	            String model = h.getString("model");
	            String cmd[] = new String[]{exe,"--moses", moses, "--model", model};
                PipedTool tool = new PipedTool(name,cmd);
                tool.setSubstitutePipes(true);
	            tools.put(name,tool);
	        }
        }

        //truecasers
        if (!config.configurationsAt("truecasers").isEmpty()) {
            SubnodeConfiguration subconf = config.configurationAt("truecasers");
            String exe = subconf.getString("command");
            List tconfs = subconf.configurationsAt("truecaser");
	        for (Iterator i = tconfs.iterator(); i.hasNext();) {
	            HierarchicalConfiguration h = (HierarchicalConfiguration)i.next();
	            String name = h.getString("name");
	            String model = h.getString("model");
	            String cmd[] = new String[]{exe,"--model", model};
	            PipedTool tool = new PipedTool(name,cmd);
                tool.setSubstitutePipes(true);
	            tools.put(name,tool);
	        }
        }

        //detruecasers
        if (!config.configurationsAt("detruecasers").isEmpty()) {
            SubnodeConfiguration subconf = config.configurationAt("detruecasers");
            String exe = subconf.getString("command");
            List tconfs = subconf.configurationsAt("detruecaser");
	        for (Iterator i = tconfs.iterator(); i.hasNext();) {
	            HierarchicalConfiguration h = (HierarchicalConfiguration)i.next();
	            String name = h.getString("name");
	            String cmd[] = new String[]{exe};
	            tools.put(name,new PipedTool(name,cmd));
	        }
        }
        
        
        
        //create tool chains
        List toolChainsConfig = config.configurationsAt("toolchain");
        for (Iterator i = toolChainsConfig.iterator(); i.hasNext();) {
            HierarchicalConfiguration h = (HierarchicalConfiguration)i.next();
            String name = h.getString("name");
            String description = h.getString("description");
            String sourceLanguage = h.getString("source");
            String targetLanguage = h.getString("target");
            List toolsInChain = h.getList("tool");
            boolean tokenisedInput = h.getBoolean("tokinput",false);
            boolean lowercasedInput = h.getBoolean("lcinput",false);
            boolean parallel = h.getBoolean("parallel", false);
            SentenceSplitter sentenceSplitter = null;
            if (h.getBoolean("split",false)) {
                String splitterCommand = config.getString("splitter");
                if (splitterCommand == null) {
                    throw new RuntimeException("No sentence splitter specified");
                }
                _logger.debug("Sentence splitter command: " + splitterCommand);
		if (splitterCommand.equals("ctm")){
                  sentenceSplitter = new NewCTMSentenceSplitter();
		} else {
                  sentenceSplitter = new PipedSentenceSplitter(splitterCommand,sourceLanguage);
		}
            }
            ToolChain toolChain = new ToolChain(name,description, sourceLanguage, targetLanguage,sentenceSplitter,lowercasedInput,tokenisedInput);
            toolChain.setParallel(parallel);
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
	        TranslationJob[] jobs = TranslationJob.create(params);
	        TranslationJob[] resultJobs = translate(jobs);
	        Object[] result = new Object[resultJobs.length];
	        for (int i = 0; i < result.length; ++i) {
	            result[i] = resultJobs[i].getResult();
                if (_logger.isDebugEnabled()) {
                    _logger.debug("Sending response #" + i + ": " + result[i]);
                }
	        }
	        return result;
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
    private TranslationJob[]  translate(TranslationJob[] jobs) throws XmlRpcException  {
        if (jobs.length == 0) {
            _logger.warn("Empty text received");
            return jobs;
        }
        ToolChain tool = _toolChains.get(jobs[0].getSystemId());
        if (tool == null) {
        	throw new XmlRpcException("Unknown system id: " + jobs[0].getSystemId());
        }
        try {
            return tool.process(jobs);
        } catch (IOException e) {
            throw new XmlRpcException("Problem processing job",e);
        }
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
    		toolConfig.put("source", toolChain.getSourceLanguage());
    		toolConfig.put("target", toolChain.getTargetLanguage());
    		toolConfig.put("description", toolChain.getDescription());
    		toolConfig.put("tokinput", toolChain.tokenisedInput());
    		toolConfig.put("lcinput",toolChain.lowercasedInput());
    		tools[j++] = toolConfig;
    	}
    	return tools;
    }
    
    

}



