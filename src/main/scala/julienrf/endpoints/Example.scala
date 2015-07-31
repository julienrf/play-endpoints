package julienrf.endpoints

import play.api.mvc.{Controller, Action}
import play.twirl.api.StringInterpolation

object Example extends Controller {
  val fooHandler = Action(Ok(html"<a href='${Endpoint.reverseRouter(foo.endpoint)}'>reverse routed link to the 'foo' endpoint</a>"))
  val foo: DocumentedEndpoint =  DocumentedEndpoint("My first endpoint", Endpoint("/foo", fooHandler))
}
