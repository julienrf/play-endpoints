package julienrf.endpoints

sealed trait QueryStringCodec[A] { codecA =>

  def decode(qs: Map[String, Seq[String]]): Option[A] // TODO Error reporting

  def encode(a: A): Map[String, Seq[String]]

  def and[B](codecB: QueryStringCodec[B])(implicit fc: FlatConcat[A, B]): QueryStringCodec[fc.Out] = QueryStringCodec.And(codecA, codecB)(fc)

}

object QueryStringCodec {

  case class And[A, B, Out](codecA: QueryStringCodec[A], codecB: QueryStringCodec[B])(implicit fc: FlatConcat.Aux[A, B, Out]) extends QueryStringCodec[Out] {
    def decode(qs: Map[String, Seq[String]]): Option[Out] =
      for {
        a <- codecA.decode(qs)
        b <- codecB.decode(qs)
      } yield fc(a, b)
    def encode(out: Out): Map[String, Seq[String]] = {
      val (a, b) = fc.unapply(out)
      codecA.encode(a) ++ codecB.encode(b)
    }
  }

  case class Parameter(ident: String) extends QueryStringCodec[String] {
    def decode(qs: Map[String, Seq[String]]): Option[String] = qs.get(ident).flatMap(_.headOption)
    def encode(a: String): Map[String, Seq[String]] = Map(ident -> Seq(a))
  }

  def documentation(qs: QueryStringCodec[_]): Seq[String] =
    qs match {
      case Parameter(ident) => Seq(ident)
      case And(codecA, codecB) => documentation(codecA) ++ documentation(codecB)
    }

}