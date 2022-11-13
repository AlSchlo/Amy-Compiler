object MatchCase3
 fn elemBeforeZero(l: L.List): Int(32)={
      l match {
          case L.Nil() => 0
          case L.Cons(_,L.Cons(0,L.Cons(_,3))) => 1  
          case L.Cons(_,t) => 1+ elemBeforeZero(t)
          case _ => error("error")
      }
  }

  elemBeforeZero(L.Cons(1,L.Cons(2,L.Cons(3,L.Cons(0,L.Nil())))))

end MatchCase3