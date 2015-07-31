package julienrf.endpoints

import play.api.mvc.{Controller, Action}
import play.twirl.api.StringInterpolation

object Example extends Controller {

  lazy val index: Endpoint[Unit] =
    Endpoint(
      "My first endpoint", MethodCodec(Get).concat(PathCodec("/"))) { _ =>
      Action {
        Ok(html"<a href='${index.codec.encode(()).url}'>foo</a>, <a href='${doc.codec.encode(()).url}'>doc</a>, <a href='${hello.codec.encode("Julien").url}'>Hello Julien</a>")
      }
    }

  lazy val doc: Endpoint[Unit] =
    Endpoint(
      "Documentation", MethodCodec(Get).concat(PathCodec("/doc")))( _ =>
      Action {
        Ok(html"<h1>Example Documentation</h1>${endpoints.map(Endpoint.documentation)}")
      }
    )

  lazy val form =
    Endpoint("Form submission", MethodCodec(Post).concat(PathCodec("/submit")))(_ => Action(NotImplemented))

  lazy val hello =
    Endpoint(
      "Say hello to someone",
      MethodCodec(Get).concat(PathCodec("/hello")).concat(QueryStringParameterCodec("name", "Name of the person to greet"))
    ) { name =>
      Action(Ok(html"<h1>Hello $name</h1>"))
    }

  lazy val endpoints = Seq(index, hello, doc, form)

}
