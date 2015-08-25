package julienrf.endpoints

/** HTTP methods */
sealed trait Method
case object Get extends Method
case object Post extends Method

object Method {
  def asText(method: Method): String =
    method match {
      case Get => "GET"
      case Post => "POST"
    }
}
