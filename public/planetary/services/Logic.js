var app = angular.module('PlanetCoordinator', ['PlanetModel','PlanetData', 'marketservice'])

app.factory('PlanetLogic',['$http', 'Planet', 'PiDataModel', 'MarketDataService',
                          function($http, Planet, Data, MarketService){
	
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
		console.log("Inside Logic.getSystemImportExports");
		var ioList = [];
		angular.forEach(planetList, function(planet){
//			planet.ioDetails {id, quantity}
			angular.forEach(planet.ioDetails, function(io){
				var index = arrayObjectIndexOf(ioList, io.id, "id");
				if(index == -1){
					ioList.push({id: io.id, quantity: io.quantity / planet.cyclesPerActiveCycle});
				}
				else {
					ioList[index].quantity += io.quantity / planet.cyclesPerActiveCycle;
				}
			})
		})
		return ioList;
	}
	
	function getSystemTaxes() {
		console.log("Inside Logic.refreshTotalTaxes");
		var taxes = {importTaxes: 0, exportTaxes: 0};
		angular.forEach(planetList, function(planet){
			taxes.importTaxes += planet.importTaxes / planet.cyclesPerActiveCycle;
			taxes.exportTaxes += planet.exportTaxes / planet.cyclesPerActiveCycle;
		})
		return taxes;
	}
	
	function getSystemRuntime() {
		console.log("Inside Logic.refreshSystemRuntime");
		var runtimeInfo = {bottleneck:'', minRuntime: 999999};
		angular.forEach(planetList, function(planet){
			if(planet.runtime > 0 && 
					planet.runtime < runtimeInfo.minRuntime &&
					planet.cyclesPerActiveCycle == 1){
				runtimeInfo.bottleneck = planet.name;
				runtimeInfo.minRuntime = planet.runtime; 
			}
		})
		if(runtimeInfo.minRuntime == 999999){
			runtimeInfo.bottleneck = 'N/A';
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
	
	service.loadOverviewTab = function(){
		service.System.importExports = getSystemImportExports();
		
		var taxes = getSystemTaxes();
		service.System.taxes.importTaxes = taxes.importTaxes;
		service.System.taxes.exportTaxes = taxes.exportTaxes;
		
		var runtimeInfo = getSystemRuntime();
		service.System.runtimeInfo.bottleneck = runtimeInfo.bottleneck;
		service.System.runtimeInfo.minRuntime = runtimeInfo.minRuntime;
		
		service.System.setupCost = getSystemSetupCost();
	}
	
	service.loadMarketDetailsTab = function(){
		console.log("Inside Logic, loadMarketDetailsTab")
		service.loadOverviewTab();
		service.getMarketInfo();
	}

	service.refreshSystemMarketTotals = function(){
		//Total hourly export revenue
		//Total hourly import cost
		//Total hourly customs tax
		//per planet:
		//	total hourly export revenue
		//	total hourly import cost
		var totalHourlyExportRevenue = 0;
		var totalHourlyImportCost = 0;
		var totalHourlyCustomsTax = 0;
		angular.forEach(planetList, function(planet){
			planet.exportRevenue = 0;
			planet.importCost = 0;
			angular.forEach(planet.ioDetails, function(io){
				console.log("Inside refreshSystemMarketTotals: ", angular.toJson(io));
				if(io.quantity > 0){
					planet.exportRevenue += this.marketprices[io.id][this.exportOrderType].fivePercent * io.quantity
				} else if (io.quantity < 0) {
					planet.importCost -= this.marketprices[io.id][this.importOrderType].fivePercent * io.quantity
				}
			},this)
			totalHourlyExportRevenue += planet.exportRevenue / planet.cyclesPerActiveCycle;
			totalHourlyImportCost += planet.importCost / planet.cyclesPerActiveCycle;
			totalHourlyCustomsTax += (planet.exportTaxes + planet.importTaxes) / planet.cyclesPerActiveCycle;
		},this)
		console.log("After refreshSystemMarketTotals: ", totalHourlyExportRevenue, totalHourlyImportCost, totalHourlyCustomsTax);
		this.totalHourlyExportRevenue = totalHourlyExportRevenue;
		this.totalHourlyImportCost = totalHourlyImportCost;
		this.totalHourlyCustomsTax = totalHourlyCustomsTax;
		//update market fees 
		this.refreshMarketFees();
	}

	service.refreshMarketFees = function(){
		console.log("inside Logic's refreshMarketFees");
		//exports have sales tax. Imports do not.
		//exports with SELL orders have broker fees. Imports with BUY orders have broker fees. 
		if(this.importOrderType == "buy"){
			//obtain import materials with buy orders: broker fees
			this.totalHourlyImportMarketFees = this.totalHourlyImportCost * this.brokerFees/100;
		} else{
			//obtain import materials with sell orders: nothing
			this.totalHourlyImportMarketFees = 0;
		}
		if(this.exportOrderType == "sell"){
			//liquidate by creating sell orders: sales tax and broker fees
			this.totalHourlyExportMarketFees = this.totalHourlyExportRevenue * (this.taxRate + this.brokerFees)/100
		} else {
			//liquidate by consuming buy orders: sales tax
			this.totalHourlyExportMarketFees = this.totalHourlyExportRevenue * this.taxRate/100;
		}
		angular.forEach(planetList, function(planet){
			if(this.importOrderType == "buy"){
				planet.importMarketFees = planet.importCost * this.brokerFees/100;
			} else{
				planet.importMarketFees = 0;
			}
			if(this.exportOrderType == "sell"){
				planet.exportMarketFees = planet.exportRevenue * (this.taxRate + this.brokerFees)/100
			} else {
				planet.exportMarketFees = planet.exportRevenue * this.taxRate/100;
			}
		},this)
		console.log("market fees? ", this.totalHourlyImportMarketFees, this.totalHourlyExportMarketFees)
	}

	this.totalHourlyCustomsTax = 0;
	this.totalHourlyImportCost = 0;
	this.totalHourlyExportRevenue = 0;
	this.totalHourlyImportMarketFees = 0;
	this.totalHourlyExportMarketFees = 0;
	
	service.marketId = 10000002; //jita
	service.taxRate = 1.5;
	service.brokerFees = 1;
	service.importOrderType = "buy";
	service.exportOrderType = "buy";
	service.marketprices = {};

	service.marketCallOngoing = false;
	
	service.getMarketInfo = function(marketId) {
		console.log("inside Logic's function getMarketInfo");
		service.marketCallOngoing = true;

		var idList = [];
		angular.forEach(service.System.importExports, function(io){
			idList.push(io.id);
		})
		var promise = MarketService.getMarketInfo(service.marketId, idList)
		
		promise.then(function(successVal){
			console.log("Successful call with val:", angular.toJson(successVal))
			service.marketprices = successVal.statMap;
			service.refreshSystemMarketTotals();
			service.marketCallOngoing = false;
		}, function(failReason){
			console.log("failed market call with reason: ", failReason);
			service.marketCallOngoing = false;
		})
	}
	
	service.hello = 'Hello World mk3!';
	service.data = data;
	service.planets = planetList;
	service.basicTypeList = basicTypeList();
	service.advancedTypeList = advancedTypeList();
	service.hightechTypeList = hightechTypeList();
		
	return service;
	
}])