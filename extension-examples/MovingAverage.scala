object MovingAverage

fn movingAverage(list: L.List, sum: Int(32), count: Int(32)): L.List = {
    list match {
        case L.Nil() => L.Nil()
        case L.Cons(h, t) => 
            val average: Int(32) = (h + sum) / (count + 1);
            L.Cons(average, movingAverage(t, sum + h, count + 1))
    }
}

val list: L.List = L.Cons(5, L.Cons(2, L.Cons(7, L.Cons(4, L.Cons(1, L.Cons(6, L.Nil()))))));
Std.printString(L.toString(L.mergeSort(list)));
Std.printString(L.toString(L.reverse(list)));
Std.printString(L.toString(movingAverage(list, 0, 0)))

end MovingAverage