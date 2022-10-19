object MonadT2 {


  case class User(id: Long, name: String)
  case class UserPreferences(userId: Long, darkMode: Boolean)
  case class DatabaseError(cause: String)

  def userById(id: Long): Option[User] = ???
  def preferences(userId: Long): Either[DatabaseError, UserPreferences] = ???
  def render(userName: String, darkMode: Boolean): String = ???


  object Plain {
    def html(id: Long): Either[DatabaseError, Option[String]] = for {
      pref <- preferences(id)
      userOrError <- Right(userById(id))
    } yield {
      for {
        user <- userOrError
      } yield render(user.name, pref.darkMode)
    }


    def html2(id: Long): Either[DatabaseError, Option[String]] = {
      preferences(id) match {
        case Left(value) => Left(value)
        case Right(pref) => userById(id) match {
          case Some(user) => Right(Some(render(user.name, pref.darkMode)))
          case None => Right(None)
        }
      }
    }
  }

  object WithT {

    import cats.data.EitherT
    import cats.implicits._

    def html(id: Long): EitherT[Option, DatabaseError, String] = for {
      user <- EitherT.fromOption(userById(1), DatabaseError("user not found"))
      preferences <- EitherT.fromEither(preferences(id))
    } yield render(user.name, preferences.darkMode)
  }


}
