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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.log4j.Logger;

/**
 * A command line tool which uses pipes for read/write.
 * @author bhaddow
 */
public class PipedTool extends TranslationTool {
    
    private static final Logger _logger = Logger.getLogger(TranslationTool.class);
    private PrintWriter _processInput;
    private OutputReader _outputReader;
   
    private BlockingDeque<String> _output;
    
    public PipedTool(String toolName, String[] progargs) throws IOException {
        super(toolName);
        _logger.info("Creating tool " + toolName + " with args " + Arrays.toString(progargs));
        Process process = Runtime.getRuntime().exec(progargs);
        _outputReader = new OutputReader(process.getInputStream());
        _outputReader.start();
        new ErrorReader(process.getErrorStream()).start();
        _processInput = new PrintWriter(new OutputStreamWriter(process.getOutputStream(),"utf8"));
        _output = new LinkedBlockingDeque<String>();
    }


    @Override
    public synchronized void  transform(TranslationJob job) {
        _processInput.println(job.getText());
        _processInput.flush();
        try {
        	String outputText = _output.takeFirst();
        	job.setText(outputText);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
    class OutputReader extends Thread {
        
        private InputStream _processOutput;
        
        public OutputReader(InputStream processOutput) {
            _processOutput = processOutput;
        }
        
        
        public void run() {
            try {
                String line = null;
                BufferedReader in = new BufferedReader(new InputStreamReader(_processOutput,"utf8"));
                    while ((line = in.readLine()) != null) {
                        _output.addLast(line);
                    }
            } catch (Exception e) {
                _logger.error("Tool " + getName() + " failed: ",e);
            }
        }
    }
    
    class ErrorReader extends Thread {
        private InputStream _processError;
        
        public ErrorReader(InputStream processError) {
            _processError = processError;
        }
        
        public void run() {
            try {
                String line = null;
                BufferedReader in = new BufferedReader(new InputStreamReader(_processError,"utf8"));
                while ((line = in.readLine()) != null) {
                    if (_logger.isDebugEnabled()) {
                        _logger.debug(getName() + " " + line);
                    }
                }
            } catch (Exception e) {
                _logger.error("Tool " +getName() + " failed: ",e);
            }
        }
    }

}
