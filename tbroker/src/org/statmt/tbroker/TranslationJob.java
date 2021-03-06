package org.statmt.tbroker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;

/**
 * Wraps a translation job as it proceeds through the tool chain.
 * @author bhaddow
 *
 */
public class TranslationJob {
	
	public static final String FIELD_TEXT = "text";
	public static final String FIELD_SYSID = "systemid";
	public static final String FIELD_DEBUG = "debug";
	public static final String FIELD_ALIGN = "align";
	public static final String FIELD_TOPT = "topt";
	public static final String FIELD_SYSTEM = "system";
	public static final String FIELD_SOURCEID = "sourceid";
	
	public static final String TIME_ELAPSED = "totaltime";
	
	private int _sourceId; //position in source
	private String _text;
	private String _state;
	private String _systemId;
	private List<Map> _alignments;
  private List<Map> _topts;
	private List<String> _debug;
	private Map<String,Long> _timings = new HashMap<String, Long>();;
	private long _startTime = System.currentTimeMillis();
  private String _errorMessage;
	
	private static final Logger _logger = Logger.getLogger(TranslationJob.class);

	/**
	 * Does the demarshalling
	 * @param params
	 */
	public static TranslationJob[] create(Map params) throws XmlRpcException{
	    Object textObject = params.get(FIELD_TEXT);
	    String[] texts;
	    if (textObject instanceof String) {
	        texts = new String[]{(String)textObject};
	    } else if (textObject instanceof Object[]) {
	        Object[] textObjects  = (Object[])textObject;
	        texts = new String[textObjects.length];
	        for (int i = 0; i < texts.length; ++i) {
	            texts[i] = (String)textObjects[i];
	        }
	    } else {
	        throw new XmlRpcException("Text field is of incorrect type: " + (textObject == null ? "null" : textObject.getClass()));
	    }
	    TranslationJob[] jobs = new TranslationJob[texts.length];
	    String systemId = (String)params.get(FIELD_SYSID);
	    boolean debug = (params.get(FIELD_DEBUG) != null);
	    boolean align = (params.get(FIELD_ALIGN) != null);
    	    _logger.debug("Debug: " + params.get(FIELD_DEBUG));
    	    _logger.debug("Align: " + params.get(FIELD_ALIGN));
      boolean topt = (params.get(FIELD_TOPT) != null);
	    if (systemId == null) {
            throw new XmlRpcException("Missing system id");
        }
	    for (int i = 0; i < jobs.length; ++i) {
    	    jobs[i] = new TranslationJob();
    	    _logger.debug("Creating job with text: " + texts[i]);
    		jobs[i]._text = texts[i];
    		if (jobs[i]._text == null) {
    			throw new XmlRpcException("Missing text");
    		}
    		jobs[i]._systemId = systemId;
    		if (debug) {
    		    jobs[i]._debug = new ArrayList<String>();
    		}
    		if (align) {
    		    jobs[i]._alignments = new ArrayList<Map>();
    		}
        if (topt) {
            jobs[i]._topts = new ArrayList<Map>();
        }
    		jobs[i]._sourceId = i;
	    }
		return jobs;
	}
	
	public TranslationJob(TranslationJob job, String text) {
	    _text = text;
	    _systemId = job._systemId;
	     if (job._debug != null) {
	         _debug = new ArrayList<String>(job._debug);
	     }
	     if (job._alignments != null) {
	         _alignments = new ArrayList<Map>(job._alignments);
		 _logger.debug("Alignments in create job: " + _alignments);
	     }
       if (job._topts != null) {
           _topts = new ArrayList<Map>(job._topts);
       }
	     _sourceId = job._sourceId;
	}
	
	private TranslationJob() {}
	
	public String getSystemId() {
		return _systemId;
	}
	
	public String getText() {
		return _text;
	}
	
	public void setText(String text) {
		_text = text;
	}
	public String getState() {
		return _state;
	}
	
	public void setState(String text) {
		_state = text;
	}

  public void setErrorMessage(String errorMessage) {
    _errorMessage = errorMessage;
  }

  public String getErrorMessage() {
    return _errorMessage;
  }


	
	public void setAlignments(ArrayList<Map> align) { 
	    _alignments = align;
	}
	public List<Map> getAlignments() { 
	    return _alignments;
	}
	public String getFormattedAlignments(){
	    ArrayList<String> out = new ArrayList<String>();
            _logger.debug("getFormattedAlignments here ");
	    if (_text == null) {
	      _logger.warn("No text information");
	      return "";
	    }
	    String[] splitted = _text.split("\\s+");
	    if (_alignments == null) {
	      _logger.warn("No alignment information");
	      return "";
	    }
	    for(int i = 0; i < _alignments.size(); i++){
	      Map align = _alignments.get(i);
	      Integer end = splitted.length;
	      if (i+1 < _alignments.size()) {
	        Map align_next = _alignments.get(i+1);
		end = (Integer)align_next.get("tgt-start");
	      }
              _logger.debug("getFormattedAlignments: i" + i + " end:" + end  );
	      Integer startSource = (Integer)align.get("src-start");
	      Integer endSource = (Integer)align.get("src-end");
	      Integer startTarget = (Integer)align.get("tgt-start");
	      if (startSource == null || endSource == null || startTarget == null) {
		  _logger.warn("Missing alignment info");
		  return "";
	      }
	      if (startTarget > splitted.length || end > splitted.length) {
		  _logger.warn("StartTarget:" + startTarget +" or end:" + end + " > length of output text string:" + _text);
		  return "";
	      }
	      for (int j=startTarget; j< end; j++) {
		  out.add(splitted[j]);
	      }
	      String al = "|" + startSource + "-" + endSource + "|";
	      out.add(al);
	    }
	    String joinedString = StringUtils.join(out, " ");
            _logger.debug("getFormattedAlignments: " + joinedString  );
	    return joinedString;
	}


  public List<Map> getTopts() {
      return _topts;
  }
	
	public Map<String,Long> getTimings() {
	    long elapsed = System.currentTimeMillis() - _startTime;
	    _timings.put(TIME_ELAPSED, elapsed);
	    return _timings;
	}
	
	public void setTiming(String key, long time) {
	    _timings.put(key,time);
	}
	
	public boolean isDebugOn() {
	    return (_debug != null);
	}
	
	public void addDebug(String msg) {
	    if (_debug != null) {
	        _debug.add(msg);
	    }
	}
	
	public Map getResult() {
		Map result = new HashMap();
		result.put(FIELD_TEXT, _text);
		result.put(FIELD_SOURCEID, _sourceId);
		if (_debug != null) {
		    result.put(FIELD_DEBUG, _debug);
		}
		if (_alignments != null) {
		    result.put(FIELD_ALIGN, _alignments);
		    _logger.debug("Alignments in getResult: " + _alignments);
		}
    if (_topts != null) {
        result.put(FIELD_TOPT, _topts);
    }
		return result;
	}
}
