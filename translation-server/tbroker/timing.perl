#!/usr/bin/env perl

#
# Extract timing info
#

use strict;

my @times = ();

my $i = 0;
my %names;
while(<>) {
    if (/Job times/) {
        my %breakdown = /([a-zA-Z0-9\-]+)=(\d+)/gm;
        foreach my $key (keys %breakdown) {
            $times[$i]->{$key} = $breakdown{$key};
            $names{$key} = 1;
        }
        ++$i;
    }
}

print join " ", sort keys %names;
print "\n";

foreach my $t (@times) {
    foreach my $name (sort keys %names) {
        my $time = $t->{$name};
        if ($time) { print "$time ";} else {print "0 "};
    }
    print "\n";
}

