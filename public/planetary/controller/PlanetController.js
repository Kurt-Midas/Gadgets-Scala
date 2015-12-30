var app = angular.module('PlanetaryGadget', ['PlanetCoordinator', 'PlanetData', 'ui.bootstrap']);

app.controller('planetAppController', ['$scope', 'PlanetLogic', 'DATA', 'LEVEL', 'PRODUCT', 'PLANETNAME',
                                       function($scope, PlanetLogic, DATA, LEVEL, PRODUCT, PLANETNAME){
	$scope.utilizationMessage = 
		'A production chain which depends on imports needs to be able to "catch up" if production cycles are missed. '
		+ 'Checking this enables boxes which can set factories or the planet itself to be active only every X cycles.';
	
	$scope.activeView = 'HELP'
	$scope.setActiveView = function(view){
		console.log("changing active view: " + view);
		$scope.activeView = view;
	}
	$scope.LEVEL = LEVEL.LEVEL;
	
	$scope.worldSeed = "The world of worlds. Planets!";
	
	//Note: unlike other factories and services, the controller gets DATA from conf.js through resolve	
	$scope.DATA = DATA;
	$scope.COST = PRODUCT.TIER_COST;
	$scope.PLANETNAME = PLANETNAME;
	
	$scope.Logic = PlanetLogic;
	$scope.addPlanet = function(){
		$scope.activeView = PlanetLogic.addPlanet();
	};
	
	$scope.deletePlanet = function(p){
		var newIndex = PlanetLogic.deletePlanet(p);
		if(newIndex >= 0){
			$scope.activeView = newIndex;
		} else {
			$scope.activeView = 'HELP';
		}
	}
	//What is best in Angular?
	//to define all actions in the controller
	//to pass through actions directly
	//or hear the lamentations of competent webdevs? 
	

	
	
}])