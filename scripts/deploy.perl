#!/usr/bin/env perl

#
# Copy the appropriate scripts into the web directory and 
# configure the moses servers.
#
use strict;
use FindBin qw($Bin);

my $html_source_dir = "$Bin/html";
my $server_source_dir = "$Bin/perl-server";
my $tools_dir = "$Bin/support-tools";
my $html_dir;
my $server_dir;
my $port;
if ($ARGV[0]  eq "prod") {
    print "Production deployment\n";
    $html_dir = "/disk4/html/demo";
    $server_dir = "/disk4/translation-server/prod";
    $port = 7890;
} else {
    $html_dir = "/disk4/html/demo/dev";
    $server_dir = "/disk4/translation-server/dev";
    $port = 7891;
}

print "Copying scripts into $html_dir\n";
#system ("rm -rf $html_dir");
system("mkdir -p  $html_dir/log");
system("chmod a+w $html_dir/log");
system("rsync -ra --exclude=.svn --delete $html_source_dir/*  $html_dir");
system("rsync -ra --exclude=.svn --delete $tools_dir/  $html_dir/web/bin");

print "Copying server into $server_dir\n";
system("mkdir -p $server_dir");
system("rsync -ra --exclude=.svn --delete $server_source_dir/*  $server_dir");
system("rsync -ra --exclude=.svn --delete $tools_dir/  $server_dir/support-tools");

print "Setting ports\n";
foreach my $file ("$html_dir/index.php","$html_dir/web/translate.cgi","$server_dir/socket_server.pl") {
       system("perl -pi -e   's/__PORT__/7891/g' $file");
   }  
