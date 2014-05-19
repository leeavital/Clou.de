import edu.lja5234.cloude._

import org.scalatest._



class EnvironmentSpec extends FlatSpec with Matchers with BeforeAndAfter{

  before  {

    DevEnvironment.files.foreach( {x =>
        println( "deleting " + x );
        (new java.io.File("workspace/" + x)).delete
      })

  }

  "DevEnvironment" should "create and delete files correctly" in {

      DevEnvironment.deleteFile( "test/foo/Main.java" )
      val s = SourceFile( "Main.java", "public class Main{}") 
      DevEnvironment.saveFile( s )
      val files = DevEnvironment.files
      files should contain("Main.java")
      val n = files.size


      DevEnvironment.deleteFile( "Main.java" )
      val afterdeletefiles = DevEnvironment.files
      afterdeletefiles should not contain ("Main.java")
      afterdeletefiles.size should be ( n - 1 )


    }


    it should "create files and work" in {
      DevEnvironment.saveFile( SourceFile("Main.java", "public class Main{}" ) )

      val str = io.Source.fromFile( "./workspace/Main.java" ).getLines mkString "\n"

      str should be("public class Main{}" )

    }

    it should "create files recusively" in {

      DevEnvironment.deleteFile( "test/foo/Main.java" )
      val s = SourceFile( "test/foo/Main.java", "public class Main{}") 
      DevEnvironment.saveFile( s )
      val files = DevEnvironment.files
      files should contain("test/foo/Main.java")
      val n = files.size


      DevEnvironment.deleteFile( "test/foo/Main.java" )
      val afterdeletefiles = DevEnvironment.files
      afterdeletefiles should not contain ("Main.java")
      afterdeletefiles.size should be ( n - 1 )
    }


    it should "build single source files correctly" in {
        val source = """public class Main{
            public static void main(String[] args){
                System.out.println( "FOO" );
            }
          }"""


         val sourceFile = new SourceFile( "Main.java", source )
         DevEnvironment.saveFile( sourceFile )

         val errors = DevEnvironment.compileAll
         errors.size should be (0)
      }


    it should "catch errors in single source files" in {
         val badsource = "publi {}"
         val sourceFile2 = SourceFile("Main.java", badsource)
         println( sourceFile2 )

         DevEnvironment.saveFile( sourceFile2 )

         println( "FOO" + scala.io.Source.fromFile( "workspace/Main.java" ).mkString )

         val results = DevEnvironment.compileAll

         results.size should not be (0)

    }


    it should "build several source files correctly" in {
      val source = """public class Main{
        public static void main(String[] args){
          Object c = new Car();
          System.out.println( c );
        }
      }"""

      val sourceCar = """public class Car{
          public String toString( ) {
            return "Tesla Model T";
          }
      }"""

      DevEnvironment.saveFile( SourceFile("Main.java", source ))
      DevEnvironment.saveFile( SourceFile("Car.java", sourceCar))

      val errs = DevEnvironment.compileAll
      errs should be ( List() )
    }






 //  "DevEnvironment" should "pop values in last-in-first-out order" in {
 //    val stack = new Stack[Int]
 //      stack.push(1)
 //      stack.push(2)
 //      stack.pop() should be (2)
 //      stack.pop() should be (1)
 //  }

 //  it should "throw NoSuchElementException if an empty stack is popped" in {
 //    val emptyStack = new Stack[Int]
 //      a [NoSuchElementException] should be thrownBy {
 //        emptyStack.pop()
 //      }
 //  }
}
