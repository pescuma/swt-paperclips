<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<script type="text/javascript">
var version = {major: 1, minor: 2, revision: 37, date: new Date("Nov 7, 2005"), extensions: {}};
</script>
<!--
TiddlyWiki 1.2.37 by Jeremy Ruston, (jeremy [at] osmosoft [dot] com)
PhpTiddlyWiki extention by Patrick Curry (wiki [at] patrickcurry [dot] com)

Published under a BSD open source license

Copyright (c) Osmosoft Limited 2005

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or other
materials provided with the distribution.

Neither the name of the Osmosoft Limited nor the names of its contributors may be
used to endorse or promote products derived from this software without specific
prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
DAMAGE.
-->
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" >
<title>PhpTiddlyWiki is loading...</title>
<script type="text/javascript">

// PMC

// cookie stuff

function createCookie(name,value,days)
{
	if (days)
	{
		var date = new Date();
		date.setTime(date.getTime()+(days*24*60*60*1000));
		var expires = "; expires="+date.toGMTString();
	}
	else var expires = "";
	document.cookie = name+"="+value+expires+"; path=/";
}

function readCookie(name)
{
	var nameEQ = name + "=";
	var ca = document.cookie.split(';');
	for(var i=0;i < ca.length;i++)
	{
		var c = ca[i];
		while (c.charAt(0)==' ') c = c.substring(1,c.length);
		if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
	}
	return null;
}

function eraseCookie(name)
{
	createCookie(name,"",-1);
}

// end cookie stuff

<?

if ($tiddly_wiki_modifier == "")
{

?>
var modifierName = prompt("What's your name?  PutItInWikiFormat!");
if (modifierName)
{
	createCookie("tiddly_wiki_modifier", modifierName, 365);
}
else
{
	var modifierName = "YourName";
}

<?

} else {

?>

var modifierName = "<?= $tiddly_wiki_modifier ?>";

<?
}
?>
// end PMC

// ---------------------------------------------------------------------------------
// Configuration repository
// ---------------------------------------------------------------------------------

var config = {
	// Options that can be set in the options panel and/or cookies
	options: {
		chkRegExpSearch: false,
		chkCaseSensitiveSearch: false,
		chkAnimate: true,
		txtUserName: modifierName, // PMC
		//txtUserName: "YourName",  // PMC
		chkSaveBackups: true,
		chkAutoSave: false,
		chkGenerateAnRssFeed: false,
		chkSaveEmptyTemplate: false,
		chkOpenInNewWindow: true,
		chkToggleLinks: false,
		chkHttpReadOnly: false,
		chkForceMinorUpdate: false,
		txtMainTab: "tabTimeline",
		txtMoreTab: "moreTabAll"
		},
	// List of notification functions to be called when certain tiddlers are changed or deleted
	notifyTiddlers: [
		{name: "StyleSheetColors", notify: refreshStyles},
		{name: "StyleSheetLayout", notify: refreshStyles},
		{name: "StyleSheet", notify: refreshStyles},
		{name: null, notify: refreshMenu},
		{name: null, notify: refreshStory},
		{name: null, notify: refreshTabs},
		{name: "SiteTitle", notify: refreshTitle},
		{name: "SiteSubtitle", notify: refreshSubtitle},
		{name: "SideBarOptions", notify: refreshSidebar}
		],
	// Shadow tiddlers for emergencies
	shadowTiddlers: {
		SideBarOptions: "<<gradient vert #ffffff #cc9900>><<search>><<newTiddler>><<closeAll>><<permaview>><<saveChanges>><<slider chkSliderOptionsPanel OptionsPanel options 'Change TiddlyWiki advanced options'>>>>",
		OptionsPanel: "These InterfaceOptions for customising TiddlyWiki are saved in your browser\n\nYour username for signing your edits. Write it as a WikiWord (eg JoeBloggs)\n\n<<option txtUserName>>\n<<option chkSaveBackups>> SaveBackups\n<<option chkAutoSave>> AutoSave\n<<option chkGenerateAnRssFeed>> GenerateAnRssFeed\n<<option chkRegExpSearch>> RegExpSearch\n<<option chkCaseSensitiveSearch>> CaseSensitiveSearch\n<<option chkAnimate>> EnableAnimations\n\nSee AdvancedOptions",
		AdvancedOptions: "<<option chkOpenInNewWindow>> OpenLinksInNewWindow\n<<option chkSaveEmptyTemplate>> SaveEmptyTemplate\n<<option chkToggleLinks>> Clicking on links to tiddlers that are already open causes them to close\n^^(override with Control or other modifier key)^^\n<<option chkHttpReadOnly>> HideEditingFeatures when viewed over HTTP\n<<option chkForceMinorUpdate>> Treat edits as MinorChanges by preserving date and time\n^^(override with Shift key when clicking 'done' or by pressing Ctrl-Shift-Enter^^",
		SideBarTabs: "<<tabs txtMainTab Timeline Timeline TabTimeline All 'All tiddlers' TabAll Tags 'All tags' TabTags More 'More lists' TabMore>>",
		TabTimeline: "<<timeline>>",
		TabAll: "<<list all>>",
		TabTags: "<<allTags>>",
		TabMore: "<<tabs txtMoreTab Missing 'Missing tiddlers' TabMoreMissing Orphans 'Orphaned tiddlers' TabMoreOrphans>>",
		TabMoreMissing: "<<list missing>>",
		TabMoreOrphans: "<<list orphans>>"
		},
	// Miscellaneous options
	numRssItems: 20, // Number of items in the RSS feed
	animFast: 0.12, // Speed for animations (lower == slower)
	animSlow: 0.01, // Speed for EasterEgg animations
	// Messages
	messages: {
		customConfigError: "Error in systemConfig tiddler '%1' - %0",
		savedSnapshotError: "It appears that this TiddlyWiki has been incorrectly saved. Please see http://www.tiddlywiki.com/#DownloadSoftware for details",
		subtitleUnknown: "(unknown)",
		undefinedTiddlerToolTip: "The tiddler '%0' doesn't yet exist",
		shadowedTiddlerToolTip: "The tiddler '%0' doesn't yet exist, but has a pre-defined shadow value",
		externalLinkTooltip: "External link to %0",
		noTags: "There are no tagged tiddlers",
		notFileUrlError: "You need to save this TiddlyWiki to a file before you can save changes.",
		cantSaveError: "It's not possible to save changes using this browser. Use FireFox if you can",
		invalidFileError: "The original file '%0' does not appear to be a valid TiddlyWiki",
		backupSaved: "Backup saved",
		backupFailed: "Failed to save backup file",
		rssSaved: "RSS feed saved",
		rssFailed: "Failed to save RSS feed file",
		emptySaved: "Empty template saved",
		emptyFailed: "Failed to save empty template file",
		mainSaved: "Main TiddlyWiki file saved",
		mainFailed: "Failed to save main TiddlyWiki file. Your changes have not been saved",
		macroError: "Error executing macro '%0'",
		overwriteWarning: "A tiddler named '%0' already exists. Choose OK to overwrite it",
		unsavedChangesWarning: "WARNING! There are unsaved changes in TiddlyWiki\n\nChoose OK to save\nChoose CANCEL to discard",
		dates: {
			months: ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November","December"],
			days: ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"]
			}
		},
	views: {
		wikified: {
			tag: {labelNoTags: "no tags", labelTags: "tags: ", tooltip: "Show tiddlers tagged with '%0'", openAllText: "Open all", openAllTooltip: "Open all of these tiddlers", popupNone: "No other tiddlers tagged with '%0'"},
			toolbarClose: {text: "close", tooltip: "Close this tiddler"},
			toolbarEdit: {text: "edit", tooltip: "Edit this tiddler"},
			toolbarPermalink: {text: "permalink", tooltip: "Permalink for this tiddler"},
			toolbarReferences: {text: "references", tooltip: "Show tiddlers that link to this one", popupNone: "No references"},
			defaultText: "The tiddler '%0' doesn't yet exist. Double-click to create it"
			},
		editor: {
			tagPrompt: "Type tags separated with spaces, [[use double square brackets]] if necessary, or add existing",
			tagChooser: {text: "tags", tooltip: "Choose existing tags to add to this tiddler", popupNone: "There are no tags defined", tagTooltip: "Add the tag '%0'"},
			toolbarDone: {text: "done", tooltip: "Save changes to this tiddler"},
			toolbarCancel: {text: "cancel", tooltip: "Undo changes to this tiddler"},
			toolbarDelete: {text: "delete", tooltip: "Delete this tiddler"},
			defaultText: "Type the text for '%0'"
			}
		},
	macros: { // Each has a 'handler' member that is inserted later
		today: {},
		version: {},
		search: {label: "search", prompt: "Search this TiddlyWiki", sizeTextbox: 15, accessKey: "F", successMsg: "%0 tiddlers found matching %1", failureMsg: "No tiddlers found matching %0"},
		tiddler: {},
		tag: {},
		timeline: {dateFormat: "DD MMM YYYY"},
		allTags: {tooltip: "Show tiddlers tagged with '%0'", noTags: "There are no tagged tiddlers"},
		list: {
			all: {prompt: "All tiddlers in alphabetical order"},
			missing: {prompt: "Tiddlers that have links to them but are not defined"},
			orphans: {prompt: "Tiddlers that are not linked to from any other tiddlers"},
			shadowed: {prompt: "Tiddlers shadowed with default contents"}
			},
		closeAll: {label: "close all", prompt: "Close all displayed tiddlers (except any that are being edited)"},
		permaview: {label: "permaview", prompt: "Link to an URL that retrieves all the currently displayed tiddlers"},
		saveChanges: {label: "save changes", prompt: "Save all tiddlers to create a new TiddlyWiki", accessKey: "S"},
		slider: {},
		option: {},
		newTiddler: {label: "new tiddler", prompt: "Create a new tiddler", title: "New Tiddler", accessKey: "N"},
		newJournal: {label: "new journal", prompt: "Create a new tiddler from the current date and time", accessKey: "J"},
		sparkline: {},
		tabs: {},
		gradient: {}
		},
	textPrimitives: {}
};

config.textPrimitives.upperLetter = "[A-Z\u00c0-\u00de\u0150\u0170]";
config.textPrimitives.lowerLetter = "[a-z\u00df-\u00ff_0-9\\-\u0151\u0171]";
config.textPrimitives.anyLetter = "[A-Za-z\u00c0-\u00de\u00df-\u00ff_0-9\\-\u0150\u0170\u0151\u0171]";
config.textPrimitives.anyDigit = "[0-9]";
config.textPrimitives.anyNumberChar = "[0-9\\.E]";
config.textPrimitives.urlPattern = "(?:http|https|mailto|ftp):[^\\s'\"]+(?:/|\\b)";
config.textPrimitives.unWikiLink = "~";
config.textPrimitives.wikiLink = "(?:" + config.textPrimitives.unWikiLink + "{0,1})(?:(?:" + config.textPrimitives.upperLetter + "+" +
											   config.textPrimitives.lowerLetter + "+" +
											   config.textPrimitives.upperLetter +
											   config.textPrimitives.anyLetter + "*)|(?:" +
											   config.textPrimitives.upperLetter + "{2,}" +
											   config.textPrimitives.lowerLetter + "+))";

// ---------------------------------------------------------------------------------
// Shadow stylesheet elements
// ---------------------------------------------------------------------------------

config.shadowTiddlers.StyleSheetColors = "body {\n	background: #fff;\n}\n\n#titleLine {\n	color: #fff;\n	background: #300;\n}\n\n#titleLine a {\n	color: #cf6;\n}\n\n#mainMenu {\n	color: #000;\n}\n\n#mainMenu .tiddlyLink {\n	color: #963;\n}\n\n#mainMenu .tiddlyLink:hover {\n	background: #963;\n	color: #fff;\n}\n\n#mainMenu .externalLink {\n	color: #963;\n}\n\n#mainMenu .externalLink:hover {\n	background: #963;\n	color: #fff;\n}\n\n#mainMenu .button {\n	color: #930;\n}\n\n#mainMenu .button:hover {\n	color: #cf6;\n	background: #930;\n}\n\n#messageArea {\n	background: #930;\n	color: #fff;\n}\n\n#messageArea a:link, #messageArea a:visited {\n	color: #c90;\n}\n\n#messageArea a:hover {\n	color: #963;\n}\n\n#messageArea a:active {\n	color: #fff;\n}\n\n.popup {\n	background: #eea;\n	border: 1px solid #930;\n}\n\n.popup hr {\n	color: #963;\n	background: #963;\n	border: 0;\n}\n\n.popup li.disabled {\n	color: #ba9;\n}\n\n.popup li a, .popup li a:visited {\n	color: #300;\n}\n\n.popup li a:hover {\n	background: #930;\n	color: #eea;\n}\n\n.tabSelected {\n	background: #eea;\n}\n\n.tabUnselected {\n	background: #c90;\n}\n\n.tabContents {\n	background: #eea;\n}\n\n.tiddler .button {\n	color: #930;\n}\n\n.tiddler .button:hover {\n	color: #cf6;\n	background: #930;\n}\n\n.tiddler .button:active {\n	color: #fff;\n	background: #c90;\n}\n\n.toolbar {\n	color: #aaa;\n}\n\n.footer {\n	color: #ddd;\n}\n\n.selectedTiddler .footer {\n	color: #888;\n}\n\n.viewer {\n	color: #000;\n}\n\n.viewer a:link, .body a:visited {\n	color: #963;\n}\n\n.viewer a:hover {\n	color: #fff;\n	background: #963;\n}\n\n.viewer .button {\n	background: #c90;\n	color: #300;\n	border-right: 1px solid #300;\n	border-bottom: 1px solid #300;\n}\n\n.viewer .button:hover {\n	background: #eea;\n	color: #c90;\n}\n\n.viewer blockquote {\n	border-left: 3px solid #666;\n}\n\n.viewer h1,.viewer h2,.viewer h3,.viewer h4,.viewer h5 {\n	background: #cc9;\n}\n\n.viewer table {\n	border: 2px solid #303030;\n}\n\n.viewer th {\n	background: #996;\n	border: 1px solid #606060;\n	color: #fff;\n}\n\n.viewer td, .viewer tr {\n	border: 1px solid #606060;\n}\n\n.viewer pre {\n	color: #000000;\n	border: 1px solid #963;\n	background: #eea;\n}\n\n.viewer code {\n	color: #630;\n}\n\n.viewer hr {\n	border-top: dashed 1px #606060;\n	border-left: none;\n	border-right: none;\n	border-bottom: none;\n	color: #666;\n}\n\n.highlight, .marked {\n	color: #000;\n	background: #ffe72f;\n}\n\n.editor {\n	color: #402C74;\n}\n\n.editor input {\n	border: 1px solid #000000;\n}\n\n.editor textarea {\n	border: 1px solid #000000;\n	width: 100%;\n}\n\n.editorFooter {\n	color: #aaa;\n}\n\n.editorFooter A {\n	color: #930;\n}\n\n.editorFooter A:hover {\n	color: #cf6;\n	background: #930;\n}\n\n.editorFooter A:active {\n	color: #fff;\n	background: #c90;\n}\n\n#sidebar {\n	color: #000;\n}\n\n#sidebarOptions {\n	background: #c90;\n}\n\n#sidebarOptions .button {\n	color: #930;\n}\n\n#sidebarOptions .button:hover {\n	color: #cf6;\n	background: #930;\n}\n\n#sidebarOptions .button:active {\n	color: #930;\n	background: #cf6;\n}\n\n#sidebarOptions .sliderPanel {\n	background: #eea;\n}\n\n#sidebarOptions .sliderPanel A {\n	color: #930;\n}\n\n#sidebarOptions .sliderPanel A:hover {\n	color: #cf6;\n	background: #930;\n}\n\n#sidebarOptions .sliderPanel A:active {\n	color: #930;\n	background: #cf6;\n}\n\n.sidebarSubHeading {\n	color: #300;\n}\n\n#sidebarTabs {\n	background: #c90;\n}\n\n#sidebarTabs .tabSelected {\n	color: #cf6;\n	background: #963;\n}\n\n#sidebarTabs .tabUnselected {\n	color: #cf6;\n	background: #930;\n}\n\n#sidebarTabs .tabContents {\n	background: #963;\n}\n\n\n#sidebarTabs .txtMoreTab .tabSelected {\n	background: #930;\n}\n\n#sidebarTabs .txtMoreTab .tabUnselected {\n	background: #300;\n}\n\n#sidebarTabs .txtMoreTab .tabContents {\n	background: #930;\n}\n\n#sidebarTabs .tabContents .tiddlyLink {\n	color: #cf6;\n}\n\n#sidebarTabs .tabContents .tiddlyLink:hover {\n	background: #ccff66;\n	color: #300;\n}\n\n#sidebarTabs .tabContents .button {\n	color: #cf6;\n}\n\n#sidebarTabs .tabContents .button:hover {\n	color: #300;\n	background: #cf6;\n}\n\n.sparkline {\n	background: #eea;\n	border: 0;\n}\n\n.sparktick {\n	background: #930;\n}\n\n.errorNoSuchMacro {\n	color: #ff0;\n	background: #f00;\n}\n\n.zoomer {\n	color: #963;\n	border: 1px solid #963;\n}";
config.shadowTiddlers.StyleSheetLayout = "body {\n	font-size: 9pt;\n	font-family: verdana,arial,helvetica;\n	margin: 0;\n	padding: 0;\n	position: relative;\n	z-index: 0;\n}\n\na:link, a:visited, a:hover, a:active{\n	text-decoration: none;\n}\n\n#contentWrapper {\n	position: relative;\n}\n\n#titleLine {\n	padding: 5em 1em 1em 1em;\n}\n\n#siteTitle {\n	font-size: 26pt;\n}\n\n#siteSubtitle {\n	padding-left: 1em;\n	font-size: 10pt;\n}\n\n#mainMenu {\n	position: absolute;\n	left: 0em;\n	width: 10em;\n	line-height: 166%;\n	padding: 1.5em 0.5em 0.5em 0.5em;\n	font-size: 10pt;\n	text-align: right;\n}\n\n\n#mainMenu .externalLink {\n	text-decoration: underline;\n}\n\n#displayArea {\n	margin: 1em 17em 0em 14em;\n}\n\n#messageArea {\n	padding: 0.5em;\n\n}\n\n#messageArea a:link, #messageArea a:visited {\n	display: inline;\n	text-decoration: underline;\n}\n\n.popup {\n	font-size: 8pt;\n	list-style: none;\n	padding: 0.2em;\n	margin: 0;\n}\n\n.popup hr {\n	display: block;\n	height: 1px;\n	width: auto;\n	padding: 0;\n	margin: 0.2em 0em;\n}\n\n.popup li {\n	display: block;\n	padding: 0;\n}\n\n.popup li.disabled {\n	padding: 0.2em;\n}\n\n.popup li a, .popup li a:visited {\n	display: block;\n	text-decoration: none;\n	padding: 0.2em;\n}\n\n.tabset {\n	padding: 1em 0em 0em 0.5em;\n}\n\n.tab {\n	margin: 0em 0em 0em 0.25em;\n	padding: 2px;\n}\n\n.tabContents {\n	padding: 0.5em;\n}\n\n.tabContents ul, .tabContents ol {\n	margin: 0;\n	padding: 0;\n}\n\n.tabContents li {\n	list-style: none;\n}\n\n.tabContents li.listLink {\n   margin-left: .75em;\n}\n\n.tiddler {\n	padding: 0;\n	padding: 1em 1em 0em 1em;\n	font-size: 9pt;\n}\n\n\n/* Can these styles be moved to the previous rule?\n.selectedTiddler {\n	padding: 1em 1em 0em 1em;\n	font-size: 9pt;\n}\n\n.unselectedTiddler {\n	padding: 1em 1em 0em 1em;\n	font-size: 9pt;\n} */\n\n.tiddler a.tiddlyLinkExisting {\n	font-weight: bold;\n}\n\n.tiddler a.tiddlyLinkNonExisting {\n	font-style: italic;\n}\n\n.tiddler a.tiddlyLinkNonExisting.shadow {\n	font-weight: bold;\n}\n\n.tiddler a.externalLink {\n	text-decoration: underline;\n}\n\n.tiddler .button {\n	padding: 0.2em 0.4em;\n}\n\n.tiddler .button:hover {\n	text-decoration: none;\n}\n\n.title {\n	font-size: 10pt;\n	font-weight: bold;\n}\n\n.shadow .title {\n	color: #888888;\n}\n\n.toolbar {\n	text-align: right;\n	font-weight: normal;\n	font-size: 8pt;\n	padding: 0;\n	padding-bottom: 2em;\n	visibility: hidden;\n}\n\n.selectedTiddler .toolbar {\n	visibility: visible;\n}\n\n.footer {\n/* Isn't 'clear:both' the right rule? */\n	clear: left;\n	clear: right;\n	font-weight: normal;\n	font-size: 8pt;\n	margin: 0;\n	padding: 0;\n}\n\n.body {\n	padding-top: 0.5em;\n}\n\n.viewer {\n	line-height: 140%;\n}\n\n.viewer .button {\n	margin: 0em 0.25em;\n	padding: 0em 0.25em;\n}\n\n.viewer blockquote {\n	font-size: 8pt;\n	line-height: 150%;\n	padding-left: 0.8em;\n	margin-left: 2.5em;\n}\n\n.viewer ul {\n	margin-left: 0.5em;\n	padding-left: 1.5em;\n}\n\n.viewer ol {\n	margin-left: 0.5em;\n	padding-left: 1.5em;\n}\n\n.viewer h1,.viewer h2,.viewer h3,.viewer h4,.viewer h5 {\n	font-weight: bold;\n	text-decoration: none;\n	padding-left: 0.4em;\n}\n\n.viewer h1 {font-size: 12pt;}\n.viewer h2 {font-size: 11pt;}\n.viewer h3 {font-size: 10pt;}\n.viewer h4 {font-size: 9pt;}\n.viewer h5 {font-size: 8pt;}\n\n.viewer table {\n	border-collapse: collapse;\n	margin: 0.8em 1.0em;;\n	font-size: 100%;\n}\n\n.viewer th, .viewer td, .viewer tr,.viewer caption{\n	padding: 3px;\n}\n\n.viewer pre {\n	padding: 0.5em;\n	margin-left: 0.5em;\n	font-size: 100%;\n	line-height: 1.4em;\n	overflow: auto;\n}\n\n.viewer code {\n	font-size: 100%;\n	line-height: 1.4em;\n}\n\n.viewer hr {\n	height: 1px;\n}\n\n.editor {\n	font-size: 8pt;\n	font-weight: normal;\n}\n\n.editor input {\n	display: block;\n	width: 100%;\n}\n\n.editor textarea {\n	display: block;\n	font: inherit;\n	width: 100%;\n}\n\n.editorFooter {\n	padding: 0.25em 0em;\n	font-size: 8pt;\n}\n\n.editorFooter A {\n	padding: 0.2em 0.4em;\n}\n\n.editorFooter A:hover {\n	text-decoration: none;\n}\n\n#sidebar {\n	position: absolute;\n	right: 0em;\n	width: 16em;\n	font-size: 8pt;\n}\n\n#sidebarOptions {\n	padding-top: 0em;\n}\n\n#sidebarOptions .button {\n	padding: 0.3em 0.2em 0.3em 1em;\n	display: block;\n}\n\n#sidebarOptions input {\n	margin: 0.4em 0em 0.3em 1em;\n}\n\n#sidebarOptions .sliderPanel {\n	padding: 0.5em;\n	font-size: 7pt;\n}\n\n#sidebarOptions .sliderPanel A {\n	font-weight: bold;\n}\n\n#sidebarOptions .sliderPanel input {\n	margin: 0;\n	margin-bottom: 0.3em;\n}\n\n.sidebarSubHeading {\n	font-size: 7pt;\n}\n\n#sidebarTabs .tabSelected {\n	position: relative;\n	top: -2px;\n}\n\n\n#sidebarTabs .tabContents {\n	width: 15em;\n	overflow: hidden;\n}\n\n.sparkline {\n	line-height: 100%;\n}\n\n.sparktick {\n	outline: 0;\n}\n\n.zoomer {\n	font-size: 10pt;\n	position: absolute;\n	padding: 1em;\n}\n";

// ---------------------------------------------------------------------------------
// Main
// ---------------------------------------------------------------------------------

// TiddlyWiki storage
var store = new TiddlyWiki();

// Animation engine
var anim = new Animator();

var readOnly = false;

// Starting up
function main()
{
	browserTests();
	addEvent(document,"click",Popup.onDocumentClick);
	saveTest();
	loadOptionsCookie();
	for(var s=0; s<config.notifyTiddlers.length; s++)
		store.addNotification(config.notifyTiddlers[s].name,config.notifyTiddlers[s].notify);
	store.loadFromDiv("storeArea","store");
	loadSystemConfig();
	readOnly = (document.location.toString().substr(0,7) == "http://") ? config.options.chkHttpReadOnly : false;
	store.notifyAll();
	restart();
}

// Restarting
function restart()
{
	var start = store.getTiddlerText("DefaultTiddlers");
	if(window.location.hash)
		displayTiddlers(null,convertUTF8ToUnicode(decodeURI(window.location.hash.substr(1))),1,null,null);
	else if(start)
		displayTiddlers(null,start,1,null,null);
}

// Shame...
function browserTests()
{
	var a = navigator.userAgent.toLowerCase();
	config.browser = {
		isIE: a.indexOf("msie") != -1 && a.indexOf("opera") == -1
		};
}

function saveTest()
{
	var saveTest = document.getElementById("saveTest");
	if(saveTest.hasChildNodes())
		alert(config.messages.savedSnapshotError);
	saveTest.appendChild(document.createTextNode("savetest"));
}

function loadSystemConfig()
{
	var configTiddlers = store.getTaggedTiddlers("systemConfig");
	for(var t=0; t<configTiddlers.length; t++)
		{
		var ex = processConfig(configTiddlers[t].text);
		if(ex)
			displayMessage(config.messages.customConfigError.format([ex,configTiddlers[t].title]));
		}
}

// Merge a custom configuration over the top of the current configuration
// Returns a string error message or null if it went OK
function processConfig(customConfig)
{
	try
		{
		if(customConfig && customConfig != "")
			window.eval(customConfig);
		}
	catch(e)
		{
		return(e.toString());
		}
	return null;
}

// ---------------------------------------------------------------------------------
// Formatters
// ---------------------------------------------------------------------------------

config.formatterHelpers = {

	charFormatHelper: function(w)
	{
		var e = createTiddlyElement(w.output,this.element);
		w.subWikify(e,this.terminator);
	},
	
	inlineCssHelper:  function(w)
	{
		var styles = [];
		var lookahead = "(?:(" + config.textPrimitives.anyLetter + "+)\\(([^\\)\\|\\n]+)(?:\\):))|(?:(" + config.textPrimitives.anyLetter + "+):([^;\\|\\n]+);)";
		var lookaheadRegExp = new RegExp(lookahead,"mg");
		var hadStyle = false;
		do {
			lookaheadRegExp.lastIndex = w.nextMatch;
			var lookaheadMatch = lookaheadRegExp.exec(w.source);
			var gotMatch = lookaheadMatch && lookaheadMatch.index == w.nextMatch;
			if(gotMatch)
				{
				var s,v;
				hadStyle = true;
				if(lookaheadMatch[1])
					{
					s = lookaheadMatch[1].unDash();
					v = lookaheadMatch[2];
					}
				else
					{
					s = lookaheadMatch[3].unDash();
					v = lookaheadMatch[4];
					}
				switch(s)
					{
					case "bgcolor": s = "backgroundColor"; break;
					}
				styles.push({style: s, value: v});
				w.nextMatch = lookaheadMatch.index + lookaheadMatch[0].length;
				}
		} while(gotMatch);
		return styles;
	},

	monospacedByLineHelper: function(w)
	{
		var lookaheadRegExp = new RegExp(this.lookahead,"mg");
		lookaheadRegExp.lastIndex = w.matchStart;
		var lookaheadMatch = lookaheadRegExp.exec(w.source);
		if(lookaheadMatch && lookaheadMatch.index == w.matchStart)
			{
			var text = lookaheadMatch[1];
			if(config.browser.isIE)
				text = text.replace(/\n/g,"\r");
			var e = createTiddlyElement(w.output,"pre",null,null,text);
			w.nextMatch = lookaheadMatch.index + lookaheadMatch[0].length;
			}
	}

};

config.formatters = [
{
	name: "table",
	match: "^\\|(?:[^\\n]*)\\|(?:[fhc]?)$",
	lookahead: "^\\|([^\\n]*)\\|([fhc]?)$",
	rowTerminator: "\\|(?:[fhc]?)$\\n?",
	cellPattern: "(?:\\|([^\\n\\|]*)\\|)|(\\|[fhc]?$\\n?)",
	cellTerminator: "(?:\\x20*)\\|",
	rowTypes: {"c": "caption", "h": "thead", "": "tbody", "f": "tfoot"},
	handler: function(w)
	{
		var table = createTiddlyElement(w.output,"table");
		w.nextMatch = w.matchStart;
		var lookaheadRegExp = new RegExp(this.lookahead,"mg");
		var currRowType = null, nextRowType;
		var rowContainer, rowElement;
		var prevColumns = [];
		var rowCount = 0;
		do {
			lookaheadRegExp.lastIndex = w.nextMatch;
			var lookaheadMatch = lookaheadRegExp.exec(w.source);
			var matched = lookaheadMatch && lookaheadMatch.index == w.nextMatch;
			if(matched)
				{
				nextRowType = lookaheadMatch[2];
				if(nextRowType != currRowType)
					rowContainer = createTiddlyElement(table,this.rowTypes[nextRowType]);
				currRowType = nextRowType;
				if(currRowType == "c")
					{
					if(rowCount == 0)
						rowContainer.setAttribute("align","top");
					else
						rowContainer.setAttribute("align","bottom");
					w.nextMatch = w.nextMatch + 1;
					w.subWikify(rowContainer,this.rowTerminator);
					}
				else
					{
					rowElement = createTiddlyElement(rowContainer,"tr");
					this.rowHandler(w,rowElement,prevColumns);
					}
				rowCount++;
				}
		} while(matched);
	},
	rowHandler: function(w,e,prevColumns)
	{
		var col = 0;
		var currColCount = 1;
		var cellRegExp = new RegExp(this.cellPattern,"mg");
		do {
			cellRegExp.lastIndex = w.nextMatch;
			var cellMatch = cellRegExp.exec(w.source);
			matched = cellMatch && cellMatch.index == w.nextMatch;
			if(matched)
				{
				if(cellMatch[1] == "~")
					{
					var last = prevColumns[col];
					if(last)
						{
						last.rowCount++;
						last.element.setAttribute("rowSpan",last.rowCount);
						last.element.setAttribute("rowspan",last.rowCount);
						last.element.valign = "center";
						}
					w.nextMatch = cellMatch.index + cellMatch[0].length-1;
					}
				else if(cellMatch[1] == ">")
					{
					currColCount++;
					w.nextMatch = cellMatch.index + cellMatch[0].length-1;
					}
				else if(cellMatch[2])
					{
					w.nextMatch = cellMatch.index + cellMatch[0].length;;
					break;
					}
				else
					{
					var spaceLeft = false, spaceRight = false;
					w.nextMatch++;
					var styles = config.formatterHelpers.inlineCssHelper(w);
					while(w.source.substr(w.nextMatch,1) == " ")
						{
						spaceLeft = true;
						w.nextMatch++;
						}
					var cell;
					if(w.source.substr(w.nextMatch,1) == "!")
						{
						cell = createTiddlyElement(e,"th");
						w.nextMatch++;
						}
					else
						cell = createTiddlyElement(e,"td");
					prevColumns[col] = {rowCount: 1, element: cell};
					lastColCount = 1;
					lastColElement = cell;
					if(currColCount > 1)
						{
						cell.setAttribute("colSpan",currColCount);
						cell.setAttribute("colspan",currColCount);
						currColCount = 1;
						}
					for(var t=0; t<styles.length; t++)
						cell.style[styles[t].style] = styles[t].value;
					w.subWikify(cell,this.cellTerminator);
					if(w.matchText.substr(w.matchText.length-2,1) == " ")
						spaceRight = true;
					if(spaceLeft && spaceRight)
						cell.align = "center";
					else if (spaceLeft)
						cell.align = "right";
					else if (spaceRight)
						cell.align = "left";
					w.nextMatch = w.nextMatch-1;
					}
				col++;
				}
		} while(matched);		
	}
},

{
	name: "rule",
	match: "^----$\\n?",
	handler: function(w)
	{
		createTiddlyElement(w.output,"hr");
	}
},

{
	name: "heading",
	match: "^!{1,5}",
	terminator: "\\n",
	handler: function(w)
	{
		var e = createTiddlyElement(w.output,"h" + w.matchLength);
		w.subWikify(e,this.terminator);
	}
},

{
	name: "monospacedByLine",
	match: "^\\{\\{\\{\\n",
	lookahead: "^\\{\\{\\{\\n((?:^[^\\n]*\\n)+?)(^\\}\\}\\}$\\n?)",
	handler: config.formatterHelpers.monospacedByLineHelper
},

{
	name: "monospacedByLineForPlugin",
	match: "^//\\{\\{\\{\\n",
	lookahead: "^//\\{\\{\\{\\n\\n*((?:^[^\\n]*\\n)+?)(\\n*^//\\}\\}\\}$\\n?)",
	handler: config.formatterHelpers.monospacedByLineHelper
},

{
	name: "wikifyCommentForPlugin", 
	match: "^/\\*\\*\\*\\n",
	terminator: "^\\*\\*\\*/\\n",
	handler: function(w)
	{
		w.subWikify(w.output,this.terminator);
	}
},

{
	name: "quoteByBlock",
	match: "^<<<\\n",
	terminator: "^<<<\\n",
	handler: function(w)
	{
		var e = createTiddlyElement(w.output,"blockquote");
		w.subWikify(e,this.terminator);
	}
},

{
	name: "quoteByLine",
	match: "^>+",
	terminator: "\\n",
	element: "blockquote",
	handler: function(w)
	{
		var lookaheadRegExp = new RegExp(this.match,"mg");
		var placeStack = [w.output];
		var currLevel = 0;
		var newLevel = w.matchLength;
		var t;
		do {
			if(newLevel > currLevel)
				{
				for(t=currLevel; t<newLevel; t++)
					placeStack.push(createTiddlyElement(placeStack[placeStack.length-1],this.element));
				}
			else if(newLevel < currLevel)
				{
				for(t=currLevel; t>newLevel; t--)
					placeStack.pop();
				}
			currLevel = newLevel;
			w.subWikify(placeStack[placeStack.length-1],this.terminator);
			createTiddlyElement(placeStack[placeStack.length-1],"br");
			lookaheadRegExp.lastIndex = w.nextMatch;
			var lookaheadMatch = lookaheadRegExp.exec(w.source);
			var matched = lookaheadMatch && lookaheadMatch.index == w.nextMatch;
			if(matched)
				{
				newLevel = lookaheadMatch[0].length;
				w.nextMatch += lookaheadMatch[0].length;
				}
		} while(matched);
	}
},

{
	name: "list",
	match: "^(?:(?:\\*+)|(?:#+))",
	lookahead: "^(?:(\\*+)|(#+))",
	terminator: "\\n",
	outerElement: "ul",
	itemElement: "li",
	handler: function(w)
	{
		var lookaheadRegExp = new RegExp(this.lookahead,"mg");
		w.nextMatch = w.matchStart;
		var placeStack = [w.output];
		var currType = null, newType;
		var currLevel = 0, newLevel;
		var t;
		do {
			lookaheadRegExp.lastIndex = w.nextMatch;
			var lookaheadMatch = lookaheadRegExp.exec(w.source);
			var matched = lookaheadMatch && lookaheadMatch.index == w.nextMatch;
			if(matched)
				{
				if(lookaheadMatch[1])
					newType = "ul";
				if(lookaheadMatch[2])
					newType = "ol";
				newLevel = lookaheadMatch[0].length;
				w.nextMatch += lookaheadMatch[0].length;
				if(newLevel > currLevel)
					{
					for(t=currLevel; t<newLevel; t++)
						placeStack.push(createTiddlyElement(placeStack[placeStack.length-1],newType));
					}
				else if(newLevel < currLevel)
					{
					for(t=currLevel; t>newLevel; t--)
						placeStack.pop();
					}
				else if(newLevel == currLevel && newType != currType)
					{
						placeStack.pop();
						placeStack.push(createTiddlyElement(placeStack[placeStack.length-1],newType));
					}
				currLevel = newLevel;
				currType = newType;
				var e = createTiddlyElement(placeStack[placeStack.length-1],"li");
				w.subWikify(e,this.terminator);
				}
		} while(matched);
	}
},

{
	name: "wikiLink",
	match: config.textPrimitives.wikiLink,
	badPrefix: config.textPrimitives.anyLetter,
	handler: function(w)
	{
		var preRegExp = new RegExp(config.textPrimitives.anyLetter,"mg");
		var preMatch = null;
		if(w.matchStart > 0)
			{
			preRegExp.lastIndex = w.matchStart-1;
			preMatch = preRegExp.exec(w.source);
			}
		if(preMatch && preMatch.index == w.matchStart-1)
			w.outputText(w.output,w.matchStart,w.nextMatch);
		else if(w.matchText.substr(0,1) == config.textPrimitives.unWikiLink)
			w.outputText(w.output,w.matchStart + 1,w.nextMatch);
		else
			{
			var link = createTiddlyLink(w.output,w.matchText,false);
			w.outputText(link,w.matchStart,w.nextMatch);
			}
	}
},

{
	name: "prettyLink",
	match: "\\[\\[",
	lookahead: "\\[\\[([^\\|\\]]*?)(?:(\\]\\])|(\\|(.*?)\\]\\]))",
	terminator: "\\|",
	handler: function(w)
	{
		var lookaheadRegExp = new RegExp(this.lookahead,"mg");
		lookaheadRegExp.lastIndex = w.matchStart;
		var lookaheadMatch = lookaheadRegExp.exec(w.source)
		if(lookaheadMatch && lookaheadMatch.index == w.matchStart && lookaheadMatch[2]) // Simple bracketted link
			{
			var link = createTiddlyLink(w.output,lookaheadMatch[1],false);
			w.outputText(link,w.nextMatch,w.nextMatch + lookaheadMatch[1].length);
			w.nextMatch += lookaheadMatch[1].length + 2;
			}
		else if(lookaheadMatch && lookaheadMatch.index == w.matchStart && lookaheadMatch[3]) // Pretty bracketted link
			{
			var e;
			if(store.tiddlerExists(lookaheadMatch[4]))
				e = createTiddlyLink(w.output,lookaheadMatch[4],false);
			else
				e = createExternalLink(w.output,lookaheadMatch[4]);
			w.outputText(e,w.nextMatch,w.nextMatch + lookaheadMatch[1].length);
			w.nextMatch = lookaheadMatch.index + lookaheadMatch[0].length;
			}
	}
},

{
	name: "urlLink",
	match: "(?:http|https|mailto|ftp):[^\\s'\"]+(?:/|\\b)",
	handler: function(w)
	{
		var e = createExternalLink(w.output,w.matchText);
		w.outputText(e,w.matchStart,w.nextMatch);
	}
},

{
	name: "image",
	match: "\\[(?:[<]{0,1})(?:[>]{0,1})[Ii][Mm][Gg]\\[",
	lookahead: "\\[([<]{0,1})([>]{0,1})[Ii][Mm][Gg]\\[(?:([^\\|\\]]+)\\|)?([^\\[\\]\\|]+)\\](?:\\[([^\\]]*)\\]?)?(\\])",
	handler: function(w)
	{
		var lookaheadRegExp = new RegExp(this.lookahead,"mg");
		lookaheadRegExp.lastIndex = w.matchStart;
		var lookaheadMatch = lookaheadRegExp.exec(w.source);
		if(lookaheadMatch && lookaheadMatch.index == w.matchStart) // Simple bracketted link
			{
			var e = w.output;
			if(lookaheadMatch[5])
				{
				if(store.tiddlerExists(lookaheadMatch[5]))
					e = createTiddlyLink(w.output,lookaheadMatch[5],false);
				else
					e = createExternalLink(w.output,lookaheadMatch[5]);
				}
			var img = createTiddlyElement(e,"img");
			if(lookaheadMatch[1])
				img.align = "left";
			else if(lookaheadMatch[2])
				img.align = "right";
			if(lookaheadMatch[3])
				img.title = lookaheadMatch[3];
			img.src = lookaheadMatch[4];
			w.nextMatch = lookaheadMatch.index + lookaheadMatch[0].length;
			}
	}
},

{
	name: "macro",
	match: "<<",
	lookahead: "<<([^>\\s]+)(?:\\s*)([^>]*)>>",
	handler: function(w)
	{
		var lookaheadRegExp = new RegExp(this.lookahead,"mg");
		lookaheadRegExp.lastIndex = w.matchStart;
		var lookaheadMatch = lookaheadRegExp.exec(w.source)
		if(lookaheadMatch && lookaheadMatch.index == w.matchStart && lookaheadMatch[1])
			{
			var params = lookaheadMatch[2].readMacroParams();			
			w.nextMatch = lookaheadMatch.index + lookaheadMatch[0].length;
			try
				{
				var macro = config.macros[lookaheadMatch[1]];
				if(macro && macro.handler)
					macro.handler(w.output,lookaheadMatch[1],params,w);
				else
					createTiddlyElement(w.output,"span",null,"errorNoSuchMacro","<<" + lookaheadMatch[1] + ">>");
				}
			catch(e)
				{
				displayMessage(config.messages.macroError.format([lookaheadMatch[1]]));
				displayMessage(e.toString());
				}
			}
	}
},

{
	name: "html",
	match: "<[Hh][Tt][Mm][Ll]>",
	lookahead: "<[Hh][Tt][Mm][Ll]>((?:.|\\n)*?)</[Hh][Tt][Mm][Ll]>",
	handler: function(w)
	{
		var lookaheadRegExp = new RegExp(this.lookahead,"mg");
		lookaheadRegExp.lastIndex = w.matchStart;
		var lookaheadMatch = lookaheadRegExp.exec(w.source)
		if(lookaheadMatch && lookaheadMatch.index == w.matchStart)
			{
			var e = createTiddlyElement(w.output,"span");
			e.innerHTML = lookaheadMatch[1];
			w.nextMatch = lookaheadMatch.index + lookaheadMatch[0].length;
			}
	}
},

{
	name: "commentByBlock",
	match: "/%",
	lookahead: "/%((?:.|\\n)*?)%/",
	handler: function(w)
	{
		var lookaheadRegExp = new RegExp(this.lookahead,"mg");
		lookaheadRegExp.lastIndex = w.matchStart;
		var lookaheadMatch = lookaheadRegExp.exec(w.source)
		if(lookaheadMatch && lookaheadMatch.index == w.matchStart)
			w.nextMatch = lookaheadMatch.index + lookaheadMatch[0].length;
	}
},

{
	name: "boldByChar",
	match: "''",
	terminator: "''",
	element: "strong",
	handler: config.formatterHelpers.charFormatHelper
},

{
	name: "strikeByChar",
	match: "==",
	terminator: "==",
	element: "strike",
	handler: config.formatterHelpers.charFormatHelper
},

{
	name: "underlineByChar",
	match: "__",
	terminator: "__",
	element: "u",
	handler: config.formatterHelpers.charFormatHelper
},

{
	name: "italicByChar",
	match: "//",
	terminator: "//",
	element: "em",
	handler: config.formatterHelpers.charFormatHelper
},

{
	name: "subscriptByChar",
	match: "~~",
	terminator: "~~",
	element: "sub",
	handler: config.formatterHelpers.charFormatHelper
},

{
	name: "superscriptByChar",
	match: "\\^\\^",
	terminator: "\\^\\^",
	element: "sup",
	handler: config.formatterHelpers.charFormatHelper
},

{
	name: "monospacedByChar",
	match: "\\{\\{\\{",
	lookahead: "\\{\\{\\{((?:.|\\n)*?)\\}\\}\\}",
	handler: function(w)
	{
		var lookaheadRegExp = new RegExp(this.lookahead,"mg");
		lookaheadRegExp.lastIndex = w.matchStart;
		var lookaheadMatch = lookaheadRegExp.exec(w.source)
		if(lookaheadMatch && lookaheadMatch.index == w.matchStart)
			{
			var e = createTiddlyElement(w.output,"code",null,null,lookaheadMatch[1]);
			w.nextMatch = lookaheadMatch.index + lookaheadMatch[0].length;
			}
	}
},

{
	name: "styleByChar",
	match: "@@",
	terminator: "@@",
	lookahead: "(?:([^\\(@]+)\\(([^\\)]+)(?:\\):))|(?:([^:@]+):([^;]+);)",
	handler:  function(w)
	{
		var e = createTiddlyElement(w.output,"span",null,null,null);
		var styles = config.formatterHelpers.inlineCssHelper(w);
		if(styles.length == 0)
			e.className = "marked";
		else
			for(var t=0; t<styles.length; t++)
				e.style[styles[t].style] = styles[t].value;
		w.subWikify(e,this.terminator);
	}
},

{
	name: "lineBreak",
	match: "\\n",
	handler: function(w)
	{
		createTiddlyElement(w.output,"br");
	}
}
];

// ---------------------------------------------------------------------------------
// Wikifier
// ---------------------------------------------------------------------------------

function wikify(source,output,highlightText,highlightCaseSensitive)
{
	var w = new Wikifier(source,output,config.formatters,highlightText,highlightCaseSensitive);
}

function Wikifier(source,output,formatters,highlightText,highlightCaseSensitive)
{
	this.source = source;
	this.output = output;
	this.nextMatch = 0;
	this.highlightText = highlightText;
	this.highlightCaseSensitive = highlightCaseSensitive;
	this.highlightRegExp = null;
	this.highlightMatch = null;
	this.assembleFormatterMatches(formatters);
	if(highlightText)
		{
		this.highlightRegExp = new RegExp(highlightText,highlightCaseSensitive ? "mg" : "img");
		this.highlightMatch = this.highlightRegExp.exec(this.source);
		}
	if(source && source != "")
		this.subWikify(output,null);
	return this;
}

Wikifier.prototype.assembleFormatterMatches = function(formatters)
{
	this.formatters = [];
	var pattern = [];
	for(var n=0; n<formatters.length; n++)
		{
		pattern.push("(" + formatters[n].match + ")");
		this.formatters.push(formatters[n]);
		}
	this.formatterRegExp = new RegExp(pattern.join("|"),"mg");
}

Wikifier.prototype.subWikify = function(output,terminator)
{
	// Temporarily replace the output pointer
	var oldOutput = this.output;
	this.output = output;
	// Prepare the terminator RegExp
	var terminatorRegExp = terminator ? new RegExp("(" + terminator + ")","mg") : null;
	do {
		// Prepare the RegExp match positions
		this.formatterRegExp.lastIndex = this.nextMatch;
		if(terminatorRegExp)
			terminatorRegExp.lastIndex = this.nextMatch;
		// Get the first matches
		var formatterMatch = this.formatterRegExp.exec(this.source);
		var terminatorMatch = terminatorRegExp ? terminatorRegExp.exec(this.source) : null;
		// Check for a terminator match
		if(terminatorMatch && (!formatterMatch || terminatorMatch.index <= formatterMatch.index))
			{
			// Output any text before the match
			if(terminatorMatch.index > this.nextMatch)
				this.outputText(this.output,this.nextMatch,terminatorMatch.index);
			// Set the match parameters
			this.matchStart = terminatorMatch.index;
			this.matchLength = terminatorMatch[1].length;
			this.matchText = terminatorMatch[1];
			this.nextMatch = terminatorMatch.index + terminatorMatch[1].length;
			// Restore the output pointer and exit
			this.output = oldOutput;
			return;		
			}
		// Check for a formatter match
		else if(formatterMatch)
			{
			// Output any text before the match
			if(formatterMatch.index > this.nextMatch)
				this.outputText(this.output,this.nextMatch,formatterMatch.index);
			// Set the match parameters
			this.matchStart = formatterMatch.index;
			this.matchLength = formatterMatch[0].length;
			this.matchText = formatterMatch[0];
			this.nextMatch = this.formatterRegExp.lastIndex;
			// Figure out which formatter matched
			var matchingformatter = -1;
			for(var t=1; t<formatterMatch.length; t++)
				if(formatterMatch[t])
					matchingFormatter = t-1;
			// Call the formatter
			if(matchingFormatter != -1)
				this.formatters[matchingFormatter].handler(this);
			}
	} while(terminatorMatch || formatterMatch);
	// Output any text after the last match
	if(this.nextMatch < this.source.length)
		{
		this.outputText(this.output,this.nextMatch,this.source.length);
		this.nextMatch = this.source.length;
		}
	// Restore the output pointer
	this.output = oldOutput;
}

Wikifier.prototype.outputText = function(place,startPos,endPos)
{
	// Check for highlights
	while(this.highlightMatch && (this.highlightRegExp.lastIndex > startPos) && (this.highlightMatch.index < endPos) && (startPos < endPos))
		{
		// Deal with any plain text before the highlight
		if(this.highlightMatch.index > startPos)
			{
			createTiddlyText(place,this.source.substring(startPos,this.highlightMatch.index));
			startPos = this.highlightMatch.index;
			}
		// Deal with the highlight
		var highlightEnd = Math.min(this.highlightRegExp.lastIndex,endPos);
		var theHighlight = createTiddlyElement(place,"span",null,"highlight",this.source.substring(startPos,highlightEnd));
		startPos = highlightEnd;
		// Nudge along to the next highlight if we're done with this one
		if(startPos >= this.highlightRegExp.lastIndex)
			this.highlightMatch = this.highlightRegExp.exec(this.source);
		}
	// Do the unhighlighted text left over
	if(startPos < endPos)
		{
		createTiddlyText(place,this.source.substring(startPos,endPos));
		}
}

// ---------------------------------------------------------------------------------
// Macro definitions
// ---------------------------------------------------------------------------------

config.macros.today.handler = function(place,macroName,params)
{
	var now = new Date();
	var text;
	if(params[0])
		text = now.formatString(params[0].trim());
	else
		text = now.toLocaleString();
	createTiddlyElement(place,"span",null,null,text);
}

config.macros.version.handler = function(place)
{
	createTiddlyElement(place,"span",null,null,version.major + "." + version.minor + "." + version.revision + (version.beta ? " (beta " + version.beta + ")" : ""));
}

config.macros.list.handler = function(place,macroName,params)
{
	var type = params[0] ? params[0] : "all";
	var theList = document.createElement("ul");
	place.appendChild(theList);
	if(this[type].prompt)
		createTiddlyElement(theList,"li",null,"listTitle",this[type].prompt);
	var results;
	if(this[type].handler)
		results = this[type].handler(params);
	for (t = 0; t < results.length; t++)
		{
		theListItem = document.createElement("li")
		theList.appendChild(theListItem);
		if(typeof results[t] == "string")
			createTiddlyLink(theListItem,results[t],true);
		else
			createTiddlyLink(theListItem,results[t].title,true);
		}
}

config.macros.list.all.handler = function(params)
{
	return store.reverseLookup("tags","excludeLists",false,"title");
}

config.macros.list.missing.handler = function(params)
{
	return store.getMissingLinks();
}

config.macros.list.orphans.handler = function(params)
{
	return store.getOrphans();
}

config.macros.list.shadowed.handler = function(params)
{
	return store.getShadowed();
}

config.macros.allTags.handler = function(place,macroName,params)
{
	var tags = store.getTags();
	var theDateList = createTiddlyElement(place,"ul",null,null,null);
	if(tags.length == 0)
		createTiddlyElement(theDateList,"li",null,"listTitle",this.noTags);
	for (t=0; t<tags.length; t++)
		{
		var theListItem =createTiddlyElement(theDateList,"li",null,null,null);
		var theTag = createTiddlyButton(theListItem,tags[t][0] + " (" + tags[t][1] + ")",this.tooltip.format([tags[t][0]]),onClickTag);
		theTag.setAttribute("tag",tags[t][0]);
		}
}

config.macros.timeline.handler = function(place,macroName,params)
{
	var tiddlers = store.reverseLookup("tags","excludeLists",false,"modified");
	var lastDay = "";
	for (t=tiddlers.length-1; t>=0; t--)
		{
		var tiddler = tiddlers[t];
		var theDay = tiddler.modified.convertToYYYYMMDDHHMM().substr(0,8);
		if(theDay != lastDay)
			{
			var theDateList = document.createElement("ul");
			place.appendChild(theDateList);
			createTiddlyElement(theDateList,"li",null,"listTitle",tiddler.modified.formatString(this.dateFormat));
			lastDay = theDay;
			}
		var theDateListItem = createTiddlyElement(theDateList,"li",null,"listLink",null);
		theDateListItem.appendChild(createTiddlyLink(place,tiddler.title,true));
		}
}

config.macros.search.handler = function(place,macroName,params)
{
	var lastSearchText = "";
	var searchTimeout = null;
	var doSearch = function(txt)
		{
		closeAllTiddlers();
		var matches = store.search(txt.value,config.options.chkCaseSensitiveSearch,config.options.chkRegExpSearch,"title","excludeSearch");
		for(var t=matches.length-1; t>=0; t--)
			displayTiddler(null,matches[t].title,0,txt.value,config.options.chkCaseSensitiveSearch,false,false);
		var q = config.options.chkRegExpSearch ? "/" : "'";
		if(matches.length > 0)
			displayMessage(config.macros.search.successMsg.format([matches.length.toString(),q + txt.value + q]));
		else
			displayMessage(config.macros.search.failureMsg.format([q + txt.value + q]));
		lastSearchText = txt.value;
		};
	var clickHandler = function(e)
		{
		doSearch(this.nextSibling);
		return false;
		};
	var keyHandler = function(e)
		{
		if (!e) var e = window.event;
		switch(e.keyCode)
			{
			case 27:
				this.value = "";
				clearMessage();
				break;
			}
		if((this.value.length > 2) && (this.value != lastSearchText))
			{
			if(searchTimeout)
				clearTimeout(searchTimeout);
			var txt = this;
			searchTimeout = setTimeout(function() {doSearch(txt);},500);
			}
		};
	var focusHandler = function(e)
		{
		this.select();
		};
	var btn = createTiddlyButton(place,this.label,this.prompt,clickHandler);
	var txt = createTiddlyElement(place,"input",null,null,null);
	if(params[0])
		txt.value = params[0];
	txt.onkeyup = keyHandler;
	txt.onfocus = focusHandler;
	txt.setAttribute("size",this.sizeTextbox);
	txt.setAttribute("accessKey",this.accessKey);
	txt.setAttribute("autocomplete","off");
	if(navigator.userAgent.toLowerCase().indexOf("safari") == -1)
		txt.setAttribute("type","text");
	else
		{
		txt.setAttribute("type","search");
		txt.setAttribute("results","5");
		}
}

config.macros.tiddler.handler = function(place,macroName,params)
{
	var wrapper = createTiddlyElement(place,"span",null,params[1] ? params[1] : null,null);
	var text = store.getTiddlerText(params[0]);
	if(text)
		wikify(text,wrapper);
}

config.macros.tag.handler = function(place,macroName,params)
{
	createTagButton(place,params[0]);
}

config.macros.closeAll.handler = function(place)
{
	createTiddlyButton(place,this.label,this.prompt,function () {closeAllTiddlers(); return false;});
}

config.macros.permaview.handler = function(place)
{
	createTiddlyButton(place,this.label,this.prompt,function () {onClickPermaView(); return false;});
}

config.macros.saveChanges.handler = function(place)
{
	if(!readOnly)
		createTiddlyButton(place,this.label,this.prompt,function () {saveChanges(); return false;},null,null,this.accessKey);
}

config.macros.slider.onClickSlider = function(e)
{
	if (!e) var e = window.event;
	var n = this.nextSibling;
	var cookie = n.getAttribute("cookie");
	var isOpen = n.style.display != "none";
	if(config.options.chkAnimate)
		anim.startAnimating(new Slider(n,!isOpen,e.shiftKey || e.altKey,"none"));
	else
		n.style.display = isOpen ? "none" : "block";
	config.options[cookie] = !isOpen;
	saveOptionCookie(cookie);
	return false;
}

config.macros.slider.handler = function(place,macroName,params)
{
	var cookie = params[0] ? params[0] : "";
	var text = store.getTiddlerText(params[1]);
	var btn = createTiddlyButton(place,params[2],params[3],this.onClickSlider);
	var panel = createTiddlyElement(place,"div",null,"sliderPanel",null);
	panel.setAttribute("cookie",cookie);
	panel.style.display = config.options[cookie] ? "block" : "none";
	if(text)
		wikify(text,panel);
}

config.macros.option.onChangeOption = function(e)
{
	var opt = this.getAttribute("option");
	var elementType,valueField;
	if(opt)
		{
		switch(opt.substr(0,3))
			{
			case "txt":
				elementType = "input";
				valueField = "value";
				break;
			case "chk":
				elementType = "input";
				valueField = "checked";
				break;
			}
		config.options[opt] = this[valueField];

			// PMC
			if (opt == "modifierName")
			{
				createCookie("tiddly_wiki_modifier", this[valueField], 365);
			}
			// end PMC

		saveOptionCookie(opt);
		var nodes = document.getElementsByTagName(elementType);
		for(var t=0; t<nodes.length; t++)
			{
			var optNode = nodes[t].getAttribute("option");
			if(opt == optNode)
				nodes[t][valueField] = this[valueField];
			}
		}
	return(true);
}

config.macros.option.handler = function(place,macroName,params)
{
	var opt = params[0];
	if(config.options[opt] == undefined)
		return;
	var c;
	switch(opt.substr(0,3))
		{
		case "txt":
			c = document.createElement("input");
			c.onkeyup = this.onChangeOption;
			c.setAttribute("option",opt);
			c.size = 15;
			place.appendChild(c);
			c.value = config.options[opt];
			break;
		case "chk":
			c = document.createElement("input");
			c.setAttribute("type","checkbox");
			c.onclick = this.onChangeOption;
			c.setAttribute("option",opt);
			place.appendChild(c);
			c.checked = config.options[opt];
			break;
		}
}

config.macros.newTiddler.onClick = function()
{
	displayTiddler(null,config.macros.newTiddler.title,2,null,null,false,false);
	var e = document.getElementById("editorTitle" + config.macros.newTiddler.title);
	e.focus();
	e.select();
	return false;
}

config.macros.newTiddler.handler = function(place)
{
	if(!readOnly)
		createTiddlyButton(place,this.label,this.prompt,this.onClick,null,null,this.accessKey);
}

config.macros.newJournal.handler = function(place,macroName,params)
{
	if(!readOnly)
		{
		var now = new Date();
		var title = now.formatString(params[0].trim());
		var createJournal = function() {
			displayTiddler(null,title,2,null,null,false,false);
			var tagsBox = document.getElementById("editorTags" + title);
			if(tagsBox && params[1])
				tagsBox.value += " " + String.encodeTiddlyLink(params[1]);
			return false;
			};
		createTiddlyButton(place,this.label,this.prompt,createJournal,null,null,this.accessKey);
		}
}

config.macros.sparkline.handler = function(place,macroName,params)
{
	var data = [];
	var min = 0;
	var max = 0;
	for(var t=0; t<params.length; t++)
		{
		var v = parseInt(params[t]);
		if(v < min)
			min = v;
		if(v > max)
			max = v;
		data.push(v);
		}
	if(data.length < 1)
		return;
	var box = createTiddlyElement(place,"span",null,"sparkline",String.fromCharCode(160));
	box.title = data.join(",");
	var w = box.offsetWidth;
	var h = box.offsetHeight;
	box.style.paddingRight = (data.length * 2 - w) + "px";
	box.style.position = "relative";
	for(var d=0; d<data.length; d++)
		{
		var tick = document.createElement("img");
		tick.border = 0;
		tick.className = "sparktick";
		tick.style.position = "absolute";
		tick.src = "data:image/gif,GIF89a%01%00%01%00%91%FF%00%FF%FF%FF%00%00%00%C0%C0%C0%00%00%00!%F9%04%01%00%00%02%00%2C%00%00%00%00%01%00%01%00%40%02%02T%01%00%3B";
		tick.style.left = d*2 + "px";
		tick.style.width = "2px";
		var v = Math.floor(((data[d] - min)/(max-min)) * h);
		tick.style.top = (h-v) + "px";
		tick.style.height = v + "px";
		box.appendChild(tick);
		}
}

config.macros.tabs.handler = function(place,macroName,params)
{
	var cookie = params[0];
	var numTabs = (params.length-1)/3;
	var wrapper = createTiddlyElement(place,"div",null,cookie,null);
	var tabset = createTiddlyElement(wrapper,"div",null,"tabset",null);
	tabset.setAttribute("cookie",cookie);
	var validTab = false;
	for(var t=0; t<numTabs; t++)
		{
		var label = params[t*3+1];
		var prompt = params[t*3+2];
		var content = params[t*3+3];
		var tab = createTiddlyButton(tabset,label,prompt,this.onClickTab,"tab tabUnselected");
		tab.setAttribute("tab",label);
		tab.setAttribute("content",content);
		tab.title = prompt;
		if(config.options[cookie] == label)
			validTab = true;
		}
	if(!validTab)
		config.options[cookie] = params[1];
	this.switchTab(tabset,config.options[cookie]);
}

config.macros.tabs.onClickTab = function(e)
{
	config.macros.tabs.switchTab(this.parentNode,this.getAttribute("tab"));
	return false;
}

config.macros.tabs.switchTab = function(tabset,tab)
{
	var cookie = tabset.getAttribute("cookie");
	var theTab = null
	var nodes = tabset.childNodes;
	for(var t=0; t<nodes.length; t++)
		if(nodes[t].getAttribute && nodes[t].getAttribute("tab") == tab)
			{
			theTab = nodes[t];
			theTab.className = "tab tabSelected";
			}
		else
			nodes[t].className = "tab tabUnselected"
	if(theTab)
		{
		if(tabset.nextSibling && tabset.nextSibling.className == "tabContents")
			tabset.parentNode.removeChild(tabset.nextSibling);
		var tabContent = createTiddlyElement(null,"div",null,"tabContents",null);
		tabset.parentNode.insertBefore(tabContent,tabset.nextSibling);
		wikify(store.getTiddlerText(theTab.getAttribute("content")),tabContent);
		if(cookie)
			{
			config.options[cookie] = tab;
			saveOptionCookie(cookie);
			}
		}
}

// <<gradient [[tiddler name]] vert|horiz rgb rgb rgb rgb... >>
config.macros.gradient.handler = function(place,macroName,params,wikifier)
{
	var terminator = ">>";
	var panel = createTiddlyElement(place,"div",null,"gradient",null);
	panel.style.position = "relative";
	panel.style.overflow = "hidden";
	var styles = config.formatterHelpers.inlineCssHelper(wikifier);
	var t;
	for(t=0; t<styles.length; t++)
		panel.style[styles[t].style] = styles[t].value;
	var colours = [];
	for(t=1; t<params.length; t++)
		{
		var c = new RGB(params[t]);
		if(c)
			colours.push(c);
		}
	drawGradient(panel,params[0] != "vert",colours);
	wikifier.subWikify(panel,terminator);
	if(document.all)
		{
		panel.style.height = "100%";
		panel.style.width = "100%";
		}
}

// ---------------------------------------------------------------------------------
// Tiddler() object
// ---------------------------------------------------------------------------------

function Tiddler()
{
	this.title = null;
	this.text = null;
	this.modifier = null;
	this.modified = new Date();
	this.links = [];
	this.tags = [];
	return this;
}

// Load a tiddler from an HTML DIV
Tiddler.prototype.loadFromDiv = function(divRef,title)
{
	var text = Tiddler.unescapeLineBreaks(divRef.firstChild ? divRef.firstChild.nodeValue : "");
	var modifier = divRef.getAttribute("modifier");
	var modified = Date.convertFromYYYYMMDDHHMM(divRef.getAttribute("modified"));
	var tags = divRef.getAttribute("tags");
	this.set(title,text,modifier,modified,tags);
	return this;
}

// Format the text for storage in an HTML DIV
Tiddler.prototype.saveToDiv = function()
{
	return '<div tiddler="' + this.title + '" modified="' +
							this.modified.convertToYYYYMMDDHHMM() + '" modifier="' + this.modifier +
							'" tags="' + this.getTags() + '">' +
							this.escapeLineBreaks().htmlEncode() + '</div>';
}

// Format the text for storage in an RSS item
Tiddler.prototype.saveToRss = function(url)
{
	var s = [];
	s.push("<item>");
	s.push("<title>" + this.title.htmlEncode() + "</title>");
	s.push("<description>" + this.text.replace(regexpNewLine,"<br />").htmlEncode() + "</description>");
	for(var t=0; t<this.tags.length; t++)
		s.push("<category>" + this.tags[t] + "</category>");
	s.push("<link>" + url + "#" + encodeURIComponent(String.encodeTiddlyLink(this.title)) + "</link>");
	s.push("<pubDate>" + this.modified.toGMTString() + "</pubDate>");
	s.push("</item>");
	return(s.join("\n"));
}

// Change the text and other attributes of a tiddler
Tiddler.prototype.set = function(title,text,modifier,modified,tags)
{
	if(title != undefined)
		this.title = title;
	if(text != undefined)
		this.text = text;
	if(modifier != undefined)
		this.modifier = modifier;
	if(modified != undefined)
		this.modified = modified;
	if(tags != undefined)
		this.tags = (typeof tags == "string") ? tags.readBracketedList() : tags;
	else
		this.tags = [];
	this.changed();
	return this;
}

// Get the tags for a tiddler as a string (space delimited, using [[brackets]] for tags containing spaces)
Tiddler.prototype.getTags = function()
{
	if(this.tags)
		{
		var results = [];
		for(var t=0; t<this.tags.length; t++)
			results.push(String.encodeTiddlyLink(this.tags[t]));
		return results.join(" ");
		}
	else
		return "";
}

var regexpBackSlashEn = new RegExp("\\\\n","mg");
var regexpBackSlash = new RegExp("\\\\","mg");
var regexpBackSlashEss = new RegExp("\\\\s","mg");
var regexpNewLine = new RegExp("\n","mg");
var regexpCarriageReturn = new RegExp("\r","mg");

// Static method to Convert "\n" to newlines, "\s" to "\"
Tiddler.unescapeLineBreaks = function(text)
{
	if(text && text != "")
		return text.replace(regexpBackSlashEn,"\n").replace(regexpBackSlashEss,"\\").replace(regexpCarriageReturn,"");
	else
		return "";
}

// Convert newlines to "\n", "\" to "\s"
Tiddler.prototype.escapeLineBreaks = function()
{
	return this.text.replace(regexpBackSlash,"\\s").replace(regexpNewLine,"\\n").replace(regexpCarriageReturn,"");
}

// Updates the secondary information (like links[] array) after a change to a tiddler
Tiddler.prototype.changed = function()
{
	this.links = [];
	var nextPos = 0;
	var theLink;
	var aliasedPrettyLink = "\\[\\[([^\\[\\]\\|]+)\\|([^\\[\\]\\|]+)\\]\\]";
	var prettyLink = "\\[\\[([^\\]]+)\\]\\]";
	var wikiNameRegExp = new RegExp("(" + config.textPrimitives.wikiLink + ")|(?:" + aliasedPrettyLink + ")|(?:" + prettyLink + ")","mg");
	do {
		var formatMatch = wikiNameRegExp.exec(this.text);
		if(formatMatch)
			{
			if(formatMatch[1] && formatMatch[1].substr(0,1) != config.textPrimitives.unWikiLink && formatMatch[1] != this.title)
				this.links.pushUnique(formatMatch[1]);
			else if(formatMatch[2] && store.tiddlerExists(formatMatch[3]))
				this.links.pushUnique(formatMatch[3]);
			else if(formatMatch[4] && formatMatch[4] != this.title)
				this.links.pushUnique(formatMatch[4]);
			}
	} while(formatMatch);
	return;
}

Tiddler.prototype.getSubtitle = function()
{
	var theModifier = this.modifier;
	if(!theModifier)
		theModifier = config.messages.subtitleUnknown;
	var theModified = this.modified;
	if(theModified)
		theModified = theModified.toLocaleString();
	else
		theModified = config.messages.subtitleUnknown;
	return(theModifier + ", " + theModified);
}

// ---------------------------------------------------------------------------------
// TiddlyWiki() object contains Tiddler()s
// ---------------------------------------------------------------------------------

function TiddlyWiki()
{
	this.tiddlers = {}; // Hashmap by name of tiddlers
	this.namedNotifications = []; // Array of {name:,notify:} of notification functions
	this.dirty = false;
}

// Set the dirty flag
TiddlyWiki.prototype.setDirty = function(dirty)
{
	this.dirty = dirty;
}

// Invoke the notification handlers for a particular tiddler
TiddlyWiki.prototype.notify = function(title,doBlanket)
{
	for(var t=0; t<this.namedNotifications.length; t++)
		{
		var n = this.namedNotifications[t];
		if((n.name == null && doBlanket) || n.name == title)
			n.notify(title);
		}
}

// Invoke the notification handlers for all tiddlers
TiddlyWiki.prototype.notifyAll = function()
{
	for(var t=0; t<this.namedNotifications.length; t++)
		{
		var n = this.namedNotifications[t];
		n.notify(n.name);
		}
}

// Add a notification handler to a tiddler
TiddlyWiki.prototype.addNotification = function(title,fn)
{
	for (var i=0; i<this.namedNotifications.length; i++)
		if((this.namedNotifications[i].name == title) && (this.namedNotifications[i].notify == fn))
			return this;
	this.namedNotifications.push({name: title, notify: fn});
	return this;
}

// Clear a TiddlyWiki so that it contains no tiddlers
TiddlyWiki.prototype.clear = function(src)
{
	this.tiddlers = {};
	this.dirty = false;
}

TiddlyWiki.prototype.removeTiddler = function(title)
{
	var tiddler = this.tiddlers[title];
	if(tiddler)
		{
		delete this.tiddlers[title];
		this.notify(title,true);
		this.dirty = true;
		}
}

TiddlyWiki.prototype.tiddlerExists = function(title)
{
	var t = this.tiddlers[title];
	var s = config.shadowTiddlers[title];
	return (t != undefined && t instanceof Tiddler) || (s != undefined && s instanceof String);
}

TiddlyWiki.prototype.getTiddlerText = function(title,defaultText)
{
	if(!title)
		return(defaultText);
	var tiddler = this.tiddlers[title];
	if(tiddler)
		return tiddler.text;
	else if(config.shadowTiddlers[title])
		return config.shadowTiddlers[title];
	else if(defaultText)
		return defaultText;
	else
		return null;
}

TiddlyWiki.prototype.getRecursiveTiddlerText = function(title,defaultText,depth)
{
	var bracketRegExp = new RegExp("(?:\\[\\[([^\\]]+)\\]\\])","mg");
	var text = this.getTiddlerText(title,defaultText);
	if(text == null)
		return "";
	var textOut = [];
	var lastPos = 0;
	do {
		var match = bracketRegExp.exec(text);
		if(match)
			{
			textOut.push(text.substr(lastPos,match.index-lastPos));
			if(match[1])
				{
				if(depth <= 0)
					textOut.push(match[1]);
				else
					textOut.push(this.getRecursiveTiddlerText(match[1],match[1],depth-1));
				}
			lastPos = match.index + match[0].length;
			}
		else
			textOut.push(text.substr(lastPos));
	} while(match);
	return(textOut.join(""));
}

TiddlyWiki.prototype.saveTiddler = function(title,newTitle,newBody,modifier,modified,tags)
{
	var tiddler = this.tiddlers[title];
	if(tiddler)
		delete this.tiddlers[title];
	else
		tiddler = new Tiddler();
	tiddler.set(newTitle,newBody,modifier,modified,tags);
	this.tiddlers[newTitle] = tiddler;
	if(title != newTitle)
		this.notify(title,true);
	this.notify(newTitle,true);
	this.dirty = true;
	return tiddler;
}

TiddlyWiki.prototype.createTiddler = function(title)
{
	tiddler = this.tiddlers[title];
	if(!tiddler)
		{
		tiddler = new Tiddler();
		this.tiddlers[title] = tiddler;
		this.dirty = true;
		}
	return tiddler;
}

// Load contents of a tiddlywiki from an HTML DIV
TiddlyWiki.prototype.loadFromDiv = function(srcID,idPrefix)
{
	if(document.normalize)
		document.normalize();
	var lenPrefix = idPrefix.length;
	var store = document.getElementById(srcID).childNodes;
	for(var t = 0; t < store.length; t++)
		{
		var e = store[t];
		var title = null;
		if(e.getAttribute)
			title = e.getAttribute("tiddler");
		if(!title && e.id && e.id.substr(0,lenPrefix) == idPrefix)
			title = e.id.substr(lenPrefix);
		if(title && title != "")
			{
			var tiddler = this.createTiddler(title);
			tiddler.loadFromDiv(e,title);
			}
		}
	this.dirty = false;
}

// Return an array of tiddlers matching a search string
TiddlyWiki.prototype.search = function(searchText,caseSensitive,useRegExp,sortField,excludeTag)
{
	if (!useRegExp)
		searchText = searchText.escapeRegExp();
	var regExp = new RegExp(searchText,caseSensitive ? "m" : "im");
	var candidates = this.reverseLookup("tags",excludeTag,false);
	var results = [];
	for(var t=0; t<candidates.length; t++)
		{
		if(regExp.test(candidates[t].title) || regExp.test(candidates[t].text))
			results.push(candidates[t]);
		}
	if(!sortField)
		sortField = "title";
	results.sort(function (a,b) {if(a[sortField] == b[sortField]) return(0); else return (a[sortField] < b[sortField]) ? -1 : +1; });
	return results;
}

// Return an array of all the tags in use. Each member of the array is another array where [0] is the name of the tag and [1] is the number of occurances
TiddlyWiki.prototype.getTags = function()
{
	var results = [];
	for(var t in this.tiddlers)
		{
		var tiddler = this.tiddlers[t];
		for(g=0; g<tiddler.tags.length; g++)
			{
			var tag = tiddler.tags[g];
			var f = false;
			for(var c=0; c<results.length; c++)
				if(results[c][0] == tag)
					{
					f = true;
					results[c][1]++;
					}
			if(!f)
				results.push([tag,1]);
			}
		}
	results.sort(function (a,b) {if(a[0].toLowerCase() == b[0].toLowerCase()) return(0); else return (a[0].toLowerCase() < b[0].toLowerCase()) ? -1 : +1; });
	return results;
}

// Return an array of the tiddlers that are tagged with a given tag
TiddlyWiki.prototype.getTaggedTiddlers = function(tag,sortField)
{
	return this.reverseLookup("tags",tag,true,sortField);
}

// Return an array of the tiddlers that link to a given tiddler
TiddlyWiki.prototype.getReferringTiddlers = function(title,unusedParameter,sortField)
{
	return this.reverseLookup("links",title,true,sortField);
}

// Return an array of the tiddlers that do or do not have a specified entry in the specified storage array (ie, "links" or "tags")
// lookupMatch == true to match tiddlers, false to exclude tiddlers
TiddlyWiki.prototype.reverseLookup = function(lookupField,lookupValue,lookupMatch,sortField)
{
	var results = [];
	for(var t in this.tiddlers)
		{
		var tiddler = this.tiddlers[t];
		var f = !lookupMatch;
		for(var lookup=0; lookup<tiddler[lookupField].length; lookup++)
			if(tiddler[lookupField][lookup] == lookupValue)
				f = lookupMatch;
		if(f)
			results.push(tiddler);
		}
	if(!sortField)
		sortField = "title";
	results.sort(function (a,b) {if(a[sortField] == b[sortField]) return(0); else return (a[sortField] < b[sortField]) ? -1 : +1; });
	return results;
}

// Return the tiddlers as a sorted array
TiddlyWiki.prototype.getTiddlers = function(field)
{
	var results = [];
	for(var t in this.tiddlers)
		results.push(this.tiddlers[t]);
	if(field)
		results.sort(function (a,b) {if(a[field] == b[field]) return(0); else return (a[field] < b[field]) ? -1 : +1; });
	return results;
}

// Return array of names of tiddlers that are referred to but not defined
TiddlyWiki.prototype.getMissingLinks = function(sortField)
{
	var results = [];
	for(var t in this.tiddlers)
		{
		var tiddler = this.tiddlers[t];
		for(var n=0; n<tiddler.links.length;n++)
			{
			var link = tiddler.links[n];
			if(this.tiddlers[link] == null)
				results.pushUnique(link);
			}
		}
	results.sort();
	return results;
}

// Return an array of names of tiddlers that are defined but not referred to
TiddlyWiki.prototype.getOrphans = function()
{
	var results = [];
	for(var t in this.tiddlers)
		if(this.getReferringTiddlers(t).length == 0)
			results.push(t);
	results.sort();
	return results;
}

// Return an array of names of tiddlers that are defined but not referred to
TiddlyWiki.prototype.getShadowed = function()
{
	var results = [];
	for(var t in config.shadowTiddlers)
		if(typeof config.shadowTiddlers[t] == "string")
			results.push(t);
	results.sort();
	return results;
}

// ---------------------------------------------------------------------------------
// Tiddler functions
// ---------------------------------------------------------------------------------

// Display several tiddlers from a list of space separated titles
function displayTiddlers(src,titles,state,highlightText,highlightCaseSensitive,animate,slowly)
{
	var tiddlerNames = titles.readBracketedList();
	for(var t = tiddlerNames.length-1;t>=0;t--)
		displayTiddler(src,tiddlerNames[t],state,highlightText,highlightCaseSensitive,animate,slowly);
}

// Display a tiddler with animation and scrolling, as though a link to it has been clicked on
//	src = source element object (eg link) for animation effects and positioning
//	title = title of tiddler to display
//	state = 0 is default or current state, 1 is read only and 2 is edittable
//	highlightText = text to highlight in the displayed tiddler
//	highlightCaseSensitive = flag for whether the highlight text is case sensitive
function displayTiddler(src,title,state,highlightText,highlightCaseSensitive,animate,slowly)
{
	var place = document.getElementById("tiddlerDisplay");
	var after = findContainingTiddler(src); // Which tiddler this one will be positioned after
	var before;
	if(after == null)
		before = place.firstChild;
	else if(after.nextSibling)
		before = after.nextSibling;
	else
		before = null;
	var theTiddler = createTiddler(place,before,title,state,highlightText,highlightCaseSensitive);
	if(src)
		{
		if(config.options.chkAnimate && (animate == undefined || animate == true))
			anim.startAnimating(new Zoomer(title,src,theTiddler,slowly),new Scroller(theTiddler,slowly));
		else
			window.scrollTo(0,ensureVisible(theTiddler));
		}
}

// PMC
function newTiddler()
{
    var place = document.getElementById("tiddlerDisplay");
	createTiddler(place,null,"CreateNewTiddler",2);
}
// end PMC

// Create a tiddler if it doesn't exist (with no fancy animating)
//	place = parent element
//	before = node before which to create/move the tiddler
//	title = title of tiddler to display
//	state = 0 is default or current state, 1 is read only and 2 is edittable
//	highlightText = text to highlight in the displayed tiddler
//	highlightCaseSensitive = flag for whether the highlight text is case sensitive
function createTiddler(place,before,title,state,highlightText,highlightCaseSensitive)
{
	var theTiddler = createTiddlerSkeleton(place,before,title);
	createTiddlerTitle(title,highlightText,highlightCaseSensitive);
	var theViewer = document.getElementById("viewer" + title);
	var theEditor = document.getElementById("editorWrapper" + title);
	switch(state)
		{
		case 0:
			if(!theViewer && !theEditor)
				{
				createTiddlerToolbar(title,false);
				createTiddlerViewer(title,highlightText,highlightCaseSensitive);
				createTiddlerFooter(title,false);
				}
			break;
		case 1: // Viewer
			if(theViewer)
				theViewer.parentNode.removeChild(theViewer);
			if(theEditor)
				theEditor.parentNode.removeChild(theEditor);
			createTiddlerToolbar(title,false);
			createTiddlerViewer(title,highlightText,highlightCaseSensitive);
			createTiddlerFooter(title,false);
			break;
		case 2: // Editor
			if(!theEditor)
				{
				if(theViewer)
					theViewer.parentNode.removeChild(theViewer);
				createTiddlerToolbar(title,true);
				createTiddlerEditor(title);
				createTiddlerFooter(title,true);
				}
			break;
		}
	return(theTiddler);
}

function refreshTiddler(title)
{
	var theViewer = document.getElementById("viewer" + title);
	if(theViewer)
		{
		theViewer.parentNode.removeChild(theViewer);
		createTiddlerViewer(title,null,null);
		}
}

function createTiddlerSkeleton(place,before,title)
{
	var theTiddler = document.getElementById("tiddler" + title);
	if(!theTiddler)
		{
		theTiddler = createTiddlyElement(null,"div","tiddler" + title,"tiddler",null);
		theTiddler.onmouseover = onMouseOverTiddler;
		theTiddler.onmouseout = onMouseOutTiddler;
		theTiddler.ondblclick = onDblClickTiddler;
		var theInnerTiddler = createTiddlyElement(theTiddler,"div",null,"unselectedTiddler",null);
		var theToolbar = createTiddlyElement(theInnerTiddler,"div","toolbar" + title,"toolbar", null);
		var theTitle = createTiddlyElement(theInnerTiddler,"div","title" + title,"title",null);
		var theBody = createTiddlyElement(theInnerTiddler,"div","body" + title,"body",null);
		var theFooter = createTiddlyElement(theInnerTiddler,"div","footer" + title,"footer",null);
		place.insertBefore(theTiddler,before);
		}
	if(typeof config.shadowTiddlers[title] == "string")
		addClass(theTiddler,"shadow");
	else
		removeClass(theTiddler,"shadow");
	return(theTiddler);
}

function createTiddlerTitle(title,highlightText,highlightCaseSensitive)
{
	var theTitle = document.getElementById("title" + title);
	if(theTitle)
		{
		removeChildren(theTitle);
		createTiddlyText(theTitle,title);
		if(store.tiddlerExists(title))
			theTitle.title = store.tiddlers[title].getSubtitle();
		}
}

// Create a tiddler toolbar according to whether it's an editor or not
function createTiddlerToolbar(title,isEditor)
{
	var theToolbar = document.getElementById("toolbar" + title);
	var lingo = config.views;
	if(theToolbar)
		{
		removeChildren(theToolbar);
		insertSpacer(theToolbar);
		if(isEditor)
			{
			// Editor toolbar
			lingo = lingo.editor;
			createTiddlyButton(theToolbar,lingo.toolbarDone.text,lingo.toolbarDone.tooltip,onClickToolbarSave);
			insertSpacer(theToolbar);
			createTiddlyButton(theToolbar,lingo.toolbarCancel.text,lingo.toolbarCancel.tooltip,onClickToolbarUndo);
			insertSpacer(theToolbar);
			createTiddlyButton(theToolbar,lingo.toolbarDelete.text,lingo.toolbarDelete.tooltip,onClickToolbarDelete);
			}
		else
			{
			// Viewer toolbar
			lingo = lingo.wikified;
			createTiddlyButton(theToolbar,lingo.toolbarClose.text,lingo.toolbarClose.tooltip,onClickToolbarClose);
			insertSpacer(theToolbar);
			if(!readOnly)
				{
				createTiddlyButton(theToolbar,lingo.toolbarEdit.text,lingo.toolbarEdit.tooltip,onClickToolbarEdit);
				insertSpacer(theToolbar);
				}
			createTiddlyButton(theToolbar,lingo.toolbarPermalink.text,lingo.toolbarPermalink.tooltip,onClickToolbarPermaLink);
			insertSpacer(theToolbar);
			createTiddlyButton(theToolbar,lingo.toolbarReferences.text,lingo.toolbarReferences.tooltip,onClickToolbarReferences);
			}
		insertSpacer(theToolbar);
		}
}

// Create the body section of a read-only tiddler
function createTiddlerViewer(title,highlightText,highlightCaseSensitive)
{
	var theBody = document.getElementById("body" + title);
	if(theBody)
		{
		var tiddlerText = store.getTiddlerText(title);
		var theViewer = createTiddlyElement(theBody,"div","viewer" + title,"viewer",null);
		if(store.tiddlerExists(title))
			theViewer.setAttribute("tags",tiddler.tags.join(" "));
		if(tiddlerText == null)
			{
			tiddlerText = config.views.wikified.defaultText.format([title]);
			theViewer.style.fontStyle = "italic";
			}
		wikify(tiddlerText,theViewer,highlightText,highlightCaseSensitive);
		theViewer.style.overflow = "visible";
		theViewer.style.height = "auto";
		}
}

// Create the footer section of a tiddler
function createTiddlerFooter(title,isEditor)
{
	var theFooter = document.getElementById("footer" + title);
	var tiddler = store.tiddlers[title];
	if(theFooter && tiddler instanceof Tiddler)
		{
		removeChildren(theFooter);
		insertSpacer(theFooter);
		if(isEditor)
			{
			}
		else
			{
			var lingo = config.views.wikified.tag;
			var prompt = tiddler.tags.length == 0 ? lingo.labelNoTags : lingo.labelTags;
			var theTags = createTiddlyElement(theFooter,"div",null,null,prompt);
			for(var t=0; t<tiddler.tags.length; t++)
				{
				var theTag = createTagButton(theTags,tiddler.tags[t],tiddler.title);
				createTiddlyText(theTags," ");
				}
			}
		}
}

// Create a button for a tag with a popup listing all the tiddlers that it tags
function createTagButton(place,tag,excludeTiddler)
{
	var theTag = createTiddlyButton(place,tag,config.views.wikified.tag.tooltip.format([tag]),onClickTag);
	theTag.setAttribute("tag",tag);
	if(excludeTiddler)
		theTag.setAttribute("tiddler",excludeTiddler);
	return(theTag);
}

// Create the body section of an edittable tiddler
function createTiddlerEditor(title)
{
	var theBody = document.getElementById("body" + title);
	if(theBody)
		{
		var tiddlerText = store.getTiddlerText(title);
		var tiddlerExists = (tiddlerText != null);
		if(!tiddlerExists)
			tiddlerText = config.views.editor.defaultText.format([title]);
		var theEditor = createTiddlyElement(theBody,"div","editorWrapper" + title,"editor",null);
		theEditor.onkeypress = onEditKey;
		var theTitleBox = createTiddlyElement(theEditor,"input","editorTitle" + title,null,null);
		theTitleBox.setAttribute("type","text");
		theTitleBox.value = title;
		theTitleBox.setAttribute("size","40");
		theTitleBox.setAttribute("autocomplete","off");
		var theBodyBox = createTiddlyElement(theEditor,"textarea","editorBody" + title,null,null);
		theBodyBox.value = tiddlerText;
		var rows = 10;
		var lines = tiddlerText.match(regexpNewLine);
		if(lines != null && lines.length > rows)
			rows = lines.length + 5;
		theBodyBox.setAttribute("rows",rows);
		var theTagsBox = createTiddlyElement(theEditor,"input","editorTags" + title,null,null);
		theTagsBox.setAttribute("type","text");
		var tiddler = store.tiddlers[title];
		theTagsBox.value = tiddler instanceof Tiddler ? tiddler.getTags() : "";
		theTagsBox.setAttribute("size","40");
		theTagsBox.setAttribute("autocomplete","off");
		var tagPrompt = createTiddlyElement(theEditor,"div",null,"editorFooter",config.views.editor.tagPrompt);
		insertSpacer(tagPrompt);
		var lingo = config.views.editor.tagChooser;
		var addTag = createTiddlyButton(tagPrompt,lingo.text,lingo.tooltip,onClickAddTag);
		addTag.setAttribute("tiddler",title);
		theBodyBox.focus();
		}
}

function saveTiddler(title,minorUpdate)
{
	var titleBox = document.getElementById("editorTitle" + title);
	var newTitle = titleBox.value.trim();
	if(store.tiddlerExists(newTitle))
		{
		if(newTitle != title && !confirm(config.messages.overwriteWarning.format([newTitle.toString()])))
			{
			titleBox.focus();
			titleBox.select();
			return;
			}
		}
	var body = document.getElementById("editorBody" + title);
	var newBody = body.value;
	newBody = newBody.replace(/\r/mg,"");
	var newTags = document.getElementById("editorTags" + title).value;
	blurTiddler(title);
	if(config.options.chkForceMinorUpdate)
		minorUpdate = !minorUpdate;
	var newDate = new Date();
	store.saveTiddler(title,newTitle,newBody,config.options.txtUserName,minorUpdate ? undefined : new Date(),newTags);
	displayTiddler(null,newTitle,1,null,null,null,false,false);
	// Close the old tiddler if this is a rename
	if(title != newTitle)
		{
		var oldTiddler = document.getElementById("tiddler" + title);
		var newTiddler = document.getElementById("tiddler" + newTitle);
		oldTiddler.parentNode.replaceChild(newTiddler,oldTiddler);
		}
	if(config.options.chkAutoSave)
		saveChanges();
		
	// PMC -- now save it to the web!
	document.forms.invisiForm.msg.value = "save";
	document.forms.invisiForm.title.value = newTitle;
	document.forms.invisiForm.body.value = newBody;
	document.forms.invisiForm.modifier.value = modifierName;
	document.forms.invisiForm.theTags.value = newTags;
	document.forms.invisiForm.submit();
	// end PMC
		
}

function selectTiddler(title)
{
	var e = document.getElementById("tiddler" + title);
	if(e != null)
		e.firstChild.className = "selectedTiddler";
}

function deselectTiddler(title)
{
	var e = document.getElementById("tiddler" + title);
	if(e != null)
		e.firstChild.className = "unselectedTiddler";
}

function blurTiddler(title)
{
	var body = document.getElementById("editorBody" + title);
	if(body)
		{
		body.focus();
		body.blur();
		}
}

function deleteTiddler(title)
{
	// PMC -- now delete it from the server!
	var areYouSure = confirm("Are you sure you want to delete this tiddler?");
	if (!areYouSure)
	{
		return false;
	}

	document.forms.invisiForm.msg.value = "delete";
	document.forms.invisiForm.title.value = title;
	document.forms.invisiForm.body.value = "";
	document.forms.invisiForm.modifier.value = modifierName;
	document.forms.invisiForm.submit();
	// end PMC
	
	closeTiddler(title,false);
	store.removeTiddler(title);
	// Autosave
	if(config.options.chkAutoSave)
		saveChanges();
}

function closeTiddler(title,slowly)
{
	var tiddler = document.getElementById("tiddler" + title);
	if(tiddler != null)
		{
		scrubIds(tiddler);
		if(config.options.chkAnimate)
			anim.startAnimating(new Slider(tiddler,false,slowly,"all"));
		else
			tiddler.parentNode.removeChild(tiddler);
		}
}

function scrubIds(e)
{
	if(e.id)
		e.id = null;
	var children = e.childNodes;
	for(var t=0; t<children.length; t++)
		{
		var c = children[t];
		if(c.id)
			c.id = null;
		}
}

function closeAllTiddlers()
{
	clearMessage();
	var place = document.getElementById("tiddlerDisplay");
	var tiddler = place.firstChild;
	var nextTiddler;
	while(tiddler)
		{
		nextTiddler = tiddler.nextSibling;
		if(tiddler.id)
			if(tiddler.id.substr(0,7) == "tiddler")
				{
				var title = tiddler.id.substr(7);
				if(!document.getElementById("editorWrapper" + title))
					place.removeChild(tiddler);
				}
		tiddler = nextTiddler;
		}
}

// ---------------------------------------------------------------------------------
// Message area
// ---------------------------------------------------------------------------------

function displayMessage(text,linkText)
{
	var msgArea = document.getElementById("messageArea");
	var msg;
	if(linkText)
		{
		msg = createTiddlyElement(msgArea,"div",null,null,null);
		var link = createTiddlyElement(msg,"a",null,null,text);
		link.href = linkText;
		link.target = "_blank";
		}
	else
		msg = createTiddlyElement(msgArea,"div",null,null,text);
	msgArea.style.display = "block";
}

function clearMessage()
{
	var msgArea = document.getElementById("messageArea");
	removeChildren(msgArea);
	msgArea.style.display = "none";
}

// ---------------------------------------------------------------------------------
// Menu and sidebar functions
// ---------------------------------------------------------------------------------

function refreshStory(hint)
{
	var hits = hint ? store.getReferringTiddlers(hint) : null;
	var displayNodes = document.getElementById("tiddlerDisplay").childNodes;
	for(var t=0;t<displayNodes.length;t++)
		{
		var theId = displayNodes[t].id;
		if(theId && theId.substr(0,7) == "tiddler")
			{
			var title = theId.substr(7);
			if(hint)
				{
				var f = false;
				for(var h=0; h<hits.length; h++)
					if(hits[h].title == title)
						f = true
				if(f)
					refreshTiddler(title);
				}
			else
				refreshTiddler(title);
			}
		}
}

function refreshTabs(hint)
{
	refreshSpecialItem("sidebarTabs","SideBarTabs","SideBarTabs");
}

function refreshMenu(hint)
{
	refreshSpecialItem("mainMenu","MainMenu","MainMenu");
}

function refreshTitle(title)
{
	refreshSpecialItem("siteTitle",title,"SiteTitle");
	refreshPageTitle();
}

function refreshSubtitle(title)
{
	refreshSpecialItem("siteSubtitle",title,"SiteSubtitle");
	refreshPageTitle();
}

function refreshPageTitle()
{
	document.title = getElementText("siteTitle") + " - " + getElementText("siteSubtitle");
}

function refreshSidebar(title)
{
	refreshSpecialItem("sidebarOptions",title,"SideBarOptions");
}

function refreshSpecialItem(elementID,title,defaultText)
{
	var place = document.getElementById(elementID);
	removeChildren(place);
	wikify(store.getTiddlerText(title,defaultText),place,null,null);
}

function refreshStyles(title)
{
	setStylesheet(title == null ? "" : store.getRecursiveTiddlerText(title,"",10),title);
}

// ---------------------------------------------------------------------------------
// Options cookie stuff
// ---------------------------------------------------------------------------------

function loadOptionsCookie()
{
	var cookies = document.cookie.split(";");
	for(var c=0; c<cookies.length; c++)
		{
		var p = cookies[c].indexOf("=");
		if(p != -1)
			{
			var name = cookies[c].substr(0,p).trim();
			var value = cookies[c].substr(p+1).trim();
			switch(name.substr(0,3))
				{
				case "txt":
					config.options[name] = unescape(value);
					break;
				case "chk":
					config.options[name] = value == "true";
					break;
				}
			}
		}
}

function saveOptionCookie(name)
{
	var c = name + "=";
	switch(name.substr(0,3))
		{
		case "txt":
			c += escape(config.options[name].toString());
			break;
		case "chk":
			c += config.options[name] ? "true" : "false";
			break;
		}
	c += "; expires=Fri, 1 Jan 2038 12:00:00 UTC; path=/";
	document.cookie = c;
}

// ---------------------------------------------------------------------------------
// Saving
// ---------------------------------------------------------------------------------

var saveUsingSafari = false;
var startSaveArea = '<div id="' + 'storeArea">'; // Split up into two so that indexOf() of this source doesn't find it
var endSaveArea = '</d' + 'iv>';

// Check if there any unsaved changes before exitting
function checkUnsavedChanges()
{
	/*** PMC ***
	if(store.dirty)
		{
		if(confirm(config.messages.unsavedChangesWarning))
			saveChanges();
		}
	*** end PMC ***/
}

// Save this tiddlywiki with the pending changes
function saveChanges()
{
	clearMessage();
	// Get the URL of the document
	var originalPath = document.location.toString();
	// Check we were loaded from a file URL
	if(originalPath.substr(0,5) != "file:")
		{
		alert(config.messages.notFileUrlError);
		displayTiddler(null,"SaveChanges",0,null,null,false,false);
		return;
		}
	// Remove any location part of the URL
	var hashPos = originalPath.indexOf("#");
	if(hashPos != -1)
		originalPath = originalPath.substr(0,hashPos);
	// Convert to a native file format assuming
	// "file:///x:/path/path/path..." - pc local file --> "x:\path\path\path..."
	// "file://///server/share/path/path/path..." - FireFox pc network file --> "\\server\share\path\path\path..."
	// "file:///path/path/path..." - mac/unix local file --> "/path/path/path..."
	// "file://server/share/path/path/path..." - pc network file --> "\\server\share\path\path\path..."
	var localPath;
	if(originalPath.charAt(9) == ":") // pc local file
		localPath = unescape(originalPath.substr(8)).replace(new RegExp("/","g"),"\\");
	else if(originalPath.indexOf("file://///") == 0) // FireFox pc network file
		localPath = "\\\\" + unescape(originalPath.substr(10)).replace(new RegExp("/","g"),"\\");
	else if(originalPath.indexOf("file:///") == 0) // mac/unix local file
		localPath = unescape(originalPath.substr(7));
	else if(originalPath.indexOf("file:/") == 0) // mac/unix local file
		localPath = unescape(originalPath.substr(5));
	else // pc network file
		localPath = "\\\\" + unescape(originalPath.substr(7)).replace(new RegExp("/","g"),"\\");
	// Load the original file
	var original = loadFile(localPath);
	if(original == null)
		{
		alert(config.messages.cantSaveError);
		displayTiddler(null,"SaveChanges",0,null,null,false,false);
		return;
		}
	// Locate the storeArea div's
	var posOpeningDiv = original.indexOf(startSaveArea);
	var posClosingDiv = original.lastIndexOf(endSaveArea);
	if((posOpeningDiv == -1) || (posClosingDiv == -1))
		{
		alert(config.messages.invalidFileError.format([localPath]));
		return;
		}
	// Save the backup
	if(config.options.chkSaveBackups)
		{
		var backupPath = localPath.substr(0,localPath.lastIndexOf(".")) + "." + (new Date()).convertToYYYYMMDDHHMMSSMMM() + ".html";
		var backup = saveFile(backupPath,original);
		if(backup)
			displayMessage(config.messages.backupSaved,"file://" + backupPath);
		else
			alert(config.messages.backupFailed);
		}
	// Save Rss
	if(config.options.chkGenerateAnRssFeed)
		{
		var rssPath = localPath.substr(0,localPath.lastIndexOf(".")) + ".xml";
		var rssSave = saveFile(rssPath,convertUnicodeToUTF8(generateRss()));
		if(rssSave)
			displayMessage(config.messages.rssSaved,"file://" + rssPath);
		else
			alert(config.messages.rssFailed);
		}
	// Save empty template
	if(config.options.chkSaveEmptyTemplate)
		{
		var emptyPath,p;
		if((p = localPath.lastIndexOf("/")) != -1)
			emptyPath = localPath.substr(0,p) + "/empty.html";
		else if((p = localPath.lastIndexOf("\\")) != -1)
			emptyPath = localPath.substr(0,p) + "\\empty.html";
		else
			emptyPath = localPath + ".empty.html";
		var empty = original.substr(0,posOpeningDiv + startSaveArea.length) + convertUnicodeToUTF8(generateEmpty()) + original.substr(posClosingDiv);
		var emptySave = saveFile(emptyPath,empty);
		if(emptySave)
			displayMessage(config.messages.emptySaved,"file://" + emptyPath);
		else
			alert(config.messages.emptyFailed);
		}
	// Save new file
	var revised = original.substr(0,posOpeningDiv + startSaveArea.length) + 
				convertUnicodeToUTF8(allTiddlersAsHtml()) + "\n\t\t" +
				original.substr(posClosingDiv);
	var newSiteTitle = convertUnicodeToUTF8((getElementText("siteTitle") + " - " + getElementText("siteSubtitle")).htmlEncode());
	revised = revised.replace(new RegExp("<title>[^<]*</title>", "im"),"<title>"+ newSiteTitle +"</title>");
	var save = saveFile(localPath,revised);
	if(save)
		{
		displayMessage(config.messages.mainSaved,"file://" + localPath);
		store.setDirty(false);
		}
	else
		alert(config.messages.mainFailed);
}

function generateRss()
{
	var s = [];
	var d = new Date();
	var u = store.getTiddlerText("SiteUrl",null);
	// Assemble the header
	s.push("<" + "?xml version=\"1.0\"?" + ">");
	s.push("<rss version=\"2.0\">");
	s.push("<channel>");
	s.push("<title>" + store.getTiddlerText("SiteTitle","").htmlEncode() + "</title>");
	if(u)
		s.push("<link>" + u.htmlEncode() + "</link>");
	s.push("<description>" + store.getTiddlerText("SiteSubtitle","").htmlEncode() + "</description>");
	s.push("<language>en-us</language>");
	s.push("<copyright>Copyright " + d.getFullYear() + " " + config.options.txtUserName.htmlEncode() + "</copyright>");
	s.push("<pubDate>" + d.toGMTString() + "</pubDate>");
	s.push("<lastBuildDate>" + d.toGMTString() + "</lastBuildDate>");
	s.push("<docs>http://blogs.law.harvard.edu/tech/rss</docs>");
	s.push("<generator>TiddlyWiki " + version.major + "." + version.minor + "." + version.revision + "</generator>");
	// The body
	var tiddlers = store.getTiddlers("modified");
	var n = config.numRssItems > tiddlers.length ? 0 : tiddlers.length-config.numRssItems;
	for (var t=tiddlers.length-1; t>=n; t--)
		s.push(tiddlers[t].saveToRss(u));
	// And footer
	s.push("</channel>");
	s.push("</rss>");
	// Save it all
	return s.join("\n");
}

function generateEmpty()
{
	var systemTiddlers = store.getTaggedTiddlers("systemTiddlers");
	var savedTiddlers = [];
	for(var s=0;s<systemTiddlers.length;s++)
		savedTiddlers.push(systemTiddlers[s].saveToDiv());
	return savedTiddlers.join("\n");
}

function allTiddlersAsHtml()
{
	var savedTiddlers = [];
	var tiddlers = store.getTiddlers("title");
	for (var t = 0; t < tiddlers.length; t++)
		savedTiddlers.push(tiddlers[t].saveToDiv());
	return savedTiddlers.join("\n");
}

// UTF-8 encoding rules:
// 0x0000 - 0x007F:	0xxxxxxx
// 0x0080 - 0x07FF:	110xxxxx 10xxxxxx
// 0x0800 - 0xFFFF:	1110xxxx 10xxxxxx 10xxxxxx

function convertUTF8ToUnicode(u)
{
	var s = "";
	var t = 0;
	var b1, b2, b3;
	while(t < u.length)
		{
		b1 = u.charCodeAt(t++);
		if(b1 < 0x80)
			s += String.fromCharCode(b1);
		else if(b1 < 0xE0)
			{
			b2 = u.charCodeAt(t++);
			s += String.fromCharCode(((b1 & 0x1F) << 6) | (b2 & 0x3F));
			}
		else
			{
			b2 = u.charCodeAt(t++);
			b3 = u.charCodeAt(t++);
			s += String.fromCharCode(((b1 & 0xF) << 12) | ((b2 & 0x3F) << 6) | (b3 & 0x3F));
			}
	}
	return(s);
}

function convertUnicodeToUTF8(s)
{
	if(saveUsingSafari)
		return s;
	else if(window.Components)
		return mozConvertUnicodeToUTF8(s);
	else
		return manualConvertUnicodeToUTF8(s);
}

function manualConvertUnicodeToUTF8(s)
{
	var re = /[^\u0000-\u007F]/g ;
	return s.replace(re, function($0) {return("&#" + $0.charCodeAt(0).toString() + ";");})
}

function mozConvertUnicodeToUTF8(s)
{
	netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
	var converter = Components.classes["@mozilla.org/intl/scriptableunicodeconverter"].createInstance(Components.interfaces.nsIScriptableUnicodeConverter);
	converter.charset = "UTF-8";
	var u = converter.ConvertFromUnicode(s);
	var fin = converter.Finish();
	if(fin.length > 0)
		return u + fin;
	else
		return u;
}

function saveFile(fileUrl, content)
{
	var r = null;
	if(saveUsingSafari)
		r = safariSaveFile(fileUrl, content);
	if((r == null) || (r == false))
		r = mozillaSaveFile(fileUrl, content);
	if((r == null) || (r == false))
		r = ieSaveFile(fileUrl, content);
	if((r == null) || (r == false))
		r = operaSaveFile(fileUrl, content);
	return(r);
}

function loadFile(fileUrl)
{
	var r = null;
	if(saveUsingSafari)
		r = safariLoadFile(fileUrl);
	if((r == null) || (r == false))
		r = mozillaLoadFile(fileUrl);
	if((r == null) || (r == false))
		r = ieLoadFile(fileUrl);
	if((r == null) || (r == false))
		r = operaLoadFile(fileUrl);
	return(r);
}

// Returns null if it can't do it, false if there's an error, true if it saved OK
function ieSaveFile(filePath, content)
{
	try
		{
		var fso = new ActiveXObject("Scripting.FileSystemObject");
		}
	catch(e)
		{
		//alert("Exception while attempting to save\n\n" + e.toString());
		return(null);
		}
	var file = fso.OpenTextFile(filePath,2,-1,0);
	file.Write(content);
	file.Close();
	return(true);
}

// Returns null if it can't do it, false if there's an error, or a string of the content if successful
function ieLoadFile(filePath)
{
	try
		{
		var fso = new ActiveXObject("Scripting.FileSystemObject");
		var file = fso.OpenTextFile(filePath,1);
		var content = file.ReadAll();
		file.Close();
		}
	catch(e)
		{
		//alert("Exception while attempting to load\n\n" + e.toString());
		return(null);
		}
	return(content);
}

// Returns null if it can't do it, false if there's an error, true if it saved OK
function mozillaSaveFile(filePath, content)
{
	if(window.Components)
		try
			{
			netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
			var file = Components.classes["@mozilla.org/file/local;1"].createInstance(Components.interfaces.nsILocalFile);
			file.initWithPath(filePath);
			if (!file.exists())
				file.create(0, 0664);
			var out = Components.classes["@mozilla.org/network/file-output-stream;1"].createInstance(Components.interfaces.nsIFileOutputStream);
			out.init(file, 0x20 | 0x02, 00004,null);
			out.write(content, content.length);
			out.flush();
			out.close();
			return(true);
			}
		catch(e)
			{
			//alert("Exception while attempting to save\n\n" + e);
			return(false);
			}
	return(null);
}

// Returns null if it can't do it, false if there's an error, or a string of the content if successful
function mozillaLoadFile(filePath)
{
	if(window.Components)
		try
			{
			netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
			var file = Components.classes["@mozilla.org/file/local;1"].createInstance(Components.interfaces.nsILocalFile);
			file.initWithPath(filePath);
			if (!file.exists())
				return(null);
			var inputStream = Components.classes["@mozilla.org/network/file-input-stream;1"].createInstance(Components.interfaces.nsIFileInputStream);
			inputStream.init(file, 0x01, 00004, null);
			var sInputStream = Components.classes["@mozilla.org/scriptableinputstream;1"].createInstance(Components.interfaces.nsIScriptableInputStream);
			sInputStream.init(inputStream);
			return(sInputStream.read(sInputStream.available()));
			}
		catch(e)
			{
			//alert("Exception while attempting to load\n\n" + e);
			return(false);
			}
	return(null);
}

function operaUrlToFilename(url)
{
	var f = "//localhost";
	if(url.indexOf(f) == 0)
		return url.substring(f.length);
	var i = url.indexOf(":");
	if(i > 0)
		return url.substring(i-1);
	return url;
}

function operaSaveFile(filePath, content)
{
	try
		{
		var s = new java.io.PrintStream(new java.io.FileOutputStream(operaUrlToFilename(filePath)));
		s.print(content);
		s.close();
		}
	catch(e)
		{
		if(window.opera)
			opera.postError(e);
		return null;
		}
	return true;
}

function operaLoadFile(filePath)
{
	var content = [];
	alert(operaUrlToFilename(filePath));
	try
		{
		var r = new java.io.BufferedReader(new java.io.FileReader(operaUrlToFilename(filePath)));
		var line;
		while ((line = r.readLine()) != null)
			content.push(new String(line));
		r.close();
		}
	catch(e)
		{
		if(window.opera)
			opera.postError(e);
		return null;
		}
	return content.join("\n");
}

function safariFilenameToUrl(filename) {
	return ("file://" + filename);
}

function safariLoadFile(url)
{
	url = safariFilenameToUrl(url);
	var plugin = document.embeds["tiddlyWikiSafariSaver"];
	return plugin.readURL(url);
}

function safariSaveFile(url,content)
{
	url = safariFilenameToUrl(url);
	var plugin = document.embeds["tiddlyWikiSafariSaver"];
	return plugin.writeStringToURL(content,url);
}

// Lifted from http://developer.apple.com/internet/webcontent/detectplugins.html
function detectPlugin()
{
	var daPlugins = detectPlugin.arguments;
	var pluginFound = false;
	if (navigator.plugins && navigator.plugins.length > 0)
		{
		var pluginsArrayLength = navigator.plugins.length;
		for (pluginsArrayCounter=0; pluginsArrayCounter < pluginsArrayLength; pluginsArrayCounter++ )
			{
			var numFound = 0;
			for(namesCounter=0; namesCounter < daPlugins.length; namesCounter++)
				{
				if( (navigator.plugins[pluginsArrayCounter].name.indexOf(daPlugins[namesCounter]) >= 0) || 
						(navigator.plugins[pluginsArrayCounter].description.indexOf(daPlugins[namesCounter]) >= 0) )
					numFound++;
				}
			if(numFound == daPlugins.length)
				{
				pluginFound = true;
				break;
				}
			}
	}
	return pluginFound;
}

// ---------------------------------------------------------------------------------
// Event handlers
// ---------------------------------------------------------------------------------

function onEditKey(e)
{
	if (!e) var e = window.event;
	clearMessage();
	var consume = false;
	switch(e.keyCode)
		{
		case 13: // Ctrl-Enter
		case 10: // Ctrl-Enter on IE PC
		case 77: // Ctrl-Enter is "M" on some platforms
			if(e.ctrlKey && this.id && this.id.substr(0,13) == "editorWrapper")
				{
				blurTiddler(this.id.substr(13));
				saveTiddler(this.id.substr(13),e.shiftKey);
				consume = true;
				}
			break;
		case 27: // Escape
			if(this.id && this.id.substr(0,13) == "editorWrapper")
				{
				blurTiddler(this.id.substr(13));
				displayTiddler(null,this.id.substr(13),1,null,null,false,false);
				consume = true;
				}
			break;
		}
	e.cancelBubble = consume;
	if(consume)
		if (e.stopPropagation) e.stopPropagation();
	return(!consume);

}

// Event handler for clicking on a tiddly link
function onClickTiddlerLink(e)
{
	if (!e) var e = window.event;
	var theTarget = resolveTarget(e);
	var theLink = theTarget;
	var title = null;
	do {
		title = theLink.getAttribute("tiddlyLink");
		theLink = theLink.parentNode;
	} while(title == null && theLink != null);
	if(title)
		{
		var toggling = e.metaKey || e.ctrlKey;
		if(config.options.chkToggleLinks)
			toggling = !toggling;
		var opening;
		if(toggling && document.getElementById("tiddler" + title))
			closeTiddler(title,e.shiftKey || e.altKey);
		else
			displayTiddler(theTarget,title,0,null,null,true,e.shiftKey || e.altKey);
		}
	clearMessage();
	return(false);
}

// Event handler for mouse over a tiddler
function onMouseOverTiddler(e)
{
	var tiddler;
	if(this.id.substr(0,7) == "tiddler")
		tiddler = this.id.substr(7);
	if(tiddler)
		selectTiddler(tiddler);
}

// Event handler for mouse out of a tiddler
function onMouseOutTiddler(e)
{
	var tiddler;
	if(this.id.substr(0,7) == "tiddler")
		tiddler = this.id.substr(7);
	if(tiddler)
		deselectTiddler(tiddler);
}

// Event handler for double click on a tiddler
function onDblClickTiddler(e)
{
	if (!e) var e = window.event;
	var theTarget = resolveTarget(e);
	if(!readOnly && theTarget && theTarget.nodeName.toLowerCase() != "input" && theTarget.nodeName.toLowerCase() != "textarea")
		{
		clearMessage();
		if(document.selection && document.selection.empty)
			document.selection.empty();
		var tiddler;
		if(this.id.substr(0,7) == "tiddler")
			tiddler = this.id.substr(7);
		if(tiddler)
			displayTiddler(null,tiddler,2,null,null,false,false);
		return true;
		}
	else
		return false;
}

// Event handler for clicking on toolbar close
function onClickToolbarClose(e)
{
	if (!e) var e = window.event;
	clearMessage();
	if(this.parentNode.id)
		closeTiddler(this.parentNode.id.substr(7),e.shiftKey || e.altKey);
	return(false);
}

// Event handler for clicking on toolbar permalink
function onClickToolbarPermaLink(e)
{
	if(this.parentNode.id)
		{
		var title = this.parentNode.id.substr(7);
		var t = encodeURIComponent(String.encodeTiddlyLink(title));
		if(window.location.hash != t)
			window.location.hash = t;
		}
	return false;
}

// Event handler for clicking on toolbar close
function onClickToolbarDelete(e)
{
	clearMessage();
	if(this.parentNode.id)
		deleteTiddler(this.parentNode.id.substr(7));
	return false;
}

// Event handler for clicking on the toolbar references button
function onClickToolbarReferences(e)
{
	if (!e) var e = window.event;
	var theTarget = resolveTarget(e);
	var popup = createTiddlerPopup(this);
	if(popup && this.parentNode.id)
		{
		var title = this.parentNode.id.substr(7);
		var references = store.getReferringTiddlers(title);
		var c = false;
		for(var r=0; r<references.length; r++)
			if(references[r].title != title)
				{
				createTiddlyLink(createTiddlyElement(popup,"li"),references[r].title,true);
				c = true;
				}
		if(!c)
			createTiddlyText(createTiddlyElement(popup,"li",null,"disabled"),config.views.wikified.toolbarReferences.popupNone);
		}
	scrollToTiddlerPopup(popup,false);
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return(false);
}

// Event handler for clicking on a tiddler tag
function onClickTag(e)
{
	if (!e) var e = window.event;
	var theTarget = resolveTarget(e);
	var popup = createTiddlerPopup(this);
	var tag = this.getAttribute("tag");
	var title = this.getAttribute("tiddler");
	if(popup && tag)
		{
		var tagged = store.getTaggedTiddlers(tag);
		var titles = [];
		var li,r;
		for(r=0;r<tagged.length;r++)
			if(tagged[r].title != title)
				titles.push(tagged[r].title);
		var lingo = config.views.wikified.tag;
		if(titles.length > 0)
			{
			var openAll = createTiddlyButton(createTiddlyElement(popup,"li"),lingo.openAllText.format([tag]),lingo.openAllTooltip,onClickTagOpenAll);
			openAll.setAttribute("tag",tag);
			createTiddlyElement(createTiddlyElement(popup,"li"),"hr");
			for(r=0; r<titles.length; r++)
				{
				createTiddlyLink(createTiddlyElement(popup,"li"),titles[r],true);
				}
			}
		else
			createTiddlyText(createTiddlyElement(popup,"li",null,"disabled"),lingo.popupNone.format([tag]));
		}
	scrollToTiddlerPopup(popup,false);
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return(false);
}

// Event handler for 'open all' on a tiddler popup
function onClickTagOpenAll(e)
{
	if (!e) var e = window.event;
	var tag = this.getAttribute("tag");
	var tagged = store.getTaggedTiddlers(tag);
	for(var t=tagged.length-1; t>=0; t--)
		displayTiddler(this,tagged[t].title,0,null,null,false,e.shiftKey || e.altKey);
	return(false);
}

// Event handler for clicking on the 'add tag' button
function onClickAddTag(e)
{
	if (!e) var e = window.event;
	var theTarget = resolveTarget(e);
	var popup = createTiddlerPopup(this);
	var tiddler = this.getAttribute("tiddler");
	var tags = store.getTags();
	var lingo = config.views.editor.tagChooser;
	if(tags.length == 0)
		createTiddlyText(createTiddlyElement(popup,"li"),lingo.popupNone);
	for (t=0; t<tags.length; t++)
		{
		var theTag = createTiddlyButton(createTiddlyElement(popup,"li"),tags[t][0],lingo.tagTooltip.format([tags[t][0]]),onClickAddTagPopup);
		theTag.setAttribute("tag",tags[t][0]);
		theTag.setAttribute("tiddler",tiddler);
		}
	scrollToTiddlerPopup(popup,false);
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return(false);
}

// Event handler for clicking on a tag in the 'add tag' popup
function onClickAddTagPopup(e)
{
	if (!e) var e = window.event;
	var theTarget = resolveTarget(e);
	var tiddler = this.getAttribute("tiddler");
	var tag = this.getAttribute("tag");
	var tagsBox = document.getElementById("editorTags" + tiddler);
	if(tagsBox)
		tagsBox.value += " " + String.encodeTiddlyLink(tag);
	return(false);
}

// Event handler for clicking on toolbar close
function onClickToolbarEdit(e)
{
	clearMessage();
	if(this.parentNode.id)
		displayTiddler(null,this.parentNode.id.substr(7),2,null,null,false,false);
	return false;
}

// Event handler for clicking on toolbar save
function onClickToolbarSave(e)
{
	if (!e) var e = window.event;
	if(this.parentNode.id)
		saveTiddler(this.parentNode.id.substr(7),e.shiftKey);
	return false;
}

// Event handler for clicking on toolbar save
function onClickToolbarUndo(e)
{
	if(this.parentNode.id)
		displayTiddler(null,this.parentNode.id.substr(7),1,null,null,false,false);
	return false;
}

// Eek... it's bad that this is done via a function rather than a normal, copy-able href
function onClickPermaView()
{
	var tiddlerDisplay = document.getElementById("tiddlerDisplay");
	var links = [];
	for(var t=0;t<tiddlerDisplay.childNodes.length;t++)
		{
		var tiddlerName = tiddlerDisplay.childNodes[t].id.substr(7);
		links.push(String.encodeTiddlyLink(tiddlerName));
		}
	window.location.hash = encodeURIComponent(links.join(" "));
	return false;
}

// ---------------------------------------------------------------------------------
// Animation engine
// ---------------------------------------------------------------------------------

function Animator()
{
	this.running = 0; // Incremented at start of each animation, decremented afterwards. If zero, the interval timer is disabled
	this.timerID; // ID of the timer used for animating
	this.animations = []; // List of animations in progress
	return this;
}

// Start animation engine
Animator.prototype.startAnimating = function() // Variable number of arguments
{
	for(var t=0; t<arguments.length; t++)
		this.animations.push(arguments[t]);
	if(this.running == 0)
		{
		var me = this;
		this.timerID = window.setInterval(function() {me.doAnimate(me);},25);
		}
	this.running += arguments.length;
}

// Perform an animation engine tick, calling each of the known animation modules
Animator.prototype.doAnimate = function(me)
{
	var a = 0;
	while(a<me.animations.length)
		{
		var animation = me.animations[a];
		animation.progress += animation.step;
		if(animation.progress < 0 || animation.progress > 1)
			{
			animation.stop();
			me.animations.splice(a,1);
			if(--me.running == 0)
				window.clearInterval(me.timerID);
			}
		else
			{
			animation.tick();
			a++;
			}
		}
}

// Map a 0..1 value to 0..1, but slow down at the start and end
Animator.slowInSlowOut = function(progress)
{
	return(1-((Math.cos(progress * Math.PI)+1)/2));
}

// ---------------------------------------------------------------------------------
// Zoomer animation
// ---------------------------------------------------------------------------------

function Zoomer(text,startElement,targetElement,slowly)
{
	this.element = document.createElement("div");
	this.element.appendChild(document.createTextNode(text));
	this.element.className = "zoomer";
	document.body.appendChild(this.element);
	this.startElement = startElement;
	this.startLeft = findPosX(this.startElement);
	this.startTop = findPosY(this.startElement);
	this.startWidth = this.startElement.offsetWidth;
	this.startHeight = this.startElement.offsetHeight;
	this.targetElement = targetElement;
	this.targetLeft = findPosX(this.targetElement);
	this.targetTop = findPosY(this.targetElement);
	this.targetWidth = this.targetElement.offsetWidth;
	this.targetHeight = this.targetElement.offsetHeight;
	this.progress = 0;
	this.step = slowly ? config.animSlow : config.animFast;
	//this.targetElement.style.opacity = 0;
	return this;
}

Zoomer.prototype.stop = function()
{
	this.element.parentNode.removeChild(this.element);
	//this.targetElement.style.opacity = 1;
}

Zoomer.prototype.tick = function()
{
	var f = Animator.slowInSlowOut(this.progress);
	this.element.style.left = this.startLeft + (this.targetLeft-this.startLeft) * f + "px";
	this.element.style.top = this.startTop + (this.targetTop-this.startTop) * f + "px";
	this.element.style.width = this.startWidth + (this.targetWidth-this.startWidth) * f + "px";
	this.element.style.height = this.startHeight + (this.targetHeight-this.startHeight) * f + "px";
	this.element.style.display = "block";
	//this.targetElement.style.opacity = this.progress;
	//this.targetElement.style.filter = "alpha(opacity:" + this.progress * 100 + ")";
}

// ---------------------------------------------------------------------------------
// Scroller animation
// ---------------------------------------------------------------------------------

function Scroller(targetElement,slowly)
{
	this.targetElement = targetElement;
	this.startScroll = findScrollY();
	this.targetScroll = ensureVisible(targetElement);
	this.progress = 0;
	this.step = slowly ? config.animSlow : config.animFast;
	return this;
}

Scroller.prototype.stop = function()
{
	window.scrollTo(0,this.targetScroll);
}

Scroller.prototype.tick = function()
{
	var f = Animator.slowInSlowOut(this.progress);
	window.scrollTo(0,this.startScroll + (this.targetScroll-this.startScroll) * f);
}

// ---------------------------------------------------------------------------------
// Slider animation
// ---------------------------------------------------------------------------------

// deleteMode - "none", "all" [delete target element and it's children], [only] "children" [but not the target element]
function Slider(element,opening,slowly,deleteMode)
{
	this.element = element;
	element.style.display = "block";
	this.deleteMode = deleteMode;
	this.element.style.height = "auto";
	this.realHeight = element.offsetHeight;
	this.opening = opening;
	this.step = slowly ? config.animSlow : config.animFast;
	if(opening)
		{
		this.progress = 0;
		element.style.height = "0px";
		element.style.display = "block";
		}
	else
		{
		this.progress = 1;
		this.step = -this.step;
		}
	element.style.overflow = "hidden";
	return this;
}

Slider.prototype.stop = function()
{
	if(this.opening)
		this.element.style.height = "auto";
	else
		{
		switch(this.deleteMode)
			{
			case "none":
				this.element.style.display = "none";
				break;
			case "all":
				this.element.parentNode.removeChild(this.element);
				break;
			case "children":
				removeChildren(this.element);
				break;
			}
		}
}

Slider.prototype.tick = function()
{
	var f = Animator.slowInSlowOut(this.progress);
	var h = this.realHeight * f;
	this.element.style.height = h + "px";
	this.element.style.opacity = f;
}

// ---------------------------------------------------------------------------------
// Popup menu
// ---------------------------------------------------------------------------------

var Popup = {
	stack: [] // Array of objects with members root: and popup:
	};

Popup.create = function(root)
{
	Popup.remove();
	var popup = createTiddlyElement(document.body,"ol","popup","popup",null);
	Popup.stack.push({root: root, popup: popup});
	return popup;
}

Popup.onDocumentClick = function(e)
{
	if (!e) var e = window.event;
	var target = resolveTarget(e);
	if(e.eventPhase == undefined)
		Popup.remove();
	else if(e.eventPhase == Event.BUBBLING_PHASE || e.eventPhase == Event.AT_TARGET)
		Popup.remove();
	return true;
}

Popup.show = function(unused,slowly)
{
	var curr = Popup.stack[Popup.stack.length-1];
	var rootLeft = findPosX(curr.root);
	var rootTop = findPosY(curr.root);
	var rootHeight = curr.root.offsetHeight;
	var popupLeft = rootLeft;
	var popupTop = rootTop + rootHeight;
	var popupWidth = curr.popup.offsetWidth;
	var winWidth = findWindowWidth();
	if(popupLeft + popupWidth > winWidth)
		popupLeft = winWidth - popupWidth;
	curr.popup.style.left = popupLeft + "px";
	curr.popup.style.top = popupTop + "px";
	curr.popup.style.display = "block";
	addClass(curr.root,"highlight");
	if(config.options.chkAnimate)
		anim.startAnimating(new Scroller(curr.popup,slowly));
	else
		window.scrollTo(0,ensureVisible(curr.popup));
}

Popup.remove = function()
{
	if(Popup.stack.length > 0)
		{
		Popup.removeFrom(0);
		}
}

Popup.removeFrom = function(from)
{
	for(var t=Popup.stack.length-1; t>=from; t--)
		{
		var p = Popup.stack[t];
		removeClass(p.root,"highlight");
		p.popup.parentNode.removeChild(p.popup);
		}
	Popup.stack = Popup.stack.slice(0,from);
}

// Backwards compatibility
var createTiddlerPopup = Popup.create;
var scrollToTiddlerPopup = Popup.show;
var hideTiddlerPopup = Popup.remove;

// ---------------------------------------------------------------------------------
// Augmented methods for the JavaScript Number(), Array() and String() objects
// ---------------------------------------------------------------------------------

// Clamp a number to a range
Number.prototype.clamp = function(min,max)
{
	c = this;
	if(c < min)
		c = min;
	if(c > max)
		c = max;
	return c;
}

// Find an entry in an array. Returns the array index or null
Array.prototype.find = function(item)
{
	for(var t=0; t<this.length; t++)
		if(this[t] == item)
			return t;
	return null;
}

// Find an entry in an array by the value of one of it's members. Returns the array index or null
Array.prototype.findByMember = function(member,value)
{
	for(var t=0; t<this.length; t++)
		if(this[t][member] == value)
			return t;
	return null;
}

// Get an item in an array by the value of a particular member of the item. Returns the item or null
Array.prototype.get = function(member,value)
{
	for(t=0; t<this.length; t++)
		if(this[t][members] == value)
			return this[t];
	return null;
}

// Push a new value into an array only if it is not already present in the array. If the optional unique parameter is false, it reverts to a normal push
Array.prototype.pushUnique = function(item,unique)
{
	if(unique != undefined && unique == false)
		this.push(item);
	else
		{
		if(this.find(item) == null)
			this.push(item);
		}
}

// Get characters from the right end of a string
String.prototype.right = function(n)
{
	if(n < this.length)
		return this.slice(this.length-n);
	else
		return this;
}

// Trim whitespace from both ends of a string
String.prototype.trim = function()
{
	var regexpTrim = new RegExp("^\\s*(.*?)\\s*$","mg");
	return(this.replace(regexpTrim,"$1"));
}

// Convert a string from a CSS style property name to a JavaScript style name ("background-color" -> "backgroundColor")
String.prototype.unDash = function()
{
	var s = this.split("-");
	if(s.length > 1)
		for(var t=1; t<s.length; t++)
			s[t] = s[t].substr(0,1).toUpperCase() + s[t].substr(1);
	return s.join("");
}

// Substitute substrings from an array into a format string that includes '%1'-type specifiers
String.prototype.format = function(substrings)
{
	var subRegExp = new RegExp("(?:%(\\d+))","mg");
	var currPos = 0;
	var r = [];
	do {
		var match = subRegExp.exec(this);
		if(match && match[1])
			{
			if(match.index > currPos)
				r.push(this.substring(currPos,match.index));
			r.push(substrings[parseInt(match[1])]);
			currPos = subRegExp.lastIndex;
			}
	} while(match);
	if(currPos < this.length)
		r.push(this.substring(currPos,this.length));
	return r.join("");
}

// Escape any special RegExp characters with that character preceded by a backslash
String.prototype.escapeRegExp = function()
{
	return(this.replace(new RegExp("[\\\\\\^\\$\\*\\+\\?\\(\\)\\=\\!\\|\\,\\{\\}\\[\\]\\.]","g"),"\\$&"));
}

// Convert & to "&amp;", < to "&lt;", > to "&gt;" and " to "&quot;"
String.prototype.htmlEncode = function()
{
	var regexpAmp = new RegExp("&","mg");
	var regexpLessThan = new RegExp("<","mg");
	var regexpGreaterThan = new RegExp(">","mg");
	var regexpQuote = new RegExp("\"","mg");
	return(this.replace(regexpAmp,"&amp;").replace(regexpLessThan,"&lt;").replace(regexpGreaterThan,"&gt;").replace(regexpQuote,"&quot;"));
}

// Process a string list of macro parameters into an array. Parameters can be quoted with "", '', [[]] or left unquoted (and therefore space-separated)
String.prototype.readMacroParams = function()
{
	var regexpMacroParam = new RegExp("(?:\\s*)(?:(?:\"([^\"]*)\")|(?:'([^']*)')|(?:\\[\\[([^\\]]*)\\]\\])|([^\"'\\s]\\S*))","mg");
	var params = [];
	do {
		var match = regexpMacroParam.exec(this);
		if(match)
			{
			if(match[1]) // Double quoted
				params.push(match[1]);
			else if(match[2]) // Single quoted
				params.push(match[2]);
			else if(match[3]) // Double-square-bracket quoted
				params.push(match[3]);
			else if(match[4]) // Unquoted
				params.push(match[4]);
			}
	} while(match);
	return params;
}

// Process a string list of tiddler names into an array. Tiddler names that have spaces in them must be [[bracketed]]
String.prototype.readBracketedList = function(unique)
{
	var bracketedPattern = "\\[\\[([^\\]]+)\\]\\]";
	var unbracketedPattern = "[^\\s$]+";
	var pattern = "(?:" + bracketedPattern + ")|(" + unbracketedPattern + ")";
	var re = new RegExp(pattern,"mg");
	var tiddlerNames = [];
	do {
		var match = re.exec(this);
		if(match)
			{
			if(match[1]) // Bracketed
				tiddlerNames.pushUnique(match[1],unique);
			else if(match[2]) // Unbracketed
				tiddlerNames.pushUnique(match[2],unique);
			}
	} while(match);
	return(tiddlerNames);
}

// Static method to bracket a string with double square brackets if it contains a space
String.encodeTiddlyLink = function(title)
{
	if(title.indexOf(" ") == -1)
		return(title);
	else
		return("[[" + title + "]]");
}

// Static method to left-pad a string with 0s to a certain width
String.zeroPad = function(n,d)
{
	var s = n.toString();
	if(s.length < d)
		s = "000000000000000000000000000".substr(0,d-s.length) + s;
	return(s);
}

// ---------------------------------------------------------------------------------
// RGB colour object
// ---------------------------------------------------------------------------------

// Construct an RGB colour object from a '#rrggbb' or 'rgb(n,n,n)' string or from separate r,g,b values
function RGB(r,g,b)
{
	this.r = 0;
	this.g = 0;
	this.b = 0;
	if(typeof r == "string")
		{
		if(r.substr(0,1) == "#")
			{
			this.r = parseInt(r.substr(1,2),16)/255;
			this.g = parseInt(r.substr(3,2),16)/255;
			this.b = parseInt(r.substr(5,2),16)/255;
			}
		else
			{
			var rgbPattern = /rgb\s*\(\s*(\d{1,3})\s*,\s*(\d{1,3})\s*,\s*(\d{1,3})\s*\)/ ;
			var c = r.match(rgbPattern);
			if (c)
				{
				this.r = parseInt(c[1],10)/255;
				this.g = parseInt(c[2],10)/255;
				this.b = parseInt(c[3],10)/255;
				}
			}
		}
	else
		{
		this.r = r;
		this.g = g;
		this.b = b;
		}
	return this;
}

// Mixes this colour with another in a specified proportion
// c = other colour to mix
// f = 0..1 where 0 is this colour and 1 is the new colour
// Returns an RGB object
RGB.prototype.mix = function(c,f)
{
	return new RGB(this.r + (c.r-this.r) * f,this.g + (c.g-this.g) * f,this.b + (c.b-this.b) * f);
}

// Return an rgb colour as a #rrggbb format hex string
RGB.prototype.toString = function()
{
	var r = this.r.clamp(0,1);
	var g = this.g.clamp(0,1);
	var b = this.b.clamp(0,1);
	return("#" + ("0" + Math.floor(r * 255).toString(16)).right(2) +
				 ("0" + Math.floor(g * 255).toString(16)).right(2) +
				 ("0" + Math.floor(b * 255).toString(16)).right(2));
}

// ---------------------------------------------------------------------------------
// Augmented methods for the JavaScript Date() object
// ---------------------------------------------------------------------------------

// Substitute date components into a string
Date.prototype.formatString = function(template)
{
	template = template.replace(/YYYY/g,this.getFullYear());
	template = template.replace(/YY/g,String.zeroPad(this.getFullYear()-2000,2));
	template = template.replace(/MMM/g,config.messages.dates.months[this.getMonth()]);
	template = template.replace(/0MM/g,String.zeroPad(this.getMonth()+1,2));
	template = template.replace(/MM/g,this.getMonth()+1);
	template = template.replace(/DDD/g,config.messages.dates.days[this.getDay()]);
	template = template.replace(/0DD/g,String.zeroPad(this.getDate(),2));
	template = template.replace(/DD/g,this.getDate());
	template = template.replace(/hh/g,this.getHours());
	template = template.replace(/mm/g,this.getMinutes());
	template = template.replace(/ss/g,this.getSeconds());
	return template;
}

// Convert a date to UTC YYYYMMDDHHMM string format
Date.prototype.convertToYYYYMMDDHHMM = function()
{
	return(String.zeroPad(this.getFullYear(),4) + String.zeroPad(this.getMonth()+1,2) + String.zeroPad(this.getDate(),2) + String.zeroPad(this.getHours(),2) + String.zeroPad(this.getMinutes(),2));
}

// Convert a date to UTC YYYYMMDD.HHMMSSMMM string format
Date.prototype.convertToYYYYMMDDHHMMSSMMM = function()
{
	return(String.zeroPad(this.getFullYear(),4) + String.zeroPad(this.getMonth()+1,2) + String.zeroPad(this.getDate(),2) + "." + String.zeroPad(this.getHours(),2) + String.zeroPad(this.getMinutes(),2) + String.zeroPad(this.getSeconds(),2) + String.zeroPad(this.getMilliseconds(),4));
}

// Static method to create a date from a UTC YYYYMMDDHHMM format string
Date.convertFromYYYYMMDDHHMM = function(d)
{
	var theDate = new Date(parseInt(d.substr(0,4),10),
							parseInt(d.substr(4,2),10)-1,
							parseInt(d.substr(6,2),10),
							parseInt(d.substr(8,2),10),
							parseInt(d.substr(10,2),10),0,0);
	return(theDate);
}

// ---------------------------------------------------------------------------------
// DOM utilities - many derived from www.quirksmode.org 
// ---------------------------------------------------------------------------------

function drawGradient(place,horiz,colours)
{
	for(var t=0; t<= 100; t+=2)
		{
		var bar = document.createElement("div");
		place.appendChild(bar);
		bar.style.position = "absolute";
		bar.style.left = horiz ? t + "%" : 0;
		bar.style.top = horiz ? 0 : t + "%";
		bar.style.width = horiz ? (101-t) + "%" : "100%";
		bar.style.height = horiz ? "100%" : (101-t) + "%";
		bar.style.zIndex = -1;
		var f = t/100;
		var p = f*(colours.length-1);
		bar.style.backgroundColor = colours[Math.floor(p)].mix(colours[Math.ceil(p)],p-Math.floor(p)).toString();
		}
}

function createTiddlyText(theParent,theText)
{
	return theParent.appendChild(document.createTextNode(theText));
}

function createTiddlyElement(theParent,theElement,theID,theClass,theText)
{
	var e = document.createElement(theElement);
	if(theClass != null)
		e.className = theClass;
	if(theID != null)
		e.setAttribute("id",theID);
	if(theText != null)
		e.appendChild(document.createTextNode(theText));
	if(theParent != null)
		theParent.appendChild(e);
	return(e);
}

function createTiddlyButton(theParent,theText,theTooltip,theAction,theClass,theId,theAccessKey)
{
	var theButton = document.createElement("a");
	theButton.className = "button";
	if(theAction)
		{
		theButton.onclick = theAction;
		theButton.setAttribute("href","#");
		}
	theButton.setAttribute("title",theTooltip);
	if(theText)
		theButton.appendChild(document.createTextNode(theText));
	if(theClass)
		theButton.className = theClass;
	if(theId)
		theButton.id = theId;
	if(theParent)
		theParent.appendChild(theButton);
	if(theAccessKey)
		theButton.setAttribute("accessKey",theAccessKey);
	return(theButton);
}

function createTiddlyLink(place,title,includeText)
{
	var text = includeText ? title : null;
	var subTitle, theClass = "tiddlyLink";
	if(store.tiddlerExists(title))
		{
		subTitle = store.tiddlers[title].getSubtitle();
		theClass += " tiddlyLinkExisting";
		}
	else
		{
		subTitle = config.messages.undefinedTiddlerToolTip.format([title]);
		theClass += " tiddlyLinkNonExisting";
		if(typeof config.shadowTiddlers[title] == "string")
			{
			subTitle = config.messages.shadowedTiddlerToolTip.format([title]);
			theClass += " shadow";
			}
		else
			subTitle = config.messages.undefinedTiddlerToolTip.format([title]);
		}
	var btn = createTiddlyButton(place,text,subTitle,onClickTiddlerLink,theClass);
	btn.setAttribute("tiddlyLink",title);
	return(btn);
}

function createExternalLink(place,url)
{
	var theLink = document.createElement("a");
	theLink.className = "externalLink";
	theLink.href = url;
	theLink.title = config.messages.externalLinkTooltip.format([url]);
	if(config.options.chkOpenInNewWindow)
		theLink.target = "_blank";
	place.appendChild(theLink);
	return(theLink);
}

// Cross-browser event handler attachment
function addEvent(element,type,fn)
{
	if (element.addEventListener)
		{
		element.addEventListener(type,fn,false);
		return true;
		}
	else if (element.attachEvent)
		{
		var r = element.attachEvent("on" + type,fn);
		return r;
		}
	else
		return false;
}

// Cross-browser event handler removal
function removeEvent(element,type,fn)
{
	if (element.removeEventListener)
		{
		element.removeEventListener(type,fn,false);
		return true;
		}
	else if (element.detachEvent)
		{
		var r = element.detachEvent("on" + type,fn);
		return r;
		}
	else
		return false;
}

function addClass(e,theClass)
{
	removeClass(e,theClass);
	e.className += " " + theClass;
}

function removeClass(e,theClass)
{
	var newClass = [];
	var currClass = e.className.split(" ");
	for(var t=0; t<currClass.length; t++)
		if(currClass[t] != theClass)
			newClass.push(currClass[t]);
	e.className = newClass.join(" ");
}

function hasClass(e,theClass)
{
	var c = e.className;
	if(c)
		{
		c = c.split(" ");
		for(var t=0; t<c.length; t++)
			if(c[t] == theClass)
				return true;
		}
	return false;
}

// Find the tiddler instance (if any) containing a specified element
function findContainingTiddler(e)
{
	while(e && !hasClass(e,"tiddler"))
		e = e.parentNode;
	return(e);
}

// Resolve the target object of an event
function resolveTarget(e)
{
	var obj;
	if (e.target)
		obj = e.target;
	else if (e.srcElement)
		obj = e.srcElement;
	if (obj.nodeType == 3) // defeat Safari bug
		obj = obj.parentNode;
	return(obj);
}

// Return the content of an element as plain text with no formatting
function getElementText(elementID)
{
	var e = document.getElementById(elementID);
	var text = "";
	if(e.innerText)
		text = e.innerText;
	else if(e.textContent)
		text = e.textContent;
	return text;
}

// Get the scroll position for window.scrollTo necessary to scroll a given element into view
function ensureVisible(e)
{
	var posTop = findPosY(e);
	var posBot = posTop + e.offsetHeight;
	var winTop = findScrollY();
	var winHeight = findWindowHeight();
	var winBot = winTop + winHeight;
	if(posTop < winTop)
		return(posTop);
	else if(posBot > winBot)
		{
		if(e.offsetHeight < winHeight)
			return(posTop - (winHeight - e.offsetHeight));
		else
			return(posTop);
		}
	else
		return(winTop);
}

// Get the current width of the display window
function findWindowWidth()
{
	return(window.innerWidth ? window.innerWidth : document.body.clientWidth);
}

// Get the current height of the display window
function findWindowHeight()
{
	return(window.innerHeight ? window.innerHeight : document.body.clientHeight);
}

// Get the current vertical page scroll position
function findScrollY()
{
	return(window.scrollY ? window.scrollY : document.body.scrollTop);
}

function findPosX(obj)
{
	var curleft = 0;
	while (obj.offsetParent)
		{
		curleft += obj.offsetLeft;
		obj = obj.offsetParent;
		}
	return curleft;
}

function findPosY(obj)
{
	var curtop = 0;
	while (obj.offsetParent)
		{
		curtop += obj.offsetTop;
		obj = obj.offsetParent;
		}
	return curtop;
}

// Create a non-breaking space
function insertSpacer(place)
{
	var e = document.createTextNode(String.fromCharCode(160));
	if(place)
		place.appendChild(e);
	return e;
}

// Remove all children of a node
function removeChildren(e)
{
	while(e.hasChildNodes())
		e.removeChild(e.firstChild);
}

// Add a stylesheet, replacing any previous custom stylesheet
function setStylesheet(s,id)
{
	if(!id)
		id = "customStyleSheet";
	var n = document.getElementById(id);
	if(document.createStyleSheet) // Test for IE's non-standard createStyleSheet method
		{
		if(n)
			n.parentNode.removeChild(n);
		// This failed without the &nbsp;
		document.body.insertAdjacentHTML("beforeEnd","&nbsp;<style id='" + id + "'>" + s + "</style>");
		}
	else
		{
		if(n)
			n.replaceChild(document.createTextNode(s),n.firstChild);
		else
			{
			var n = document.createElement("style");
			n.type = "text/css";
			n.id = id;
			n.appendChild(document.createTextNode(s));
			document.getElementsByTagName("head")[0].appendChild(n);
			}
		}
}

// ---------------------------------------------------------------------------------
// End of scripts
// ---------------------------------------------------------------------------------

</script>
<style type="text/css">

#saveTest {	display: none;}.zoomer {	display: none;}#messageArea {	display: none;}#storeArea, #copyright {	display: none;}.popup {	position: absolute;}

</style>
<noscript>
<style type="text/css">

#contentWrapper {
	display: none;
}

#storeArea {
	display: block;
	margin: 4em 17em 3em 17em;
}

#storeArea div {
 padding: 0.5em;
 margin: 1em 0em 0em 0em;
 border-color: #f0f0f0 #606060 #404040 #d0d0d0;
 border-style: solid;
 border-width: 2px;
 height: 7em;
 overflow: auto;
}

#javascriptWarning {
	width: 100%;
	text-align: center;
	font-weight: bold;
	background-color: #dd1100;
	color: #ffffff;
	padding:1em 0em 1em 0em;
}

</style>
</noscript>
</head>
<body onload="main();" onunload="if(checkUnsavedChanges) checkUnsavedChanges();">
	<script>
	if (detectPlugin("TiddlyWiki Saver"))
		{
		document.write('<embed style="display: none" name="tiddlyWikiSafariSaver" width="0" height="0" type="application/x-webkit-tiddlywiki-saver"></embed>'); 
		saveUsingSafari = true;
		}
	</script>
	<div id="copyright">
	Welcome to TiddlyWiki by Jeremy Ruston, Copyright &copy; 2005 Osmosoft Limited
      <!-- PMC -->
            <a href="http://www.patrickcurry.com/tiddly" target="_new">
				This site is powered by PhpTiddlyWiki.  Booyah!
			</a>
			<!-- end PMC -->
	</div>
	<noscript>
		<div id="javascriptWarning">This page requires JavaScript to function properly</div>
	</noscript>
	<div id="saveTest"></div>
	<div id="contentWrapper">
		<div id="header">
			<div id="titleLine">
				<span id="siteTitle"></span>
				<span id="siteSubtitle"></span>
			</div>
		</div>
		<div id="sidebar">
			<div id="sidebarOptions"></div>
			<div id="sidebarTabs"></div>
		</div>
		<div id="mainMenu"></div>
		<div id="displayArea">
			<div id="messageArea"></div>
			<div id="tiddlerDisplay"></div>
		</div>
	</div>
<!-- PMC -->
<div id="invisiFormDiv" style="visibility:hidden">
	<form name="invisiForm" action="<?= $tiddly_name_of_script ?>" method="post" target="invisiFormFrame">
		<input type="hidden" name="msg" value="" />
		<input type="hidden" name="title" value="" />
		<input type="hidden" name="body" value="" />
		<input type="hidden" name="modifier" value="" />
		<input type="hidden" name="theTags" value="" />
	</form>
</div>
<iframe id="invisiFormFrame" name="invisiFormFrame" style="visibility:hidden"></iframe>
<!-- end PMC -->
	<div id="storeArea">

