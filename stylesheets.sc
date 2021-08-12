import $ivy.`com.github.japgolly.scalacss::core:0.5.1`
import scalacss.Defaults._
object MyStyles extends StyleSheet.Standalone {
  import dsl._

  ".header" - (
    top(0 px),
    position.fixed,
    dsl.width(100 %%),
    backgroundColor(c"#fee")
  )

  ".content" - (
    backgroundColor(c"#a00")
  )

  ".container" - (
    display.flex,
    flexDirection.column,
    dsl.width(100 %%),
    dsl.height(100 %%)
  )

  ".footer" - (
    bottom(0 px),
    dsl.width(100 %%),
    backgroundColor(c"#ccc")
  )

}
