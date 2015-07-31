package julienrf.endpoints

import java.net.URLEncoder

import play.api.mvc.{Handler, RequestHeader}
import play.api.routing.Router
import play.twirl.api.{Html, StringInterpolation}

trait FlatConcat[A, B] {
  type Out
  def apply(a: A, b: B): Out
  def unapply(out: Out): (A, B)
}
trait FlatConcatLowImplicits {
  type Aux[A, B, Out0] = FlatConcat[A, B] { type Out = Out0 }
  implicit def aB[A, B]: Aux[A, B, (A, B)] = new FlatConcat[A, B] {
    type Out = (A, B)
    def apply(a: A, b: B): (A, B) = (a, b)
    def unapply(out: (A, B)): (A, B) = out
  }
}
object FlatConcat extends FlatConcatLowImplicits {
  implicit val unitUnit: Aux[Unit, Unit, Unit] = new FlatConcat[Unit, Unit] {
    type Out = Unit
    def apply(a: Unit, b: Unit): Unit = Unit
    def unapply(out: Out): (Unit, Unit) = (Unit, Unit)
  }
  implicit def unitA[A]: Aux[Unit, A, A] = new FlatConcat[Unit, A] {
    type Out = A
    def apply(a: Unit, b: A): A = b
    def unapply(out: Out): (Unit, A) = (Unit, out)
  }
  implicit def AUnit[A]: Aux[A, Unit, A] = new FlatConcat[A, Unit] {
    type Out = A
    def apply(a: A, b: Unit): A = a
    def unapply(out: Out): (A, Unit) = (out, Unit)
  }
}

trait RequestCodec[A] { codecA =>

  def decode(requestHeader: RequestHeader): Option[A]

  def encode(a: A): MethodAndURL

  def docMethod: Seq[Method]
  def docPath: Option[String]
  def docQS: Map[String, String]

  def concat[B](codecB: RequestCodec[B])(implicit fc: FlatConcat[A, B]): RequestCodec[fc.Out] =
    new RequestCodec[fc.Out] {
      def decode(requestHeader: RequestHeader): Option[fc.Out] =
        (codecA.decode(requestHeader), codecB.decode(requestHeader)) match {
          case (Some(a), Some(b)) => Some(fc(a, b))
          case _ => None
        }
      def encode(out: fc.Out): MethodAndURL = {
        val (a, b) = fc.unapply(out)
        val methodAndUrlA = codecA.encode(a)
        val methodAndUrlB = codecB.encode(b)
        MethodAndURL(
          methodAndUrlA.method.orElse(methodAndUrlB.method),
          methodAndUrlA.path.fold(methodAndUrlB.path)(prefix => methodAndUrlB.path.map(prefix ++ _).orElse(Some(prefix))),
          methodAndUrlA.queryString ++ methodAndUrlB.queryString
        )
      }
      val docMethod: Seq[Method] = (codecA.docMethod ++ codecB.docMethod).distinct
      def docPath: Option[String] = codecA.docPath.fold(codecB.docPath)(prefix => codecB.docPath.map(prefix ++ _).orElse(Some(prefix)))
      def docQS: Map[String, String] = codecA.docQS ++ codecB.docQS
    }
}

trait RequestEncoder[A] {
  def encode(a: A): MethodAndURL
}

case class MethodAndURL(method: Option[Method], path: Option[String], queryString: Map[String, Seq[String]]) {
  lazy val url: String =
    path.getOrElse("/") ++
      queryString.flatMap { case (k, vs) => vs.map(v => URLEncoder.encode(k, "UTF8") ++ "=" ++ URLEncoder.encode(v, "UTF8")) }
        .mkString("?", "&", "")
}

case class MethodCodec(method: Method) extends RequestCodec[Unit] {
  def decode(requestHeader: RequestHeader): Option[Unit] =
    if (requestHeader.method == Method.asText(method)) Some(Unit) else None
  def encode(a: Unit): MethodAndURL = MethodAndURL(Some(method), None, Map.empty)
  def docMethod: Seq[Method] = Seq(method)
  def docQS: Map[String, String] = Map.empty
  def docPath: Option[String] = None
}

case class PathCodec(path: String) extends RequestCodec[Unit] {
  def decode(requestHeader: RequestHeader): Option[Unit] =
    if (requestHeader.path == path) Some(Unit) else None
  def encode(a: Unit): MethodAndURL = MethodAndURL(None, Some(path), Map.empty)
  def docMethod: Seq[Method] = Nil
  def docQS: Map[String, String] = Map.empty
  def docPath: Option[String] = Some(path)
}

case class QueryStringParameterCodec(name: String, description: String) extends RequestCodec[String] {
  def decode(requestHeader: RequestHeader): Option[String] =
    requestHeader.getQueryString(name)
  def encode(a: String): MethodAndURL = MethodAndURL(None, None, Map(name -> Seq(a)))
  def docMethod: Seq[Method] = Nil
  def docQS: Map[String, String] = Map(name -> description)
  def docPath: Option[String] = None
}

case class Endpoint[A](description: String, codec: RequestCodec[A])(val handler: A => Handler)

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

  def router(endpoints: Seq[Endpoint[_]]): Router =
    Router.from(Function.unlift { (requestHeader: RequestHeader) =>
      endpoints
        .collectFirst {
          case endpoint if endpoint.codec.decode(requestHeader).nonEmpty => endpoint.handler(endpoint.codec.decode(requestHeader).get)
        }
    })

  def documentation(endpoint: Endpoint[_]): Html =
    html"""
      <h2>${endpoint.codec.docMethod.map(Method.asText).mkString(" or ")} ${endpoint.codec.docPath}</h2>
      <dl>
        ${
          for ((n, d) <- endpoint.codec.docQS) yield
            html"""
              <dt>$n</dt><dd>$d</dd>
            """
        }
      </dl>
      <p>${endpoint.description}</p>
    """

}
