package edu.lja5234.cloude

import org.scalatra._
import scalate.ScalateSupport


import javax.tools._

// JSON-related libraries
import org.json4s.{DefaultFormats, Formats}

// JSON handling support from Scalatra
import org.scalatra.json._

// JDK
import scala.collection.JavaConverters._
import java.io.{File => JFile}

import scala.util.{Try, Success, Failure}

/**
 * name will have the .java suffix and be relative to workspace
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

    val localuri = "workspace/" + name
    val pattern = """(.*/)([^\.]*\.java)""".r
    val pattern( pre, filename ) = localuri


    // create the directories
    (new JFile( pre )).mkdirs()

    val ofile = new JFile( pre + filename )
    println( ofile )

    val ps = new java.io.PrintWriter( ofile )

    ps.println( f.source )
    ps.close
    println( "wrote to " + f.name )
  }

  def deleteFile( file :String ) = {
    (new JFile("./workspace/" + file)).delete

  }



  def compileFile( fname : String ) = {
    // delete the original class file
    val clspath = ("workspace_dist/" + fname).replace( ".java", ".class" )
    val f = new java.io.File( clspath )
    f.delete()


    val filepath = "workspace/" + fname
    val compiler = ToolProvider.getSystemJavaCompiler();
    var errStream = new ErrorStream
    compiler.run( System.in, System.out,  errStream, filepath, "-d",  "workspace_dist" )
    errStream.errors
  }


  def compileAll( ) = {


    val javaFiles = DevEnvironment.files.map( x => new JFile( "workspace/" + x ) )
    println( javaFiles )

    val compiler = ToolProvider.getSystemJavaCompiler();
    //note that the "compiler" may be null if your environment path is not set correctly


    val dc = new DiagnosticCollector[JavaFileObject];

    val sjfm = compiler.getStandardFileManager(dc, null, null);

    val fileObjects = sjfm.getJavaFileObjectsFromFiles(javaFiles.asJava); //javaFiles is an array of File

    val options = List("-d", "workspace_dist" ).asJava

    val errorstream =  new ErrorStream()
    val ewriter = new java.io.PrintWriter( errorstream )
    compiler.getTask(null, sjfm, dc, options, null, fileObjects).call();

    for( d <- dc.getDiagnostics.asScala ) {
      errorstream.write( d.toString.getBytes )
    }

    errorstream.errors
  }


  def run (r : RunConfig ) : String = {
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


  // get a list of all the files in this dev environment (relative to the workspace)
  def files : List[String] = {

    val root : JFile = new JFile( "./workspace" )
    val prefix = root.getCanonicalPath + "/"
    def helper( f : JFile ) : List[String] = {
        f.list match {
          case null =>
            List( f.getCanonicalPath replace(prefix, "")  )
          case (subs: Array[String]) =>
            val subfiles = subs.toList.map( new JFile( f.getCanonicalPath, _ ) )
            val sublistings : List[List[String]]= subfiles.map( helper( _ ) )
            List.flatten( sublistings )
        }
    }

    val e = helper( root )
    e
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
  get( "/file/*" ) {
    val Seq(fname : String ) = multiParams( "splat" )

    val source = scala.io.Source.fromFile( "workspace/" + fname )
    val lines = source.mkString
    source.close()

    SourceFile( fname , lines )
  }


  delete( "/file/:fname" ){
    val file = new java.io.File( "workspace/" + params( "fname" ) )
    file.delete
  }


  // get a list of all the source files
  // as  a list of strings
  get( "/files" ){
    (DevEnvironment.files : List[String])
  }



  post( "/run" ) {
    val runConfig : RunConfig = parsedRequestBody.extract[RunConfig]
    DevEnvironment.run( runConfig )
  }


  get( "/compile" ){
    val errors = DevEnvironment.compileAll

    errors
  }
}
