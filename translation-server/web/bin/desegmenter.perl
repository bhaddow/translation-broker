#!/usr/bin/perl -w


# Desegmenter for Japanese

binmode(STDIN, ":utf8");
binmode(STDOUT, ":utf8");
binmode(STDERR, ":utf8");

use IO::Handle;
use strict;

STDOUT->autoflush(1);
STDERR->autoflush(1);


while(<STDIN>) {
    chomp;
    # remove all spaces that have japanese characters on either side
    s/(?<!\p{InBasicLatin})\s+(?!\p{InBasicLatin})//g;
    print;
    print "\n";
}
