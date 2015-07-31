package julienrf.formats

import Constraints.Constraint
import FormatValue.KeyedValueBuilder.Key
import ValidationUtils._
import play.api.data.mapping._
import play.api.data.mapping.json.Rules._
import play.api.data.mapping.json.Writes._
import play.api.data.mapping.json.{Rules, Writes}
import play.api.libs.json.JsValue
import play.api.libs.json._

sealed trait FormatValue

case class FormatArray(value: FormatValue) extends FormatValue

case class FormatObject(values: Seq[FormatKeyedValue]) extends FormatValue

case class FormatKeyedValue(key: String, description: String, value: FormatValue)

case class FormatPrimitive(rule: Constraint[JsValue], description: String) extends FormatValue

object FormatValue {

  object KeyedValueBuilder {
    val emptyDescription = ""

    case class Key(key: String) {
      def as(value: FormatValue) = KeyValue(key, value)
    }

    case class KeyValue(key: String, value: FormatValue) {
      def whichMeans(description: String) = FormatKeyedValue(key, description, value)
    }
  }

  object Implicits {

    import play.api.libs.json.JsValue

    implicit def stringToKeyedValueBuilder(key: String): Key = KeyedValueBuilder.Key(key)
    implicit def keyValueBuilderToFormatValue(keyValue: KeyedValueBuilder.KeyValue) =
      FormatKeyedValue(keyValue.key, KeyedValueBuilder.emptyDescription ,keyValue.value)

    implicit def formatValueToValidationRule(formatValue: FormatValue): play.api.data.mapping.Rule[JsValue, JsValue] = {
      def validate(formatValue: FormatValue) = formatValueToValidationRule(formatValue)

      formatValue match {
        case FormatArray(value) =>
          val get = Path.from[JsValue](Rules.seqR[JsValue, JsValue])
          val validateInner = Rules.seqR(validate(value))
          val put = Writes.seqToJsArray[JsValue]
          get compose validateInner compose put

        case FormatObject(values) =>
          values.map {
            case FormatKeyedValue(key, _, value) =>
              val validateInner = From[JsValue] { r =>
                (r \ key).read[JsValue, JsValue](validate(value))
              }
              val put = To[JsObject] { __ =>
                (__ \ key).write[JsValue]
              }
              validateInner compose put
          }.reduce(_ |++| _) compose ValidationUtils.jsValueW[JsObject]

        case FormatPrimitive(rule, _) => rule
      }
    }

    implicit def formatValueToJson(formatValue: FormatValue): JsValue = formatValue match {
      case FormatArray(value) =>
        JsArray (
          Seq(formatValueToJson(value))
        )
      case FormatObject(values) =>
        JsObject(
          values.map {
            case FormatKeyedValue(key, description, value) =>
              key -> Json.obj(
                "format" -> formatValueToJson(value),
                "description" -> description
              )
          }
        )
      case FormatPrimitive(_, description) => JsString(description)
    }
  }

  def obj(values: FormatKeyedValue*) = FormatObject(values.toSeq)

  def arr(value: FormatValue) = FormatArray(value)

  def string = FormatPrimitive(Constraints.stringC, "string")

  def integer = FormatPrimitive(Constraints.intC, "integer")

  def decimal = FormatPrimitive(Constraints.decimalC, "decimal")

  def date = FormatPrimitive(Constraints.jodaDateC, "date")

  def boolean = FormatPrimitive(Constraints.booleanC, "boolean")
}





