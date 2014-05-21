javaOptions += "-XX:MaxPermSize=512M" 

javaOptions += "Xmx1500M"


libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0.1-SNAP" % "test"


scalacOptions := Seq("-feature", "-deprecation")


