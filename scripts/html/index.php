<?php
header('Content-type: text/html; charset=utf-8');


$systems = array("fr-en"=>"French-English (Europarl)"
                ,"de-en"=>"German-English (Europarl)"
                ,"es-en"=>"Spanish-English (Europarl)"
                ,"en-de"=>"English-German (Europarl)"
	 	);

$input = $_POST['input'];
$sysid = $_POST['sysid'];
$rdebug =  array_key_exists('debug',$_POST);
$alignment =  array_key_exists('alignment',$_POST);
$port = "__PORT__";

if (!$input) $input="";
$input = stripslashes($input);

function head() {
  global $rdebug;
?><html><head>
<title>Moses Online MT Demo</title>
<style type="text/css">

body, table, tr, td<?php if (!$rdebug) print(", textarea");?>
{
font-family:verdana, helvetica, arial, sans-serif;
font-size:12px;
color:navy;
}
rdebu
textarea
{
font-size:12px;
<?php if ($rdebug) print("font-family:courier;font-size:12px;");?>
<?php if (!$rdebug) print("font-family:verdana, helvetica, arial, sans-serif;font-size:14px;");?>

}

td
{
font-size:13px;
}

body 
{
background-image:url(WebLogo.jpg);
background-repeat:no-repeat;
background-position:top right;
}

.translation
{
font-family:verdana, helvetica, arial, sans-serif;
font-size:14px;
color:navy;
}

table, tr, td
{
background-color:white;
}

.tableHeading
{
background-color:navy;
font-color:yellow;
text-align:center;
font-weight:bold;
}

.cost
{
text-align:center;
font-weight:bold;
}

</style>

</head>
<body>
<h1>Moses Machine Translation Demo</h1>
<?php
}

function log_correction() {
  global $sysid;
  $fh = fopen("/disk4/html/demo/log/corrections.$sysid","a");
  fwrite($fh,"=== REQUEST AT ".$_POST["time"]."\n");
  
  foreach (array_keys($_POST) as $var) {
    if (preg_match("/^IN-(.+)/",$var,$match)) {
      fwrite($fh,"=== IN $match[1]\n" .$_POST["IN-" .$match[1]]."\n");
      fwrite($fh,"=== MT $match[1]\n" .$_POST["MT-" .$match[1]]."\n");
      fwrite($fh,"=== OUT $match[1]\n".$_POST["OUT-".$match[1]]."\n");
      $i++;
    }
  }
  fclose($fh);
  print "<h2>Thank you for contributing $i sentence".(($i==1)?"":"s")."!</h2>";
}

head();
if ($_POST['CORRECTION']) { log_correction(); };

?>
<form action="index.php" method="POST">
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
<a href="web">Web Translation</a></br>
<?php 
if ($input) {
  print "<h2>Translation:</h2>\n";
}
include_once("mt_functions.php");


$location = array("ip"=>"127.0.0.1","port"=>$port);

$translation_array_string = "";

if ($input) $translation_array_string = translate($input."\n",$sysid,$location);

$translation_array = split(" %%% ",$translation_array_string);
$last_step = $translation_array[sizeof($translation_array)-5];
for($i=1;$i<sizeof($translation_array);$i+=5) {
  if ($translation_array[$i] == $last_step) {
    $translation .= $translation_array[$i+2]." ";
  }
}
print "<span class=\"translation\">$translation</span><P>\n";

# output phrase alignment
if ($alignment) {
	#find mapping
	$moses_input = "";
	for($i=1;$i<sizeof($translation_array);$i++) {
		$tool = $translation_array[$i++];
		$success = $translation_array[$i++];
		$out = $translation_array[$i++];
		$err = $translation_array[$i++];
		if ($tool == "moses") {
                   $moses_input_list = split("\n",$moses_input);
		   $err_list = split("\n",$err);
                   $line_number = 0;
                   foreach ($err_list as $parse_str) {
                        if (! preg_match("/^\[\[/",$parse_str)) continue;
			$tgt_display = array();
			$src_display = array();		
			$matchups = split("[][]",$parse_str);
			$range="";
			$target="";
                        $moses_input_line = $moses_input_list[$line_number++];
			$source_tokens = explode(" ",$moses_input_line);
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
			?><table><tr>
			<?php 
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
		$moses_input = $out;
	}
}

if ($rdebug) {
	for($i=1;$i<sizeof($translation_array);$i++) {
		
		$tool = $translation_array[$i++];
		$success = $translation_array[$i++];
		$msg = "";
		if ($success == 0) $out = "<font color='red'>FAILURE</font>";
		print("<h2>$tool $msg</h2>");
		$out = $translation_array[$i++];
		$err = $translation_array[$i++];
		if ($out != "") { print("<h4>STDOUT</h4>\n<PRE>$out</PRE>\n"); }
		if ($err != "") { print("<h4>STDERR</h4>\n<PRE>$err</PRE>\n"); }
	}
}

# log
if ($input) {
 $fh = fopen("/disk4/html/demo/log/translations.$sysid","a");
 $time = time();
 $time = date("D M j G:i:s T Y",$time)." ($time)";
 fwrite($fh,"=== REQUEST AT $time\n");
 fwrite($fh,"=== RAW INPUT:\n$input\n");
 $moses_input = "";
 print "<h2>Help to improve statistical machine translation!</h2>\n";
 print "<FORM ACTION=\"/\" METHOD=POST>\n";
 print "<INPUT TYPE=HIDDEN NAME=sysid VALUE=\"$sysid\">\n";
 print "<INPUT TYPE=HIDDEN NAME=TIME VALUE=$time>\n";
 for($i=1;$i<sizeof($translation_array);$i++) {
  $tool = $translation_array[$i++];
  $success = $translation_array[$i++];
  $out = $translation_array[$i++];
  $err = $translation_array[$i++];
  if ($tool == "splitter") {
    $moses_input = $out;
  }
  else if (preg_match("/detokenizer/",$tool)) {
    $in_list = split("\n",$moses_input);
    $out_list = split("\n",$out);
    $line_number = 0;
    foreach ($out_list as $out_line) {
        $in_line = $in_list[$line_number];
        if ($in_line != "") {
            print "$in_list[$line_number]<BR>\n";
            print "<INPUT TYPE=HIDDEN NAME=IN-$i-$line_number VALUE=\"".htmlspecialchars($in_line)."\">\n";
            print "<INPUT TYPE=HIDDEN NAME=MT-$i-$line_number VALUE=\"".htmlspecialchars($out_line)."\">\n";
             print "<TEXTAREA COLS=80 ROWS=3 NAME=OUT-$i-$line_number>$out_line</TEXTAREA><P>";
            fwrite($fh,"=== IN [$i-$line_number]\n$in_line\n");
            fwrite($fh,"=== OUT [$i-$line_number]\n$out_line\n"); 
        }
	$line_number++;
    }
  }
 }
 fclose($fh);
 print "<INPUT TYPE=SUBMIT NAME=CORRECTION VALUE=\"Submit correction\">\n";
 print "</FORM>\n";
}
print "<hr>This site is maintained by the <A HREF=\"http://www.statmt.org/ued/\">Machine Translation Group</A> at the University of Edinburgh.<br>&nbsp;";
print "</body></html>\n";

