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
    # remove all spaces that have japanese characters on either side
    s/(\P{InBasicLatin}) (\P{InBasicLatin})/$1$2/g;
    print;
}
