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
    
    public ToolChain(String name, String description, boolean lowercasedInput, boolean tokenisedInput) {
        super(name);
        _description = description;
        _lowercasedInput = lowercasedInput;
        _tokenisedInput = tokenisedInput;
    }
      
    public String getDescription() {
        return _description;
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
