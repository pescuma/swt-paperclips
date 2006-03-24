<?

include("tiddly_conf.php");

/***************** FUNCTIONS ******************************/

function tiddly_validate_user($un, $pw)
{
	for ($i = 0; $i < count($tiddly_users); $i++)
	{
		if ($tiddly_users[$i][0] == $un && $tiddly_users[$i][1] == $pw)
		{
			return true;
		}
	}
	return false;
}

function tiddly_db_connect()
{
        global $tiddly_db_host;
        global $tiddly_db_login;
        global $tiddly_db_pass;
        global $tiddly_db_name;
	global $tiddly_db_table_name;

        // open DB connection

        $link = mysql_pconnect($tiddly_db_host, $tiddly_db_login, $tiddly_db_pass)
                or die("User $tiddly_db_login could not connect to database server: $tiddly_db_host");
        mysql_select_db($tiddly_db_name) or die("Could not select database: $tiddly_db_name");

}

function tiddly_print_all_entries()
{
        global $tiddly_db_table_name;

	tiddly_db_connect();

	$query = "select * from $tiddly_db_table_name";
        $result = mysql_query($query) or die("Query failed: $query");

        while ($row = mysql_fetch_assoc($result))
        {
?>
<DIV id="store<?= $row["title"] ?>" modified="<?= $row["modified"] ?>" modifier="<?= $row["modifier"] ?>" version="<?= $row["version"] ?>" tags="<?= $row["tags"] ?>"><?= $row["body"] ?></DIV>
<?
        }
}

function tiddly_print_all_versions($title)
{
        global $tiddly_db_table_name;
        global $tiddly_db_backup_table_name;

	tiddly_db_connect();

	// print the mandatory tiddlers
	$query = "select * from $tiddly_db_table_name WHERE (title='SiteTitle' OR title='SiteSubtitle' OR title='MainMenu')";
        $result = mysql_query($query) or die("Query failed: $query");

        while ($row = mysql_fetch_assoc($result))
        {
?>
<DIV id="store<?= $row["title"] ?>" modified="<?= $row["modified"] ?>" modifier="<?= $row["modifier"] ?>" version="<?= $row["version"] ?>" tags="<?= $row["tags"] ?>"><?= $row["body"] ?></DIV>
<?
        }


	$query = "SELECT * FROM $tiddly_db_backup_table_name WHERE title='$title' ORDER BY version";
        $result = mysql_query($query) or die("Query failed: $query");

        while ($row = mysql_fetch_assoc($result))
        {
			$modVersion = $row["version"];
			if ($modVersion < 10)
			{
				$modVersion = "0" . $modVersion;
			}
?>
<DIV id="storeVersionNum<?= $modVersion ?>" modified="<?= $row["modified"] ?>" modifier="<?= $row["modifier"] ?>" version="<?= $row["version"] ?>" tags="<?= $row["tags"] ?>"><?= $row["body"] ?></DIV>
<?
        }
}

function tiddly_is_locked($title)
{
	global $locked_titles;
	
	for ($i = 0; $i < count($locked_titles); $i++)
	{
		if ($title == $locked_titles[$i])
			return true;
	}
	return false;
}

function tiddly_save_entry($title, $body, $modified=0, $modifier="unknown", $tags="")
{
	global $tiddly_pref_vers;
	global $tiddly_db_table_name;
	global $tiddly_db_backup_table_name;

   tiddly_db_connect();

	$query = "SELECT id, version FROM $tiddly_db_table_name WHERE title = '" . $title . "'";
	$result = mysql_query($query) or die("Query failed: $query");
	$tidVersion = 0;

	if ($modified == 0)
	{
		$modified = date("YmdHi");
	}

	if (mysql_num_rows($result) == 0)
	{
			// No rows found... do the insert
			$query = "INSERT INTO $tiddly_db_table_name (title, body, modified, modifier, created, creator, tags) VALUES ('$title', '$body', '$modified', '$modifier', '" . date("YmdHi") . "', '$modifier', '" . $tags . "');";
			$result = mysql_query($query) or die("Query failed: $query");
			$id = mysql_insert_id();
	}
	else
	{
			while ($row = mysql_fetch_assoc($result))
			{
					$id = $row["id"];
					$tidVersion = $row["version"];
			}
			$tidVersion = $tidVersion + 1;

			//print("hello 2?");

			// update the main entry...
			$query = "UPDATE " . $tiddly_db_table_name . " SET title='$title', body='$body', modified='$modified', modifier='$modifier', version=" . $tidVersion . ", tags='" . $tags . "' WHERE id = $id";
			$result = mysql_query($query) or die("Query failed: $query");
	}

	//print("hello 1?");

	// put into the versioning table too
	if ($tiddly_pref_vers == 1)
	{
		// testing
		//print("hello?");
	
		$query = "INSERT INTO " . $tiddly_db_backup_table_name . " (title, body, modified, modifier, version, tags) VALUES ('$title', '$body', '$modified', '$modifier', " . $tidVersion . ", '" . $tags . "')";
		$result = mysql_query($query) or die("Query failed: $query");
	}
}

// returns true if there's a difference...
function tiddly_diff_entry($title, $body, $theTags="")
{
	global $tiddly_db_table_name;

	tiddly_db_connect();

	$query = "SELECT id FROM $tiddly_db_table_name WHERE title = '" . $title . "' AND body = '" . $body . "' AND tags = '" . $theTags . "'";
	$result = mysql_query($query) or die("Query failed: $query");

	if (mysql_num_rows($result) == 0)
	{
		return true;
	}

	return false;

}

function tiddly_delete_entry($title)
{
	global $tiddly_db_table_name;
	
	tiddly_db_connect();

	$query = "DELETE from $tiddly_db_table_name WHERE title='$title'";
	$result = mysql_query($query) or die("Query failed: $query");
}

/***************** HANDLE MESSAGES AND RENDER PAGES ******************************/

if ($msg == "save")
{

	if ($title != "" && $body != "")
	{
		if (!tiddly_is_locked($title))
		{
			if (tiddly_diff_entry($title, $body, $theTags))
			{	
				tiddly_save_entry($title, $body, $modified, $modifier, $theTags);
			}
		}
		else
		{
			?>
			<html><body><script language="JavaScript"><!--
			alert("This entry is locked.");
			//--></script></body></html>");
			<?		
		}
	}
	else
	{
		?>
		<html><body><script language="JavaScript"><!--
		alert("You cannot create an entry with blank fields.");
		//--></script></body></html>");
		<?
	}

	setcookie ("tiddly_wiki_modifier", $modifier);
}
else if ($msg == "delete")
{
	if ($tiddly_pref_delete)
	{
		tiddly_delete_entry($title);
	}
	else
	{
		?>
		<html><body><script language="JavaScript"><!--
		alert("Deleting has been disabled for now.  But it works, I promise.");
		//--></script></body></html>");
		<?
	}

	setcookie ("tiddly_wiki_modifier", $modifier);

}
else if ($msg == "versions")
{
	if ($title != "")
	{
		include("tiddly1.php");
	
		tiddly_print_all_versions($title);
	
		include("tiddly2.php");
	}
	else
	{
		?>
		<html><body>No title specified.</body></html>");
		<?
	}

}
else
{
	include("tiddly1.php");

	tiddly_print_all_entries();

	include("tiddly2.php");
}

?>
