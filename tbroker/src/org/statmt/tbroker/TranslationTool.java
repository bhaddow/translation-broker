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

/**
 * A tool used in the TranslationToolChain. Transforms strings to strings.
 * @author bhaddow
 */
public abstract class TranslationTool {
    
    private String _name;
    
    public TranslationTool(String name) {
        _name = name;
    }
    
    
    /**
     * Batched version of transform operation.
     * @param input
     */
    public abstract String[] transform(String[] input);
    
    public String getName() {
        return _name;
    }

}
