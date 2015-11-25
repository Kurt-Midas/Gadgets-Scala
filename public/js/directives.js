/*global define */

'use strict';

define(['angular'], function(angular) {

/* Directives */

angular.module('gadgets.directives', []).
  directive('appVersion', ['version', function(version) {
    return function(scope, elm, attrs) {
      elm.text(version);
    };
  }]);

});