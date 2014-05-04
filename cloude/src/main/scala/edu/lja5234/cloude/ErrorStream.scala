package edu.lja5234.cloude


case class JavaCompilerError( line  : Int, file : String, message : String )


class ErrorStream  extends java.io.OutputStream {

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


  def errors : List[JavaCompilerError] = {
    val errstrings = ("""\n|\r""".r).split( str )

   val l1 = errstrings.map { errstring =>
     val  errLine = """(.*\.java):(\d+): error: (.*)""".r


     errstring.trim match {
         case errLine( fileName : String , lineNo : String , message : String ) =>
           Some(JavaCompilerError( Integer.parseInt( lineNo ), fileName , message ))
         case _ => None
     }
   }.toList

   l1.flatten

  }
}
