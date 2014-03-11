name := "cloudfinishService"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "org.apache.jena" % "jena-core" % "2.11.1"
)     

play.Project.playJavaSettings
