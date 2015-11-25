var app = angular.module('PlanetaryGadget', ['PlanetCoordinator']);

app.controller('planetAppController', function($scope, PlanetLogic){
	$scope.worldSeed = "The world of worlds. Planets!";
	
	$scope.Logic = PlanetLogic;
})