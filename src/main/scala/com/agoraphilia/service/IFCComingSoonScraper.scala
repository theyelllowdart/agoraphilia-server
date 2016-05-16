package com.agoraphilia.service

import com.agoraphilia.model.Event
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.{HttpRequest, HttpRequestInitializer}
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.twitter.util.Future
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import org.joda.time.{DateTime, DateTimeZone, Days, Interval, LocalDate, LocalTime}

import scalaj.http._

class IFCComingSoonScraper {
  val OpensPattern = "(Opens )(.*)".r
  val RangePattern = "(.*) - (.*)".r

  def timezone: DateTimeZone = DateTimeZone.forID("America/New_York")


  def get(interval: Interval): Future[Seq[Event]] = {
    ???
  }

  def parse(content: String): Seq[Event] = {
    val browser = JsoupBrowser()
    val doc = browser.parseString(content)
    (for {
      listing <- doc >> element("#coming_soon_box") >> elementList(".wide_listing_right")
    } yield {
      val title = listing >> text("h3")
      val href = listing >> element("h3") >> attr("href")("a")
      val descriptions = (listing >> texts("strong")).toVector
      val format: DateTimeFormatter = DateTimeFormat.forPattern("E, MMM d")
        .withZone(timezone)
        .withDefaultYear(DateTime.now.getYear)
      val details: Vector[(Interval, Option[String])] = descriptions.head match {
        case OpensPattern(_, startDateStr) =>
          val start = LocalDate.parse(startDateStr, format).toDateTime(new LocalTime(19, 0))
          Vector((new Interval(start, start), Some("Opening")))
        case RangePattern(startDateStr, endDateStr) =>
          val start = LocalDate.parse(startDateStr, format)
          val end = LocalDate.parse(endDateStr, format)
          Range(0, Days.daysBetween(start, end).getDays).inclusive.toVector.map(offset => {
            val date = start.plusDays(offset).toDateTime(new LocalTime(19, 0))
            (new Interval(date, date), descriptions.lift(1))
          })
        case blah =>
          val start = LocalDate.parse(blah, format).toDateTime(new LocalTime(19, 0))
          Vector((new Interval(start, start), descriptions.lift(1)))
      }
      details.map({ case (interval, descriptionOpt) =>
        new Event(title, href, interval.getStart, Some(interval.getEnd), None, descriptionOpt)
      })
    }).flatten.toVector
  }

}
