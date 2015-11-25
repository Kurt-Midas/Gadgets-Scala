/*global require, requirejs */

'use strict';

requirejs.config({
  paths: {
    'angular': ['../lib/angularjs/angular'],
    'angular-route': ['../lib/angularjs/angular-route'],
    'piController': ['/planetary/controller/PlanetController']
  },
  shim: {
    'angular': {
      exports : 'angular'
    },
    'angular-route': {
      deps: ['angular'],
      exports : 'angular'
    }
  }
});

require(['angular', './controllers', './directives', './filters', './services', 'angular-route', 
         'piController'],
  function(angular, controllers) {

    // Declare app level module which depends on filters, and services

    angular.module('gadgets', ['gadgets.filters', 'gadgets.services', 'gadgets.directives', 'ngRoute']).
      config(['$routeProvider', function($routeProvider) {
        $routeProvider.when('/pi', {templateUrl: 'partials/partial1.html', controller: 'planetAppController'});
        $routeProvider.when('/scrap', {templateUrl: 'partials/partial2.html', controller: controllers.ScrapController});
        $routeProvider.otherwise({redirectTo: '/pi'});
      }]);

    angular.bootstrap(document, ['gadgets','piController']);

});
