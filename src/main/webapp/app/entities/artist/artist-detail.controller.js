(function() {
    'use strict';

    angular
        .module('musicFinderApp')
        .controller('ArtistDetailController', ArtistDetailController);

    ArtistDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Artist'];

    function ArtistDetailController($scope, $rootScope, $stateParams, previousState, entity, Artist) {
        var vm = this;

        vm.artist = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('musicFinderApp:artistUpdate', function(event, result) {
            vm.artist = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
