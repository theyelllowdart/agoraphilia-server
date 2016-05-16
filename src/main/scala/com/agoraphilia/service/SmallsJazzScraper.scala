package com.agoraphilia.service

import java.net.{URL, URLEncoder}

import com.agoraphilia.model.Event
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.{HttpRequest, HttpRequestInitializer}
import com.google.api.client.json.jackson2.JacksonFactory
import com.twitter.finatra.httpclient.RichHttpClient
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import org.joda.time.{DateTimeZone, Interval, LocalDate, LocalTime, Months}
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import com.twitter.util.Future
import com.google.api.services.youtube.YouTube

import scalaj.http._
import com.google.api.services.youtube.model.SearchResult

import scala.collection.immutable.IndexedSeq

class SmallsJazzScraper {
  def timezone: DateTimeZone = DateTimeZone.forID("America/New_York")

  def get(interval: Interval): Future[Seq[Event]] = {
    val distinctMonths = interval.getStart.getMonthOfYear - interval.getEnd.getMonthOfYear
    val eventRequestsF: Seq[Future[Seq[Event]]] = Range.inclusive(0, distinctMonths).map(plusMonths => {
      val date = interval.getStart.plusMonths(plusMonths)
      val url = s"https://www.smallslive.com/events/calendar/${date.getYear}/${date.getMonthOfYear}/"
      Future.apply(parse(Http(url).asString.body))
    })
    Future.collect(eventRequestsF).map(eventRequests => {
      val laxRequestInterval = new Interval(interval.getStart, interval.getEnd.plusDays(1).plusHours(4))
      eventRequests
        .flatten
        .filter(event => laxRequestInterval.contains(event.start))
        .map(event => {
          val youTube = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance, new HttpRequestInitializer() {
            override def initialize(request: HttpRequest): Unit = {}
          }).build()
          val resp = youTube.search()
            .list("snippet")
            .setQ(event.title.replaceAll("[^A-Za-z0-9 ]", ""))
            .setKey("AIzaSyBCTs_JL87OwtGHuIgs3AHibDrq217R2XU")
            .setType("video")
            .setMaxResults(1l)
            .execute()
          val videoId = resp.getItems.headOption.map(_.getId.getVideoId)
          event.copy(youtubeVideoId = videoId)
        })
        .sortBy(_.start.getMillis)
    })
  }

  def parse(content: String): Seq[Event] = {
    val browser = JsoupBrowser()
    val doc = browser.parseString(content)
    (for {
      day <- (doc >> elementList(".day")).toVector
    } yield {
      val intervals = {
        val localDate: LocalDate = {
          val dateStr: String = day >> text(".day__date")
          // Sunday 5/1/2016
          val format: DateTimeFormatter = DateTimeFormat.forPattern("E MM/dd/yyyy").withZone(timezone)
          LocalDate.parse(dateStr, format)
        }
        for {
          timeRangeString <- day >> texts(".day__event-time")
        } yield {
          val times = timeRangeString.split('-').map(time => {
            // 1:00 PM - 3:00 PM
            val format: DateTimeFormatter = DateTimeFormat.forPattern("hh:mm a").withZone(timezone)
            val localTime = LocalTime.parse(time.trim, format)
            val dateTime = localDate.toDateTime(localTime)
            if (dateTime.hourOfDay().get() <= 4) {
              dateTime.plusDays(1)
            } else {
              dateTime
            }
          })
          val (start, rawEnd) = (times(0), times(1))
          // Fix 10:30 PM - 1:30 PM
          val end = if (start.isAfter(rawEnd)) {
            rawEnd.plusDays(1).minusHours(12)
          } else {
            rawEnd
          }
          new Interval(start, end)
        }
      }
      val titles = for {
        title <- day >> elementList(".day__event-title")
      } yield {
        (title.text, title >> attr("href")("a"))
      }
      intervals.zip(titles).map({ case (interval, (title, href)) => new Event(title, href, interval.getStart, Option(interval.getEnd)) })
    }).flatten
  }

}
