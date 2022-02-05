package org.virtuslab.inkuire.js.handlers

import io.circe.generic.auto._
import io.circe.parser._
import org.scalajs.dom.ext.Ajax
import org.virtuslab.inkuire.engine.common.api.ConfigReader
import org.virtuslab.inkuire.engine.common.api.InputHandler
import org.virtuslab.inkuire.engine.common.model.AppConfig
import org.virtuslab.inkuire.engine.common.model.InkuireDb
import org.virtuslab.inkuire.engine.common.serialization.EngineModelSerializers
import org.virtuslab.inkuire.engine.common.utils.fp._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.chaining._

class JSInputHandler(private val scriptPath: String) extends InputHandler with ConfigReader {

  private def tryGetURLContent(url: String)(implicit ec: ExecutionContext): EitherT[Future, String, String] =
    Ajax
      .get(url)
      .map(_.responseText.pipe(Right(_)))
      .fallbackTo(Future(Left("Could not read contents of file")))
      .pipe(new EitherT(_))

  override def readConfig(args: Seq[String])(implicit ec: ExecutionContext): EitherT[Future, String, AppConfig] = {
    args.headOption
      .toRight("Missing configuration url")
      .pipe(e => new EitherT(Future(e)))
      .flatMap[String](tryGetURLContent(_))
      .semiflatmap(parseConfig)
      .mapInner {
        case Left(_) => Left("Inkuire seems to be disabled. To enable it add `-Ygenerate-inkuire` flag to scaladoc options.")
        case Right(value) => Right(value)
      }
  }

  override def readInput(appConfig: AppConfig)(implicit ec: ExecutionContext): EitherT[Future, String, InkuireDb] = {
    appConfig.inkuirePaths
      .map(scriptPath + _)
      .map(tryGetURLContent(_).value)
      .toList
      .foldLeft(Future(List.empty[Either[String, String]])) { case (acc, f) => acc.zip(f).map { case (list, e) => e +: list } }
      .map { contents =>
        contents
          .map(_.flatMap(EngineModelSerializers.deserialize))
          .collect {
            case Right(db) => db
          }
          .pipe(Monoid.combineAll[InkuireDb])
      }
      .map(Right(_).asInstanceOf[Either[String, InkuireDb]])
      .pipe(new EitherT(_))
  }

  private def parseConfig(config: String): Either[String, AppConfig] = {
    parse(config)
      .flatMap(_.as[AppConfig])
      .pipe {
        case Left(value) => Left(value.toString)
        case Right(value) => Right(value)
      }
  }
}
