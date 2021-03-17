package repositories

import common.slick._
import models.Tables
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.Inject

class UserRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)
  extends EntityActions
    with JdbcProfileProvider
    with DatabaseProvider
    with Tables {
  protected val dbConfig = dbConfigProvider.get[JdbcProfile]
  override val profile   = dbConfig.profile
  override val db        = dbConfig.db

  import profile.api._

  override type EntityTable = UserTable
  override type Entity      = User

  override def tableQuery = UserTable

  override protected def filterById(id: Int) = tableQuery.filter(_.id === id)

  override def idLens: Lens[User, Int] = Lens(get = _.id.get, set = (e, id) => e.copy(id = Some(id)))
}
