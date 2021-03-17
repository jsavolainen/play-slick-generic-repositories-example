package common.slick

import slick.jdbc.JdbcProfile

trait JdbcProfileProvider {
  val profile: JdbcProfile
}
