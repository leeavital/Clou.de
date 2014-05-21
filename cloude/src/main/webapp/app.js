var ClouDe = angular.module( 'ClouDe', [ 'ui.bootstrap', 'ui.bootstrap.tabs', 'ui.ace'] );
console.log( ClouDe );


// http://stackoverflow.com/questions/14076783/angularjs-focusing-a-input-element-when-a-checkbox-is-clicked
// ClouDe.directive('xngFocus', function() {
//   return function(scope, element, attrs) {
//     scope.$watch(attrs.xngFocus,  function (newValue) {
//       newValue && element.focus();
//     },true);
//   };
// });

var Ctl = ClouDe.controller( 'Ctl', [ '$scope', '$http', '$modal', function( $scope, $http, $modal ){

    $scope.getSourceFiles = function( cb ){
      $http.get( '/files' ).then( function(response) {
        $scope.sourceFiles = response.data;

        cb( $scope.sourceFiles);
      });
    }


    var openFile = $scope.openFile = function( file ){
    $http.get( '/file/' + file ).then( function(response){
      $scope.sourceFile = response.data;
      });
    }


    $scope.save = function(){
      $http.post( '/file', $scope.sourceFile );
    }


    $scope.run = function(){
      var runconfig = $scope.currentRun;
      $http.post( '/run', runconfig ).then( function (response) {
        $scope.last_run_output = response.data;
      });
    }

    $scope.getSourceFiles( function( files ){
        $scope.openFile( files[0] );
    });


    $scope.openNewFileWizard = function(){
      var superscope = $scope.getSourceFiles;
      $modal.open( {
        templateUrl: 'partials/newfile.html',
        controller: function( $modalInstance, $scope ){
          $scope.addClass = function( filename, type ){
            // TODO better default stuff
            var sf = {
              name: filename + '.java',
              source: 'public class ' + filename + ' {} '
            }
            $http.post( '/file', sf ).then( function( ){
                openFile( filename + '.java' );
                $modalInstance.close();

                superscope.getSourceFiles( function(){} );
            });
          }
        },
        size: {width: '800px'}
      });

    }


    $scope.openConfigurationWizard = function(){
      var superscope = $scope;
      $modal.open({
        templateUrl: 'partials/configuration.html',
        controller: function( $modalInstance, $scope ){
          $scope.close =  function(){
            $modalInstance.close();
          }

          $scope.setCurrent = function( run ){
            $scope.currentRun = run;
          }


          $scope.$watch( 'configuration', function( e ) {
            $http.post( '/config', $scope.configuration );
          });
        },
        scope: $scope
      });
    }


    $scope.compileErrors = [];
    $scope.build = function(){
        $http.post( '/file', $scope.sourceFile ).then( function(){
          $http.get( '/compile' ).then( function( response ){
            $scope.compileErrors = response.data
          });
        });
    }


    // populate the configuration
    $http.get( '/config' ).then ( function( response ) {
      $scope.configuration = response.data;
      $scope.currentRun = $scope.configuration.runs[0];
    });

}]);


console.log( Ctl );
