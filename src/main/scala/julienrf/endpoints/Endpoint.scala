package julienrf.endpoints

import julienrf.schema.Schema
import play.api.mvc.{Handler, RequestHeader}
import play.api.routing.Router

case class Endpoint[A](description: String, codec: RequestCodec[A], inputSchema: Option[Schema] = None, outputSchema: Option[Schema] = None)(val handler: A => Handler)

object Endpoint {

  def router(endpoints: Seq[Endpoint[_]]): Router =
    Router.from(Function.unlift { (requestHeader: RequestHeader) =>
      endpoints
        .collectFirst {
          case endpoint if endpoint.codec.decode(requestHeader).nonEmpty => endpoint.handler(endpoint.codec.decode(requestHeader).get)
        }
    })
}
