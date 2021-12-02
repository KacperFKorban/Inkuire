package org.virtuslab.inkuire.engine.common

import org.scalameter.api._
import org.scalameter.picklers.Implicits._
import java.io.File

object PerformanceTest extends Bench.LocalTime {

  val file = new File("./engineCommon/shared/src/test/resources/inkuire-db.json")
  val testService = new InkuireTestService(file.toURI.toURL.toString())

  val signatures = Gen.enumeration("signature")(
    "List[A] => (A => B) => List[B]",
    "List[A] => (A => List[B]) => List[B]",
    "List[Int] => (Int => List[Float]) => List[Float]",
    "List[Int] => (Int => Float) => List[AnyVal]",
    "Seq[Int] => (Int => String) => Seq[String]",
    "A => (A => B) => B",
    "Char => (Char => B) => B",
    "Char => (Any => Double) => Double",
    "Boolean => A => Option[A]",
    "Boolean => B => A => Either[A, B]",
    "IArray[Float] => (Float => Boolean) => Boolean"
  )

  performance of "Inkuire query" in {
    using(signatures) in { signature =>
      testService.query(signature)
    }
  }
}