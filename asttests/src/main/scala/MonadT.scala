object MonadT {

  case class User(id: Long, name: String)

  case class UserPreferences(userId: Long, darkMode: Boolean)

  case class DatabaseError()

  def userById(id: Long): Either[DatabaseError, Option[User]] = ???

  def preferences(userId: Long): Either[DatabaseError, Option[UserPreferences]] = ???

  def render(userName: String, darkMode: Boolean): String = ???

  object Plain {
    def html(id: Long): Either[DatabaseError, Option[String]] = for {
      maybeUser <- userById(id)
      maybePref <- preferences(id)
    } yield {
      for {
        user <- maybeUser
        pref <- maybePref
      } yield render(user.name, pref.darkMode)
    }
  }

  object WithTransformer {

    import cats.data.OptionT
    import cats.implicits._

    type MaybeDataBaseError[A] = Either[DatabaseError, A]

    def html(id: Long): OptionT[MaybeDataBaseError, String] = for {
      user <- OptionT(userById(id))
      pref <- OptionT(preferences(id))
    } yield render(user.name, pref.darkMode)
  }


}
