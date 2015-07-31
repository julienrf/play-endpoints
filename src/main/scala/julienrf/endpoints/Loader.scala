package julienrf.endpoints

import play.api.routing.Router
import play.api.{BuiltInComponentsFromContext, Application, ApplicationLoader}
import play.api.ApplicationLoader.Context

class Loader extends ApplicationLoader {
  def load(context: Context): Application =
    new BuiltInComponentsFromContext(context) {

      val example = new Example

      def router: Router = Endpoint.router(example.foo.endpoint)

    }.application
}
