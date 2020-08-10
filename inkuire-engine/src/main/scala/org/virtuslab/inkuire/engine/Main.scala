package org.virtuslab.inkuire.engine

import org.virtuslab.inkuire.engine.cli.Cli
import org.virtuslab.inkuire.engine.cli.service.KotlinExternalSignaturePrettifier
import org.virtuslab.inkuire.engine.http.HttpServer
import org.virtuslab.inkuire.engine.model.Engine.Env
import org.virtuslab.inkuire.engine.model.InkuireDb
import org.virtuslab.inkuire.engine.parser.KotlinSignatureParserService
import org.virtuslab.inkuire.engine.service.ExactMatchService

object Main extends App {

  val in           = new Cli
  val out          = new HttpServer
  val matchService = (db: InkuireDb) => new ExactMatchService(db)
  val prettifier   = new KotlinExternalSignaturePrettifier
  val parser       = new KotlinSignatureParserService

  in.readInput(args)
    .toOption
    .semiflatMap { db: InkuireDb =>
      out
        .serveOutput()
        .runA(
          Env(db, matchService(db), prettifier, parser)
        )
    }
    .fold(println("Unexpected Error occured!"))(identity)
    .unsafeRunSync()

}
