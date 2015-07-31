name := "play-endpoints"

enablePlugins(PlayScala)

disablePlugins(PlayLayoutPlugin)

scalaVersion := "2.11.7"

libraryDependencies += "com.chuusai" %% "shapeless" % "2.2.5"

libraryDependencies ++= Seq(
  "validation-core",
  "validation-json").map("io.github.jto" %% _ % "1.1")