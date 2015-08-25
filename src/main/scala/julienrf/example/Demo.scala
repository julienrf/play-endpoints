package julienrf.example

import julienrf.endpoints._
import julienrf.formats.FormatValue.Implicits._
import julienrf.schema.Schema
import play.api.libs.json.Json
import play.twirl.api.{Html, StringInterpolation}

object Demo {

  def render(body: => Html) = {
    html"""
      <html>
        <head>
          <link href="http://getbootstrap.com/dist/css/bootstrap.min.css" rel="stylesheet">
        </head>
        <body>
          <nav class="navbar navbar-inverse">
            <div class="container"><div class="navbar-header"><span class="navbar-brand">Play Endpoints</span></div></div>
          </nav>
          <div class="container">
            ${body}
          </div>
        </body>
      </html>
    """
  }

  def documentation(endpoint: Endpoint[_]): Html = html""
/*    html"""
      <div class="panel">
      <h2>${endpoint.codec.docMethod.map(Method.asText).mkString(" or ")} ${endpoint.codec.docPath}</h2>
      <dl>
        ${
      for ((n, d) <- endpoint.codec.docQS) yield {
        html"""
              <p><b>$n</b>: $d</p>
            """
      }}
      ${
      for (schema <- endpoint.inputSchema) yield {
        html"""
              <p><b>Input schema</b>: <a href='${Example.schemaUrl(schema)}'>${schema.id}</a></p>
            """
      }}
      ${
      for (schema <- endpoint.outputSchema) yield {
        html"""
              <p><b>Output schema</b>: <a href='${Example.schemaUrl(schema)}'>${schema.id}</a></p>
          """
      }
      }
      </dl>
      <p>${endpoint.description}</p>
      </div>
    """*/

  def schemaTemplate(schema: Schema): Html =
    html"""
      <h2><a name='${schema.id}'>${schema.id}</a></h2>
      <p><b>Description:</b> ${schema.description}</p>
      <p><b>Content-Type:</b> application/json </p>
      <p><b>Format:</b></p>
      <pre>${Json.prettyPrint(schema.format)}</pre>
    """

}
