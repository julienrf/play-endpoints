package julienrf.endpoints

import julienrf.example.Example
import play.api.routing.Router
import play.api.{BuiltInComponentsFromContext, Application, ApplicationLoader}
import play.api.ApplicationLoader.Context

class Loader extends ApplicationLoader {
  def load(context: Context): Application =
    new BuiltInComponentsFromContext(context) {

      def router: Router = Endpoint.router(Example.endpoints)

    }.application
}
