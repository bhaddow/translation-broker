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


function translate ($input, $system_id, $port, $debug, $topt, $align) {
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
    if ($topt) {
      $param->addStruct(
        array("topt" => new xmlrpcval("yes", "string")));

    }
    if ($align) {
      $param->addStruct(
        array("align" => new xmlrpcval("yes", "string")));

    }
    $request->addParam($param);
    #print "<pre>"; print $request->serialize(); print "</pre>>\n";
    $resp = $client->send($request);
    if ($resp->faultCode()) {
        die("Unable to communicate with translation server");
    }
    $result = array();
    for ($i = 0; $i < $resp->value()->arraySize(); ++$i) {
        $field = $resp->value()->arrayMem($i);
        $result[$i] = array("translation" => $field->structMem("text")->scalarVal());
        if ($debug) {
            $result[$i]["debug"] = array();
            $debugMsgs = $field->structMem("debug");
            for ($j = 0; $j < $debugMsgs->arraySize(); ++$j) {
                $result[$i]["debug"][$j] = $debugMsgs->arrayMem($j)->scalarVal();
            }
        }
        if ($topt) {
          $result[$i]["topt"] = array();
          $returned_topts = $field->structMem("topt");
          for ($j = 0; $j < $returned_topts->arraySize(); ++$j) {
            $result[$i]["topt"][$j] = array();
            $returned_topt = $returned_topts->arrayMem($j);
            $result[$i]["topt"][$j]['phrase'] =
                $returned_topt->structMem("phrase")->scalarVal();
            $result[$i]["topt"][$j]['start'] =
                $returned_topt->structMem("start")->scalarVal();
            $result[$i]["topt"][$j]['end'] =
                $returned_topt->structMem("end")->scalarVal();
            $result[$i]["topt"][$j]['score'] =
                $returned_topt->structMem("fscore")->scalarVal();
          }
        }
    }
#    if ($debug) {
#        $result["debug"] = array();
#        $debugMsgs = $resp->value()->structMem("debug");
#        for ($i = 0; $i < $debugMsgs->arraySize(); ++$i) {
#            $result["debug"][$i] = $debugMsgs->arrayMem($i)->scalarVal();
#        }
#    }
	return $result;

}

?>
