#!/usr/bin/perl -w
# Written by Alexandra Birch 2/2014
#
# Outputs the MT translation with the MT source ctm timing information for aligning translated subtitles
#
# We need access to the MT output and to the ctm timing in alternate sentences:
# a |2-2| big |3-3| test |4-4| is |1-1| TThis |0-0|@@@talkid767_15_50 15.50%%%talkid767 1 16.02 0.14        i'm 0.999752%%%talkid767 1 16.16 0.12      going 0.999994%%%talkid767 1 16.28 0.07         to 0.999995%%%# talkid767_17_98 17.98
# another |2-2| big |3-3| test |4-4| is |1-1| TThis |0-0|@@@talkid767_15_50 15.50%%%talkid767 1 16.02 0.14        i'm 0.999752%%%talkid767 1 16.16 0.12      going 0.999994%%%talkid767 1 16.28 0.07         to 0.999995%%%# talkid767_17_98 17.98

use strict;

binmode(STDIN,"utf8");
binmode(STDOUT,"utf8");
binmode(STDERR, ":utf8");

print STDERR "Packaging translation in CTM \n";
STDOUT->autoflush(1);
STDERR->autoflush(1);

my $ctm_delim = "%%%";
my $parts_delim = "@@@";

my @trans_data =();
my @ctm_data =();
while(<STDIN>){
  my $text = $_;
  chomp($text);
  my @parts = split ($parts_delim,$text);
  preprocess_mtout($parts[0],\@trans_data);
  preprocess_ctm($parts[1],\@ctm_data);
  print_ctm(\@trans_data,\@ctm_data);
  @trans_data =();
  @ctm_data =();
}

sub print_ctm {
  my ($trans_data, $ctm_data) = @_;

  my $count=0;
  if (!defined $ctm_data or scalar @$ctm_data == 0) {
    print "Missing ctm sentence\n";
    return;
  } 
  if (!defined $trans_data or scalar @$trans_data == 0) {
    print "Missing translation sentence\n";
    return;
  } 
  foreach my $phrase (@$trans_data){
    my $start_word;
    my $start_time = 0;
    if (defined $ctm_data->[$phrase->{'start-pos'}]){
      $start_word = $ctm_data->[$phrase->{'start-pos'}];
      $start_time = $start_word->{'start-time'};
    } else {
      print STDERR "Missing start position $phrase->{'start-pos'}\n";
    }
    my $duration=0;
    for (my $i=$phrase->{'start-pos'}; $i <= $phrase->{'end-pos'}; $i++){
      if (defined $ctm_data->[$i]) {
        $duration += $ctm_data->[$i]->{'duration'};
      } else {
        print STDERR "Missing position $i\n";
      }
    }
    if (defined $start_word) {
      my $out = $phrase->{word};
      $out =~ s/\&apos;/'/g;
      $out =~ s/' /'/g;
      $out =~ s/@\-@/\-/g;
      $out =~ s/ \- /\-/g;
      print "$start_word->{name} $start_word->{somefield} $start_time $duration $out$ctm_delim";
    }
    $count++;
  }
  print "#end sent\n";
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
    push(@$ctm_line,$word);
  }
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
    push(@$trans_data,$phrase);
  }
}
#close(TRANS);
