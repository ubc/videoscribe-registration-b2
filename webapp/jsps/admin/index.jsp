<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%-- Include the Blackboard Tag Library --%>
<%@ taglib prefix="bbNG" uri="/bbNG"%>

<%-- 
Building block pages should be enclosed in one of the Blackboard provided page
tags. In this example, we used bbNG:genericPage, which lets the page to rendered
with nothing wrapping around it. Other page tags are available such as
bbNG:learningSystemPage (will render the page with course menus, etc) and
bbNG:includedPage (for pages that needs to be jsp-included to another page).

The ctxId params provides access  to a Blackboard Context object where
information about the current request being served can be retrieved. In this
example, we gave it a short 'ctx' name, but the default name is 'bbContext'.
--%>
<bbNG:genericPage ctxId="ctx" entitlement="system.admin.VIEW">

<bbNG:pageHeader instructions="Configure API usernames and passwords for VideoScribe registration.">
	<bbNG:pageTitleBar >VideoScribe Registration Building Block Configuration</bbNG:pageTitleBar>
</bbNG:pageHeader>

<link href="${ctx.request.contextPath}/styles/style.css" type="text/css" rel="stylesheet" />

<script type="text/javascript">
var b2path = "${ctx.request.contextPath}"; // sort of a hacky way to get the b2's url for use by Angular so we don't have to hard code it
</script>
<script src="${ctx.request.contextPath}/bower_components/angular/angular.js"></script>
<script src="${ctx.request.contextPath}/bower_components/angular-resource/angular-resource.js"></script>
<script src="${ctx.request.contextPath}/bower_components/angular-sanitize/angular-sanitize.js"></script>
<script src="${ctx.request.contextPath}/scripts/admin.js"></script>

<%-- AngularJS needs a wrapping ng-app element. We give it a class too to make it easy to isolate this building block's CSS. --%>
<div class="VideoScribeRegistrationAdmin" ng-app="VideoScribeRegistrationAdminApp">
	<%-- Only a simple single controller app that, given a username, retrieves some
	basic info about the user. Will initially load the current logged in user's info. --%>
	<div ng-controller="AdminCtrl">

		<form ng-submit="submit()" onsubmit="return false;">
			<h1>Select Operation Mode</h1>
			<p>
			For testing purposes, a <i>Development Mode</i> is available.
			Accounts created in <i>Development Mode</i> will have a
			confirmation email sent out but the account is non-functional. This
			is done by directing VideoScribe API calls to their dev server. The
			username and password fields are used to authenticate to the
			VideoScribe API.
			</p>
			<div class="modeBlock">
				<label class="modeSelectLabel">
					<input type="radio" ng-model="config.mode" value="prod"
						name="configMode" required />
					Production Mode
				</label>
				<label class="alignedLabel" for="usernameProd">Username</label>
				<input type="text" ng-model="config.prod.username" id="usernameProd"/>

				<label class="alignedLabel"for="passwordProd">Password</label>
				<input type="password" ng-hide="showProdPassword" 
					ng-model="config.prod.password" id="passwordProd"/>
				<input type="text" ng-show="showProdPassword" 
					ng-model="config.prod.password"/>
				<label class="smallText">
					<input type="checkbox" ng-model="showProdPassword" 
						ng-checked="false">Show
				</label>
			</div>
			<div class="modeBlock">
				<label class="modeSelectLabel">
					<input type="radio" ng-model="config.mode" value="dev"
						name="configMode" required />
					Development Mode
				</label>
				<label class="alignedLabel" for="usernameDev">Username</label>
				<input type="text" ng-model="config.dev.username" id="usernameDev"/>

				<label class="alignedLabel" for="passwordDev">Password</label>
				<input type="password" ng-hide="showDevPassword" 
					ng-model="config.dev.password" id="passwordDev"/>
				<input type="text" ng-show="showDevPassword" 
					ng-model="config.dev.password"/>
				<label class="smallText">
					<input type="checkbox" ng-model="showDevPassword" 
						ng-checked="false">Show
				</label>
			</div>
			<h1>Instruction Text</h1>
			<div>
				<p>This is the text shown to the user which explains how to use the VideoScribe Registration module. Basic HTML tags may be used.</p>
				<textarea ng-model="config.instructions"></textarea>
				<h3>Preview</h3>
				<div ng-bind-html="config.instructions"></div>
			</div>
			<div class="centerSubmit">
				<input type="submit" value="Save" />
			</div>
			<div class="messages">
				<%-- This controls messages that show up while the page is loading --%>
				<span class="processing" ng-if="duringInit">Initializing...</span>
				<span class="error" ng-if="initError">Initialization failed. Unable to retrieve stored settings.</span>
				<%-- Status messages for saving settings. --%>
				<span class="processing" ng-show='submitMsg == "processing"'>Saving...</span>
				<span class="success" ng-show="submitMsg == 'success'">Save Successful!</span>
				<span class="error" ng-show="submitMsg == 'error'">Server returned an error while saving.</span>
			</div>

		</form>
	</div>
</div>

</bbNG:genericPage>
