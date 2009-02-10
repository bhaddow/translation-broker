package AppWrapper;

use strict;
use OutAndError;
use IPC::Open3;
use IO::Handle;
use IO::Select;
use utf8;

my $debug = 1;

sub new {
	my $self = shift;
	
	foreach my $attr (qw(name cmd arg)) {
		die "Attribute $attr required for AppWrapper"
		if !defined $self->{$attr};
	}
	
	my ($reader, $writer, $stderr, $pid) = bipipe("$self->{cmd} $self->{arg}");
	$self->{reader} = $reader;
	$self->{writer} = $writer;
	$self->{stderr} = $stderr;
	$self->{pid} = $pid;
	
	bless $self;
		
	return $self;
}


sub send_to_app {
	my $self = shift;
	my $input = shift;
	
	$input =~ s/^ | $//g;
	
	my @input_lines = split(/[\n\r]+/,$input);
	
	my @results = ();
	my @errors = ();
	return MaybeError::new_ok([]) if length(@input_lines) == 0;

	my $wr = $self->{writer};
	my $rd = $self->{reader};
	my $er = $self->{stderr};
	
	foreach my $input (@input_lines) {
		if (length($input) == 0 ) {
			push(@results,"");
		} else {
			print $wr $input."\n";
			print STDERR "WRAP_IN: ".$input."\n" if $debug;
			
			my $selector = IO::Select->new();
			$selector->add($er,$rd);
							
			READLOOP:while ((($selector->count() > 1) || $self->{name} eq "moses") && (my @ready = $selector->can_read)) {
				foreach my $fh (@ready) {
					my $raw_line = scalar <$fh>;
					if (fileno($fh) == fileno($er)) {
						#print ("error handles: ".fileno($fh)." ".fileno($rd)." ".fileno($er)."\n");
						my $line = $raw_line;
						$line =~ s/[\n\r]+//g;
						print STDERR "$self->{name} WRAP_ERR: $line\n" if $debug;
						push(@errors,$line);
						#stop moses once we get word mappings
						if ($self->{name} eq "moses" && $line =~ /^\[\[/) {
							last READLOOP;							
						}
						#print($self->{name}." removed err\n".$selector->count()."\n");
						
					} else {
						#print ("handles: ".fileno($fh)." ".fileno($rd)." ".fileno($er)."\n");
						#if (length(@results)> 0);
						#we guarantee only 1 line of stdout, so once we've got one, we remove this from the canread, stopping the loop
						$selector->remove($fh);
						my $line = $raw_line;
						$line =~ s/[\n\r]+//g;
						$line =~ s/^ | $//g;
						
						print STDERR "$self->{name} WRAP_OUT: $line\n" if $debug;
						push(@results,$line);
						#if stdout gives us a line, then we are done
						#print(join(" ",$selector->handles)." are left \n");;
						#last READLOOP if eof($raw_line);
					    
					}
				}
				
			}
		}
	
	}
	
	my $return = OutAndError::new();
	$return->ok(1);
	$return->app($self->{name});
	$return->out(\@results);
	$return->err(\@errors);
	
	return $return;
}


sub bipipe {
  my $cmd = shift;
  my $reader; my $writer;
  local (*SUB_WRITE, *SUB_READ, *SUB_ERR);
  my $pid = open3(*SUB_WRITE, *SUB_READ, *SUB_ERR, $cmd);
  die "Failed to open bipipe to: $cmd" if !$pid;

  #binmode(SUB_WRITE, ":utf8");
 # binmode(SUB_READ, ":utf8");
 # binmode(SUB_ERR, ":utf8");
  
  SUB_WRITE->autoflush(1);
  SUB_READ->autoflush(1);
  SUB_ERR->autoflush(1);

  return (*SUB_READ, *SUB_WRITE, *SUB_ERR, $pid);
}


1;


