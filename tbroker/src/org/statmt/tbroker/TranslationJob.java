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
	
	private String _text;
	private String _systemId;
	private List<String> _debug;

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
            System.out.println("Collecting debug");
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
		return result;
	}
}
