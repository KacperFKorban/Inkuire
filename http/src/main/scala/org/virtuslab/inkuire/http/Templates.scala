package org.virtuslab.inkuire.http

import org.virtuslab.inkuire.engine.impl.model.ResultFormat
import scalatags.Text
import scalatags.Text.all._

object Templates {
  private def logoHtml =
    div(id := "logo-div")(
      img(id := "logo", src := s"/assets/Inkuire_vertical@4x.png")
    )

  private def resources =
    List(
      link(href := "https://fonts.googleapis.com/icon?family=Material+Icons", rel := "stylesheet"),
      link(rel := "stylesheet", href := "https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css"),
      link(
        rel := "stylesheet",
        href := "https://fonts.googleapis.com/css2?family=Fira+Code:wght@300;400;500;600;700&display=swap"
      ),
      link(
        rel := "stylesheet",
        href := "https://fonts.googleapis.com/css2?family=Fira+Code:wght@300;400;500;600;700&display=swap"
      ),
      link(rel := "stylesheet", href := "/assets/bootstrap-theme.css"),
      link(rel := "stylesheet", href := "/assets/styles.css"),
      link(rel := "icon", `type` := "image/png", href := "/assets/Inkuire_sygnet@4x.png")
    )

  def formTemplate(): String = {
    def generateExamples(queries: String*): Seq[Text.TypedTag[String]] =
      queries.zipWithIndex.map {
        case (q, i) => example(q, s"exampleform$i")
      }

    def example(query: String, formId: String): Text.TypedTag[String] =
      div(cls := "form-div")(
        form(id := formId, cls := "query-form")(method := "post")(
          div(cls := "query-input code")(
            input(name := "query", cls := "form-control", value := query, readonly := true)
          ),
          div(cls := "query-button")(
            input(`type` := "submit", value := "Query", cls := "btn btn-primary")
          )
        )
      )

    html(
      head(
        resources
      ),
      body(
        logoHtml,
        div(cls := "form-div")(
          form(id := "form", cls := "query-form")(method := "post")(
            div(cls := "query-input code")(
              input(name := "query", cls := "form-control", placeholder := "Enter signature query")
            ),
            div(cls := "query-button")(
              input(`type` := "submit", value := "Send", cls := "btn btn-primary")
            )
          )
        ),
        div(id := "examples")(
          h2("Examples"),
          generateExamples(
            "Seq[Int] => (Int => Long) => Seq[Long]",
            "Set[Long] => Long => Boolean",
            "BigDecimal => Byte",
            "Seq[Int] => Int",
            "[A, B] => A => B => A",
            "Int => Long => Int",
            "Seq[Int] => (Int => Seq[Long]) => Seq[Long]",
            "A => (A => B) => B",
            "A => (A => B) => A",
            "Seq[A] => A",
            "String => Char",
            "String => Int => Char"
          )
        )
      )
    ).toString()
  }

  def result(results: ResultFormat): String = {
    html(
      head(
        resources
      ),
      body(
        logoHtml,
        h1(s"Result for: ${results.query}"),
        div(id := "table-container")(
          table(cls := "table table-borderless table-stripped")(
            thead(
              tr(cls := "result-table-header")(
                th(cls := "left-header-cell", scoped := "col")("#"),
                th(scoped := "col")("Name"),
                th(scoped := "col")("Signature"),
                th(cls := "right-header-cell", scoped := "col")("Localization")
              )
            ),
            tbody(
              results.matches.zipWithIndex.map {
                case (res, i) =>
                  tr(cls := "code")(
                    th(scoped := "row")(i + 1),
                    td(a(href := res.pageLocation, res.functionName)),
                    td(res.prettifiedSignature),
                    td(res.packageLocation)
                  )
              }
            )
          )
        ),
        div(id := "header")(
          a(href := "./query")(
            span(cls := "material-icons")("undo"),
            "Go back to query input"
          )
        )
      )
    ).toString()
  }

  def rootPage: String =
    html(
      head(
        meta(httpEquiv := "refresh", content := "0; url=./query"),
        script(raw("""window.location.replace("./query")"""))
      ),
      body(
        p(
          "If you wasn't redirected, click this ",
          a(href := "./query")("link")
        )
      )
    ).toString
}
