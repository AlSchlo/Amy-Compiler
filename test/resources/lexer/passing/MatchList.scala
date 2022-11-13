object matchList 

    fn rec (num : L.List):L.List={
        num match {
            case L.Cons(x,xs) => L.Cons(f(x),rec(xs))
            case L.Nil() => L.Nil()
        }
    }

    
    fn f(n : Int(32)):Int(32)={
        n match {
            case 1 => 0
            case 2 => 1
            case 0 => 2
            case _ => n
        }
    }
  
    val l: L.List = L.Cons(5, L.Cons(1, L.Cons(2, L.Cons(0, L.Cons(-3, L.Nil())))));
    Std.printString(L.toString(rec(l)))

end matchList 

