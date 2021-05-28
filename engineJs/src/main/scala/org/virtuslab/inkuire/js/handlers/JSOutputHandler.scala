package org.virtuslab.inkuire.js.handlers

import cats.effect.IO
import monix.execution.Ack
import monix.reactive.subjects.PublishSubject
import monix.execution.Scheduler.Implicits.global
import monix.reactive.Observable
import org.virtuslab.inkuire.engine.common.api.OutputHandler
import org.virtuslab.inkuire.engine.common.model.{Engine, Signature}
import org.virtuslab.inkuire.engine.http.http.OutputFormatter
import org.virtuslab.inkuire.js.html.{BaseInput, BaseOutput, DokkaSearchbar}
import org.virtuslab.inkuire.engine.common.model.OutputFormat

class JSOutputHandler(private val inputApi: BaseInput, private val outputApi: BaseOutput) extends OutputHandler {
  private val subject = PublishSubject[Observable[OutputFormat]]()

  override def serveOutput(env: Engine.Env): IO[Unit] = {
    val outputFormatter = new OutputFormatter(env.prettifier)

    def executeQuery(query: String): Either[String, Observable[OutputFormat]] = {
      env.parser
        .parse(query)
        .map(env.resolver.resolve)
        .map { r =>
          Observable
            .fromIterable(env.db.functions)
            .filterEvalF(eSgn => IO.async[Boolean](_(Right(env.matcher.|?|(r)(eSgn)))))
            .map(eSgn => outputFormatter.createOutput(query, Seq(eSgn)))
        }
    }

    IO.async(_ => {
      outputApi.registerOutput(subject)
      inputApi.inputChanges.map(executeQuery).subscribe {
        case Right(v) =>
          subject.onNext(v)
          Ack.Continue
        case Left(v) =>
          println(s"From output: $v")
          Ack.Continue
      }
      inputApi.notifyEngineReady
    })
  }
}
