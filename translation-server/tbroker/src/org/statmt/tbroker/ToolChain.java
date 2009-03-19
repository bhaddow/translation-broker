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

public class ToolChain extends TranslationTool {
    
    private List<TranslationTool> _tools = new ArrayList<TranslationTool>();
    
    public void addTool(TranslationTool tool) {
        _tools.add(tool);
    }

    @Override
    public String[] transform(String[] input) {
        for (TranslationTool tool: _tools) {
            input = tool.transform(input);
        }
        return input;
    }

}
