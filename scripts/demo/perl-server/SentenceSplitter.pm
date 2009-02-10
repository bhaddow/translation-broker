package SentenceSplitter;

use OutAndError;
use strict;
use IO::Handle;
use utf8;

my $mydir =  "/var/www/html/webtrans/support-tools";

sub new {

	my $self = shift;
	
	foreach my $attr (qw(lang)) {
		die "Attribute $attr required for SentenceSplitter"
		if !defined $self->{$attr};
	}
	
	my $language = $self->{lang};
		
	my %NONBREAKING_PREFIX;
	
	if (-e "$mydir/nonbreaking_prefix.$language") {
		open(PREFIX, "<:utf8", "$mydir/nonbreaking_prefix.$language");
		while (<PREFIX>) {
			my $item = $_;
			chomp($item);
			$NONBREAKING_PREFIX{$item} = 1 if ($item);
		}
		close(PREFIX);
	} else {
		print STDERR "Warning: No known abbreviations for language '$language'\n";

	}
			
	$self->{prefix} = \%NONBREAKING_PREFIX;
			
	bless $self;
		
	return $self;
}

sub split_string {
	my $self = shift;
	my @input = split(/[\n\r]+/,shift);

	my @results = ();
	my $text = "";
	
	foreach (@input) {
		if (/^<.+>$/ || /^\s*$/) {
			#time to process this block, we've hit a blank or <p>
			push(@results,$self->split_contents($text,$_));
			push(@results,"<P>\n") if (/^\s*$/ && $text); ##if we have text followed by <P>
			$text = "";
		}
		else {
			#append the text, with a space(why the space?)
			$text .= $_. " ";
		}
	}
	
	#do the leftover text
	if ($text) {
		push(@results,$self->split_contents($text,""));
	}
	
	my $return = OutAndError::new();
	$return->app("splitter");
	$return->ok(1);
	$return->out(\@results);
	$return->err(\());
	
	return $return;
}

sub send_to_app {
	my $self = shift;
	return $self->split_string(shift);
	
}

sub split_contents {
	my $self = shift;
	my $text = shift;
	my $markup = shift;
	
	my %NONBREAKING_PREFIX = %{$self->{prefix}};
	my $language = $self->{lang};
	
	#TODO change ` to ' here?
	#TODO Spanish question & exlamation starters
	
	# clean up spaces at head and tail of each line as well as any double-spacing
	$text =~ s/ +/ /g;
	$text =~ s/\n /\n/g;
	$text =~ s/ \n/\n/g;
	$text =~ s/^ //g;
	$text =~ s/ $//g;
	
	#####add sentence breaks as needed#####
	
	#non-period end of sentence markers (?!) followed by sentence starters.
	$text =~ s/([?!]) +([\'\"\(\[\¿\¡\p{IsPi}]*[\p{IsUpper}])/$1\n$2/g;
		
	#multi-dots followed by sentence starters
	$text =~ s/(\.[\.]+) +([\'\"\(\[\¿\¡\p{IsPi}]*[\p{IsUpper}])/$1\n$2/g;
	
	# add breaks for sentences that end with some sort of punctuation inside a quote or parenthetical and are followed by a possible sentence starter punctuation and upper case
	$text =~ s/([?!\.][\ ]*[\'\"\)\]\p{IsPf}]+) +([\'\"\(\[\¿\¡\p{IsPi}]*[\ ]*[\p{IsUpper}])/$1\n$2/g;
		
	# add breaks for sentences that end with some sort of punctuation are followed by a sentence starter punctuation and upper case
	$text =~ s/([?!\.]) +([\'\"\(\[\¿\¡\p{IsPi}]+[\ ]*[\p{IsUpper}])/$1\n$2/g;
	
	# special punctuation cases are covered. Check all remaining periods.
	my $word;
	my $i;
	my @words = split(/ /,$text);
	$text = "";
	for ($i=0;$i<(scalar(@words)-1);$i++) {
		if ($words[$i] =~ /([\p{IsAlnum}]*)([\'\"\)\]\%\p{IsPf}]*)(\.+)$/) {
			#check if $1 is a known honorific and $2 is empty, never break
			if($1 && $NONBREAKING_PREFIX{$1} && !$2) {
				#not breaking;
			} elsif($words[$i+1] =~ /^([ ]*[\'\"\(\[\¿\¡\p{IsPi}]*[ ]*[\p{IsUpper}0-9])/) {
				#the next word has a bunch of initial quotes, maybe a space, then either upper case or a number
				$words[$i] = $words[$i]."\n";
			}
			
		}
		$text = $text.$words[$i]." ";
	}
	
	#we stopped one token from the end to allow for easy look-ahead. Append it now.
	$text = $text.$words[$i];
	
	# clean up spaces at head and tail of each line as well as any double-spacing
	$text =~ s/ +/ /g;
	$text =~ s/\n /\n/g;
	$text =~ s/ \n/\n/g;
	$text =~ s/^ //g;
	$text =~ s/ $//g;
	
	#add trailing break
	$text .= "\n" unless $text =~ /\n$/;
	
	my @results = ();
	
	push(@results, split(/[\n]+/,$text)) if $text;
	
	push(@results, split(/[\n]+/,$markup)) if ($markup =~ /^<.+>$/);
	
	return (@results);

}
