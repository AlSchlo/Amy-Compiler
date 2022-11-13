object H
 abstract class Human
    case class Woman(age: Int(32), timesPregnant: O.Option) extends Human
    case class Man(age: Int(32), nbKids: Int(32)) extends Human
    
    fn printHuman(human: Human): Unit = {
        human match {
            case Woman(age, timesPregnant) => printWoman(age,timesPregnant)
            case Man(age, nbKids) => printMan(age,nbKids)
            case _ => error("What are you ?")
        }
    }
    fn printMan(age: Int(32),nbKids: Int(32)): Unit ={
        Std.printInt(age);
        Std.printInt(nbKids)
    }
    fn printWoman(age: Int(32), timesPregnant : O.Option): Unit = {
                Std.printString("Woman");
                Std.printInt(age);
                timesPregnant match {
                    case O.Some(nb) => Std.printInt(nb)
                    case O.None() => Std.printString("No pregnancies.")
                }
    }

    printHuman( Woman( 47 , O.Some(10)));
    printHuman( Woman(40, O.None()) );
    printHuman( H.Man(18, 1))

end H

