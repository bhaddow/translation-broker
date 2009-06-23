#!/usr/bin/env perl

use strict;
use XMLRPC::Lite;


print XMLRPC::Lite
      -> proxy('http://localhost:8080/xmlrpc')
      -> call('Translator.translate', "source", "systemId" )
      -> result;
print "\n";
