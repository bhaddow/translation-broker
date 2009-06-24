#!/usr/bin/env perl

#
# Copy the appropriate scripts into the web directory
#

use strict;
use FindBin qw($Bin);

my $html_source_dir = "$Bin";

my $html_dir;
my $port;
my $dev;
my $delete;

if ($ARGV[0] eq "prod") {
    $html_dir = "/disk4/html/demo";
    $port = 7894;
    $dev = "";
    $delete = "";
} else {
    $html_dir = "/disk4/html/demo/dev";
    $port = 7893;
    $dev = "dev";
    $delete = "--delete";
}

system("mkdir -p  $html_dir/log");
system("chmod a+w $html_dir/log");
print "Copying scripts into $html_dir\n";
system("rsync -rav --exclude=.svn --exclude=deploy.perl --exclude=*.swp --exclude=log $delete $html_source_dir/  $html_dir");

print "Setting port  to $port, and setting dev variable\n";
foreach my $file ("$html_dir/index.php", "$html_dir/web.cgi", "$html_dir/translate.cgi") {
       system("perl -pi -e   's/__PORT__/$port/g' $file");
       system("perl -pi -e   's/__DEV__/$dev/g' $file");
   }  

