name := """slick-starting-on-the-right-foot"""

version := "1.0"

scalaVersion := "2.11.8"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  "com.github.tminglei"    %% "slick-pg_date2"       % "0.12.2",
  "com.github.tminglei"    %% "slick-pg"             % "0.12.2",
  "org.postgresql"         %  "postgresql"           % "9.4.1209",
  "com.zaxxer"             % "HikariCP"              % "2.3.12" withSources(),
  "com.typesafe.slick"     %% "slick-hikaricp"       % "3.1.1"  withSources()
    exclude("com.zaxxer", "HikariCP-java6"),
  "ch.qos.logback"         %  "logback-classic"      % "1.1.7",
  "com.typesafe.slick"     %% "slick"            	   % "3.1.1"  withSources(),
  "mysql"                  %  "mysql-connector-java" % "5.1.36",
  //
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "junit"                  % "junit"               % "4.12"  % Test
)
