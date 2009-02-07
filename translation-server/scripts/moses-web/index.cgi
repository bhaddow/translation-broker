#!/usr/bin/perl -Tw
use warnings;
use strict;
$|++;

# file: index.cgi

# Herve Saint-Amand
# Universitaet des Saarlandes
# Tue May 13 20:09:25 2008

# When we do Moses translation of Web pages there's a little tool frame at the
# top of the screen. This script takes care of printing the frameset and that
# top frame. It also invokes the main script with the proper URL param.

# You're most probably not interested in the code that's in here. Have a look
# a translate.cgi instead. That's where the meat is.

#------------------------------------------------------------------------------
# includes

use CGI;
use CGI::Carp qw/fatalsToBrowser/;

use URI::Escape;

#------------------------------------------------------------------------------
# constants, global vars

(my $SELF_URL = $ENV{QUERY_STRING}) =~ s![^/]*$!!;

my $TRANSLATE_CGI = 'translate.cgi';

#------------------------------------------------------------------------------
# read CGI params

my %params = %{{
    url   => undef,
    frame => undef,
    sysid => undef,
}};

my $cgi = new CGI;

foreach my $p (keys %params) {
    $params{$p} = $cgi->param ($p)
        if (defined $cgi->param ($p));
}

# Read server info
open CFG, "server.cfg" or die "Unable to open server config";
my %descs;
my %addresses;
while (<CFG>) {
    next if /#/;
    my @fields = split;
    my $name = $fields[0];
    my $host = $fields[2];
    my $port = $fields[3];
    my $desc = join " ",@fields[4..$#fields];
    $descs{$name} = $desc;
    $addresses{$name} = "$host:$port";
}


#------------------------------------------------------------------------------
# print out

print "Content-Type: text/html\n\n";

print
    "<html>\n" .
    "  <head>\n" .
    "    <title>$params{url} -- Moses translation</title>\n" .
    "    <style>\n" .
    "      p, a, b, body {\n" .
    "        font-family: verdana;\n" .
    "        font-size: 9pt;\n" .
    "      }\n" .
    "    </style>\n" .
    "  </head>\n";

if (!$params{url}) {
    print
        "  <body bgcolor='#ffFFfF'>\n" .
        "    <h1>Moses Web Interface</h1>\n" .
        "    <form method='GET' action='$SELF_URL'>\n" .
        "      <input name='url' size='60'>\n" .
        "      <input type='submit' value='Translate'>\n" .
        "      <p>\n" .
        "      <select name='sysid'>\n";
        my $name;
        foreach $name (keys %descs) {
            my $desc = $descs{$name};
            print 
                "          <option value='$name'>$desc</option>\n";
        }

    print 
        "       </select>\n" .
        "    </form>\n" .
        "  </body>\n";

} else {

    # check that we have a URL and it's absolute
    $params{url} = "http://$params{url}"
        unless ($params{url} =~ m!^[a-z]+://!);
    my $URL = uri_escape ($params{url});
    my $sysid = $params{sysid};
    my $address = $addresses{$sysid};

    if (!$params{frame}) {
        print
            "  <frameset rows='30,*' border='1' frameborder='1'>\n" .
            "    <frame src='$SELF_URL?frame=top&url=$URL'>\n" .
            "    <frame src='$TRANSLATE_CGI?url=$URL&address=$address&sysid=$sysid'>\n" .
            "  </frameset>\n";

    } else {
        print
            "  <script src='index.js'></script>\n" .
            "  <body bgcolor='#ccCCcC' onload='startCount()'>\n" .
            "    <b>Moses translation of\n" .
            "    <a href='$params{url}' target='_top'>$params{url}</a></b>\n" .
            "    <span id='status'></span>\n" .
            "  </body>\n";
    }
}

print "</html>\n";

#------------------------------------------------------------------------------
