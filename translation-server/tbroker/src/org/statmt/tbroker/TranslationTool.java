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
    
    
    /**
     * Batched version of transform operation.
     * @param input
     */
    public abstract String[] transform(String[] input);

}
