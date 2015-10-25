package webpage

import scala.scalajs.js.JSApp
import scalatags.Text.all._
import org.scalajs.dom.document
import util._

object IndexPage extends JSApp {
  val htmlFrag = {
    html(
      head(title:="Index")(
        jsincl("ghpage-opt.js")
      ),
      body(
        script(`type`:="text/javascript")("webpage.IndexPage().main();")
      )
    )
  }

  def main(): Unit = {
    document.documentElement.innerHTML = htmlFrag.toString()
  }
}
