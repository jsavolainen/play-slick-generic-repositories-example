package services

import play.api.libs.json.Json
import repositories.UserRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserService @Inject() (repo: UserRepository)(implicit ec: ExecutionContext) {
  import io.scalaland.chimney.dsl._

  def findAll(): Future[Seq[UserService.User]] = repo.fetchAll().map(_.map(fromRepo))

  def findUser(userId: Int): Future[Option[UserService.User]] =
    repo.findById(userId).map(_.map(fromRepo))

  def createUser(email: String, name: String): Future[UserService.User] =
    repo
      .insert(repo.User(email = email, name = name))
      .map(fromRepo)

  private def fromRepo: repo.User => UserService.User =
    _.into[UserService.User].withFieldComputed(_.id, _.id.get).transform

}

object UserService {
  case class User(id: Int, email: String, name: String)

  implicit val UserFormat = Json.format[User]
}
