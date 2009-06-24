<?php
header('Content-type: text/html; charset=utf-8');

include_once('xmlrpc.inc');

$input = $_POST['input'];
$sysid = $_POST['sysid'];
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
    if (preg_match("/^IN-(.+)/",$var,$match)) {
      fwrite($fh,"=== IN $match[1]\n" .$_POST["IN-" .$match[1]]."\n");
      fwrite($fh,"=== MT $match[1]\n" .$_POST["MT-" .$match[1]]."\n");
      fwrite($fh,"=== OUT $match[1]\n".$_POST["OUT-".$match[1]]."\n");
    }
  }
  fclose($fh);
  print "<h2>Thank you for contributing!</h2>";
}

head();
if ($_POST['CORRECTION']) { log_correction(); };

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

if ($input) $translation_result = translate($input,$sysid,$port,true);
$translation = $translation_result["translation"];

$translation_array = $translation_result["debug"];

print "<span class=\"translation\">$translation</span><P>\n";

$source_tokens = array_slice(explode(" ", $translation_array[1]),1);
$moses_input_line = join(" ", $source_tokens);

# output phrase alignment
if ($alignment) {
	#find mapping
	for($i=1;$i<sizeof($translation_array);$i++) {
        $parse_str = $translation_array[$i];
        if (! preg_match("/^\[\[/",$parse_str)) continue;
        $tgt_display = array();
        $src_display = array();		
        $matchups = split("[][]",$parse_str);
        $range="";
        $target="";
        foreach ($matchups as $item) {
            $item = trim($item);
            if (!$item) continue;
            if (!$range) $range = $item;
            else if (!$target) $target = $item;
            if ($target && $range) {
                list($src_start,$src_end) = explode("..",$range);
                $target = substr($target,1);
                $src_display[] = join(" ",array_slice($source_tokens,$src_start,$src_end-$src_start+1));
                $tgt_display[] = $target;
                $target = "";
                $range = "";				
            }
        
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



if ($rdebug) {
    print "<h4>Moses debug</h4>";
    print "<pre>";
    foreach (array_keys($translation_array) as $var) {
        print $translation_array[$var] . "\n";
    }
    print "</pre>";
}

# log
if ($input) {
 $fh = fopen("/disk4/html/demo/log/translations.$dev$sysid","a");
 $time = time();
 $time = date("D M j G:i:s T Y",$time)." ($time)";
 fwrite($fh,"=== REQUEST AT $time\n");
 fwrite($fh,"=== RAW INPUT:\n$input\n");
 $moses_input = "";
 print "<h2>Help to improve statistical machine translation!</h2>\n";
 print "<FORM ACTION=\"index.php\" METHOD=POST>\n";
 print "<INPUT TYPE=HIDDEN NAME=sysid VALUE=\"$sysid\">\n";
 print "<INPUT TYPE=HIDDEN NAME=TIME VALUE=$time>\n";

 print $moses_input_line . "<br>\n";
 print "<INPUT TYPE=HIDDEN NAME=IN VALUE=\"".htmlspecialchars($moses_input_line)."\">\n";
 print "<INPUT TYPE=HIDDEN NAME=MT VALUE=\"".htmlspecialchars($translation)."\">\n";
 print "<TEXTAREA COLS=80 ROWS=3 NAME=OUT>$translation</TEXTAREA><P>";
            fwrite($fh,"=== IN \n$moses_input_line\n");
            fwrite($fh,"=== OUT \n$translation\n"); 
 fclose($fh);
 print "<INPUT TYPE=SUBMIT NAME=CORRECTION VALUE=\"Submit correction\">\n";
 print "</FORM>\n";
}
print "<hr>This site is maintained by the <A HREF=\"http://www.statmt.org/ued/\">Machine Translation Group</A> at the University of Edinburgh.<br>&nbsp;";
print "</body></html>\n";

