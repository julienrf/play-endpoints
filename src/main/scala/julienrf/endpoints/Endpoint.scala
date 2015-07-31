package julienrf.endpoints

case class Endpoint(path: String)

case class DocumentedEndpoint(endpoint: Endpoint, description: String)