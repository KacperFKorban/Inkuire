package org.virtuslab.inkuire.engine.http

import cats.effect._
import cats.implicits._
import com.google.gson.Gson
import org.http4s.dsl.io._
import org.http4s.headers.`Content-Type`
import org.http4s.implicits._
import org.http4s.server.blaze._
import org.http4s.{HttpRoutes, MediaType, Request, StaticFile, UrlForm}
import org.virtuslab.inkuire.engine.api.OutputHandler
import org.virtuslab.inkuire.engine.model.Engine._
import org.virtuslab.inkuire.model.OutputFormat

import scala.concurrent.ExecutionContext.global
import scala.io.Source

object SignatureParameter extends QueryParamDecoderMatcher[String]("signature")

class HttpServer extends OutputHandler {

  override def serveOutput(env: Env): IO[Unit] = {

    implicit val cs:    ContextShift[IO] = IO.contextShift(global)
    implicit val timer: Timer[IO]        = IO.timer(global)

    val formatter = new OutputFormatter(env.prettifier)
    val gson      = new Gson()

    def results(signature: String): Either[String, OutputFormat] = {
      env.parser
        .parse(signature)
        .map(
          fb => formatter.createOutput(signature, env.matcher |??| fb)
        )
    }

    def static(file: String, blocker: Blocker, request: Request[IO]) =
      StaticFile.fromResource("/" + file, blocker, Some(request)).getOrElseF(NotFound())

    def appService(b: Blocker) =
      HttpRoutes
        .of[IO] {
          case GET -> Root / "query" =>
            Ok(Templates.formTemplate(), `Content-Type`(MediaType.text.html))
          case req @ POST -> Root / "query" =>
            req.decode[UrlForm] { m =>
              val signature = m.values("query").headOption.get
              val res       = results(signature)
              res.fold(
                fa => BadRequest(fa),
                fb => Ok(Templates.result(fb), `Content-Type`(MediaType.text.html))
              )
            }
          case GET -> Root / "forSignature" :? SignatureParameter(signature) =>
            results(signature).fold(
              fa => BadRequest(fa),
              fb =>
                Ok(
                  gson.toJson(fb),
                  `Content-Type`(MediaType.application.json)
              )
            )
          case req @ GET -> Root / "assets" / path => static(s"assets/$path", b, req)
        }
        .orNotFound

    val app = for {
      blocker <- Blocker[IO]
      server <- BlazeServerBuilder[IO]
        .bindHttp(env.appConfig.port.port, env.appConfig.address.address)
        .withHttpApp(appService(blocker))
        .resource
    } yield server

    app.use(_ => IO.never).as(ExitCode.Success)
  }
}
