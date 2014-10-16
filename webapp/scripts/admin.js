var app = angular.module('templateApp', ['admin']);

// Put all building block admin functions into its own module.
var adminModule = angular.module('admin', ['ngResource']);

adminModule.factory('AdminRsc',
	function ($resource)
	{
		return $resource(
			b2path + '/admin/:username'
		);
	}
);

adminModule.controller('AdminCtrl', 
	function ($scope, $timeout, AdminRsc)
	{
		$scope.duringInit = true;
		$scope.submitMsg = "";
		// AdminRsc is how we interact with the server side REST data source, this 
		// does a GET request to /admin/
		// This is also using a "on success" callback to set initDone to true.
		$scope.info = AdminRsc.get({}, function() { $scope.duringInit = false; });
		// this is the function for the username form submit
		$scope.submit = function() 
		{ 
			// Change the status message to let the user know that stuff is happening.
			// Users might not see this if we get the results back fast enough.
			$scope.submitMsg = "processing";
			// Note that we're using the same AdminRsc as previously but using the more
			// advanced promise mechanism instead of callbacks to process the result.
			// We've also supplied a username param which means we do a request to
			// /admin/username
			AdminRsc.get({username:this.username}).$promise.then(
				// if the request was successful
				function(result)
				{
					// Show the success status message first, then populate the user info fields.
					$scope.submitMsg = "success"; 
					$scope.info = result; 
					// remove the status message after a delay
					$timeout(function() { $scope.submitMsg = ""; }, 5000);
				},
				// if the request returned an error
				function(error)
				{
					var errMsg;
					if (error.status == 404)
					{
						errMsg = "notfound";
					}
					else
					{
						errMsg = "servererror";
					}
					$timeout(function() { $scope.submitMsg = errMsg; }, 500);
					$timeout(function() { $scope.submitMsg = ""; }, 5000);
				}
			);
		};
	}
);
