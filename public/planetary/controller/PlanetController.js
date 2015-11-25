var app = angular.module('planetary.MainController', [planetary.mainlogic]);

app.controller('planetAppController', function($scope){
	$scope.worldSeed = "This is a working world"
})