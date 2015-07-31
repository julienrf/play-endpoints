package julienrf.endpoints

import play.api.mvc.{Handler, Results, Action, RequestHeader}
import play.api.routing.Router
import play.twirl.api.{Html, StringInterpolation}

case class DocumentedEndpoint(description: String, endpoint: Endpoint)

case class Endpoint(method: Method, path: String, handler: Handler)

/** HTTP methods */
sealed trait Method
case object Get extends Method
case object Post extends Method

object Endpoint {

  def router(endpoint: Endpoint): Router =
    Router.from(Function.unlift { (requestHeader: RequestHeader) =>
      if (requestHeader.path == endpoint.path) Some(endpoint.handler)
      else None
    })

  def reverseRouter(endpoint: Endpoint): String = endpoint.path

  def documentation(documentedEndpoint: DocumentedEndpoint): Html = {

    def httpMethodName(method: Method): String =
      method match {
        case Get => "GET"
        case Post => "POST"
      }

    html"""
      <h2>${httpMethodName(documentedEndpoint.endpoint.method)} ${documentedEndpoint.endpoint.path}</h2>
      <p>${documentedEndpoint.description}</p>
    """
  }

}
