// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
//addSbtPlugin("play" % "sbt-plugin" % "2.0.3")

resolvers ++= Seq(
  Resolver.file("Local Repository", file("<projdir>/Play20/repository/local"))(Resolver.ivyStylePatterns)
)

addSbtPlugin("play" % "sbt-plugin" % "2.1-SNAPSHOT")