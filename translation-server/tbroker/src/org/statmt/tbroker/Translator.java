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
import java.util.Arrays;

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
   
    
    private TranslationTool _tool;
    
    public Translator() throws IOException {
        _logger.debug("Created a translator");
        _tool = new PipedTool("en-tok", new String[]{"/home/bhaddow/statmt/repository/experiments/trunk/scripts/tokenizer.perl", "-l", "en"});
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
