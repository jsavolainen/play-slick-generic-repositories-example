package controllers

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import services.UserService

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserController @Inject() (userService: UserService, val controllerComponents: ControllerComponents)(implicit
  ec: ExecutionContext
) extends BaseController {
  import UserController._

  def getUsers(): Action[AnyContent] =
    Action.async { implicit request =>
      userService.findAll().map(user => Ok(Json.toJson(user)))
    }

  def getUser(userId: Int): Action[AnyContent] =
    Action.async { implicit request =>
      userService.findUser(userId).map {
        case Some(user) => Ok(Json.toJson(user))
        case None => NotFound
      }
    }

  def createUser(): Action[AnyContent] =
    Action.async { implicit request =>
      newUserForm
        .bindFromRequest()
        .fold(
          error => Future.successful(BadRequest(error.toString)),
          form => userService.createUser(form.email, form.name).map(user => Ok(Json.toJson(user)))
        )
    }
}

object UserController {
  case class NewUserForm(email: String, name: String)

  val newUserForm: Form[NewUserForm] = Form {
    mapping(
      "email" -> email,
      "name" -> nonEmptyText
    )(NewUserForm.apply)(NewUserForm.unapply)
  }

  implicit val NewUserFromFormat = Json.format[NewUserForm]
}
