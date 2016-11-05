(function() {
    'use strict';

    angular
        .module('musicFinderApp')
        .factory('VoteSearch', VoteSearch);

    VoteSearch.$inject = ['$resource'];

    function VoteSearch($resource) {
        var resourceUrl =  'api/_search/votes/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
