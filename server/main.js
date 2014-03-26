var express = require( 'express' );
var http = require( 'http' );

var app = express();


app.use( express.static( __dirname + '/../client'  ) );
app.use(express.json() );



app.get( '/project', function( request, response ){
  response.send( app.get( 'project' ) );
});


app.post( '/project', function( request, response ){
  console.log( request.param('project') );
  response.send( 'ok' );
});


http.createServer( app ).listen( 7000, function( error ){
    if( error ){
      console.log( 'error ', error  );
    }
});
