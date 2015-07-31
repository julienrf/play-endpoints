package julienrf.endpoints

import play.api.mvc.{Controller, Action}
import play.twirl.api.StringInterpolation

object Example extends Controller {

  lazy val foo: FixedPath =
    FixedPath(
      "My first endpoint", Get, "/foo",
      Action {
        Ok(html"<a href='${foo.reverse._2}'>foo</a>, <a href='${doc.reverse._2}'>doc</a>, <a href='${hello.reverse("Julien")._2}'>Hello Julien</a>")
      }
    )

  lazy val doc: FixedPath =
    FixedPath(
      "Documentation", Get, "/doc",
      Action {
        Ok(html"<h1>Example Documentation</h1>${endpoints.map(Endpoint.documentation)}")
      }
    )

  lazy val form =
    FixedPath("Form submission", Post, "/submit", Action(NotImplemented))

  lazy val hello =
    EndpointQS(
      "Say hello to someone", Get, "/hello", QueryString("name", "Name of the person to greet"),
      name => Action(Ok(html"<h1>Hello $name</h1>"))
    )

  lazy val endpoints = Seq(foo, hello, doc, form)

}
