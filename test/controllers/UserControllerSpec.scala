package controllers

import controllers.UserController._
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.libs.json.Json
import play.api.test.Injecting
import play.api.mvc.Result
import play.api.test.Helpers._
import play.api.test._
import services.UserService

import scala.concurrent.Future

class UserControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "UserController" should {
    "perform all CRUD operations" in {
      val createResult: Future[Result] =
        route(app, FakeRequest(POST, "/users"), Json.toJson(NewUserForm(email = "test@example.com", name = "Test"))).get
      status(createResult) mustBe OK
      val user = Json.fromJson[UserService.User](contentAsJson(createResult)).get

      val getUserResult = route(app, FakeRequest(GET, s"/users/${user.id}")).get
      status(getUserResult) mustBe OK

      val getUsersResult = route(app, FakeRequest(GET, "/users")).get
      status(getUserResult) mustBe OK

      val users = Json.fromJson[Seq[UserService.User]](contentAsJson(getUsersResult)).get
      users must contain(user)
    }
  }
}
