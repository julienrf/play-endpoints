package julienrf.endpoints

import java.net.URLEncoder

import play.api.mvc.{Handler, RequestHeader}
import play.api.routing.Router
import play.twirl.api.{Html, StringInterpolation}

sealed trait Endpoint {
  def description: String
  def method: Method
  def path: String
}
case class FixedPath(description: String, method: Method, path: String, handler: Handler) extends Endpoint {
  def reverse: (Method, String) = (method, path)
}
case class EndpointQS(description: String, method: Method, path: String, queryString: QueryString, handler: String => Handler) extends Endpoint {
  def reverse(param: String): (Method, String) = (method, path ++ s"?${URLEncoder.encode(queryString.name, "UTF8")}=${URLEncoder.encode(param, "UTF8")}")
}

/** A query string parameter */
case class QueryString(name: String, description: String)

/** HTTP methods */
sealed trait Method
case object Get extends Method
case object Post extends Method

object Method {
  def asText(method: Method): String =
    method match {
      case Get => "GET"
      case Post => "POST"
    }
}

object Endpoint {

  def router(endpoints: Seq[Endpoint]): Router =
    Router.from(Function.unlift { (requestHeader: RequestHeader) =>
      endpoints
        .collect {
          case FixedPath(_, method, path, handler) if Method.asText(method) == requestHeader.method && path == requestHeader.path => handler
          case EndpointQS(_, method, path, queryString, handler) if Method.asText(method) == requestHeader.method && path == requestHeader.path && requestHeader.queryString.get(queryString.name).isDefined => handler(requestHeader.queryString(queryString.name).head)
        }
        .headOption
    })

  def documentation(endpoint: Endpoint): Html =
    endpoint match {
      case FixedPath(description, method, path, _) =>
        html"""
          <h2>${Method.asText(method)} $path</h2>
          <p>$description</p>
        """
      case EndpointQS(description, method, path, querystring, _) =>
        html"""
          <h2>${Method.asText(method)} $path?${querystring.name}=</h2>
          <dl>
            <dt>${querystring.name}</dt><dd>${querystring.description}</dd>
          </dl>
          <p>$description</p>
        """
    }

}
