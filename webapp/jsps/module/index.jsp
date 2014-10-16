<%@ page contentType="text/html; charset=UTF-8" 
	import="blackboard.platform.plugin.PlugInUtil" %>
<%@ taglib prefix="bbNG" uri="/bbNG"%>

<%
// As an included module page, we can't use the {bbContext.request.contextPath}
// object to get the url to our building block since it can be loaded from
// outside the building block. E.g.: Modules placed in the home portal would
// return "/webapps/portal". Using the PlugInUtil is the only way to accurately
// get an url to our building block in this case.  
// getUriStem() takes 2 params, first is the b2's vendor id, next is the
// handle, both are configured in bb-manifest.xml
String b2url = PlugInUtil.getUriStem("ubc", "videoscribe-registration");
%>

<!-- Lists courses, first separated by whether the course has been marked as
unavailable or not, then by the user's role in the course '-->
<bbNG:includedPage>
	
	<div id="VideoScribeRegistrationAppDiv">
		<div ng-controller="VSModuleController">
			<form ng-submit="submit()">
				<div>
					<label for="ubc-vsr-firstname">Firstname </label>
					<input id="ubc-vsr-firstname" type="text" ng-model="info.firstname" />
				</div>
				<div>
					<label for="ubc-vsr-lastname">Lastname </label>
					<input id="ubc-vsr-lastname" type="text" ng-model="info.lastname" />
				</div>
				<div>
					<label for="ubc-vsr-password">Password </label>
					<input id="ubc-vsr-password" type="text" ng-model="info.password" />
				</div>
				<div>
					<label for="ubc-vsr-email">Email </label>
					<input id="ubc-vsr-email" type="text" ng-model="info.email" required />
				</div>
				<div class="submit">
					<input id="ubc-vsr-submit" type="submit" value="Submit" />
				</div>
			</form>
		</div>
	</div>

<!-- Inline styles placed after the div or IE8 will not apply them -->
<style type="text/css">
#VideoScribeRegistrationAppDiv .courseList li
{
	margin-bottom: 0.5em;
}
#VideoScribeRegistrationAppDiv form .submit
{
	text-align: center;
}
#VideoScribeRegistrationAppDiv form .submit input
{
	padding: 0.2em 0.5em;
}
#VideoScribeRegistrationAppDiv form div
{
	font-size: 1.1em;
	margin-bottom: 0.5em;
}
#VideoScribeRegistrationAppDiv form label
{
	display: inline-block;
	width: 7em;
	margin-right: 0.5em;
	text-align: right;
}
</style>
<bbNG:jsBlock>
<script type="text/javascript">
// There's quite a few issues with loading AngularJS in a portal module. This
// module uses it's own Javascript Library Loader in order to fix these issues:
// - There appears to be some kind of a Javascript loader in effect on portals.
// It causes weird dependency issues that I can't seem to solve, such as
// controllers not being recognized unless it's declared as a function.  Using
// the <jsFile> or <jsBlock> tags to load javascript solves some problems, but
// not all.
// - There doesn't seem to be a way to get an url to your building block
// without using PlugInUtil. This is currently done by a Java code block in the
// JSP. It's better to have a servlet execute the Java code and then pass the
// value to the JSP, but BBL seems to be very particular about how that servlet
// is hooked up. Jersey MVC's setup, even though it reads a JSP and returns it,
// doesn't seem to be recognized by the portal as a valid module. Spring MVC
// can do it somehow, but I'm not sure how.
//
/***** JS LIBRARY LOADER *****/
// Need to keep the loader isolated so it doesn't interfere with other modules
// built using the same template. To do this, we wrap the entire loader in an
// anonymous function.
(function() {
// URL to the building block
var b2url = "<%=b2url%>";
// the name of the angular module that'll drive this page
var angularModuleName = "VideoScribeRegistrationApp";
// the id of the div that's supposed to contain ng-app
var ngAppDivId = "VideoScribeRegistrationAppDiv";

// Need a separate function to start angularjs due to delayed library load.
var startAngular = function() 
{
	// To prevent an exception when angular first loads, we don't declare an
	// ng-app until we're sure angular + extensions have all been loaded. Tags
	// like ng-controller can be placed normally since they're not recognized
	// without ng-app.
	$(ngAppDivId).setAttribute('ng-app', angularModuleName);
	// pass the b2 url to the Angular module as a constant
	angular.module(angularModuleName).constant('B2URL', b2url);
	angular.bootstrap($(ngAppDivId), [angularModuleName]);
};

// lets you pre-set arguments in a javascript function, for passing around
// functions like a variable
var partial = function (func /*, 0..n args */) 
{
	var args = Array.prototype.slice.call(arguments, 1);
	return function() 
	{
		var allArguments = args.concat(Array.prototype.slice.call(arguments));
		return func.apply(this, allArguments);
	};
};

// load a library dynamically, execute a call back function once done
// the loading is done by creating a 
var loadFile = function loadFile(url, onloadcb) 
{
	var head = document.getElementById(ngAppDivId);
	var script = document.createElement('script');
	script.src = url;
	script.onload = onloadcb;
	script.onerror = function() { console.log("Error loading library: " + url); };
	script.onreadystatechange = onloadcb; // cause IE just has to be contrary
	head.parentNode.insertBefore(script, head);
};

// given a chain of libraries, load them sequentially
// will only load libraries that haven't been loaded yet
var libLoader = function libLoader(libs, after)
{
	if (typeof after == 'function') after();
	if (libs.length == 0) return; // base case
	var lib = libs.shift();
	if (lib.loaded())
	{ // move on to next library
		libLoader(libs);
	}
	else
	{ // load the library and then move on
		if ('after' in lib) 
			loadFile(lib.url, partial(libLoader, libs, lib.after));
		else 
			loadFile(lib.url, partial(libLoader, libs, null));
	}
};
// The list of libraries to load, "loaded" and "url" are required params,
// "after" is optional.
// "loaded" contains a function testing to see if the library is already loaded
// "url" is the url to the library that needs to be loaded
// "after" is a function that needs to be execute after the library is loaded
libLoader([
	{
		"loaded": function() { return typeof angular != 'undefined'}, 
		"url": b2url + "bower_components/angular/angular.js"
	},
	{
		"loaded": function() { 
			try { angular.module("ngResource"); } 
			catch(e) { return false; } 
			return true; 
		}, 
		"url": b2url + "bower_components/angular-resource/angular-resource.js"
	},
	{
		"loaded": function() { 
			try { angular.module(angularModuleName); } 
			catch(e) { return false; } 
			return true; 
		}, 
		"url": b2url + "scripts/module.js", // CHANGEME
		"after": startAngular
	}
]);
// end anonymous function wrapper
})();

</script>
</bbNG:jsBlock>

</bbNG:includedPage>
