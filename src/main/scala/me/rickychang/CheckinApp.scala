package me.rickychang

import scalaj.http.{ Http, HttpOptions }
import net.liftweb.json._


object CheckinApp extends App {

  def checkin(venueId: String, oauthToken: String, lat: Float, long: Float, retries: Int): Option[String] = {
    val checkinParams = List(
      "venueId" -> venueId,
      "ll" -> s"$lat,$long",
      "v" -> "20130704",
      "broadcast" -> "public",
      "oauth_token" -> oauthToken)
    val req = Http.post("https://api.foursquare.com/v2/checkins/add")
      .option(HttpOptions.connTimeout(1000))
      .option(HttpOptions.readTimeout(5000))
      .params(checkinParams)
    val (responseCode, headersMap, resultString) = req.asHeadersAndParse(Http.readString)

    responseCode match {
      case 200 => {
        val resultJSON = parse(resultString)
        val JString(checkinId) = resultJSON \ "response" \ "checkin" \ "id"
        Some(checkinId)
      }
      case _ => {
        if (retries > 0) checkin(venueId, oauthToken, lat, long, retries - 1) else None
      }
    }
  }

  if (args.length != 4) {
    Console.println("Usage: CheckinApp <venueId> <oauth_token> <lat> <long>")
    System.exit(0)
  }

  val venueId = args(0)
  val oauthToken = args(1)
  val lat = args(2).toFloat
  val long = args(3).toFloat

  Console.println(s"Attempting to checkin to $venueId")
  val checkinId = checkin(venueId, oauthToken, lat, long, 2)
  val message = checkinId.map(id => s"Checkin succeeded. Checkin ID: $id")
    .getOrElse("Checkin failed.")
  Console.println(message)

  val exitCode = if (checkinId.isDefined) 0 else 1
  System.exit(exitCode)
}
