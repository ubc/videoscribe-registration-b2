var app = angular.module('VideoScribeRegistrationAdminApp', ['admin']);

// Put all building block admin functions into its own module.
var adminModule = angular.module('admin', ['ngResource', 'ngSanitize']);

adminModule.factory('AdminRsc',
	function ($resource)
	{
		return $resource(b2path + '/admin/');
	}
);

adminModule.controller('AdminCtrl', 
	function ($log, $scope, $timeout, AdminRsc)
	{
		$scope.duringInit = true;
		$scope.initError = false;
		$scope.submitMsg = "";
		// AdminRsc is how we interact with the server side REST data source, this 
		// does a GET request to /admin/
		$scope.info = AdminRsc.get().$promise.then(
			function(ret)
			{ 
				$scope.config = ret;
				$timeout(function() { $scope.duringInit = false; }, 500);
			},
			function(ret) 
			{
				$timeout(function() { $scope.duringInit = false; $scope.initError = true; }, 500);
			}
		);
		// this is the function for the username form submit
		$scope.submit = function() 
		{ 
			$scope.submitMsg = "processing";
			AdminRsc.save($scope.config).$promise.then(
				function(result)
				{
					$scope.submitMsg = "success"; 
					$scope.config = result;
					$timeout(function() { $scope.submitMsg = ""; }, 5000);
				},
				function(error)
				{
					$timeout(function() { $scope.submitMsg = "error"; }, 500);
					$timeout(function() { $scope.submitMsg = ""; }, 5000);
				}
			);
		};
	}
);
