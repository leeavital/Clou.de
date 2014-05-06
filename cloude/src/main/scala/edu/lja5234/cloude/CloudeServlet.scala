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


/**
 * Singleton :(
 */
object DevEnvironment{

  val compiler = javax.tools.ToolProvider.getSystemJavaCompiler();

  def saveFile ( f : SourceFile ) = {
    val SourceFile(name, source) = f

    val ofile = new java.io.File( "workspace/" + name )
    val ps = new java.io.PrintWriter( ofile )

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


  def run (r : RunConfig ) = {

    // swap out error streams -- this is really gross, I should probably subclass PrintStream
    val sysout = System.out
    val syserr = System.err
    val ss = new SimpleStream 
    val mockout = new java.io.PrintStream(  ss )
    System.setOut( mockout )
    System.setErr( mockout )

    // load the classes
    val classpath = Array( new java.io.File( "workspace_dist" ).toURL )
    val classloader = new java.net.URLClassLoader( classpath );

    // load the main class and run the main method with the given args
    val main = classloader.loadClass( r.main );
    main.newInstance.asInstanceOf[{def main(args : Array[String] ) : Unit }].main( r.args.toArray );

    System.setOut( sysout )
    System.setErr( syserr )

    ss.getContents
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
    DevEnvironment.run( runConfig )
  }


  post( "/compile" ){
    val sf  : String = parsedRequestBody.extract[String];
    val errors = DevEnvironment.compileFile( sf  )

    errors
  }
}
