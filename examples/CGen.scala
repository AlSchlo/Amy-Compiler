object L
  abstract class List
  case class Nil() extends List
  case class Cons(h: Int(32), t: List) extends List

 fn concat(l1: List, l2: List): List = {
    l1 match {
      case Nil() => l2
      case Cons(h, t) => Cons(h, concat(t, l2))
    }
  }

end L