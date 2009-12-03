<?php

include_once('xmlrpc.inc');
include_once("mt_functions.php");

$port = __PORT__;
$key_data = "responseData";
$key_status = "responseStatus";
$key_details = "responseDetails";

# read input params
if (isset($_POST['langpair'])) {
    $langpair = $_POST['langpair'];
} else {
    error("No language pair");
}

if (isset($_POST['q'])) {
    $input = $_POST['q'];
} else {
    error("No input text");
}


# Get the list of systems
$client = new xmlrpc_client("/xmlrpc", "localhost", $port);
$request = new xmlrpcmsg('list');
$response = $client->send($request);
if ($response->faultCode()) {
   error("Unable to communicate with translation server");
}

$sysid = Null;

# Find a system for this language pair
for ($i = 0; $i < $response->value()->arraySize(); ++$i) {
    $system = $response->value()->arrayMem($i);
    $name = $system->structMem("name")->scalarVal();
    $source = $system->structMem("source")->scalarVal();
    $target = $system->structMem("target")->scalarVal();
    $tokinput = $system->structMem("tokinput")->scalarVal();
    $lcinput = $system->structMem("lcinput")->scalarVal();
    if (!$tokinput && !$lcinput) {
        if ($langpair == "$source|$target") {
            $sysid = $name;
        }
    }
}

if (!$sysid) {
    error("invalid translation language pair");
}

$output = "";
$translation_result = translate($input,$sysid,$port,false);
foreach (array_keys($translation_result) as $i) {
    $translation = $translation_result[$i]["translation"];
    if ($output) {
        $output = $output . " ";
    }
    $output = $output . $translation;
}


$response = array();
$response['responseData'] = array();
$response['responseData']['translatedText'] = $output; 
$response['responseStatus'] = 200;
$response['responseDetails'] = Null;

$encoded = json_encode($response);
die($encoded);


function error($msg) {
    global $key_data,$key_details,$key_status;
    $response = array();
    $response[$key_data] = Null;
    $response[$key_details] = $msg;
    $response[$key_status] = 400;
    die(json_encode($response));
}

?>
