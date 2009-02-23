#!/usr/bin/perl -w

use strict;
use IO::Socket;
use utf8;
use FindBin qw($Bin);

my $debug = 1;

STDOUT->autoflush(1);
STDERR->autoflush(1);

use AppWrapper;
use SentenceSplitter;
use OutAndError;

my $moses = "/home/pkoehn/moses/moses-cmd/src/moses.1905.irst.64bit";
my $moses_recase = "/home/pkoehn/moses/moses-cmd/src/moses.1905.irst.64bit";
my $tools = "$Bin/../support-tools";
my $model_dir = "/disk4/webtrans-models";

my $en_recaser = AppWrapper::new({
	name=>"en recaser", 
	cmd=>"$tools/recase.perl", 
	arg=>"--moses $moses_recase --model $model_dir/recasing/matrix07b/model.1/moses.ini.1"
});

my $de_recaser = AppWrapper::new({
	name=>"de recaser", 
	cmd=>"$tools/recase.perl", 
	arg=>"--moses $moses_recase --model $model_dir/recasing/matrix07b/model.4/moses.ini.4"
});

#my $fr_recaser = AppWrapper::new({
#	name=>"fr recaser", 
#	cmd=>"$tools/recase.perl", 
#	arg=>"--moses $moses_recase --model $model_dir/recasing/matrix07b/model.10/moses.ini.10"
#});

#my $es_recaser = AppWrapper::new({
#	name=>"es recaser", 
#	cmd=>"$tools/recase.perl", 
#	arg=>"--moses $moses_recase --model $model_dir/recasing/matrix07b/model.7/moses.ini.7"
#});

my $en_detok = AppWrapper::new({
	name=>"en detokenizer", 
	cmd=>"$tools/detokenizer.perl", 
	arg=>"-l en"
});

my $de_detok = AppWrapper::new({
	name=>"de detokenizer", 
	cmd=>"$tools/detokenizer.perl", 
	arg=>"-l de"
});

#my $fr_detok = AppWrapper::new({
#	name=>"fr detokenizer", 
#	cmd=>"$tools/detokenizer.perl", 
#	arg=>"-l fr"
#});

#my $es_detok = AppWrapper::new({
#	name=>"es detokenizer", 
#	cmd=>"$tools/detokenizer.perl", 
#	arg=>"-l es"
#});

my $de_tok = AppWrapper::new({
	name=>"de tokenizer", 
	cmd=>"$tools/tokenizer.perl", 
	arg=>"-l de"
});

my $fr_tok = AppWrapper::new({
	name=>"fr tokenizer", 
	cmd=>"$tools/tokenizer.perl", 
	arg=>"-l fr"
});

my $es_tok = AppWrapper::new({
	name=>"es tokenizer", 
	cmd=>"$tools/tokenizer.perl", 
	arg=>"-l es"
});

my $en_tok = AppWrapper::new({
	name=>"en tokenizer", 
	cmd=>"$tools/tokenizer.perl", 
	arg=>"-l en"
});

my $lowercaser = AppWrapper::new({
	name=>"lowercaser", 
	cmd=>"$tools/lowercase.perl", 
	arg=>""
});

my $fr_en_moses = AppWrapper::new({
		name=>"moses", 
		cmd=>$moses, 
		arg=>"-config  $model_dir/fr-en.matrix07b/moses.ini.1 -v 2 -s 100"
});

my $de_en_moses = 	AppWrapper::new({
		name=>"moses", 
		cmd=>$moses, 
		arg=>"-config  $model_dir/de-en.matrix07b/moses.ini.3 -v 2 -s 100"
});

my $es_en_moses = AppWrapper::new({
		name=>"moses", 
		cmd=>$moses, 
		arg=>"-config  $model_dir/es-en.matrix07b/moses.ini.2 -v 2 -s 100"
});

my $en_de_moses = AppWrapper::new({
		name=>"moses", 
		cmd=>$moses, 
		arg=>"-config  $model_dir/en-de.matrix07b/moses.ini.4 -v 2 -s 100"
});

my %translation_systems = ( 
    "fr-en"=>[
	SentenceSplitter::new({lang=>"fr"}),
    $fr_tok,
	$lowercaser,
    $fr_en_moses,
	$en_recaser,
	$en_detok
    ],
    "de-en"=>[
	SentenceSplitter::new({lang=>"de"}),
    $de_tok,
	$lowercaser,
    $de_en_moses,
	$en_recaser,
	$en_detok
    ],
    "es-en"=>[
	SentenceSplitter::new({lang=>"es"}),
    $es_tok,
	$lowercaser,
    $es_en_moses,
	$en_recaser,
	$en_detok
    ],
    "en-de"=>[
	SentenceSplitter::new({lang=>"en"}),
    $en_tok,
	$lowercaser,
    $en_de_moses,
	$de_recaser,
	$de_detok
    ],
    "fr-en-raw"=>[
    $fr_en_moses,
    ],
    "de-en-raw"=>[
    $de_en_moses,
    ],
    "es-en-raw"=>[
    $es_en_moses,
    ],
    "en-de-raw"=>[
    $en_de_moses,
    ],
);

my $sock = new IO::Socket::INET(
	LocalHost => 'localhost',
	LocalPort => 7891,
	Proto => 'tcp',
	Listen => SOMAXCONN,
	Reuse => 1);

$sock or die "no socket :$!";

my($new_sock, $c_addr);

while (($new_sock, $c_addr) = $sock->accept()) {
	my ($client_port, $c_ip) =sockaddr_in($c_addr);
	my $client_ipnum = inet_ntoa($c_ip);
	my $client_host =gethostbyaddr($c_ip, AF_INET);
	print "got a connection from: $client_host"," [$client_ipnum] \n" if $debug;

	my $trans_system_id = '';

	while (defined (my $content_string = <$new_sock>))
	{
		chomp($content_string);
		if (!$trans_system_id) {
			$trans_system_id = $content_string;
			print ("Got System ID: \"$trans_system_id\"\n") if $debug;
			next;
		}
		if ($content_string eq "DONESTR") {
			print("Got done string\n") if $debug;
			$trans_system_id = '';
			last;
		}
		
		print("Received: $content_string\n") if $debug;
		
		my $trans_chain = $translation_systems{$trans_system_id};
		
		my $response = "";
		my ($result,$results);

		if (!$trans_chain) {
			
			$response = "invalid code: $trans_system_id\n";
		} else {
			
			foreach my $step (@$trans_chain) {
				
				$result = $step->send_to_app($content_string);
				$results = $result->out();
				$content_string = join("\n",@$results);				
				$response .= $result->serialize();
			}
			
			$response = $content_string.$response;
			
		}
		
		print $new_sock("$response\n");
		
		print("Sent: $content_string\n") if $debug;


	}
	print ("closing connection to: $client_host"," [$client_ipnum] \n") if $debug;
	close($new_sock);
	
} 
