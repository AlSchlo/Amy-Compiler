object Call1
  fn weirdFunction(l: L.List, i : Int(32), s: String, b: Boolean, u : Unit): Unit={
      error(s)
  }

  weirdFunction(L.Cons(1,L.Nil()),9,"s",true,3)
  

end Call1