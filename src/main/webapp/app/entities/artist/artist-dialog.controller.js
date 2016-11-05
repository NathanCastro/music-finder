(function() {
    'use strict';

    angular
        .module('musicFinderApp')
        .controller('ArtistDialogController', ArtistDialogController);

    ArtistDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Artist'];

    function ArtistDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Artist) {
        var vm = this;

        vm.artist = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.artist.id !== null) {
                Artist.update(vm.artist, onSaveSuccess, onSaveError);
            } else {
                Artist.save(vm.artist, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('musicFinderApp:artistUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
