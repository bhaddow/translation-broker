#!/usr/bin/perl -w

# $Id$
# Based on Preprocessor written by Philipp Koehn
# Written by Alexandra Birch 2/2014
# 
#Can take in ctm with one or multiple sentences marked with #
#;;Comments in beginning
## talkid767_15_50 15.50
#talkid767 1 16.02 0.14        i'm 0.999752
#talkid767 1 16.16 0.12      going 0.999994
#talkid767 1 16.28 0.07         to 0.999995
## talkid767_17_98 17.98
#talkid767 1 16.35 0.27       talk 1.000000
#talkid767 1 16.62 0.33      today 1.000000
#talkid767 1 16.95 0.34      about 1.000000
## talkid767_17_98 17.98
# Returns a ctm file, with one sentence per line, with each word separated with a %%% ie.
#
# # talkid767_15_50 15.50%%%talkid767 1 16.02 0.14        i'm 0.999752%%%talkid767 1 16.16 0.12      going 0.999994%%%talkid767 1 16.28 0.07         to 0.999995
# # talkid767_17_98 17.98%%%talkid767 1 16.35 0.27       talk 1.000000%%%talkid767 1 16.62 0.33      today 1.000000%%%talkid767 1 16.95 0.34      about 1.000000
$|++;

binmode(STDIN, ":utf8");
binmode(STDOUT, ":utf8");
binmode(STDERR, ":utf8");

use FindBin qw($Bin);
use strict;

my $mydir = "$Bin";

my %NONBREAKING_PREFIX = ();
my $language = "en";
my $QUIET = 0;
my $HELP = 0;
my $DIR = "";

while (@ARGV) {
	$_ = shift;
	/^-dir$/ && ($DIR = shift, next);
	/^-q$/ && ($QUIET = 1, next);
	/^-h$/ && ($HELP = 1, next);
}

if ($HELP) {
    print "Usage ./split-sentences-ctm.perl < ctmfile > flatctmfile\n";
	exit;
}
if (!$QUIET) {
	print STDERR "CTM to FlatCTM\n";
}

##loop text, add lines together until we get a line starting with # 
my $text = "";
while(<STDIN>) {
        chomp;
        s/^\s+//g;
        s/\s+$//g;
	if (/^;;/) {
	  #ignore comment line
	} 
	elsif (/^\s*$/) {
	  #ignore empty lines
	}
	elsif (/^#/) {
		#time to process this block, we've hit a new line
		print &preprocess($text) if ($text);
		$text = "";
	}
	else {
		#append the text
		$text .= $_ . "\n";
	}
}
print &preprocess($text) if ($text);

sub preprocess {
  	my ($text) = @_;
	
	my @out;
        $text =~ s/'/\&apos;/g;
	my @lines = split ("\n",$text);
	my $out = join ("%%%", @lines);
	#Moses can't handle empty txt lines, add a . as a dummy
	if (!$out) {
	  $out = ".";
	}
	return $out,"\n";
}


