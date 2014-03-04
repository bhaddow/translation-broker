#!/usr/bin/perl -w
# Written by Alexandra Birch 2/2014
#
# We need access to the MT output and to the ctm file. 
# We save the original ctm file and store the file name in the jobarray
# When calling this script, we prepend the ctm file name in the file,
# and then pipe it as input to this script.
# So here we extract the ctm file name from the first line of the mt output.
#
# 
# ctm file with timing information which contains the 
# words from the original source sentence:
# a |2-2| big |3-3| test |4-4| is |1-1| TThis |0-0|
## talkid767_15_50 15.50%%%talkid767 1 16.02 0.14        i'm 0.999752%%%talkid767 1 16.16 0.12      going 0.999994%%%talkid767 1 16.28 0.07         to 0.999995%%%# talkid767_17_98 17.98
# another |2-2| big |3-3| test |4-4| is |1-1| TThis |0-0|
## talkid767_15_50 15.50%%%talkid767 1 16.02 0.14        i'm 0.999752%%%talkid767 1 16.16 0.12      going 0.999994%%%talkid767 1 16.28 0.07         to 0.999995%%%# talkid767_17_98 17.98

use strict;

binmode(STDIN,"utf8");
binmode(STDOUT,"utf8");

my $alt = 0;
my @trans_data =[];
my @ctm_data =[];
while(<STDIN>){
  my $text = $_;
  chomp($text);
  if ($alt == 0) {
    &preprocess_mtout($text,\@trans_data);
    $alt = 1;
  } else {
    &preprocess_ctm($text,\@ctm_data);
    $alt = 0;
  }
}

my $count=0;
foreach my $sent (@trans_data){
  my $ctm_sent;
  if (defined  $ctm_data[$count]) {
    $ctm_sent = $ctm_data[$count];
  } else {
    print "Missing ctm sentence $count\n";
    $count++;
    next;
  } 
  foreach my $phrase (@$sent){
    my $start_word;
    my $start_time = 0;
    if (defined $ctm_sent->[$phrase->{'start-pos'}]){
      $start_word = $ctm_sent->[$phrase->{'start-pos'}];
      $start_time = $start_word->{'start-time'};
    } else {
      print "Missing start position $phrase->{'start-pos'}\n";
    }
    my $duration=0;
    for (my $i=$phrase->{'start-pos'}; $i <= $phrase->{'end-pos'}; $i++){
      if (defined $ctm_sent->[$i]) {
        $duration += $ctm_sent->[$i]->{'duration'};
      } else {
        print STDERR "Missing position $i\n";
      }
    }
    if (defined $start_word) {
      my $out = $phrase->{word};
      $out =~ s/\&apos;/'/;
      $out =~ s/' /'/;
      $out =~ s/@\-@/\-/;
      $out =~ s/ \- /\-/;
      print STDOUT "$start_word->{name} $start_word->{somefield} $start_time $duration $out\n";
    }
  }
  $count++;
  print "#end of sentence\n";
}

sub preprocess_ctm {
  my ($text,$ctm_line,$count) = @_;
  my @lines = split ("%%%",$text);
  my @entry;
  foreach my $line (@lines) {
    if ($line =~ /^#/) {
      next;
    }
    $line =~ s/\r//g;
    $line =~ s/\s+/ /g;
    my @el = split(" ",$line);
    my $word;
    $word->{'start-time'} = $el[2];
    $word->{'duration'} = $el[3];
    $word->{'name'} = $el[0];
    $word->{'somefield'} = $el[1];
    $word->{'source word'} = $el[4];
    $word->{'probability'} = $el[5];
    push(@entry,$word);
  }
  push(@$ctm_line,@entry);
}

#Bonjour |0-0| , même |1-2| avec plus |3-4| de |6-6| nuages |5-5|
#open (TRANS, "$translated_file") or die ("Failed opening $translated_file");
#binmode(TRANS,"utf8");
sub preprocess_mtout {
  my ($line,$trans_data) = @_;
  chomp($line);
  $line =~ s/\s+/ /g;
  my $working = $line;
  my @phrase;
  while ($working =~ s/([^|]+) \|(\d+)\-(\d+)\|\s*//){
    my $phrase->{'word'} = $1;
    $phrase->{'start-pos'} = $2;
    $phrase->{'end-pos'} = $3;
    push(@phrase,$phrase);
  }
  push(@$trans_data,@phrase);
  if (length $line > 0){
    print STDERR "WARNING: no source phrase information in translated text expecting source alignment eg. |0-0| for each output phrase: $line\n";
    print "$line";
  }
  $count++;
}
#close(TRANS);
