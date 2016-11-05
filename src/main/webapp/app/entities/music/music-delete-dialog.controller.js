(function() {
    'use strict';

    angular
        .module('musicFinderApp')
        .controller('MusicDeleteController',MusicDeleteController);

    MusicDeleteController.$inject = ['$uibModalInstance', 'entity', 'Music'];

    function MusicDeleteController($uibModalInstance, entity, Music) {
        var vm = this;

        vm.music = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Music.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
