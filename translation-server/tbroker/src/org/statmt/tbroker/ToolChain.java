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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class ToolChain extends TranslationTool {
    
    private static final Logger _logger = Logger.getLogger(ToolChain.class);
    
    private List<TranslationTool> _tools = new ArrayList<TranslationTool>();
    private boolean _lowercasedInput;
    private boolean _tokenisedInput;
    private String _description;
    private String _sourceLanguage;
    private String _targetLanguage;
    
    public ToolChain(String name, String description, String sourceLanguage, String targetLanguage, boolean lowercasedInput, boolean tokenisedInput) {
        super(name);
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


	public boolean tokenisedInput() {
		return _tokenisedInput;
	}


	public void addTool(TranslationTool tool) {
        _tools.add(tool);
    }


    @Override
    public void transform(TranslationJob job) {
        _logger.debug("Toolchain " + getName() + " processing request");
        for (TranslationTool tool: _tools) {
            _logger.debug(tool.getName() + "> " + job.getText());
            tool.transform(job);
            _logger.debug(tool.getName() +"< " + job.getText());
        }
    }

}
