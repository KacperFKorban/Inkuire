package org.virtuslab.inkuire.engine

import java.io.File

class EndToEndEngineTest extends munit.FunSuite {
  val testService: Fixture[InkuireTestService] = new Fixture[InkuireTestService]("testService") {
    var testService: InkuireTestService = null
    def apply() = testService
    override def beforeAll(): Unit = {
      val file = new File("./engine/shared/src/test/resources/inkuire-db.json")
      testService = new InkuireTestService(file.toURI.toURL.toString())
    }
    override def afterAll(): Unit = {}
  }
  override def munitFixtures: List[Fixture[InkuireTestService]] = List(testService)

  /** Test whether a search using a `signature` includes `funName`
    */
  def testFunctionFound(signature: String, funName: String)(implicit loc: munit.Location): Unit = {
    test(s"$funName : $signature") {
      assert(testService().query(signature).exists(_.name == funName))
    }
  }

  testFunctionFound("List[A] => (A => B) => List[B]", "map")

  testFunctionFound("List[A] => (A => List[B]) => List[B]", "flatMap")

  testFunctionFound("List[Int] => (Int => List[Float]) => List[Float]", "flatMap")

  testFunctionFound("List[Int] => (Int => Float) => List[AnyVal]", "map")

  testFunctionFound("Seq[Int] => (Int => String) => Seq[String]", "map")

  testFunctionFound("A => (A => B) => B", "pipe")

  testFunctionFound("Char => (Char => B) => B", "pipe")

  testFunctionFound("Char => (Any => Double) => Double", "pipe")

  testFunctionFound("Boolean => A => Option[A]", "Option.when")

  testFunctionFound("Boolean => B => A => Either[A, B]", "Either.cond")

  testFunctionFound(
    "IArray[Float] => (Float => Boolean) => Boolean",
    "IArray.forall"
  ) // TODO(kπ) IMHO should be just `forall` (generation bug)

  testFunctionFound("List[A] => B => (B => A => B) => B", "foldLeft")

  testFunctionFound("F[A] => B => (B => A => B) => B", "foldLeft")

  testFunctionFound("List[A] => B => ((B, A) => B) => B", "foldLeft")

  testFunctionFound("F[A] => B => ((B, A) => B) => B", "foldLeft")

  // TODO(kπ) this is a bug in constraint checking
  // testFunctionFound("List[A] => A => (A => A => A) => A", "foldLeft")

  // testFunctionFound("List[A] => A => ((A, A) => A) => A", "foldLeft")

  // testFunctionFound("F[A] => A => (A => A => A) => A", "foldLeft")

  // testFunctionFound("F[A] => A => ((A, A) => A) => A", "foldLeft")
}
