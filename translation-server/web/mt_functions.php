<?php

# Tell xmlrpc that we're giving it utf8 strings, so that it will
# encode them correctly
$GLOBALS['xmlrpc_internalencoding']='UTF-8'; 

$debug = false;

include_once("xmlrpc.inc");

function debug($msg) {
	global $debug;
	
	if ($debug) print $msg;

}

error_reporting(E_ALL);


function translate ($input, $system_id, $port, $debug) {
    #print "$input<br>\n";
    #$encoded = new xmlrpcval($input,"string");
    #print $encoded->scalarVal(); print "<br>\n";
    $client = new xmlrpc_client("/xmlrpc", "localhost", $port);
    $request = new xmlrpcmsg('translate');
    $param = new xmlrpcval(
        array(
            "text" => new xmlrpcval($input,"string"),
            "systemid" => new xmlrpcval($system_id,"string")
            ), "struct");
    if ($debug) {
        $param->addStruct(
            array("debug" => new xmlrpcval("yes", "string")));
    }
    $request->addParam($param);
    #print "<pre>"; print $request->serialize(); print "</pre>>\n";
    $resp = $client->send($request);
    if ($resp->faultCode()) {
        die("Unable to communicate with translation server");
    }
    $result = array("translation" => $resp->value()->structMem("text")->scalarVal());
    if ($debug) {
        $result["debug"] = array();
        $debugMsgs = $resp->value()->structMem("debug");
        for ($i = 0; $i < $debugMsgs->arraySize(); ++$i) {
            $result["debug"][$i] = $debugMsgs->arrayMem($i)->scalarVal();
        }
    }
	return $result;

}

?>
