package julienrf.schema

import play.api.libs.json.Reads
import play.twirl.api._

case class Schema(id: String, reads: Reads[_], description: String)

object Schema {


  def schemaTemplate(schema: Schema) = {
    html"""
      {
        "foo: String,
        "bar": Int
      }
    """
  }

  def documentation(schema: Schema): Html =
    html"""
      <h2><a name='${schema.id}'>${schema.id}</a></h2>
      <p>Description: ${schema.description}</p>
      <p>Content-Type: application/json </p>
      <p>Schema: ${schemaTemplate(schema)}</p>
    """
}
