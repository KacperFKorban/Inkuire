package org.virtuslab.inkuire.engine.impl.service

import org.virtuslab.inkuire.engine.impl.model._

case class VariableBindings(bindings: Map[ITID, Seq[Type]]) {
  def add(dri: ITID, typ: Type): VariableBindings = {
    VariableBindings {
      val types = bindings.getOrElse(dri, Seq.empty)
      bindings.updated(dri, types :+ typ)
    }
  }
}

object VariableBindings {
  def empty: VariableBindings =
    VariableBindings(Map.empty)
}
