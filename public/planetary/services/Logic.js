var app = angular.module('PlanetCoordinator', ['PlanetModel','PlanetData'])

app.factory('PlanetLogic',['$http', 'Planet', 'PiDataModel',
                          function($http, Planet, Data){
	
	function arrayObjectIndexOf(array, term, property){
		for(var i = 0; i < array.length; i++){
			if(array[i][property] == term) return i;
		}
		return -1;
	}
	
	var data = Data.getData();
	var len = 0;
	var planetList = [];
	
	var basicTypeList = function() {
		console.log("Inside Logic.basicTypeList")
		var typeList = [];
		angular.forEach(data.itemDetails, function(det, id){
			if(det.tier == 1){
				typeList.push({id: id, name: det.name})
			}
		});
		console.log("basic type list: " + typeList);
		return typeList;
	}
	
	var advancedTypeList = function() {
		console.log("Inside Logic.advancedTypeList")
		var typeList = [];
		angular.forEach(data.itemDetails, function(det, id){
			if(det.tier == 2 || det.tier == 3){
				typeList.push({id: id, name: det.name})
			}
		});
		return typeList;
	}
	
	var hightechTypeList = function() {
		console.log("Inside Logic.hightechTypeList");
		var typeList = [];
		angular.forEach(data.itemDetails, function(det, id){
			if(det.tier == 4){
				typeList.push({id: id, name: det.name})
			}
		});
		return typeList;
	}
	
	function getSystemImportExports() {
		console.log("Inside Logic.refreshTotalImportExports");
		var ioList = [];
		angular.forEach(planetList, function(planet){
//			planet.ioDetails {id, quantity}
			angular.forEach(planet.ioDetails, function(io){
				var index = arrayObjectIndexOf(ioList, io.id, "id");
				if(index == -1){
					ioList.push({id: io.id, quantity: io.quantity});
				}
				else {
					ioList[index].quantity += io.quantity;
				}
			})
		})
		return ioList;
	}
	
	function getSystemTaxes() {
		console.log("Inside Logic.refreshTotalTaxes");
		var taxes = {importTaxes: 0, exportTaxes: 0};
		angular.forEach(planetList, function(planet){
			taxes.importTaxes += planet.importTaxes;
			taxes.exportTaxes += planet.exportTaxes;
		})
		return taxes;
	}
	
	function getSystemRuntime() {
		console.log("Inside Logic.refreshSystemRuntime");
		var runtimeInfo = {bottleneck:'', minRuntime: 999999};
		angular.forEach(planetList, function(planet){
			if(planet.runtime < runtimeInfo.minRuntime){
				runtimeInfo.bottleneck = planet.name;
				runtimeInfo.minRuntime = planet.runtime; 
			}
		})
		if(runtimeInfo.minRuntime == 999999){
			runtimeInfo.minRuntime = 0;
		}
		return runtimeInfo;
	}
	
	function getSystemSetupCost() {
		console.log("Inside Logic.refreshSystemSetupCost");
		var totalCost = 0;
		angular.forEach(planetList, function(planet){
			totalCost += planet.Cost;
		})
		return totalCost;
	}
	
	
	
	
	
	var service = {};
	service.addPlanet = function() {
		console.log("Inside Logic.addPlanet");
		len = len + 1;
		var p = new Planet("Planet " + len);
		planetList.push(p);
		return planetList.length-1;
	}
	service.createPlanetFromCopy = function(p) {
		console.log("Inside Logic.createPlanetFromCopy with ars: " + angular.toJson(p))
		var newPlanet = p.getCopyOfThisPlanet();
		len = len + 1;
		planetList.push(newPlanet);
	}
	service.deletePlanet = function(p){
		var index = planetList.indexOf(p);
		planetList.splice(index, 1);
		return index-1;
	}
	
	service.System = {};
	service.System.importExports = [];
	service.System.taxes = {};
	service.System.taxes.importTaxes = 0;
	service.System.taxes.exportTaxes = 0;
	service.System.runtimeInfo = {}
	service.System.runtimeInfo.bottleneck = '';
	service.System.runtimeInfo.minRuntime = 0;
	service.System.setupCost = 0;
	
	service.refreshOverviewTab = function(){
		service.System.importExports = getSystemImportExports();
		
		var taxes = getSystemTaxes();
		service.System.taxes.importTaxes = taxes.importTaxes;
		service.System.taxes.exportTaxes = taxes.exportTaxes;
		
		var runtimeInfo = getSystemRuntime();
		service.System.runtimeInfo.bottleneck = runtimeInfo.bottleneck;
		service.System.runtimeInfo.minRuntime = runtimeInfo.minRuntime;
		
		service.System.setupCost = getSystemSetupCost();
	}
	
	service.hello = 'Hello World mk3!';
	service.data = data;
	service.planets = planetList;
	service.basicTypeList = basicTypeList();
	service.advancedTypeList = advancedTypeList();
	service.hightechTypeList = hightechTypeList();
		
	return service;
	
}])