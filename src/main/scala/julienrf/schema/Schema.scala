package julienrf.schema

import julienrf.formats.{FormatArray, FormatValue}
import play.api.libs.json.{Json, Reads}
import play.twirl.api._
import julienrf.formats.FormatValue.Implicits._

case class Schema(id: String, format: FormatValue, description: String)

object Schema {

  def documentation(schema: Schema): Html =
    html"""
      <h2><a name='${schema.id}'>${schema.id}</a></h2>
      <p>Description: ${schema.description}</p>
      <p>Content-Type: application/json </p>
      <p>Schema:</p>
      <pre>${Json.prettyPrint(schema.format)}</pre>
    """
}
