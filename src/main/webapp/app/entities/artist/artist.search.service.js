(function() {
    'use strict';

    angular
        .module('musicFinderApp')
        .factory('ArtistSearch', ArtistSearch);

    ArtistSearch.$inject = ['$resource'];

    function ArtistSearch($resource) {
        var resourceUrl =  'api/_search/artists/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
