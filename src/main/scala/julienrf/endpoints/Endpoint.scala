package julienrf.endpoints

import play.api.mvc.{Handler, Results, Action, RequestHeader}
import play.api.routing.Router
import play.twirl.api.{Html, StringInterpolation}

case class DocumentedEndpoint(description: String, endpoint: Endpoint)

case class Endpoint(method: Method, path: String, handler: Handler)

sealed trait Endpoints

/** HTTP methods */
sealed trait Method
case object Get extends Method
case object Post extends Method

object Method {
  def toString(method: Method): String =
    method match {
      case Get => "GET"
      case Post => "POST"
    }
}

object Endpoint {

  def router(endpoints: Seq[Endpoint]): Router =
    Router.from(Function.unlift { (requestHeader: RequestHeader) =>
      endpoints
        .find(endpoint => Method.toString(endpoint.method) == requestHeader.method && endpoint.path == requestHeader.path)
        .map(_.handler)
    })

  def reverseRouter(endpoint: Endpoint): String = endpoint.path

  def documentation(documentedEndpoint: DocumentedEndpoint): Html =
    html"""
      <h2>${Method.toString(documentedEndpoint.endpoint.method)} ${documentedEndpoint.endpoint.path}</h2>
      <p>${documentedEndpoint.description}</p>
    """

}
