package com.agoraphilia.service


import org.joda.time._
import org.junit.Assert.assertEquals
import org.junit.Test

import scala.io.Source

class SmallsJazzScraperTest {

  @Test
  def parse(): Unit = {
    val content = Source.fromFile(getClass.getClassLoader.getResource("smalls-jazz/events-calendar-2016-5.html").toURI).mkString
    val events = new SmallsJazzScraper().parse(content)

    val edt = DateTimeZone.forID("America/New_York")
    assertEquals(events.head.title, "Vocal Masterclass with Marion Cowings")
    assertEquals(events.head.href, "/events/11243-vocal-masterclass-with-marion-cowings/")
    assertEquals(events.head.start, new DateTime(2016, 5, 1, 13, 0, edt))
    assertEquals(events.head.end.get, new DateTime(2016, 5, 1, 15, 0, edt))

    assertEquals(events.last.title, "After-hours Jam Session with Jovan Alexander")
    assertEquals(events.last.href, "/events/11256-after-hours-jam-session-with-jovan-alexander/")
    assertEquals(events.last.start, new DateTime(2016, 6, 1, 1, 30, edt))
    assertEquals(events.last.end.get, new DateTime(2016, 6, 1, 4, 0, edt))

  }
}
