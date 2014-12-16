scalaVersion := "2.11.4"

name := "Vision"

version := "0.5"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.1.0"

libraryDependencies += "junit" % "junit" % "4.10"

libraryDependencies += "com.novocode" % "junit-interface" % "0.10" % "test"

resolvers += "http://maven.xwiki.org" at "http://maven.xwiki.org/externals"

resolvers += "OpenIMAJ maven releases repository" at "http://maven.openimaj.org"

libraryDependencies += "org.openimaj" % "image-processing" % "1.3.1"

libraryDependencies += "org.openimaj" % "video-processing" % "1.3.1"

libraryDependencies += "org.openimaj" % "core-video-capture" % "1.3.1"

libraryDependencies += "com.twelvemonkeys.imageio" % "imageio-core" % "3.0.1"

libraryDependencies += "com.twelvemonkeys.common" % "common-lang" % "3.0.1"







