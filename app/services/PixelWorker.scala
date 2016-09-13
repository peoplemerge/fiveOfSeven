package services

import model.{Anonymous, Authenticated}
import org.apache.spark.streaming.akka.ActorReceiver
/**
  * Created by davethomas on 9/11/16.
  */



class PixelWorker extends ActorReceiver {
  override def receive: Receive = {
    // Send the message to SparkStream https://github.com/apache/bahir/blob/master/streaming-akka/README.md
    case Anonymous(cookie, loc) => store(Anonymous(cookie, loc))
    case Authenticated(userId, loc) => store(Authenticated(userId, loc))
  }
}
