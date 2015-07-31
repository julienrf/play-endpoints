package julienrf.endpoints

import play.api.mvc.{Handler, Results, Action, RequestHeader}
import play.api.routing.Router
import play.twirl.api.{Html, StringInterpolation}

case class DocumentedEndpoint(description: String, endpoint: Endpoint)

case class Endpoint(path: String, handler: Handler)

object Endpoint {

  def router(endpoint: Endpoint): Router =
    Router.from(Function.unlift { (requestHeader: RequestHeader) =>
      if (requestHeader.path == endpoint.path) Some(endpoint.handler)
      else None
    })

  def reverseRouter(endpoint: Endpoint): String = endpoint.path

  def documentation(documentedEndpoint: DocumentedEndpoint): Html =
    html"""
      <h2>GET ${documentedEndpoint.endpoint.path}</h2>
      <p>${documentedEndpoint.description}</p>
    """

}
