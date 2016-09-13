package model

/**
  * Created by davethomas on 9/11/16.
  */
sealed trait PixelEvent
case class Authenticated(userId: String, loc: String) extends PixelEvent
case class Anonymous(cookie: String, loc: String) extends PixelEvent
