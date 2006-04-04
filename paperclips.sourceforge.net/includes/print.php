<?
//////////////////////////////////////////////////////// description ////////////////////////////////////////////////////////
	/**description
		print functions that printout code into tiddly wiki to change its action.  only required in "index.php"
	*/

	/**
		author: CoolCold
		email: cctiddly.coolcold@dfgh.net
		version: 0.5
	*/
	
	/**
		license:
			This is licensed under GPL v2
			http://www.gnu.org/licenses/gpl.txt
	*/

	if( !isset($tiddlyCfg) )
	{
		exit("config not found");
	}
//////////////////////////////////////////////////////////////general print to page functions/////////////////////////////////////////////////////////////
	//!	@fn cct_print_includes($standalone)
	//!	@brief print all includes javascript, usually external ones
	//!	@param $standalone check if it is a standalone version
	function cct_print_includes($standalone)
	{
		if( $standalone )
		{
			return "";
		}
		return '<script type="text/javascript" src="includes/md5.js"></script>';
	}
	
	//!	@fn cct_print_includes($standalone)
	//!	@brief print all includes javascript, usually external ones
	//!	@param $standalone check if it is a standalone version
	function cct_print_divConfig($standalone)
	{
		if( $standalone )
		{
			return "";
		}
		//return '
//<div tiddler="_ccTiddlyConfig" modifier="ccTiddly" modified="200602260015" created="200602260014" tags="systemConfig">/*{{{*/\n// Enable editing over HTTP\nconfig.options.chkHttpReadOnly = false;\n/*}}}*/</div>
		//';
		return '
<div tiddler="_ccTiddlyConfig" modified="200603111945" modifier="ccTiddly" created="200603111915" version="13" tags="systemConfig">/***\n!!!ccTiddly config/marco\nThis tiddler is specially for ccTiddly. Please do not edit or save otherwise it may not work properly (unless you know what you are doing). If you happen to save it, and it didn\'t work, just delete the tiddler and the original config tiddler would reappear.\n***/\n\n/***\nchanging the default options\n***/\n/*{{{*/\nconfig.options.chkHttpReadOnly = false;    //make it HTTP writable by default\nconfig.options.pasSecretCode = &quot;&quot;;           //password variable\n/*}}}*/\n\n/***\nadd copyright shadow tiddler\n***/\n/*{{{*/\nconfig.shadowTiddlers.Copyright = &quot;powered by [[TiddlyWiki|http://www.tiddlywiki.com/]] ver. &lt;&lt;version&gt;&gt; and [[ccTiddly v0.5|http://cctiddly.sourceforge.net]]\sn[[stand alone|?standalone=1]]&quot;;\n/*}}}*/\n</div>
';
	}
	
	//!	@fn cct_print_saveTiddler($standalone)
	//!	@brief adjust form to save data
	//!	@param $standalone check if it is a standalone version
	function cct_print_saveTiddler($standalone)
	{
		if( $standalone )
		{
			return "";
		}
		//document.forms.invisiForm.secretCode.value = config.options.pasSecretCode;
		return '
this.setDirty(false);
document.forms.invisiForm.msg.value = "save";
document.forms.invisiForm.oldtitle.value = title;
document.forms.invisiForm.title.value = newTitle;
document.forms.invisiForm.body.value = newBody;
document.forms.invisiForm.modifier.value = modifier;
document.forms.invisiForm.tags.value = tags;
document.forms.invisiForm.submit();';
	}
	
	//!	@fn cct_print_deleteTiddler($standalone)
	//!	@brief adjust form to delete data
	//!	@param $standalone check if it is a standalone version
	function cct_print_deleteTiddler($standalone)
	{
		if( $standalone )
		{
			return "";
		}
		//document.forms.invisiForm.secretCode.value = config.options.pasSecretCode;
		return '
this.setDirty(false);
document.forms.invisiForm.msg.value = "delete";
document.forms.invisiForm.title.value = title;
document.forms.invisiForm.modifier.value = config.options.txtUserName;
document.forms.invisiForm.submit();';
	}
	
	/////////////////////////////////////////////////////////////////other/////////////////////////////////////////////
	//!	@fn cct_print_commentS($standalone)
	//!	@brief comment out javascript code, start of comment
	//!	@param $standalone check if it is a standalone version
	function cct_print_commentS($standalone)
	{
		if( $standalone )
		{
			return "";
		}
		return "/*";
	}
	
	//!	@fn cct_print_commentE($standalone)
	//!	@brief comment out javascript code, end of comment
	//!	@param $standalone check if it is a standalone version
	function cct_print_commentE($standalone)
	{
		if( $standalone )
		{
			return "";
		}
		return "*/";
	}
	/////////////////////////////////////////////////////////////////shadowTiddlers/////////////////////////////////////////////
	function cct_print_pageTemplate($standalone)
	{
		return "<div id='sidebarCopyright' refresh='content' tiddler='Copyright'></div>\\n";
	}
	/*function cct_print_shadowTiddlers($standalone)
	{
		global $tiddlyCfg;
		return ',Copyright: "powered by [[TiddlyWiki|http://www.tiddlywiki.com/]] ver. <<version>> and [[ccTiddly v'.$tiddlyCfg['version'].'|http://cctiddly.sourceforge.net]]\\n[[Standalone version|http://'.$_SERVER['PHP_SELF'].'?standalone=1]]"
		';
	}*/
	/////////////////////////////////////////////////////////////////secret code/////////////////////////////////////////////
	function cct_print_pasSecretCode($standalone)
	{
		if( $standalone )
		{
			return "";
		}
		return "pasSecretCode:\"\",\n";
	}
	
	function cct_print_pasSecretCodeOpt($standalone)
	{
		if( $standalone )
		{
			return "";
		}
		return "<<option pasSecretCode>> \\n";
	}
	
	function cct_print_pasSecretCodeMacroOnChange($standalone)
	{
		if( $standalone )
		{
			return "";
		}
		return "case \"pas\":
				elementType = \"input\";
				valueField = \"value\";
				break;";
	}

	function cct_print_pasSecretCodeMacroHandle($standalone)
	{
		if( $standalone )
		{
			return "";
		}
		return "case \"pas\":
			c = document.createElement(\"input\");
			c.onkeyup = this.onChangeOption;
			c.setAttribute(\"option\",opt);
			c.setAttribute(\"type\",\"password\");
			c.size = 15;
			place.appendChild(c);
			c.value = config.options[opt];
			break;";
	}
	
	function cct_print_pasSecretCodeSaveOptionCookie($standalone)
	{
		if( $standalone )
		{
			return "";
		}
		global $tiddlyCfg;
		
		$str = "case \"pas\":
			c += hex_md5(escape(config.options[name].toString()));\n";
		if( $tiddlyCfg['pref']['cookies']!=0 )		//will not set expire if it is 0
		{
			$str .= "var date = new Date();
				date.setTime(date.getTime()+".$tiddlyCfg['pref']['cookies'].");
				document.cookie = c+\"; expires=\"+date.toGMTString()+\"; path=/\";
				return TRUE;\n";
		}
		$str .= "break;\n";
			
		return $str;
	}
	
	function cct_print_pasSecretCodeLoadOptionCookie($standalone)
	{
		if( $standalone )
		{
			return "";
		}
		return "case \"pas\":
			config.options[name] = \"\";
			break;\n";
	}
	
	/////////////////////////////////////////////////////////////////forms/////////////////////////////////////////////
	function cct_print_form($standalone)
	{
		if( $standalone )
		{
			return "";
		}
		global $tiddlyCfg;
		//<input type="hidden" name="secretCode" value="" />
		$ret = '<div id="invisiFormDiv" style="visibility:hidden">
<form name="invisiForm" action="msghandle.php?'.$_SERVER['QUERY_STRING'].'" method="post" target="invisiFormFrame">
	<input type="hidden" name="msg" value="" />
	<input type="hidden" name="oldtitle" value="" />
	<input type="hidden" name="title" value="" />
	<input type="hidden" name="body" value="" />
	<input type="hidden" name="modifier" value="" />
	<input type="hidden" name="tags" value="" />
</form>
</div>';

		if( $tiddlyCfg['developing']>0 )//if developing
		{
			return $ret.'<iframe id="invisiFormFrame" name="invisiFormFrame" style="visibility:visible;width:400px;height:400px">&nbsp;</iframe>';
		}
		else							//if release
		{
			return $ret.'<iframe id="invisiFormFrame" name="invisiFormFrame" style="visibility:hidden;width:1px;height:1px">&nbsp;</iframe>';
		}
	}
	
	/////////////////////////////////////////////////////////////////comments/////////////////////////////////////////////
	
	

?>
