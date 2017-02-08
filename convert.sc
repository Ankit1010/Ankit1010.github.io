import $ivy.`com.atlassian.commonmark:commonmark:0.5.1`
import $ivy.`com.lihaoyi::ammonite-ops:0.8.0`
import $ivy.`com.lihaoyi::scalatags:0.6.2`
import $ivy.`org.scalikejdbc::scalikejdbc:2.5.0`
import $ivy.`com.h2database:h2:1.4.193`
import ammonite.ops._
import scalatags.Text.all._
// case class MDPost(path:Path)

// case class MDPost(title:String,content:String,timestamp:java.sql.Timestamp)
case class HTMLPost(title:String,content:String)
object HTMLPost {
  def fromPath(path:Path) = {
    import org.commonmark.node._
    import org.commonmark.parser.Parser
    import org.commonmark.html.HtmlRenderer

    val parser = Parser.builder().build();
    val document = parser.parse(read! path);
    val renderer = HtmlRenderer.builder().build();
    val htmlOut = renderer.render(document);

    val Array(postNum,name) = path.last.split(" - ")
    val htmlName = name.stripSuffix(".md").replace(" ", "-").toLowerCase
    HTMLPost(htmlName,htmlOut)
  }
}

def mdPathToHtml(mdPath: Path) = {
  val Array(postNum,name) = mdPath.last.split(" - ")
  val htmlName = name.stripSuffix(".md").replace(" ", "-").toLowerCase + ".html"
  cwd/"html_posts"/htmlName
}

def mdPosts(): Seq[Path] = (ls! cwd/'posts)


// import scalikejdbc._

// initialize JDBC driver & connection pool
// Class.forName("org.h2.Driver")
// ConnectionPool.singleton("jdbc:h2:file:./posts", "sa", "")
//
// // ad-hoc session provider on the REPL
// implicit val session = AutoSession
//
// def createPostsTable() = {
//   sql"""
//   create table posts (
//     title varchar not null primary key,
//     content varchar,
//     created_at timestamp not null
//   )
//   """.execute.apply()
// }

// try { createPostsTable } catch { case _ => () }

for (mdPost <- mdPosts) {
  val htmlPost = HTMLPost.fromPath(mdPost)
  rm! mdPathToHtml(mdPost)
  write.over(
    mdPathToHtml(mdPost),
    pageSkeletonWith(htmlPost.content, Some(htmlPost.title))
  )
  // sql"""
  //   insert into posts (title,content,created_at) values
  //   (${htmlPost.title},${htmlPost.content},current_timestamp)
  // """.update.apply()
}

write.over(
  cwd/"index.html",
  indexPage()
)

def postListing(post: HTMLPost) = {
  div(
    a(
      href:=s"html_posts/${post.title}.html#disqus_thread",
      h1(post.title)
    )
  )
}


def indexPage() = {
  val posts = for (mdPost <- mdPosts) yield HTMLPost.fromPath(mdPost)
  val postListing = div(
    for (post <- posts) yield
    a(href:=f"html_posts/${post.title}.html#disqus_thread", h1(post.title))
  ).render

  pageSkeletonWith(postListing, None)
}

def pageSkeletonWith(innerContent: String, postTitle: Option[String])= {
  val cssIncludes = List(
    "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css",
    "https://cdnjs.cloudflare.com/ajax/libs/highlight.js/9.1.0/styles/github-gist.min.css"
  ).map(url => link(href:=url,rel:="stylesheet",`type`:="text/css"))

  "<!DOCTYPE html>" +
  html(
    head(
      meta(charset:="utf-8"),meta(name:="viewport", content:="width=device-width, initial-scale=1"),
      cssIncludes,
      script(src:="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/9.1.0/highlight.min.js"),
      script(src:="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/9.1.0/languages/scala.min.js"),
      script(raw("hljs.initHighlightingOnLoad();"))
    ),
    body(
      div(`class`:="container",
        div(`class`:="row",
          div(id:="sidebar",`class`:="col-md-3",
            h1("Ankit's Blog")
          ),
          div(id:="content",`class`:="col-md-9",
            raw(innerContent)
          )
        ),
        div(`class`:="row",
          div(id:="footer",
            postTitle.map(pTitle=>disqusSnippet(pTitle)).getOrElse(div())
          )
        )
      ),
      script(id:="dsq-count-scr", src:="//ankitson.disqus.com/count.js",attr("async"):="async")
    )
  ).render
}

def disqusSnippet(postTitle: String) = {
  div(
    id := "disqus_thread",
    script(raw(
      s"""
        |
        |  /**
        |  *  RECOMMENDED CONFIGURATION VARIABLES: EDIT AND UNCOMMENT THE SECTION BELOW TO INSERT DYNAMIC VALUES FROM YOUR PLATFORM OR CMS.
        |  *  LEARN WHY DEFINING THESE VARIABLES IS IMPORTANT: https://disqus.com/admin/universalcode/#configuration-variables*/
        |
        |  var disqus_config = function () {
        |  this.page.url = "http://ankitson.github.io/html_posts/${postTitle}.html"; // Replace PAGE_URL with your page's canonical URL variable
        |  this.page.identifier = "$postTitle"; // Replace PAGE_IDENTIFIER with your page's unique identifier variable
        |  };
        |
        |  (function() { // DON'T EDIT BELOW THIS LINE
        |  var d = document, s = d.createElement('script');
        |  s.src = '//ankitson.disqus.com/embed.js';
        |  s.setAttribute('data-timestamp', +new Date());
        |  (d.head || d.body).appendChild(s);
        |  })();

      """.stripMargin
    ))
  )
}
