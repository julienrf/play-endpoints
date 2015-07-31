package julienrf.formats

import play.api.data.mapping._
import play.api.libs.json.{JsObject, JsValue}

object ValidationUtils {

  /**
   * Converts a writer into a rule which always succeeds
   */
  implicit def writerToRule[I, O](write: Write[I, O]): Rule[I, O] = Rule[I, O] {
    input => Success(write.writes(input))
  }

  implicit class PimpedRule[I](r1: RuleLike[I, JsObject]) {
    /**
     * Merges two validations of a JsObject. Two successes are merged at the first level into a new JsObject.
     * If either fail, the failure will be returned. If both fail, the errors will be concatenated.
     */
    def |++|(r2: RuleLike[I, JsObject]) = Rule[I, JsObject] { v =>
      (r1.validate(v), r2.validate(v)) match {
        case (Success(_), Failure(e)) => Failure(e)
        case (Failure(e), Success(_)) => Failure(e)
        case (Success(s1), Success(s2)) => Success(s1 ++ s2)
        case (Failure(e1), Failure(e2)) => Failure(e1 ++ e2)
      }
    }
  }

  /**
   * Trivial writer which only propagates the input JsValue.
   */
  def jsValueW[T <: JsValue] = Rule[T, JsValue](x => Success(x))
}
