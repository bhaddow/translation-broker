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
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

public class ToolChain extends TranslationTool {
    
    private static final Logger _logger = Logger.getLogger(ToolChain.class);
    
    private List<TranslationTool> _tools = new ArrayList<TranslationTool>();
    
    public ToolChain(String name) {
        super(name);
    }
    
    public void addTool(TranslationTool tool) {
        _tools.add(tool);
    }

    @Override
    public String[] transform(String[] input) {
        _logger.debug("Toolchain " + getName() + " processing request");
        for (TranslationTool tool: _tools) {
            _logger.debug(tool.getName() + "> " + Arrays.toString(input));
            input = tool.transform(input);
            _logger.debug(tool.getName() +"< " + Arrays.toString(input));
        }
        return input;
    }

}
