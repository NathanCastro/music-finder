(function() {
    'use strict';

    angular
        .module('musicFinderApp')
        .controller('VoteDialogController', VoteDialogController);

    VoteDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Vote', 'Music', 'Tag', 'User'];

    function VoteDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Vote, Music, Tag, User) {
        var vm = this;

        vm.vote = entity;
        vm.clear = clear;
        vm.save = save;
        vm.music = Music.query();
        vm.tags = Tag.query();
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.vote.id !== null) {
                Vote.update(vm.vote, onSaveSuccess, onSaveError);
            } else {
                Vote.save(vm.vote, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('musicFinderApp:voteUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
