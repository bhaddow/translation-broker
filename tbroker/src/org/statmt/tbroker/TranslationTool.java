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
     * @param input
     */
    public abstract void transform(TranslationJob job);
    
    public String getName() {
        return _name;
    }

}
