package OutAndError;
use strict;

sub new {
	my $self = {
		app => undef,
        ok => undef,
        out => undef,
        err => undef
    };
	
	bless $self;
		
	return $self;
}

sub ok {
	my ($self,$ok) = @_;
	$self->{ok} = $ok if defined($ok);
	return $self->{ok};
}

sub app {
	my ($self,$app) = @_;
	$self->{app} = $app if defined($app);
	return $self->{app};
}

sub out {
	my ($self,$out) = @_;
	$self->{out} = $out if defined($out);
	return $self->{out};
}

sub err {
	my ($self,$err) = @_;
	$self->{err} = $err if defined($err);
	return $self->{err};
}

sub serialize {
	my $self = shift;
	my $flat = "";
	my $sep = " %%% ";
	$flat .= $sep;
	$flat .= $self->{app} if defined($self->{app});
	$flat .= $sep;
	$flat .= $self->{ok};
	$flat .= $sep;
	$flat .= join("\n", @{$self->{out}}) if defined($self->{out});
	$flat .= $sep;
	$flat .= join("\n", @{$self->{err}}) if defined($self->{err});
	$flat .= $sep;
}

1;