package julienrf.example

import julienrf.endpoints._
import julienrf.formats.FormatValue.Implicits._
import julienrf.formats.FormatValue._
import julienrf.schema.Schema
import play.api.mvc.{Action, Controller}
import play.twirl.api.StringInterpolation

object Example extends Controller {

  lazy val index: Endpoint[Unit] =
    Endpoint(
      "My first endpoint", MethodCodec(Get).concat(PathCodec("/"))) { _ =>
      Action {
        Ok(
          html"""
            <a href='${index.codec.encode(()).url}'>foo</a>
            <a href='${doc.codec.encode(()).url}'>doc</a>,
            <a href='${hello.codec.encode("Julien").url}'>Hello Julien</a>"""
        )
      }
    }

  lazy val doc: Endpoint[Unit] =
    Endpoint(
      "Documentation", MethodCodec(Get).concat(PathCodec("/doc")))(_ =>
      Action {
        Ok(Demo.render(
          html"""
            <div class="jumbotron">
            <h1>Example Documentation</h1>
            </div>
            ${endpoints.map(Demo.documentation)}
            """))
      }
      )

  lazy val form =
    Endpoint("Form submission", MethodCodec(Post).concat(PathCodec("/submit")))(_ => Action(NotImplemented))

  lazy val hello =
    Endpoint(
      "Say hello to someone",
      MethodCodec(Post).concat(PathCodec("/hello")).concat(QueryStringParameterCodec("name", "Name of the person to greet")),
      inputSchema = Some(messageSchema),
      outputSchema = Some(messageSchema)
    ) { name =>
      Action(Ok(html"<h1>Hello $name</h1>"))
    }

  lazy val messageSchema = Schema(
    id = "helloSchema",
    format = obj(
      "msg" as string whichMeans "The content of the message",
      "sender" as string whichMeans "The name of the sender"
    ),
    description = "The schema of a hello message")

  lazy val schemasDoc =
    Endpoint(
      "Schemas Documentation",
      MethodCodec(Get).concat(PathCodec("/schema")))(_ =>
      Action {
        Ok( Demo.render(html"""
            <h1>Schemas</h1>${schemas.map(Demo.schemaTemplate)}
            """))
      }
      )

  def schemaUrl(schema: Schema) = schemasDoc.codec.encode(()).url + s"#${schema.id}"



  lazy val endpoints = Seq(index, hello, doc, form, schemasDoc)

  lazy val schemas = Seq(messageSchema)

}
