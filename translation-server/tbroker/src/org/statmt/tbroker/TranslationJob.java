package org.statmt.tbroker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public static final String TIME_ELAPSED = "totaltime";
	
	
	private String _text;
	private String _systemId;
	private List<Map> _alignments;
	private List<String> _debug;
	private Map<String,Long> _timings = new HashMap<String, Long>();;
	private long _startTime = System.currentTimeMillis();

	/**
	 * Does the demarshalling
	 * @param params
	 */
	public TranslationJob(Map params) throws XmlRpcException{
		_text = (String)params.get(FIELD_TEXT);
		if (_text == null) {
			throw new XmlRpcException("Missing text");
		}
		_systemId = (String)params.get(FIELD_SYSID);
		if (_systemId == null) {
			throw new XmlRpcException("Missing system id");
		}
		if (params.get(FIELD_DEBUG) != null) {
		    _debug = new ArrayList<String>();
		}
		if (params.get(FIELD_ALIGN) != null) {
		    _alignments = new ArrayList<Map>();
		}
		
	}
	
	public TranslationJob(TranslationJob job, String text) {
	    _text = text;
	    _systemId = job._systemId;
	     if (job._debug != null) {
	         _debug = new ArrayList<String>(job._debug);
	     }
	     if (job._alignments != null) {
	         _alignments = new ArrayList<Map>(job._alignments);
	     }
	}
	
	public String getSystemId() {
		return _systemId;
	}
	
	public String getText() {
		return _text;
	}
	
	public void setText(String text) {
		_text = text;
	}
	
	public List<Map> getAlignments() { 
	    return _alignments;
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
		if (_debug != null) {
		    result.put(FIELD_DEBUG, _debug);
		}
		if (_alignments != null) {
		    result.put(FIELD_ALIGN, _alignments);
		}
		return result;
	}
}
