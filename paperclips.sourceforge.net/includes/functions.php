<?
//////////////////////////////////////////////////////// description ////////////////////////////////////////////////////////
	/**
		@file
		
		@brief This is a php version of TiddlyWiki, which provide server side functions allowing the wiki to be editted over HTTP.
		
		@author: CoolCold
		@date: 4-2-2006
		@email: cctiddly.coolcold@dfgh.net
		@version: 0.4
		@bug private tags are too sensitive.  if "a" is private tag, all words with a would be considered as private
	*/
	
	/**
		features:
			-standalone: make standalone version of tiddly
			-access control: only users with correct secret code can change content
			-privacy control: can hide certain tags from display to unauthorized user
			-versioning: see all versions of a particular tiddly
			
		acknowledgement:
			This is derived from phpTiddlyWiki written by Patrick Curry [http://www.patrickcurry.com/tiddly/]
			
		license:
			This is licensed under GPL v2
			http://www.gnu.org/licenses/gpl.txt

	*/
	

	include_once("includes/config.php");
//////////////////////////////////////////////////////// parameter check ////////////////////////////////////////////////////////
	//?standalone=1, used for making the script standalone form like a regular tiddlywiki
	$standalone = (isset($_GET['standalone'])?$_GET['standalone']:0);		//if 1, will make it into standalone form
	//?title="tiddly title", get all version of that tiddly [security check performed in printAllVersionTiddly function]
	//?config="configfile", force the use of config file "configfile.php" [security performed in including config file]
	//?time=<number>, override the presetted cookie expiry time for PASSWORD ONLY, UNIT: minutes
	if( isset($_GET['time']) )
	{
		$tiddlyCfg['pref']['cookies'] = ((int)$_GET['time'])*$tiddlyCfg['time_unit'];
	}
	
	
/////////////////////////////////////////////////////// check db accessability, forward to install if required////////////////////////////////////////////////////////
	//check existance of db if not install script
	if( strcmp(substr($_SERVER['PHP_SELF'],-11),"install.php") != 0 )
	{
		if( connectDB()===FALSE )		//test connection to db and check if it can use db
		{
			header("Location: install.php?config=".$config);
			//exit("could not select database");
		}
		
		//check if the required table exist, go to install script it does not exist
		if( mysql_query("DESCRIBE ".$tiddlyCfg['table']['name'])===FALSE )
		{
			header("Location: install.php?config=".$config);
		}
	}
//////////////////////////////////////////////////////// FUNCTIONS ////////////////////////////////////////////////////////

	//!	@fn bool validateUser($un)
	//!	@brief check username and password
	//!	@param $un username
	function validateUser($un)
	{
		global $tiddlyCfg;
		$pw=isset($_COOKIE['pasSecretCode'])?$_COOKIE['pasSecretCode']:"";		//get cookie pasSecretCode
		
		if( sizeof($tiddlyCfg['user'])==0 )		//always return true if login not required (empty user array)
		{
			return TRUE;
		}

		if( isset($tiddlyCfg['user'][$un]) && strcmp(md5($tiddlyCfg['user'][$un]),$pw)==0 )
		{
			return TRUE;
		}
		return FALSE;
	}

	//!	@fn SQL connection connectDB()
	//!	@brief make connection to db
	function connectDB()
	{
		global $tiddlyCfg;
	
		// open DB connection
	
		$link = mysql_connect($tiddlyCfg['db']['host'], $tiddlyCfg['db']['login'], $tiddlyCfg['db']['pass'])
			or die("Could not connect to database server");
		//				or die("User ".$tiddlyCfg['db']['login']." could not connect to database server: ".$tiddlyCfg['db']['host']);
		if( mysql_select_db($tiddlyCfg['db']['name'],$link) )
		{
			return $link;
		}
		return FALSE;
	}

	//!	@fn bool printAllTiddly()
	//!	@brief print all tiddly
	function printAllTiddly()
	{
		global $tiddlyCfg;
	
		connectDB();
		
		//validate user and display accordingly
		$query = "select * from ".$tiddlyCfg['table']['name'];
		if( !validateUser((isset($_COOKIE['txtUserName'])?$_COOKIE['txtUserName']:"")/*,(isset($_COOKIE['pasSecretCode'])?$_COOKIE['pasSecretCode']:"")*/) && sizeof($tiddlyCfg['pref']['private_tags'])>0 )
		{
			//reject private tags
			/*$query .= " WHERE NOT (";
			foreach($tiddlyCfg['pref']['private_tags'] as $p)
			{
				//$query .= " OR locate(' $p ',tags)";
				$query .= " OR locate('$p ',tags)";
			}
			$query .= " )";
			$query = str_replace("( OR","(",$query);*/
			$query .= " WHERE (tags REGEXP '[[:<:]](".implode("|",$tiddlyCfg['pref']['private_tags']).")[[:>:]]')=0";
		}
		
		debug( $query.'???');
		$result = mysql_query($query) or die("Query failed: $query \n error: ".mysql_error());
		
		//display tiddler
		while ($row = mysql_fetch_assoc($result))
		{
?>
<div tiddler="<?= $row["title"] ?>" modified="<?= $row["modified"] ?>" modifier="<?= $row["modifier"] ?>" created="<?= $row["created"] ?>" version="<?= $row["version"] ?>" tags="<?= $row["tags"] ?>"><?= $row["body"] ?></div>
<?
		}
		return TRUE;
	}

	//!	@fn bool printAllVersionTiddly($title)
	//!	@brief print all version of a particular tiddler
	//!	@param $title title of required tiddler
	function printAllVersionTiddly($title)
	{
		//init
		global $tiddlyCfg;
		connectDB();
		//display = TRUE if username and password correct or no private tags exist
		$display = ( validateUser((isset($_COOKIE['txtUserName'])?$_COOKIE['txtUserName']:"")/*,(isset($_COOKIE['pasSecretCode'])?$_COOKIE['pasSecretCode']:"")*/) || sizeof($tiddlyCfg['pref']['private_tags'])==0 );
		
		//formatting
		$title = format4SQL($title);
		
		//used to find rejected tags
		$rejectProtect = "";
		if( !$display )
		{
			//reject private tags
			$rejectProtect = " AND (tags REGEXP '[[:<:]](".implode("|",$tiddlyCfg['pref']['private_tags']).")[[:>:]]')=0";
		}
		
		//get systemTiddlers, stlesheet and the required title tag
		//would not display current one if $display=FALSE and tiddler protected
		$query = "SELECT * FROM ".$tiddlyCfg['table']['name']." WHERE ( title='$title' OR title='MainMenu' )$rejectProtect";
		$result = mysql_query($query) or die("Query failed: $query \n error: ".mysql_error());
		$id=0;
		
		//display tiddlys
		while ($row = mysql_fetch_assoc($result))
		{
?>
<div tiddler="<?= $row["title"] ?>" modified="<?= $row["modified"] ?>" modifier="<?= $row["modifier"] ?>" created="<?= $row["created"] ?>" version="<?= $row["version"] ?>" tags="<?= $row["tags"] ?>"><?= $row["body"] ?></div>
<?
			if( $title == $row["title"] )
			{
				$id=$row["id"];
			}
		}

		//if title is currently protected, do not display other versions
		if( $id==0 )
		{
			return TRUE;
		}
		
		//get all version from db using id, rejecting ones that are protected
		$query = "SELECT * FROM ".$tiddlyCfg['table']['backup']." WHERE oid='$id' $rejectProtect ORDER BY version";
		$result = mysql_query($query) or die("Query failed: $query \n error: ".mysql_error());
	
		//display versions of tiddler
		while ($row = mysql_fetch_assoc($result))
		{
?>
<div tiddler="<?= $row["title"].$row["version"] ?>" modified="<?= $row["modified"] ?>" modifier="<?= $row["modifier"] ?>" created="<?= $row["created"] ?>" version="<?= $row["version"] ?>" tags="<?= $row["tags"] ?>"><?= $row["body"] ?></div>
<?
		}
		return TRUE;
	}

	//!	@fn bool isLockTiddly($title)
	//!	@brief check if tiddler is locked
	//!	@param $title title of required tiddler
	function isLockTiddly($title)
	{
		global $tiddlyCfg;
		
		$t = array_flip($tiddlyCfg['pref']['lock_titles']);
		return isset($t[$title]);
	}
	
	//!	@fn bool saveTiddly($otitle, $title, $body, $modifier="YourName", $tags="")
	//!	@brief save tiddler
	//!	@param $otitle current title
	//!	@param $title new title
	//!	@param $body new body
	//!	@param $modifier person adding/changing the tiddler
	//!	@param $tags tags
	function saveTiddly($otitle, $title, $body, $modifier="YourName", $tags="")
	{
		global $tiddlyCfg;
		$modified = date("YmdHi");
		
		connectDB();

		//format string
		//$body = str_replace('\\',"\\s",$body);		//replace'\' with '\s'
		if( get_magic_quotes_gpc() )		//if magic_quote enabled, storing string with \\ would result in \ being stored
		{
			$body = str_replace('\\\\',"\\\\s",$body);		//replace'\' with '\s'
			$body = str_replace("\n","\\\\n",$body);		//replace newline with '\n'
		}
		else		//if magic_quote disabled, storing \n (2 char) would result in \n being stored
		{
			$body = str_replace('\\',"\\s",$body);		//replace'\' with '\s'
			$body = str_replace("\n","\\n",$body);		//replace newline with '\n'
		}
		$body = str_replace("\r","",$body);		//return character is not required
		$body = htmlspecialchars($body);		//replace <, >, &, " with their html code
		$otitle = format4SQL($otitle);
		$title = format4SQL($title);
		$body = format4SQL($body);
		$modifier = format4SQL($modifier);
		$tags = format4SQL($tags);
		$tidVersion = 0;
		
		//get id and version of old and new title
		$query = "SELECT id, version FROM ".$tiddlyCfg['table']['name']." WHERE title = '$otitle'";
		$oresult = mysql_query($query);
		if( strcmp($otitle,$title)!=0 )
		{
			$query = "SELECT id, version FROM ".$tiddlyCfg['table']['name']." WHERE title = '$title'";
			$nresult = mysql_query($query);
		}else{
			$nresult = $oresult;
		}
		if( $oresult === FALSE || $nresult === FALSE )
		{
			//debug("Query failed: $query \n error: ".mysql_error());
			return FALSE;
		}

		//insert into db if no result found (both old and new title)
		if( mysql_num_rows($oresult) == 0 && mysql_num_rows($nresult) == 0 )
		{
			$query = "INSERT INTO ".$tiddlyCfg['table']['name']." (title, body, modified, modifier, created, creator, tags) VALUES ('$title', '$body', '$modified', '$modifier', '$modified', '$modifier', '" . $tags . "');";
			if( ($result = mysql_query($query))===FALSE ) {
				return FALSE;
			}
			$id = mysql_insert_id();
		}
		else//update found title
		{
			//fetch id and version
			while ($row = mysql_fetch_assoc($nresult))
			{
				$nid = $row["id"];
				$ntidVersion = $row["version"];
			}
			while ($row = mysql_fetch_assoc($oresult))
			{
				$id = $row["id"];
				$tidVersion = $row["version"];
			}

			//oid not exist, nid exist = new tiddler to overwrite other tiddler (use nid)
			//oid exist, nid not exist = rename tiddler (as usual)
			//oid and nid exist and different = one tiddler overwrite another (use oid, delete nid)
			//oid and nid exist and same = renaming (as usual)
			if( !isset($id) )
			{
				$id=$nid;
				$tidVersion=$ntidVersion;
			}
			$tidVersion = $tidVersion + 1;
			
			//remove ntitle entry first since title can't be duplicate
			if( isset($nid) && $id != $nid && $tiddlyCfg['pref']['delete'])
			{
				$query = "DELETE from ".$tiddlyCfg['table']['name']." WHERE id='$nid'";
				$result = mysql_query($query);
				if( $result===FALSE )
				{
					//debug("Query failed: $query \n error: ".mysql_error());
					return FALSE;
				}
			}
			
			// update the main entry...
			$query = "UPDATE ".$tiddlyCfg['table']['name']." SET title='$title', body='$body', modified='$modified', modifier='$modifier', version=$tidVersion, tags='$tags' WHERE id = $id";
			if( ($result = mysql_query($query))===FALSE ) {
				return FALSE;
			}
		}
	
		// put into the versioning table too
		if( $tiddlyCfg['pref']['version'] == 1 )
		{
			$query = "INSERT INTO " . $tiddlyCfg['table']['backup'] . " (title, body, modified, modifier, version, tags,oid) VALUES ('$title', '$body', '$modified', '$modifier', ".((int)$tidVersion).",'$tags','$id')";
			if( ($result = mysql_query($query))===FALSE ) {
				return FALSE;
			}
		}
		
		return TRUE;
	}

	//!	@fn bool tiddly_changed($title, $body, $theTags="")
	//!	@brief check if changes were made to tiddler
	//!	@param $title new title
	//!	@param $body body
	//!	@param $theTags tags
	function tiddly_changed($title, $body, $theTags="")
	{
		global $tiddlyCfg;
		
		//format string
		if( get_magic_quotes_gpc() )		//if magic_quote enabled, storing string with \\ would result in \ being stored
		{
			$body = str_replace('\\\\',"\\\\s",$body);		//replace'\' with '\s'
			$body = str_replace("\n","\\\\n",$body);		//replace newline with '\n'
		}
		else		//if magic_quote disabled, storing \n (2 char) would result in \n being stored
		{
			$body = str_replace('\\',"\\s",$body);		//replace'\' with '\s'
			$body = str_replace("\n","\\n",$body);		//replace newline with '\n'
		}
		$body = str_replace("\r","",$body);		//replace newline with '\n'
		$body = htmlspecialchars($body);		//replace <, >, &, " with their html code
		$title = format4SQL($title);
		$body = format4SQL($body);
		$theTags = format4SQL($theTags);
		
		connectDB();
	
		$query = "SELECT id FROM ".$tiddlyCfg['table']['name']." WHERE title = '" . $title . "' AND body = '" . $body . "' AND tags = '" . $theTags . "'";
		$result = mysql_query($query) or die("Query failed: $query \n error: ".mysql_error());
	
		if (mysql_num_rows($result) == 0)
		{
			return TRUE;
		}
		return FALSE;
	}

	//!	@fn bool deleteTiddly($title)
	//!	@brief delete tiddler, does not delete in backup table
	//!	@param $title title to be deleted
	function deleteTiddly($title)
	{
		global $tiddlyCfg;
		
		connectDB();
	
		$query = "DELETE from ".$tiddlyCfg['table']['name']." WHERE title='$title'";
		$result = mysql_query($query) or die("Query failed: $query \n error: ".mysql_error());
		return TRUE;
	}
	
	//!	@fn string format4SQL($str)
	//!	@brief format string, adding "\", for SQL
	//!	@param $str string to be formatted
	function format4SQL($str)
	{
		if( get_magic_quotes_gpc() )
			return $str;
		else{
			return addslashes($str);
		}
	}
//////////////////////////////////////////////////////// error related////////////////////////////////////////////////////////
	//!	@fn tiddly_alert($msg)
	//!	@brief output alert to page to create a popup error msg
	//!	@param $msg error message
	function tiddly_alert($msg)
	{
?>
<script language="JavaScript"><!--
alert("<? print $msg ?>");
//--></script>
<?
	}
	
	//!	@fn display_error($msg, $stop=0)
	//!	@brief output alert to page to create a popup error msg AND print out error msg on page
	//!	@param $msg error message
	//!	@param $stop stop script running if equals to 1
	function display_error($msg, $stop=0)
	{
		tiddly_alert($msg);
		print $msg;
		if( $stop==1 )
		{
			exit();
		}
		return TRUE;
	}
//////////////////////////////////////////////////////// debug only////////////////////////////////////////////////////////

	//!	@fn bool debugV($str)
	//!	@brief debug function, replace var_dump so any var_dump left in the code won't be notice
	//!	@param $str string to be var_dumped
	function debugV($str)
	{
		global $tiddlyCfg;
		if( $tiddlyCfg['developing']>0 )
		{
			var_dump($str);
		}
		return TRUE;
	}
	
	//!	@fn bool debug($str)
	//!	@brief debug function, similar to debugV but use print instead
	//!	@param $str string to be printed
	function debug($str)
	{
		global $tiddlyCfg;
		if( $tiddlyCfg['developing']>0 )
		{
			print $str;
		}
		return TRUE;
	}

?>
