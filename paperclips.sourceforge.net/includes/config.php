<?
/**
	@file
		
	@brief This holds and sort out configuration files and default configurations
*/
	//////////////////////////////////////////////////////// config file ////////////////////////////////////////////////////////
	//check for forced config
	include_once("./config/default.php");
	$config="default";
	if( isset($_GET['config']) && file_exists("./config/".$_GET['config'].".php") )
	{//replace [\,/,?,*] from string
		$config = substr(preg_replace("![/,\\\\,?,*]!","",$_GET['config']),0,255);
	}
	else
	{
		//include config file according to hostname
		//start looking with the shortest form from the end, e.g. for "address.com", it will check "com" before checking "address.com"
		// will use the last config it found
		$host = explode(".",trim($_SERVER['HTTP_HOST']));
		$tmp="";
		for( $i=sizeof($host)-1; $i>-1; $i-- )
		{
			if( $i==sizeof($host)-1 ) {
				$tmp = $host[$i];
			}else{
				$tmp = $host[$i].".$tmp";
			}
			if( file_exists("./config/".$tmp.".php") )
			{
				$config = $tmp;
				//$i=-1;			//<----when enabled, this will use the first config it find
			}
		}
	}
	if( strcmp($config,"default") != 0 )
	{
		include_once("./config/".$config.".php");
	}

	
	//make sure the config files is used
	if( !isset($tiddlyCfg) )
	{
		exit("error in getting config");
	}
////////////////////////////////////////////////////////////////////////set default values////////////////////////////////////////////////////.
$tiddlyCfg['time_unit']=60*1000;		//default time units
$tiddlyCfg['pref']['cookies'] = $tiddlyCfg['pref']['cookies']*$tiddlyCfg['time_unit'];		//convert time to minutes
$tiddlyCfg['table']['name'] = $tiddlyCfg['table']['pref'].$tiddlyCfg['table']['name'];
$tiddlyCfg['table']['backup'] = $tiddlyCfg['table']['pref'].$tiddlyCfg['table']['backup'];
$tiddlyCfg['version']="0.5c";
	
?>