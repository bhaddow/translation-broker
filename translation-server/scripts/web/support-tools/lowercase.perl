#!/usr/bin/perl -w

# $Id: lowercase.perl 41 2007-03-14 22:54:18Z hieu $

use strict;
use IO::Handle;
use utf8;


binmode(STDIN, ":utf8");
binmode(STDOUT, ":utf8");
binmode(STDERR, ":utf8");

STDOUT->autoflush(1);
STDERR->autoflush(1);

print STDERR "Lowercaser \n";

while(<STDIN>) {
  print lc($_);
}
