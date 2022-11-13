object SubsetSum

    fn subsetSum(a: Int(32), b: Int(32), sum: Int(32)): Int(32) = {
        if(b < a || sum < 0) {
            0
        } else {
            if(sum == 0) {
                1
            } else {
                subsetSum(a, b, sum - b) + subsetSum(a, b - 1, sum)
            }
        }
    }
    Std.printString("Caution this problem is NP complete do not input too big number:");
    Std.printString("Enter the Sum :");
    val sum: Int(32) = Std.readInt();
    Std.printString("Enter lower bound of interval :");
    val a: Int(32) = Std.readInt();
    Std.printString("Enter upper bound of interval :");
    val b: Int(32) = Std.readInt();
    Std.printInt(subsetSum(a, b, sum))

end SubsetSum