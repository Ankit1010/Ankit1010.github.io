import $ivy.`com.atlassian.commonmark:commonmark:0.5.1`
import $ivy.`com.lihaoyi::ammonite-ops:0.8.0`
import $ivy.`com.lihaoyi::scalatags:0.6.2`
import ammonite.ops._
import scalatags.Text.all._

case class HTMLPost(title:String,content:String,date:String)
object HTMLPost {
  def fromPath(mdPath:Path) = {
    import org.commonmark.node._
    import org.commonmark.parser.Parser
    import org.commonmark.html.HtmlRenderer

    val parser = Parser.builder().build();
    val document = parser.parse(read! mdPath);
    val renderer = HtmlRenderer.builder().build();
    val htmlOut = renderer.render(document);

    val Array(dateStr,postTitle,md) = mdPath.last.split("\\.")
    HTMLPost(title=postTitle,content=htmlOut,date=dateStr)
  }
}

def htmlName(mdPath: Path) = {
  /* Expecting posts named like "ddmmyy.title.md" */
  val Array(dateStr,postTitle,md) = mdPath.last.split("\\.")
  dateStr + postTitle
}

def mdPathToHtml(mdPath: Path) = {
  cwd/"posts"/(htmlName(mdPath) + ".html")
}

def mdPosts(): Seq[Path] = (ls! cwd/"md_posts")

for (mdPost <- mdPosts) {
  val htmlPost = HTMLPost.fromPath(mdPost)
  rm! mdPathToHtml(mdPost)
  write.over(
    mdPathToHtml(mdPost),
    pageSkeletonWith(htmlPost.content, Some(htmlPost.title))
  )
}

rm! cwd/"index.html"
write.over(
  cwd/"index.html",
  indexPage()
)

def postListing(post: HTMLPost) = {
  div(`class`:="post-link",
    a(
      href:=s"posts/${post.date}${post.title}.html",
      h1(post.title)
    )
  )
}


def indexPage() = {
  val posts = for (mdPost <- mdPosts) yield HTMLPost.fromPath(mdPost)
  pageSkeletonWith(div(posts.map(postListing)).render,None)
}

def pageSkeletonWith(innerContent: String, disqusPostTitle: Option[String])= {
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
            disqusPostTitle.map(postTitle=>disqusSnippet(postTitle)).getOrElse(div())
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
        |  this.page.url = "http://ankitson.github.io/posts/${postTitle}.html"; // Replace PAGE_URL with your page's canonical URL variable
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
