

var app = angular.module('fizzBuzzApp', ['ngRoute', 'ngAnimate','ngResource','ngMaterial', 'ngMessages', 'material.svgAssetsCache', 'ui.bootstrap' ]);


 
app.config(function($routeProvider, $httpProvider) {
	$routeProvider.when('/main', {
		templateUrl: 'main.html',
		controller: 'MainController'
	}).otherwise({
	    redirectTo: '/'

	});
	
}).run(function($rootScope,$timeout ) {
	
	$rootScope.messeges=[];
	  
	$rootScope.addMessege = function(message, messageClass, strong) {
		var message = { message: message, messageClass: messageClass, strong: strong };
		$rootScope.messeges.push(message)

		// Simulate 2 seconds loading delay
		$timeout(function() {

			// Loadind done here - Show message for 3 more seconds.
			$timeout(function() {
				$rootScope.messeges.splice(0,1);
			}, 3000);

        }, 2000);
    };
	
	
});	 

app.controller('MainController', function($scope, $rootScope, $http, $window, $interval) {
	
	var auto = $interval(function() {
		$scope.listActiveFolowed();
		$scope.getInstaInfo($scope.insteSearch.user, $scope.currentPage);
	}, 15000);
	
	$scope.folowed = [];
	$scope.photos = [];
	$scope.currentPage = 1;
	$scope.insteSearch = {
			user : "",
			page: 0
		};
	
	$scope.user = '';
	$scope.result = '';
	$scope.title = 'Main';
	
	$scope.selected = '';
	
	
	
	 
	$scope.addUser = function() {
		$http({
			method : "POST",
			url : 'addFollowed',
			data : $scope.user, 
			headers : {
				'Content-Type' : 'text/plain'
			}
		}).then(_successAdd, _error);
	};
	
	$scope.listActiveFolowed = function() {
		$http({
			method : "GET",
			url : 'listActiveFolowed',
			headers : {
				'Content-Type' : 'application/json'
			}
		}).then(_successFolowed, _error);
	};
	
	$scope.getInstaInfo = function(user, page) {
		$scope.selected=user;
		$scope.insteSearch.user=user;
		$scope.insteSearch.page=page;
		$scope.currentPage=page;
		$http({ 
			method : "GET",
			url : 'getInstaInfo/'+angular.toJson($scope.insteSearch),
			headers : {
				'Content-Type' : 'text/html'
			}
		}).then(_successInfor, _error) ;
	};
	
	$scope.deleteUser = function(user) {
		$http({
			method : 'DELETE',
			url : 'deleteFollowed/' + user
		}).then(_successDel, _error);
	};
	
	$scope.listActiveFolowed();
	
	function _successDel(response) {
		$scope.listActiveFolowed();
		$rootScope.addMessege("You're unfollow "+$scope.selected, "alert-success", "Congratulations! ");
		$scope.user='';
	}
	
	function _successAdd(response) {
		$scope.listActiveFolowed();
		$rootScope.addMessege("You're following "+$scope.user, "alert-success", "Congratulations! ");
		$scope.user='';
	}
	function _successFolowed(response) {
		$scope.folowed = response.data;
		
//		$rootScope.addMessege("", "alert-success", "Congratulations! ");
	}
	
	function _successInfor(response) {
		$scope.photos = response.data;
		$scope.insteSearch.user=$scope.selected;
//		$rootScope.addMessege("Choose others numbers and play again", "alert-success", "Congratulations! ");
	}

	function _error(response) {
		$rootScope.addMessege(response.data.error, "alert-danger", "Error: ");
//		$interval.cancel(auto);
	}
});

