package edu.lja5234.cloude

import org.scalatra._
import scalate.ScalateSupport


import javax.tools._

// JSON-related libraries
import org.json4s.{DefaultFormats, Formats}

// JSON handling support from Scalatra
import org.scalatra.json._

import scala.collection.JavaConverters._

import scala.util.{Try, Success, Failure}





/**
 * name will have the .java suffix
 */
case class SourceFile( name : String, source : String );


/**
 * main: the name of the main class
 * args: a list of arguments to pass to the command line
 */
case class RunConfig( main: String, args: List[String] );

object DevEnvironment{

  val compiler = javax.tools.ToolProvider.getSystemJavaCompiler();

  def saveFile ( f : SourceFile ) = {
    val SourceFile(name, source) = f

    val ofile = new java.io.File( "workspace/" + name )
    val ps = new java.io.PrintWriter( ofile )
    ps.print( source )

    ps.close
  }



  def compileFile( fname : String ) = {
    val filepath = "workspace/" + fname
    val compiler = ToolProvider.getSystemJavaCompiler();
    var errStream = new ErrorStream
    compiler.run( System.in, System.out,  errStream, filepath, "-d",  "workspace_dist" )
    errStream.errors
  }


  def compileAll( ) = {
    val compiler= ToolProvider.getSystemJavaCompiler()
    compiler.run( System.in, System.out, System.err, "workspace/*.java", "-d",  "workspace_dist" ) 
  }



}



class CloudeServlet extends CloudeStack with JacksonJsonSupport{

  protected implicit val jsonFormats : Formats = DefaultFormats
  var parsedRequestBody : org.json4s.JValue = null

  before(){
    contentType = formats( "json" )
    parsedRequestBody = Try[org.json4s.JValue]( parse( org.json4s.StringInput( request.body ) ) ) match {
      case Success(v) => v
      case Failure(e) => null

    }
  }

  // save a file -- takes a SourceFile
  post( "/file" ) {
    val file = parsedRequestBody.extract[SourceFile]
    DevEnvironment saveFile( file )
    println ( request.body )
    println( file )
    file
  }

  // get the contents of a file -- returns a source file
  get( "/file/:fname" ) {
    val source = scala.io.Source.fromFile( "workspace/" + params("fname") )
    val lines = source.mkString
    source.close()

    SourceFile( params("fname"), lines )
  }


  delete( "/file/:fname" ){
    val file = new java.io.File( "workspace/" + params( "fname" ) )
    file.delete
  }


  // get a list of files
  get( "/files" ){
    val ws = new java.io.File( "workspace" )
    (ws.list) toList
  }

  get( "/compile" ){
    DevEnvironment.compileAll( )
  }


  post( "/run" ) {
    val runConfig : RunConfig = parsedRequestBody.extract[RunConfig]
  }


  post( "/compile" ){
    val sf  : String = parsedRequestBody.extract[String];
    DevEnvironment.compileFile( sf  )
    sf
  }

}
