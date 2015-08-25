package julienrf.endpoints

import java.net.URLEncoder

import play.api.mvc.RequestHeader

sealed trait RequestCodec[A] {
  def decode(requestHeader: RequestHeader): Option[A]
  def encode(a: A): MethodAndURL
}

object RequestCodec {

  def apply[A](method: Method, pathCodec: PathCodec[A]): RequestCodec[A] =
    MethodPathCodec(method, pathCodec)

  def apply[A, B](method: Method, pathCodec: PathCodec[A], qsCodec: QueryStringCodec[B])(implicit fc: FlatConcat[A, B]): RequestCodec[fc.Out] =
    MethodPathQueryStringCodec(method, pathCodec, qsCodec)(fc)

  case class MethodPathCodec[A](method: Method, pathCodec: PathCodec[A]) extends RequestCodec[A] {
    def decode(requestHeader: RequestHeader): Option[A] =
      for {
        _ <- Some(())
        if requestHeader.method == Method.asText(method)
        (a, remaining) <- pathCodec.decode(requestHeader.path)
        if remaining.isEmpty
      } yield a
    def encode(a: A): MethodAndURL =
      MethodAndURL(method, pathCodec.encode(a), Map.empty)
  }

  case class MethodPathQueryStringCodec[A, B, Out](method: Method, pathCodec: PathCodec[A], qsCodec: QueryStringCodec[B])(implicit fc: FlatConcat.Aux[A, B, Out]) extends RequestCodec[Out] {
    def decode(requestHeader: RequestHeader): Option[Out] =
      for {
        _ <- Some(())
        if requestHeader.method == Method.asText(method)
        (a, remaining) <- pathCodec.decode(requestHeader.path)
        if remaining.isEmpty
        b <- qsCodec.decode(requestHeader.queryString)
      } yield fc(a, b)
    def encode(out: Out): MethodAndURL = {
      val (a, b) = fc.unapply(out)
      MethodAndURL(method, pathCodec.encode(a), qsCodec.encode(b))
    }
  }

}

case class MethodAndURL(method: Method, path: String, queryString: Map[String, Seq[String]]) {
  lazy val url: String =
    path ++
      queryString.flatMap { case (k, vs) => vs.map(v => URLEncoder.encode(k, "UTF8") ++ "=" ++ URLEncoder.encode(v, "UTF8")) }
        .mkString("?", "&", "")
}
