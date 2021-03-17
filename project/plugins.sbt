addSbtPlugin("com.typesafe.play"        % "sbt-plugin"          % "2.8.7")
addSbtPlugin("org.foundweekends.giter8" % "sbt-giter8-scaffold" % "0.11.0")
addSbtPlugin("com.github.tototoshi"     % "sbt-slick-codegen"   % "1.4.0")
addSbtPlugin("io.github.davidmweber"    % "flyway-sbt"          % "7.4.0")

libraryDependencies += "com.h2database" % "h2" % "1.4.200"
