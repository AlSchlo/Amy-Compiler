object Std

  fn printInt(i: Int(32)): Unit = {
    error("")
  }

  fn printString(s: String): Unit = {
    error("")
  }

  fn printBoolean(b: Boolean): Unit = {
    printString(booleanToString(b))
  }

  fn readString(): String = {
    error("")
  }

  fn readInt(): Int(32) = {
    error("")
  }

  fn intToString(i: Int(32)): String = {
    (if((i < 0)) {
      ("-" ++ intToString(-(i)))
    } else {
      (
        val rem: Int(32) =
          (i % 10);
        val div: Int(32) =
          (i / 10);
        (if((div == 0)) {
          digitToString(rem)
        } else {
          (intToString(div) ++ digitToString(rem))
        })
      )
    })
  }

  fn digitToString(i: Int(32)): String = {
    error("")
  }

  fn booleanToString(b: Boolean): String = {
    (if(b) {
      "true"
    } else {
      "false"
    })
  }
end Std
