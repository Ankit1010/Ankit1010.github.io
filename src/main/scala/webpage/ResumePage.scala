package webpage

import org.scalajs.dom
import scala.scalajs.js.JSApp
import scalatags.Text.all._
import util._

object ResumePage extends JSApp {
  val htmlFrag = {
    html(
      head(title:="Resume")(
        jsincl("ghpage-opt.js")
      ),
      body(
        embed(src:="static/Resume.pdf", width:="100%", height:="100%")
      )
    )
  }

  def main(): Unit = {
    dom.document.documentElement.innerHTML = htmlFrag.toString()
  }
}
