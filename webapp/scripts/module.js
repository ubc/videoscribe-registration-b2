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
	function($scope, $log, ModuleResource)
	{
		$scope.courses = ModuleResource.get().$promise.then(
			function(ret) {
				$log.debug(ret);
			},
			function() {
			}
		);
		$scope.submit = function() {
			$log.debug($scope.info);
			ModuleResource.save($scope.info);
		};
	}
);
