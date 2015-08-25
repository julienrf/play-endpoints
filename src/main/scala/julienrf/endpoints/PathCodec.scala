package julienrf.endpoints

sealed trait PathCodec[A] { codecA =>

  def decode(path: String): Option[(A, String)] // TODO Error handling

  def encode(a: A): String

  def andThen[B](codecB: PathCodec[B])(implicit fc: FlatConcat[A, B]): PathCodec[fc.Out] = PathCodec.AndThen(codecA, codecB)(fc)

  def / [B](codecB: PathCodec[B])(implicit fc: FlatConcat[A, B]): PathCodec[fc.Out] = andThen(codecB)

}

object PathCodec {

  case class Const(part: String) extends PathCodec[Unit] {
    def decode(path: String): Option[(Unit, String)] =
      if (path.startsWith(part)) Some(((), path.drop(part.length))) else None
    def encode(a: Unit): String = part
  }

  // TODO Factor out documentation concern (ident)
  case class Segment(ident: String) extends PathCodec[String] {
    val partRegex = "([^/]+)".r
    def decode(path: String): Option[(String, String)] =
      partRegex.findFirstMatchIn(path).map { `match` =>
        (`match`.group(0), path.drop(`match`.end))
      }
    def encode(part: String): String = part
  }

   case class AndThen[A, B, Out](codecA: PathCodec[A], codecB: PathCodec[B])(implicit fc: FlatConcat.Aux[A, B, Out]) extends PathCodec[Out] {
    def decode(path: String): Option[(Out, String)] =
      for {
        (a, remaining) <- codecA.decode(path)
        (b, remaining2) <- codecB.decode(remaining)
      } yield (fc(a, b), remaining2)
    def encode(out: Out): String = {
      val (a, b) = fc.unapply(out)
      codecA.encode(a) ++ "/" ++ codecB.encode(b)
    }
  }

  def documentation(pathCodec2: PathCodec[_]): String =
    pathCodec2 match {
      case Const(part) => part
      case Segment(ident) => ":" ++ ident
      case AndThen(codecA, codecB) => documentation(codecA) ++ "/" ++ documentation(codecB)
    }

}
