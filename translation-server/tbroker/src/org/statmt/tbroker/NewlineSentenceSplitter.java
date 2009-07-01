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
 * Split sentences at newlines.
 * @author bhaddow
 */
public class NewlineSentenceSplitter extends SentenceSplitter {

    public NewlineSentenceSplitter() {
        super(null);
    }

    @Override
    public String[] split(String  input) {
        return input.split("\n");
    }

}
