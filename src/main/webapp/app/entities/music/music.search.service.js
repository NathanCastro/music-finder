(function() {
    'use strict';

    angular
        .module('musicFinderApp')
        .factory('MusicSearch', MusicSearch);

    MusicSearch.$inject = ['$resource'];

    function MusicSearch($resource) {
        var resourceUrl =  'api/_search/music/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
