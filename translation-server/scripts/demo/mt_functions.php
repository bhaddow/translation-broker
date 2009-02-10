<?php

$debug = true;


function debug($msg) {
	global $debug;
	
	if ($debug) print $msg;

}

error_reporting(E_ALL);


function open_socket_connection ($ip, $port) {
	
print "here now... open_socket_connection ($ip, $port)";
	/* Create a TCP/IP socket. */
	$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
print "here now... $socket";
	if ($socket === false) {
		print "socket_create() failed: reason: " . socket_strerror(socket_last_error()) . "\n";
		return false;
	} else {
		debug("OK.\n");
	}
print "here now...";
	
	debug("Attempting to connect to '$ip' on port '$port'...");
	$result = socket_connect($socket, $ip, $port);
	if ($result === false) {
		print "socket_connect() failed.\nReason: ($result) " . socket_strerror(socket_last_error($socket)) . "\n";
		return false;
	} else {
		debug("OK.\n");
	}
	return $socket;

}

function translate ($input, $system_id, $location) {

   print "hello world!";
	$socket = open_socket_connection($location['ip'],$location['port']);
   print "socket -> $socket\n";
	
	$result = '';
	
	debug("Sending request... ");
	socket_write($socket, "$system_id\n", strlen("$system_id\n"));
	socket_write($socket, $input, strlen($input));
	socket_write($socket,"DONESTR\n", strlen("DONESTR\n"));
	debug("OK.\n");	
	
	debug("Reading response... ");
	while ($out = socket_read($socket, 2048)) {
		$result .= $out;
	}
	debug("OK.\n");

	debug("Closing socket...");
	socket_close($socket);
	debug("OK.\n");
	
	return $result;

}

?>
