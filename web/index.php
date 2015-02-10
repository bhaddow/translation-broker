<?php
header('Content-type: text/html; charset=utf-8');

include_once('xmlrpc.inc');

$input = isset($_POST['input']) ? $_POST['input'] : "";
$sysid = isset($_POST['sysid']) ? $_POST['sysid'] : "";
$rdebug =  array_key_exists('debug',$_POST);
$alignment =  array_key_exists('alignment',$_POST);
$port = __PORT__;
$dev = "__DEV__";

$client = new xmlrpc_client("/xmlrpc", "localhost", $port);
$request = new xmlrpcmsg('list');
$resp = $client->send($request);
if ($resp->faultCode()) {
   die("Unable to communicate with translation server");
}

$systems=array();

for ($i = 0; $i < $resp->value()->arraySize(); ++$i) {
    $system = $resp->value()->arrayMem($i);
    $name = $system->structMem("name")->scalarVal();
    $desc = $system->structMem("description")->scalarVal();
    $tokinput = $system->structMem("tokinput")->scalarVal();
    $lcinput = $system->structMem("lcinput")->scalarVal();
    if (!$tokinput && !$lcinput) {
        $systems[$name] = $desc;
    }
}

if (!$input) $input="";
$input = stripslashes($input);

function head() {
  global $rdebug;
?><html><head>
<title>Moses Online MT Demo</title>
<link rel=StyleSheet href="demo.css" type="text/css" >
</head>
<body>
<h1>Moses Machine Translation Demo</h1>
<?php
}

function log_correction() {
  global $sysid;
  $fh = fopen("log/corrections.$sysid","a");
  fwrite($fh,"=== REQUEST AT ".$_POST["time"]."\n");
  
  foreach (array_keys($_POST) as $var) {
    if (preg_match("/^IN(.*)/",$var,$match)) {
      fwrite($fh,"=== IN $match[1]\n" .$_POST["IN" .$match[1]]."\n");
      fwrite($fh,"=== MT $match[1]\n" .$_POST["MT" .$match[1]]."\n");
      fwrite($fh,"=== OUT $match[1]\n".$_POST["OUT".$match[1]]."\n");
    }
  }
  fclose($fh);
  print "<h2>Thank you for contributing!</h2>";
}

head();
if (isset($_POST['CORRECTION'])) { log_correction(); };

print "<form action=\"index.php\" method=\"POST\">\n";
?>
<h2>Source:</h2>
<textarea name="input" cols=80 rows=5><?php print $input;?></textarea>
<P>
<select name="sysid">
<?php 
foreach (array_keys($systems) as $syskey) {
  print "<option value=\"".$syskey."\""; 
  if ($syskey == $sysid) { print " selected"; }
  print ">".$systems[$syskey]."</option>\n";
}
?></select>
&nbsp;<INPUT TYPE=CHECKBOX NAME="debug"<?php  if ($rdebug) print("checked")?>>Show Debug Output
&nbsp;<INPUT TYPE=CHECKBOX NAME="alignment"<?php  if ($alignment) print("checked")?>>Show Alignment<P>
<input type="Submit" value="Translate">
<P>
</form>
<h3>Looking to translate a web page? Then click <a href="web.cgi">here</a></h3></br>
<?php 
if ($input) {
  print "<h2>Translation:</h2>\n";
}
include_once("mt_functions.php");



$translation_array_string = "";

if ($input) {
    $moses_input_line = ""; 
    $translation_result = translate($input,$sysid,$port,$rdebug,false,$alignment);
    foreach (array_keys($translation_result) as $i) {

        $translation = $translation_result[$i]["translation"];
        print "<span class=\"translation\">$translation</span><P>\n";
        $translation_array_string = $translation_array_string . 
            $translation . "\n";


	# output phrase alignment
        if ($alignment) {
            $tgt_display = array();
            $src_display = array();			    
	    $source_tokens = $translation_result[$i]["src_tokens"];
	    $target_tokens = $translation_result[$i]["tgt_tokens"];
	    $alignment_infos = $translation_result[$i]["alignment"];

	    if (count($source_tokens) > 0 && 
		count($target_tokens) > 0 && 
		count($alignment_infos) > 0) {
	        for($j=0;$j<sizeof($alignment_infos);$j++) {
  	            $src_start = $alignment_infos[$j]["src_start"];
		    $src_end = $alignment_infos[$j]["src_end"];
		    $tgt_start = $alignment_infos[$j]["tgt_start"];
		    $tgt_end = $alignment_infos[$j]["tgt_end"];
		    $src_display[] = join(" ",array_slice($source_tokens,$src_start,$src_end-$src_start+1));
		    $tgt_display[] = join(" ",array_slice($target_tokens,$tgt_start,$tgt_end-$tgt_start+1));
		} 
	    }
            print "<table><tr>\n";
            foreach ($tgt_display as $tgt_token) {
                print("<td align=center style=\"background-color:LightGray;padding:5px\">$tgt_token</td>");
            }
            ?></tr><tr> <?php
            foreach ($src_display as $src_token) {
                print("<td align=center style=\"background-color:Lavender;padding:5px\">$src_token</td>");
            }
            print "</table>\n";
        }
    }

    if ($rdebug) {
        print "<h4>Moses debug</h4>";
        print "<pre>";
        foreach (array_keys($translation_result) as $i) {
            $debug_array = $translation_result[$i]["debug"];
            foreach (array_keys($debug_array) as $var) {
                print $debug_array[$var] . "\n";
            }
	    $source_tokens = explode(" ", $debug_array[0]);
	    $moses_input_line = $moses_input_line .  join(" ", $source_tokens) . "\n";
        }
        print "</pre>";
    }

    $fh = fopen("log/translations.$dev$sysid","a");
    $time = time();
    $time = date("D M j G:i:s T Y",$time)." ($time)";
    fwrite($fh,"=== REQUEST AT $time\n");
    fwrite($fh,"=== RAW INPUT:\n$input\n");
    print "<h2>Help to improve statistical machine translation!</h2>\n";
    print "<FORM ACTION=\"index.php\" METHOD=POST>\n";
    print "<INPUT TYPE=HIDDEN NAME=sysid VALUE=\"$sysid\">\n";
    print "<INPUT TYPE=HIDDEN NAME=TIME VALUE=$time>\n";

    print $moses_input_line . "<br>\n";
    print "<INPUT TYPE=HIDDEN NAME=IN VALUE=\"".htmlspecialchars($moses_input_line)."\">\n";
    print "<INPUT TYPE=HIDDEN NAME=MT VALUE=\"".htmlspecialchars($translation_array_string)."\">\n";
    print "<TEXTAREA COLS=80 ROWS=3 NAME=OUT>$translation_array_string</TEXTAREA><P>";
                fwrite($fh,"=== IN \n$moses_input_line");
                fwrite($fh,"=== OUT \n$translation_array_string"); 
    fclose($fh);


    print "<INPUT TYPE=SUBMIT NAME=CORRECTION VALUE=\"Submit correction\">\n";
    print "</FORM>\n";
}

print "<hr>This site is maintained by the <A HREF=\"http://www.statmt.org/ued/\">Machine Translation Group</A> at the University of Edinburgh.<br>&nbsp;";
print "</body></html>\n";

