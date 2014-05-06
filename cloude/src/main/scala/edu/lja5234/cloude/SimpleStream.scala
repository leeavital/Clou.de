package edu.lja5234.cloude

import java.io.OutputStream

class SimpleStream  extends OutputStream {

  var str = ""

  override def close() = {

  }

  override def write( b : Array[Byte] ) = {
   println( "got some error" )
   str += new String( b )
  }


  override def write( i : Int ) = {
    str += new String( Array( i.toByte ) )
  }


  override def flush() = {

  }


  def getContents = {
    str
  }


}
