package julienrf.formats

import org.joda.time.DateTime
import play.api.libs.json.{JsArray, JsObject, JsValue}
import play.api.data.mapping.json.{Rules, Writes}
import play.api.data.mapping._
import ValidationUtils.writerToRule

/**
 * Factory method for constraints over JsValues, i.e. realisations of Rule[JsValue, JsValue]. These methods allow to pass
 * an inner constraint on T, in order to validate JsValue => T => JsValue.
 */
object Constraints {

  type Constraint[T] = Rule[T, T]

  def objC: Constraint[JsValue] = objC(Rule.zero)
  def objC(inner: Constraint[JsObject]): Constraint[JsValue] = Rules.jsObjectR compose inner compose ValidationUtils.jsValueW[JsObject]

  def arrC: Constraint[JsValue] = intC(Rule.zero)
  def arrC(inner: Constraint[JsArray]): Constraint[JsValue] = Rules.jsArrayR compose inner compose ValidationUtils.jsValueW[JsArray]

  def intC: Constraint[JsValue] = intC(Rule.zero)
  def intC(inner: Constraint[Int]): Constraint[JsValue] = Rules.intR compose inner compose Writes.intW

  def decimalC: Constraint[JsValue] = decimalC(Rule.zero)
  def decimalC(inner: Constraint[BigDecimal]): Constraint[JsValue] = Rules.bigDecimal compose inner compose Writes.bigDecimalW

  def stringC: Constraint[JsValue] = stringC(Rule.zero)
  def stringC(inner: Constraint[String]): Constraint[JsValue] = Rules.stringR compose inner compose Writes.string

  def booleanC: Constraint[JsValue] = booleanC(Rule.zero)
  def booleanC(inner: Constraint[Boolean]): Constraint[JsValue] = Rules.booleanR compose inner compose Writes.booleanW

  def jodaDateC: Constraint[JsValue] = jodaDateC(Rule.zero)
  def jodaDateC(inner: Constraint[DateTime]): Constraint[JsValue] = stringC(Rules.jodaDate compose inner compose Writes.jodaDate)
}
