(function() {
    'use strict';
    angular
        .module('musicFinderApp')
        .factory('Music', Music);

    Music.$inject = ['$resource'];

    function Music ($resource) {
        var resourceUrl =  'api/music/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
