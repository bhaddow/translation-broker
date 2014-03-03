#!/usr/bin/perl -w

# $Id$
# Based on Preprocessor written by Philipp Koehn
# Written by Alexandra Birch 2/2014
# 
#Takes in a flatctm style file ie:
## talkid767_15_50 15.50%%%talkid767 1 16.02 0.14        i'm 0.999752%%%talkid767 1 16.16 0.12      going 0.999994%%%talkid767 1 16.28 0.07         to 0.999995
## talkid767_17_98 17.98%%%talkid767 1 16.35 0.27       talk 1.000000%%%talkid767 1 16.62 0.33      today 1.000000%%%talkid767 1 16.95 0.34      about 1.000000
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
    print "Usage ./ctm-to-txt.perl < ctmfile > txtfile\n";
	exit;
}
if (!$QUIET) {
	print STDERR "CTM to TXT\n";
}

##loop text, add lines together until we get a line starting with # 
my $text = "";
while(<STDIN>) {
        my $text = $_;
        print &preprocess($text) if ($text);
}

sub preprocess {
  	my ($text) = @_;
	
	my @out;
	my @lines = split ("%%%",$text);
	foreach my $line (@lines) {
	  	my @fields = split /\s+/, $line;
		if (scalar @fields < 5) {
		  print STDERR "WARNING: input ctm not right $line, $text";
		}
	        push(@out,$fields[4]);	
	}
	my $out = join (" ", @out);
	#Make sure all ctm input is lowercased
	$out = lc $out;
	#Moses can't handle empty txt lines, add a . as a dummy
	if (!$out) {
	  $out = ".";
	}
	return $out,"\n";
}


