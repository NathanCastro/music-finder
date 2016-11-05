(function() {
    'use strict';

    angular
        .module('musicFinderApp')
        .controller('MusicDetailController', MusicDetailController);

    MusicDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Music', 'Artist'];

    function MusicDetailController($scope, $rootScope, $stateParams, previousState, entity, Music, Artist) {
        var vm = this;

        vm.music = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('musicFinderApp:musicUpdate', function(event, result) {
            vm.music = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
