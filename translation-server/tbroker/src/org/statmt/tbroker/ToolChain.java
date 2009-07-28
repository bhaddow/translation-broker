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
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;

public class ToolChain  {
    
    private static final Logger _logger = Logger.getLogger(ToolChain.class);
    
    private List<TranslationTool> _tools = new ArrayList<TranslationTool>();
    private boolean _lowercasedInput;
    private boolean _tokenisedInput;
    private SentenceSplitter _splitter;
    private boolean _parallel;
    private String _description;
    private String _sourceLanguage;
    private String _targetLanguage;
    private String _name;
    
    public ToolChain(String name, String description, String sourceLanguage, String targetLanguage, SentenceSplitter sentenceSplitter, boolean lowercasedInput, boolean tokenisedInput) {
        _name = name;
        if (sourceLanguage == null || targetLanguage == null) {
            throw new IllegalArgumentException("Need to specify source and target language for tool chain " + name);
        }
        if (description == null) {
            throw new IllegalArgumentException("Need to provide description for tool chain " + name);
        }
        _description = description;
        _lowercasedInput = lowercasedInput;
        _tokenisedInput = tokenisedInput;
        _sourceLanguage = sourceLanguage;
        _targetLanguage = targetLanguage;
        if (sentenceSplitter != null) {
            _splitter = sentenceSplitter;
       } else {
           _splitter = new NewlineSentenceSplitter();
       }
    }
    
    public String getName() {
        return _name;
    }
      
    public String getDescription() {
        return _description;
    }
    
    public String getSourceLanguage() {
        return _sourceLanguage;
    }
    
    public String getTargetLanguage() {
        return _targetLanguage;
    }
    
    public boolean lowercasedInput() {
		return _lowercasedInput;
	}
    
    public void setParallel(boolean parallel) {
        _parallel = parallel;
    }


	public boolean tokenisedInput() {
		return _tokenisedInput;
	}


	public void addTool(TranslationTool tool) {
        _tools.add(tool);
    }



    public TranslationJob[] process(TranslationJob[] inputJobs) throws IOException{
        _logger.debug("Toolchain " + getName() + " processing request");
        TranslationJob[] jobs = inputJobs;
        if (_splitter != null) {
            List<TranslationJob> jobsList = new ArrayList<TranslationJob>();
            for (TranslationJob inputJob: inputJobs) {
            	String[] outputText  = _splitter.split(inputJob.getText());
                for (int i = 0; i < outputText.length; ++i) {
                    jobsList.add(new TranslationJob(inputJob, outputText[i]));
                }
            }
            jobs = jobsList.toArray(new TranslationJob[]{});
        }
        _logger.debug("Total jobs to process: " + jobs.length);
        if (_parallel) {
            Thread[] threads = new Thread[jobs.length];
            for (int i = 0; i < jobs.length; ++i) {
                final TranslationJob job = jobs[i];
                threads[i] = new Thread("ToolChain-" + getName() + "-" + i) {
                    public void run() {
                        processJob(job);
                    }
                };
                threads[i].start();
            }
            for (int i = 0; i < jobs.length; ++i) {
                    try {
                        threads[i].join();
                    } catch (InterruptedException e) {
                        _logger.error("join() failed",e);
                    }
            }
        } else {
                for (TranslationJob job: jobs ) {
                   processJob(job);
                }
        }
        return jobs;
    }
    
    private void processJob(TranslationJob job) {
        for (TranslationTool tool: _tools) {
                _logger.debug(tool.getName() + "> " + job.getText());
                tool.transform(job);
                _logger.debug(tool.getName() +"< " + job.getText());
            
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug("Job times (ms): " + job.getTimings());
        }
    }

}
