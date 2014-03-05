/*=======================================================================
 *
 *  Copyright (c) Barry Haddow
 *  All rights reserved
 *
 *  First Published: 2009
 *
 *  $Author: bhaddow $
 *  $Date: 2009-07-22 15:30:29 +0100 (Wed, 22 Jul 2009) $
 *  $Revision: 116 $
 *  $URL: http://abmayne@svn.statmt.org/repository/code/translation-server/tbroker/src/org/statmt/tbroker/PipedTool.java $
 *  ========================================================================*/
package org.statmt.tbroker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.File;
import java.util.Arrays;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.UUID;

import org.apache.log4j.Logger;

/**
 * A command line tool which uses pipes for read/write.
 * This tool either saves the input in a file, and saves the filename in the job or
 *   it appends the filename from the job as the first line of the stdin for the script.
 *   This was originally for restoring the ctm timing information from the original
 *   input stream, to the translated output
 * @author abirch
 * based on PipedTool by bhaddow
 */
public class PipedToolState extends PipedTool {
    
    private String _stateFlag;
    public  static final String stateDir = "/tmp/";
   
    public PipedToolState(String toolName, String[] progargs, String stateFlag) throws IOException {
        this(toolName,progargs,false,"","",stateFlag);
    }
    
    public PipedToolState(String toolName, String[] progargs, boolean catchDebug, String initFinishedMessage, String finishedMessage,String stateFlag) throws IOException {
        super(toolName,progargs,catchDebug,initFinishedMessage,finishedMessage);
        _stateFlag = stateFlag;
	_logger.info("State flag set to:" + stateFlag);
    }

    @Override
    public synchronized void  transform(TranslationJob job) {
        long startTime = System.currentTimeMillis() ;
        String text = job.getText();
        if (_substitutePipes) {
            text = text.replaceAll("\\|", MosesServerTool.PIPE);
        }
        //Append state info to text, so alternate text lines with state lines
        if (_stateFlag.equals("add-state")) {
            text += "\n" + job.getState();
        }
	else if (_stateFlag.equals("add-state-align")) {
            //Alignments in the format source words |0-1| and more |2-3|
	    text = job.getFormattedAlignments();
            text += "\n" + job.getState();
        }
	else if (_stateFlag.equals("save")){
	        //Save in state not in file
                //job.setState(writeStateToFile(text));
                job.setState(text);
	}
	_logger.debug("Sending to PipedToolState process:" + text);
        
        _processInput.println(text);
        _processInput.flush();
        try {
            String outputText = _output.takeFirst();
            if (_substitutePipes) {
                outputText = outputText.replaceAll(MosesServerTool.PIPE,"|");
            }
            job.setText(outputText);
            if (_catchDebug) {
                while(true) {
                    String errorText = _error.poll(60,TimeUnit.SECONDS);
                    if (errorText == null) {
                        _logger.info("Timed out waiting for debug message");
                        break;
                    }
                    job.addDebug(errorText);
                    if (errorText.startsWith(_finishedMessage)) {
                        break;
                    }
                }
                
            }
            if (_stateFlag.equals("add-state")) {
	        //deleteFile(job.getState());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        long totalTime = System.currentTimeMillis() - startTime;
        job.setTiming(getName(), totalTime);
    }

    private String writeStateToFile (String inputText) {
        String fileName = stateDir + "rpcPipedTool" + UUID.randomUUID().toString() + ".tmp";
	BufferedWriter writer;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(fileName), "utf-8"));
	    writer.write(inputText);
	    writer.close(); 
        } catch (IOException e) {
            throw new RuntimeException(e);
            // report
	}
	return fileName;
    }
    private void deleteFile (String fileName) {
        File file = new File(fileName);
        try {
 	    file.delete();
        } catch (SecurityException x) {
	    // File permission problems are caught here.
	    System.err.println(x);
        }
    }
    
    class OutputReader extends Thread {
        
        private InputStream _processOutput;
        
        public OutputReader(InputStream processOutput) {
            setDaemon(true);
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
            setDaemon(true);
            _processError = processError;
        }
        
        public void run() {
            try {
                String line = null;
                BufferedReader in = new BufferedReader(new InputStreamReader(_processError,"utf8"));
                while ((line = in.readLine()) != null) {
                    if (_catchDebug) {
                        _error.addLast(line);
                    }
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
