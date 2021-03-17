import slick.codegen.SourceCodeGenerator
import slick.{model => m}

lazy val commonSettings = Seq(
  organization := "com.example",
  version := "1.0.0",
  scalaVersion := "2.13.5"
)

val databaseUrl = "jdbc:h2:mem:test;MODE=PostgreSQL;DATABASE_TO_UPPER=FALSE;DB_CLOSE_DELAY=1"
val databaseUser = "SA"
val databasePassword = ""

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, CodegenPlugin, FlywayPlugin)
  .settings(commonSettings: _*)
  .settings(
    name := "play-generic-slick-repositories-example",
    libraryDependencies ++= Seq(
      guice,
      ws,
      "com.typesafe.play"      %% "play-slick"         % "5.0.0",
      "org.postgresql"          % "postgresql"         % "42.2.10",
      "org.flywaydb"           %% "flyway-play"        % "7.7.0",
      "io.scalaland"           %% "chimney"            % "0.6.1",
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0"   % Test,
      "com.h2database"          % "h2"                 % "1.4.200" % Test
    ),
    flywayUrl := databaseUrl,
    flywayUser := databaseUser,
    flywayPassword := databasePassword,
    flywayLocations := Seq("filesystem:conf/db/migration/default"),
    slickCodegenDatabaseUrl := databaseUrl,
    slickCodegenDatabaseUser := databaseUser,
    slickCodegenDatabasePassword := databasePassword,
    slickCodegenDriver := slick.jdbc.H2Profile,
    slickCodegenJdbcDriver := "org.h2.Driver",
    slickCodegenOutputPackage := "models",
    slickCodegenExcludedTables := Seq("flyway_schema_history"),
    slickCodegenCodeGenerator := { (model: m.Model) =>
      new SourceCodeGenerator(model) {
        override def tableName: String => String = (dbName: String) => dbName.toCamelCase + "Table"
        override def entityName: String => String = (dbName: String) => dbName.toCamelCase

        override def code: String =
          """
            |import common.slick._
            |""".stripMargin + super.code

        override def Table: slick.model.Table => TableDef =
          model =>
            new Table(model.copy(name = model.name.copy(schema = Some("public")))) {
              override def Column =
                new Column(_) {
                  override def asOption: Boolean = autoInc
                  override def rawType =
                    model.tpe match {
                      case "java.sql.Timestamp" => "java.time.Instant"
                      case _ => super.rawType
                    }
                }

              override def TableClass =
                new TableClass {
                  val extraParents: Seq[String] = model.name.table match {
                    case _ => Seq("EntityTableLike")
                  }

                  override def parents: Seq[String] =
                    super.parents ++ extraParents
                }

              override def EntityType =
                new EntityType {
                  val extraParents: Seq[String] = model.name.table match {
                    case _ => Seq("EntityLike")
                  }

                  override def parents: Seq[String] =
                    super.parents ++ extraParents
                }
            }
      }
    },
    slickCodegen := slickCodegen.dependsOn(flywayMigrate).value,
    sourceGenerators in Compile += slickCodegen.taskValue,
    slickCodegenOutputDir := (sourceManaged in Compile).value / "slick",
    javaOptions in Test += "-Dconfig.path=conf/application.dev.conf"
  )
