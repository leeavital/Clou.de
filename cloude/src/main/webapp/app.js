var ClouDe = angular.module( 'ClouDe', [ 'ui.bootstrap'] );
console.log( ClouDe );

var Ctl = ClouDe.controller( 'Ctl', [ '$scope', '$http', function( $scope, $http ){

    $scope.getSourceFiles = function( cb ){
    $http.get( '/files' ).then( function(response) {
      $scope.sourceFiles = response.data;

      cb( $scope.sourceFiles);
      });
    }


    $scope.openFile = function( file ){
    $http.get( '/file/' + file ).then( function(response){
      $scope.sourceFile = response.data;
      });
    }


    $scope.save = function(){
    $http.post( '/file', $scope.sourceFile );
    }

    $scope.getSourceFiles( function( files ){
        $scope.openFile( files[0] ); 
    });

}]);


console.log( Ctl );


