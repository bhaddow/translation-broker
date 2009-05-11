<?php

$debug = false;

include_once("xmlrpc.inc");

function debug($msg) {
	global $debug;
	
	if ($debug) print $msg;

}

error_reporting(E_ALL);


function translate ($input, $system_id, $port) {
    $client = new xmlrpc_client("/xmlrpc", "localhost", $port);
    $request = new xmlrpcmsg('translate');
    $param = new xmlrpcval(
        array(
            "text" => new xmlrpcval($input,"string"),
            "systemid" => new xmlrpcval($system_id,"string")
            ), "struct");
    $request->addParam($param);
    $resp = $client->send($request);
    if ($resp->faultCode()) {
        die("Unable to communicate with translation server");
    }
    $result = array("translation" => $resp->value()->structMem("text")->scalarVal());
	return $result;

}

?>
