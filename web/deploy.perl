#!/usr/bin/env perl

#
# Copy the appropriate scripts into the web directory
#

use strict;
use FindBin qw($Bin);

my $html_source_dir = "$Bin";

my $html_dir;
my $port;

if ($ARGV[0] eq "prod") {
    print "Production deployment\n";
    die "Not yet implemented";
} else {
    $html_dir = "/disk4/html/demo/dev";
    $port = 7893;
}

system("mkdir -p  $html_dir/log");
system("chmod a+w $html_dir/log");
print "Copying scripts into $html_dir\n";
system("rsync -rav --exclude=.svn --exclude=*.perl --exclude=log --delete $html_source_dir/  $html_dir");

print "Setting ports\n";
foreach my $file ("$html_dir/index.php") {
       system("perl -pi -e   's/__PORT__/$port/g' $file");
   }  

