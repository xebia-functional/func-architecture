# Functional Programming Patterns #
(For the pragmatic programmer)

In this talk we will see a pragmatic approach to building a purely functional architecture that delivers cohesive functional components. 
We will cover functional patterns such as Free Monads, Transformers, Kleisli arrows, dependently typed checked exceptions 
and types as well as how they can be glued together to achieve pure functions that are composable, context free, dependently injectable and testable.

**Download the presentation**

1. Interactive : Clone this repo and run `sbt console`
2. [PDF](presentation.pdf) Download
3. [MD](presentation.md)
4. [SpeakerDeck](https://speakerdeck.com/raulraja/functional-programming-patterns-for-the-pragmatic-programmer)

Ultimately one can achieve functions whose dependencies are injected, free interpreted and potential
exception may be accumulated and simplified through the use of monad transformers:

*Given some dependencies and a module containing them:*

```scala
case class Converter() {
  def convert(x: String): Int = x.toInt
}

case class Adder() {
  def add(x: Int): Int = x + 1
}

case class Config(converter: Converter, adder: Adder)

val system = Config(Converter(), Adder())
```

We can define some functions defining deps as input params, that perform some ops and capture potential
Exceptions.

```scala
def service1(x : String) = Service { converter: Converter =>
    ask(Result.catching[NumberFormatException](converter.convert(x)))
}

def service2 = Service { adder: Adder =>
    ask(Result.catching[IllegalArgumentException](println("serv2 invoked"); adder.add(22) + " added "))
}
```

We may compose our monadic functions and lifting potentially dispair dependencies to a common module

```scala 
val composed: Service[Config, String, NumberFormatException with IllegalArgumentException] = for {
  a <- service1("1").liftD[Config]
  b <- service2.liftD[Config]
} yield a + b
```

And run them through interpreters that replace our Free Algebra by actual types separating interpretation
completely from program definition

```scala 
composed.exec(system)(TestInterpreter) // yield the Id[Value]

composed.exec(system)(ProdInterpreter) // yields a `Task` or `Future`
``

