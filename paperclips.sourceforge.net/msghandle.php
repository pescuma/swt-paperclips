<?
	include_once("includes/functions.php");

//////////////////////////////////////////////////////////initial checking and required functions////////////////////////////////////////
	if( !isset($_POST['msg']) )
	{
		exit();
	}

//////////////////////////////////////////////////////////save//////////////////////////////////////////////////////////////
	if( strcmp($_POST['msg'],"save")==0 )
	{
		//validate user
		if( validateUser($_POST['modifier']/*,$_POST['secretCode']/*$_COOKIE['tiddly_wiki_secretcode']*/) === FALSE )
		{
			tiddly_alert("You are not allowed to change the content");
			exit();
		}
		//check if empty msg
		if( strlen($_POST['title']) == 0 || strlen($_POST['body']) == 0 )
		{
			tiddly_alert("You cannot create an entry with blank fields.");
			exit();
		}
		
		//check if post is editable (changable in config file)
		if( isLockTiddly($_POST['title']) || isLockTiddly($_POST['oldtitle'])  )
		{
			tiddly_alert("This entry is locked.");
			exit();
		}
		
		//save entry if different
		if( tiddly_changed($_POST['title'], $_POST['body'], $_POST['tags']) )
		{	
			if( !saveTiddly($_POST['oldtitle'],$_POST['title'], $_POST['body'], $_POST['modifier'], $_POST['tags']) )
			{
				tiddly_alert("save error");
			}
			//tiddly_save_entry($_POST['title'], $_POST['body'], $_POST['modifier'], $_POST['theTags']);
		}
//		debug("saved");
		exit();
	}

//////////////////////////////////////////////////////////delete//////////////////////////////////////////////////////////////
	if( strcmp($_POST['msg'],"delete")==0 )
	{
		//validate user
		if( validateUser($_POST['modifier']/*,$_POST['secretCode']/*$_COOKIE['tiddly_wiki_secretcode']*/) === FALSE )
		{
			tiddly_alert("You are not allowed to change the content");
			exit();
		}
		
		//remove from db if allowed
		//although removing content is effectively the same as removing the tiddly, disable delete would still allow owner to be notified and find the tiddly back in backup if required, through id
		if ($tiddlyCfg['pref']['delete'])
		{
			deleteTiddly($_POST['title']);
		}
		else
		{
			tiddly_alert("Deleting has been disabled.");
		}
	}
	exit();
?>
