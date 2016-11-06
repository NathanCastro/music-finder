(function() {
    'use strict';

    angular
        .module('musicFinderApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('vote', {
            parent: 'entity',
            url: '/vote?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'musicFinderApp.vote.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/vote/votes.html',
                    controller: 'VoteController',
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
                    $translatePartialLoader.addPart('vote');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('vote-detail', {
            parent: 'entity',
            url: '/vote/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'musicFinderApp.vote.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/vote/vote-detail.html',
                    controller: 'VoteDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('vote');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Vote', function($stateParams, Vote) {
                    return Vote.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'vote',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('vote-detail.edit', {
            parent: 'vote-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/vote/vote-dialog.html',
                    controller: 'VoteDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Vote', function(Vote) {
                            return Vote.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('vote.new', {
            parent: 'vote',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/vote/vote-dialog.html',
                    controller: 'VoteDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('vote', null, { reload: 'vote' });
                }, function() {
                    $state.go('vote');
                });
            }]
        })
        .state('vote.edit', {
            parent: 'vote',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/vote/vote-dialog.html',
                    controller: 'VoteDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Vote', function(Vote) {
                            return Vote.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('vote', null, { reload: 'vote' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('vote.delete', {
            parent: 'vote',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/vote/vote-delete-dialog.html',
                    controller: 'VoteDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Vote', function(Vote) {
                            return Vote.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('vote', null, { reload: 'vote' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
