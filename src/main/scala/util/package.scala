package object util {

  import scalatags.Text.all._
  def jsincl(source: String) = {
    script(`type`:="text/javascript",src:=s"./target/scala-2.11/$source")
  }

}
