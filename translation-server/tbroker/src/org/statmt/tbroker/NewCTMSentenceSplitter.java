/*=======================================================================
 *
 *  Copyright (c) Barry Haddow
 *  All rights reserved
 *
 *  First Published: 2009
 *
 *  $Author: abirch $
 *  $Date: 2014-02 
 *  $URL: http://abmayne@svn.statmt.org/repository/code/translation-server/tbroker/src/org/statmt/tbroker/NewCTMSentenceSplitter.java $
 *  ========================================================================*/
package org.statmt.tbroker;

/**
 * Split sentences at CTM newlines ie. a line which starts with a #
 * @author abirch
 */
public class NewlineSentenceSplitter extends SentenceSplitter {

    public NewlineSentenceSplitter() {
        super(null);
    }

    @Override
    public String[] split(String  input) {
        return input.split("\n#");
    }

}
