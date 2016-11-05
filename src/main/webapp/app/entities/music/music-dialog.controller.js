(function() {
    'use strict';

    angular
        .module('musicFinderApp')
        .controller('MusicDialogController', MusicDialogController);

    MusicDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Music', 'Tag', 'Artist'];

    function MusicDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Music, Tag, Artist) {
        var vm = this;

        vm.music = entity;
        vm.clear = clear;
        vm.save = save;
        vm.tags = Tag.query();
        vm.artists = Artist.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.music.id !== null) {
                Music.update(vm.music, onSaveSuccess, onSaveError);
            } else {
                Music.save(vm.music, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('musicFinderApp:musicUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
