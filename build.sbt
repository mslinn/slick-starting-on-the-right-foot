name := """slick-starting-on-the-right-foot"""

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  //"com.h2database"       %  "h2"                   % "1.4.187"   %   "test",
  "com.github.tminglei"  %% "slick-pg_date2"       % "0.12.2",
  "com.github.tminglei"  %% "slick-pg"             % "0.12.2",
  "org.postgresql"       %  "postgresql"           % "9.4.1209",
  "com.zaxxer"           % "HikariCP"              % "2.3.12" withSources(),
  "com.typesafe.slick"   %% "slick-hikaricp"       % "3.1.1"  withSources()
    exclude("com.zaxxer", "HikariCP-java6"),
  "ch.qos.logback"       %  "logback-classic"      % "1.1.7",
  "com.typesafe.slick"   %% "slick"            	   % "3.1.1"  withSources(),
  "mysql"                %  "mysql-connector-java" % "5.1.36",
  "org.scalatest"        %% "scalatest"	           % "2.2.6"     %   "test"
)
