package julienrf.endpoints

import play.api.mvc.{Controller, Action}
import play.twirl.api.StringInterpolation

object Example extends Controller {

  lazy val foo: DocumentedEndpoint =
    DocumentedEndpoint(
      "My first endpoint",
      Endpoint(Get, "/foo",
        Action(
          Ok(html"<a href='${Endpoint.reverseRouter(foo.endpoint)}'>reverse routed link to the 'foo' endpoint</a> and a link to the <a href='${Endpoint.reverseRouter(doc.endpoint)}'>doc</a>")
        )
      )
    )

  lazy val doc: DocumentedEndpoint =
    DocumentedEndpoint(
      "Documentation",
      Endpoint(Get, "/doc",
        Action {
          Ok(html"<h1>Example Documentation</h1>${endpoints.map(Endpoint.documentation)}")
        }
      )
    )

  lazy val form =
    DocumentedEndpoint("Form submission", Endpoint(Post, "/submit", Action(NotImplemented)))

  lazy val endpoints = Seq(foo, doc, form)

}
