#!/usr/bin/env perl

use strict;
use XMLRPC::Lite;


my %params = ("systemid" => "fr-en", "text" => "Translate me! I'm the second sentence.");
my $rets = XMLRPC::Lite
      -> proxy('http://localhost:8080/xmlrpc')
      -> call('translate',\%params )
      -> result;
foreach my $ret (@$rets) {
    print $ret->{"text"} . "\n";
}
