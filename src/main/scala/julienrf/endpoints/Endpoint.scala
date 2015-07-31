package julienrf.endpoints

import play.api.mvc.{Results, Action, RequestHeader}
import play.api.routing.Router

case class DocumentedEndpoint(endpoint: Endpoint, description: String)

case class Endpoint(path: String)

object Endpoint {

  def router(endpoint: Endpoint): Router =
    Router.from(Function.unlift { (requestHeader: RequestHeader) =>
      if (requestHeader.path == endpoint.path) Some(Action(Results.Ok))
      else None
    })

}
