package controllers

import java.io.File
import java.util.UUID
import javax.inject._

import akka.actor.{ActorSystem, Props}
import model.{Anonymous, Authenticated}
import play.api.mvc._
import services.PixelWorker

import scala.concurrent.ExecutionContext

/**
 * This controller creates an `Action` that demonstrates how to write
 * simple asynchronous code in a controller. It sends request metrics
 * to an actor for persistence
 *
 * @param actorSystem We need the `ActorSystem`'s `Scheduler` to
 * run code after a delay.
 * @param exec We need an `ExecutionContext` to execute our
 * asynchronous code.
 */
@Singleton
class AsyncController @Inject() (actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends Controller {

  val cookieKey: String = "i";
  val cookieLifetime: Int = Integer.MAX_VALUE;
  val mimeTypeGif: String = "image/gif"

  lazy val image : Array[Byte] = {
    val file = new File("public/images/px.gif")
    val source = scala.io.Source.fromFile(file)(scala.io.Codec.ISO8859)
    val byteArray = source.map(_.toByte).toArray
    source.close()
    byteArray
  }

  // Create the 'pixel' actor
  val pxWorker = actorSystem.actorOf(Props[PixelWorker], "pixel")

  def originFor(headers: Headers) :String = {
    headers
      .get(ORIGIN)
      .getOrElse(
        headers
          .get(REFERER)
          .getOrElse(
            ""
          )
      )
  }

  // Note - a "real" pixel request would use JS on the frontend to eliminate client-side caching
  // and send the page's referrer (as opposed to this request's referrer, which will always be the page it's embedded on)
  def px = Action{
    request =>{
      val origin = originFor(request.headers)
      request.cookies.get(cookieKey) match {
        case Some(k) => {
          pxWorker ! Authenticated(k.value, origin)
          Ok( image ).as(mimeTypeGif)
        }
        case None => {
          val newId: String = UUID.randomUUID().toString
          pxWorker ! Anonymous(newId, origin)
          Ok(image).withCookies(Cookie(cookieKey, newId, Some(cookieLifetime))).as(mimeTypeGif)
        }
      }
    }
  }

  // Feature Roadmap

  // def multipleChoice - a simple A/B framework.  Return a few sets of JSON or HTML text to see the ones that users
  //                      engage with most.  Similar to a polling app.  Requires an authenticated REST call or HTML
  //                      interface to set values.  Useful if you want to A/B test different versions of verbiage
  //                      to entice people to buy your product

  // def forwardLink -    link shortener - forward a user to a new location.  Useful for tracking where users go when
  //                      they leave your site.


}
