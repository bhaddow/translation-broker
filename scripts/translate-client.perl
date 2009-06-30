#!/usr/bin/env perl

use strict;
use XMLRPC::Lite;


my %params = ("systemid" => "fr-en", "text" => "Translate me! I'm the second sentence.");
my $ret = XMLRPC::Lite
      -> proxy('http://localhost:8080/xmlrpc')
      -> call('translate',\%params )
      -> result;
print $ret->{"text"} . "\n";
