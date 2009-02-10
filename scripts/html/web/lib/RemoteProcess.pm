# file: RemoteProcess.pm

# Herve Saint-Amand
# Universitaet des Saarlandes
# Thu May 15 08:30:19 2008

#------------------------------------------------------------------------------
# includes

package RemoteProcess;
our @ISA = qw/Subprocess/;

use warnings;
use strict;

use IO::Socket::INET;
use Encode;

use Subprocess;

#------------------------------------------------------------------------------
# constructor

sub new {
    my ($class, $host, $port) = @_;

    my $self = new Subprocess;
    $self->{host} = $host;
    $self->{port} = $port;
    $self->{sock} = undef;

    bless $self, $class;
}

#------------------------------------------------------------------------------
# should have the same interface as Subprocess.pm

sub start {
    my ($self) = @_;

    $self->{sock} = new IO::Socket::INET (%{{
        PeerAddr => $self->{host},
        PeerPort => $self->{port},
    }}) || die "Can't connect to $self->{host}:$self->{port}";

    $self->{child_in} = $self->{child_out} = $self->{sock};
}


sub do_line {
    my ($self, $line) = @_;
    my ($in, $out) = ($self->{child_in}, $self->{child_out});

    $line =~ s/\s+/ /g;
    print $in encode ('UTF-8', $line), "\n";
    $in->flush ();

    # Just get the first line
    my $ret = decode ('UTF-8', scalar <$out>);
    chomp $ret;
    while (<$out>) {
        last if /^\[\[/;
    }
    $ret =~ s/%%%.*//g;

    $self->{num_done}++;
    return $ret;
}


#------------------------------------------------------------------------------

1;

