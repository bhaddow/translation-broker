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

use XMLRPC::Lite;
use URI::Escape;

#------------------------------------------------------------------------------
# constants, global vars

(my $SELF_URL = $ENV{QUERY_STRING}) =~ s![^/]*$!!;

my $TRANSLATE_CGI = 'translate.cgi';
my $port = "__PORT__";
my $server_url = "http://localhost:$port/xmlrpc";
my $server_proxy = XMLRPC::Lite->proxy($server_url);

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

# Read systems
my $systemlist = $server_proxy->call('list')->result or 
    die "Unable to communicate with translation server";
    
my %systems;
foreach my $system (@$systemlist) {
# Just want the raw systems
    my $tokinput = $system->{tokinput};
    my $lcinput = $system->{lcinput};
    next unless $tokinput && $lcinput;
    my $sysid = $system->{name};
    my $description = $system->{description};
    my $source_lang = $system->{source};
    my $target_lang = $system->{target};
    $systems{$sysid} = [$description,$source_lang,$target_lang];
}

#my %systems = (
#    "fr-en-raw" => ["French-English (Europarl)","fr","en"],
#    "de-en-raw" => ["German-English (Europarl)","de","en"],
#    "es-en-raw" => ["Spanish-English (Europarl)","es","en"],
#    "en-de-raw" => ["English-German (Europarl)","en","de"]
#);


#------------------------------------------------------------------------------
# print out

print "Content-Type: text/html\n\n";

print
    "<html>\n" .
    "  <head>\n" .
    "    <title>$params{url} -- Moses translation</title>\n" .
    " <link rel=StyleSheet href=\"demo.css\" type=\"text/css\" >\n" .
    "  </head>\n";


if (!$params{url}) {
    print
        "  <body bgcolor='#ffFFfF'>\n" .
        "    <h1>Moses Web Translation Demo</h1>\n" .
        "    <form method='GET' action='$SELF_URL'>\n" .
        "      <input name='url' size='60'>\n" .
        "      <input type='submit' value='Translate'>\n" .
        "      <p>\n" .
        "      <select name='sysid'>\n";
        foreach my $sysid (keys %systems) {
            my $desc = $systems{$sysid}[0];
            print 
                "          <option value='$sysid'>$desc</option>\n";
        }

    print 
        "       </select>\n" .
        "    </form>\n" .
        "<h3>Looking to translate a text snippet? Then click <a href=\"index.php\">here</a></h3></br>\n" .
        "<hr>This site is maintained by the <A HREF=\"http://www.statmt.org/ued/\">Machine Translation Group</A> at the University of Edinburgh.<br>&nbsp;\n" .
        "  </body>\n";

} else {

    # check that we have a URL and it's absolute
    $params{url} = "http://$params{url}"
        unless ($params{url} =~ m!^[a-z]+://!);
    my $URL = uri_escape ($params{url});
    my $sysid = $params{sysid};
    my $source_language = $systems{$sysid}[1];
    my $target_language = $systems{$sysid}[2];

    if (!$params{frame}) {
        print
            "  <frameset rows='30,*' border='1' frameborder='1'>\n" .
            "    <frame src='$SELF_URL?frame=top&url=$URL'>\n" .
            "    <frame src='$TRANSLATE_CGI?url=$URL&sysid=$sysid&source_language=$source_language&target_language=$target_language'>\n" .
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
