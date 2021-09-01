package org.virtuslab.inkuire.engine.common.service

import com.softwaremill.quicklens._
import org.virtuslab.inkuire.engine.common.model._

case class TypingState(
  variableBindings: VariableBindings
) {
  def addBinding(dri: ITID, typ: Type): TypingState =
    this.modify(_.variableBindings).using(_.add(dri, typ))
}

object TypingState {
  def empty: TypingState = TypingState(VariableBindings.empty)
}
