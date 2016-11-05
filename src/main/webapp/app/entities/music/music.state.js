(function() {
    'use strict';

    angular
        .module('musicFinderApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('music', {
            parent: 'entity',
            url: '/music?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'musicFinderApp.music.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/music/music.html',
                    controller: 'MusicController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('music');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('music-detail', {
            parent: 'entity',
            url: '/music/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'musicFinderApp.music.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/music/music-detail.html',
                    controller: 'MusicDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('music');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Music', function($stateParams, Music) {
                    return Music.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'music',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('music-detail.edit', {
            parent: 'music-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/music/music-dialog.html',
                    controller: 'MusicDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Music', function(Music) {
                            return Music.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('music.new', {
            parent: 'music',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/music/music-dialog.html',
                    controller: 'MusicDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                title: null,
                                lyrics: null,
                                link: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('music', null, { reload: 'music' });
                }, function() {
                    $state.go('music');
                });
            }]
        })
        .state('music.edit', {
            parent: 'music',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/music/music-dialog.html',
                    controller: 'MusicDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Music', function(Music) {
                            return Music.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('music', null, { reload: 'music' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('music.delete', {
            parent: 'music',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/music/music-delete-dialog.html',
                    controller: 'MusicDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Music', function(Music) {
                            return Music.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('music', null, { reload: 'music' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
