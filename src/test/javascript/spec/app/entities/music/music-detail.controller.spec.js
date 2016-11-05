'use strict';

describe('Controller Tests', function() {

    describe('Music Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockMusic, MockTag, MockArtist;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockMusic = jasmine.createSpy('MockMusic');
            MockTag = jasmine.createSpy('MockTag');
            MockArtist = jasmine.createSpy('MockArtist');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Music': MockMusic,
                'Tag': MockTag,
                'Artist': MockArtist
            };
            createController = function() {
                $injector.get('$controller')("MusicDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'musicFinderApp:musicUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
