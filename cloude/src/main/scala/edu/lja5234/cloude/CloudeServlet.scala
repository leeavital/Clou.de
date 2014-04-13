package edu.lja5234.cloude

import org.scalatra._
import scalate.ScalateSupport


// JSON-related libraries
import org.json4s.{DefaultFormats, Formats}

// JSON handling support from Scalatra
import org.scalatra.json._

import scala.collection.JavaConverters._

import scala.util.{Try, Success, Failure}




case class SourceFile( name : String, source : String );



object DevEnvironment{

  def saveFile ( f : SourceFile ) = {
    val SourceFile(name, source) = f

    val ofile = new java.io.File( "workspace/" + name )
    val ps = new java.io.PrintWriter( ofile )
    ps.print( source )

    ps.close
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
    "ok"
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

    "compile is unimplemented"
  }

}
