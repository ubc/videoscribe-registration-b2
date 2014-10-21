var vsregiApp = angular.module('VideoScribeRegistrationApp', ['ngResource']);

vsregiApp.factory(
	'ModuleResource',
	function ($resource, B2URL)
	{
		return $resource(B2URL + 'module');
	}
);


vsregiApp.controller(
	'VSModuleController', 
	function($scope, $log, $timeout, ModuleResource)
	{
		$scope.submitStatus = "initializing";
		ModuleResource.get().$promise.then(
			function(result)
			{
				$scope.info = result;
				$scope.submitStatus = "";
			},
			function(result)
			{
				$scope.submitStatus = "error";
				$scope.errorMsg = "Error initializing.";
			}
		);
		$scope.submit = function() 
		{
			$scope.submitStatus = "processing";
			ModuleResource.save($scope.info).$promise.then(
				function(result) 
				{
					$log.debug("Submit Success");
					$log.debug(result);
					$timeout(function() { $scope.submitStatus = "success"; }, 200);
					$timeout(function() { $scope.submitStatus = ""; }, 5200);
				},
				function(result)
				{
					$log.debug("Failed Submit");
					$log.debug(result);
					$scope.submitStatus = "error";
					if (result.status >= 500)
					{
						$scope.errorMsg = "Registration failed due to a server error.";
					}
					else
					{
						var errorCode = result.data.desc.error_code;
						$log.debug(errorCode);
						if (errorCode == "error_email_exists")
						{
							$log.debug("Email Exists");
							$scope.errorMsg = result.data.desc.error_message;
						}
						else if (errorCode == "error_validation")
						{
							$log.debug("Error Validation");
							var problematicField = 
								result.data.desc.error_validation[0].fieldname;
							$log.debug(problematicField);
							if (problematicField == "surname")
							{
								$scope.errorMsg = "Error, last name is too short.";
							}
							else if (problematicField == "email")
							{
								$scope.errorMsg = "Error, invalid email format."

							}
							else
							{
								$scope.errorMsg = "Error, " + problematicField +
									" is too short.";
							}
						}
					}
				}
			);
		};
	}
);
