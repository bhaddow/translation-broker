#!/usr/bin/env perl

use Encode;
use XMLRPC::Lite;
use utf8;
binmode(STDOUT, ":utf8");
use open ':encoding(utf8)';

$url = "http://localhost:7893/xmlrpc";
$proxy = XMLRPC::Lite->proxy($url);

$input = "les gardiens de la révolution";
print Encode::is_utf8($input) . "\n";
my %param;
$encoded = SOAP::Data->type(string => Encode::encode_utf8($input));
print $encoded->value();
print "\n";
$param{text} = $encoded;
$param{systemid} = "fr-en-bare";

$result = $proxy->call('translate',\%param)->result;
print $result->{text} . "\n";


