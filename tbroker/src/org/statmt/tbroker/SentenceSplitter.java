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

/**
 * Does sentence splitting.
 * @author bhaddow
 */
public abstract class SentenceSplitter {
    
    protected String _language;
    
    public SentenceSplitter(String language) {
        _language = language;
    }
    
    
    public abstract String[] split(String  input) throws IOException ;

}
