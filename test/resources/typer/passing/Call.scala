object Call
  fn weirdFunction(l: L.List, i : Int(32), s: String, b: Boolean, u : Unit): Unit ={
      error(s)
  }

  weirdFunction(L.Cons(1,L.Nil()),9,"s",true,());
  weirdFunction(L.Nil(),9+3/2,"s"++"s",true && false,2;())
  

end Call