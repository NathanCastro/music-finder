(function() {
    'use strict';

    angular
        .module('musicFinderApp')
        .controller('VoteDetailController', VoteDetailController);

    VoteDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Vote', 'Music', 'Tag', 'User'];

    function VoteDetailController($scope, $rootScope, $stateParams, previousState, entity, Vote, Music, Tag, User) {
        var vm = this;

        vm.vote = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('musicFinderApp:voteUpdate', function(event, result) {
            vm.vote = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
