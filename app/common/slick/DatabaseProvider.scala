package common.slick

import slick.jdbc.JdbcBackend

trait DatabaseProvider {
  val db: JdbcBackend#Database
}
