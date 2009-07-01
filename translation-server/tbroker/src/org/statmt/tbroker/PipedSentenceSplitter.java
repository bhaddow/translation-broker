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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Wraps the perl sentence splitter.
 * @author bhaddow
 */
public class PipedSentenceSplitter extends SentenceSplitter {
    
    private static final Logger _logger = Logger.getLogger(PipedSentenceSplitter.class);
    private String _command;
    
    public PipedSentenceSplitter(String command, String language) {
        super(language);
        _command = command;
    }

    @Override
    public  String[] split(String  input) throws IOException{
        Process process = Runtime.getRuntime().exec(new String[]{_command,"-l",_language});
        PrintWriter processIn = new PrintWriter(new OutputStreamWriter(process.getOutputStream(),"utf8"));
        BufferedReader processOut = new BufferedReader(new InputStreamReader(process.getInputStream(),"utf8"));
        BufferedReader processErr = new BufferedReader(new InputStreamReader(process.getErrorStream(),"utf8"));
        processIn.println(input);
        //processIn.println("<p>");
        processIn.flush();
        processIn.close();
        
        String line;
        while ((line = processErr.readLine()) != null) {
            if (_logger.isDebugEnabled()) {
                _logger.debug(line);
            }
        }
       
        List<String> output = new ArrayList<String>();
        while ((line = processOut.readLine()) != null) {
            output.add(line);
            if (_logger.isDebugEnabled()) {
                _logger.debug(line);
            }
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug(" Read " + output.size() + " lines from splitter");
        }
        
        return output.toArray(new String[]{});
    }

}
