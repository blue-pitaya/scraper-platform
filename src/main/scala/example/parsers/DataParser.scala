package example.parsers

import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model._
import example.models._
import net.ruippeixotog.scalascraper.scraper.HtmlExtractor
import scala.util.Try

object DataParser {
  def parseH1Text(ticket: UrlTicket, document: Document): Option[HeaderInfo] = {
    val h1Elements = document >> elementList("h1")
    h1Elements.map(e => e >> text("h1")).collectFirst { case x =>
      HeaderInfo(x, ticket.url)
    }
  }

  def parseHotel(document: Document): Option[Hotel] = {
    for {
      name <- document >?> element(".productName-holder h1")
      stars <- parseStars(document)
      description <- document >?> elementList("#product-tab-productdescription p")
      location <- parseLocation(document)
      basePriceText <- document >?> element("span.price strong[data-js-value=\"offerPrice\"]")
      basePrice <- parsePrice(basePriceText.text)
    } yield Hotel(
      name = name.text,
      stars = stars,
      description = description.map(d => d.text).mkString(";;;"),
      location = location,
      basePrice = basePrice
    )
  }

  def parsePrice(text: String): Option[Double] =
    Try(("""\d|\.""".r findAllIn text).mkString.toDouble).toOption

  def parseStars(document: Document): Option[Double] = {
    val maybeStarsNumClass = for {
      starsSpan <- document >?> element("a span.stars")
    } yield {
      val spanClasses = starsSpan.attr("class")
      spanClasses.replaceFirst("stars", "").trim()
    }
    maybeStarsNumClass.flatMap { c => classToStars.lift(c) }
  }

  def classToStars: PartialFunction[String, Double] = {
    case "star10" => 1
    case "star15" => 1.5
    case "star20" => 2
    case "star25" => 2.5
    case "star30" => 3
    case "star35" => 3.5
    case "star40" => 4
    case "star45" => 4.5
    case "star50" => 5
  }

  def parseLocation(document: Document): Option[Location] = {
    val maybeLocationParts = for {
      locationText <- document >?> element("span.destination-country-region")
    } yield (locationText.text.split("/").map(_.trim()).toList)
    maybeLocationParts.flatMap {
      _ match {
        case country :: region :: city :: _ => Some(Location(country, Some(region), Some(city)))
        case country :: region :: Nil       => Some(Location(country, Some(region), None))
        case country :: Nil                 => Some(Location(country, None, None))
        case _                              => None
      }
    }
  }

  //def parseHotel(document: Document): Option[Hotel] = {
  //  for {
  //    name <- document >?> element(".top-section__hotel-name")
  //    stars <- document >?> elementList(".top-section__hotel-rating ul li")
  //    description <- document >?> element(".HotelDescription_hotelDescription__PYHkg p")
  //    location <- parseLocation(document)
  //  } yield Hotel(name.text, stars.size, description.text, location)
  //}

  //private def parseLocation(document: Document): Option[Location] = {
  //  val locationParts = for {
  //    locationElements <- document >?> elementList(
  //      ".top-section__subheading nav ol li span[property=\"name\"]"
  //    )
  //  } yield (locationElements.map(e => e.text))

  //  locationParts match {
  //    case Some(loc) =>
  //      loc match {
  //        case counrty :: Nil                   => Some(Location(counrty, None, None))
  //        case counrty :: region :: Nil         => Some(Location(counrty, Some(region), None))
  //        case counrty :: region :: city :: Nil => Some(Location(counrty, Some(region), Some(city)))
  //        case _                                => None
  //      }
  //    case None => None
  //  }
  //}
}
