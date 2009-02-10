#!/usr/bin/env perl

#
# Copy the appropriate scripts into the web directory and 
# configure the moses servers.
#
use strict;
use FindBin qw($Bin);

my $log_dir = "$Bin/../logs";
my $html_source_dir = "$Bin/html";
my $tools_dir = "$Bin/support-tools";
my $html_dir = "/disk4/html/demo/dev";

system("mkdir -p $log_dir");

print "Copying scripts into $html_dir\n";
system ("rm -rf $html_dir");
system("rsync -ra --exclude=.svn --delete $html_source_dir/*  $html_dir");
system("rsync -ra --exclude=.svn --delete $tools_dir/  $html_dir/web/bin");


#my $moses_home = "/disk3/bhaddow/moses";
#my $moses_exe = "$moses_home/moses-cmd/src/moses";
#my $log_dir = "$Bin/../logs";
#my $moses_web = "$Bin/moses-web";
#my $cfg_file = "$Bin/server.cfg";
#my $daemon_script_in = "$moses_web/bin/daemon.pl";


#system("mkdir -p $log_dir");

#
# Create daemon scripts
#
#open(CFG, $cfg_file) || die "Error: unable to open server config file: $cfg_file";
#while(<CFG>) {
#    next if /^#/;
#    my ($name,$ini,$host,$portstring,@descr) = split;
#    print "Creating daemon scripts for $name\n";
#    my @ports = split /,/,$portstring;
#    my $daemon_dir = "$Bin/daemon-$name";
#    my $launch_script = "$Bin/launch-$name.sh";
#    my $kill_script = "$Bin/kill-$name.sh";
#    my $pid_file="$Bin/$name.pid";
    
#    open(LAUNCH, ">$launch_script") || die "Error: Unable to create launch script: $launch_script";
#    system("rm -rf $daemon_dir; mkdir -p $daemon_dir");
#    print LAUNCH  "#!/bin/sh\n";
#    print LAUNCH  "rm -f $pid_file\n";
#    foreach my $port  (@ports) {
#        print "Creating daemon on port $port\n";
#        open(DIN, "$daemon_script_in") || die "Error: unable to open daemon script: $daemon_script_in";
#        my $daemon_script_out = "$daemon_dir/daemon.$port.pl";
#        open(DOUT,">$daemon_script_out") || die "Error: unable to open daemon script for writing: $daemon_script_out";
#        while (<DIN>) {
#            next if /die \"usage.*/;
#            s/^my \$MOSES .*/my \$MOSES = '$moses_exe';/g;
#            s/^my \$MOSES_INI.*/my \$MOSES_INI = '$ini';/g;
#            s/^my \$LISTEN_H.*/my \$LISTEN_HOST = '$host';/g;
#            s/^my \$LISTEN_P.*/my \$LISTEN_PORT = '$port';/g;
#            s///g;
#            print DOUT;
#        }
#        close DIN;
#        close DOUT;
#        chmod 0775, $daemon_script_out;
#        print LAUNCH "echo \"Launching $name translation daemon on port $port\"\n";
#        print LAUNCH "nice $daemon_script_out &> $log_dir/$name.$port.log & \n";
#        print LAUNCH "echo \$! >> $pid_file\n";
#    }
#    close LAUNCH;
#    chmod 0775, $launch_script;
#    print "Created daemon launch script: $launch_script\n";
#    open (KILL, ">$kill_script") || die "Error: unable to create kill script: $kill_script";
#    print KILL "#!/bin/sh\n";
#    print KILL "for pid in `cat $pid_file`; do\n";
#    print KILL "    kill \$pid\n";
#    print KILL "done\n";
#    chmod 0775, $kill_script;
#    print "Created daemon kill script: $kill_script\n";
#}

#
# Copy cgi scripts into place
#
#print "Copying cgi scripts into $html_dir\n";
#system ("rm -rf $html_dir");
#system("rsync -r --exclude=.svn $moses_web/*  $html_dir");
#system("cp server.cfg $html_dir");
#system("chmod a+x $html_dir/*.cgi");
#system("chmod a+x $html_dir/bin/*.perl");


