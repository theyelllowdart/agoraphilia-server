package com.agoraphilia.service


import com.agoraphilia.model.Event
import org.joda.time.{DateTime, Interval}
import org.junit.Assert.assertEquals
import org.junit.Test

import scala.io.Source

class IFCComingSoonScraperTest {
  @Test
  def parse(): Unit = {
    val content = Source.fromFile(getClass.getClassLoader.getResource("ifc/coming-soon/2016-05-08.html").toURI).mkString
    val events = new IFCComingSoonScraper().parse(content)
    val expectedEvents = Vector(
      new Event("THE LOVERS AND THE DESPOT", "http://www.ifccenter.com/films/the-lovers-and-the-despot/",
        new DateTime(2016, 5, 10, 19, 0), Some(new DateTime(2016, 5, 10, 19, 0))),

      new Event("Kramer vs. Kramer", "http://www.ifccenter.com/films/kramer-vs-kramer/",
        new DateTime(2016, 5, 12, 19, 0), Some(new DateTime(2016, 5, 12, 19, 0)),
        None, Some("Director Robert Benton in person for Q&A with Meryl Streep biographer Michael Schulman!")),

      new Event("Fargo", "http://www.ifccenter.com/films/fargo-2/",
        new DateTime(2016, 5, 13, 19, 0), Some(new DateTime(2016, 5, 13, 19, 0)),
        None, Some("DCP projection")),
      new Event("Fargo", "http://www.ifccenter.com/films/fargo-2/",
        new DateTime(2016, 5, 14, 19, 0), Some(new DateTime(2016, 5, 14, 19, 0)),
        None, Some("DCP projection")),

      new Event("Needful Things", "http://www.ifccenter.com/films/needful-things/",
        new DateTime(2016, 5, 13, 19, 0), Some(new DateTime(2016, 5, 13, 19, 0)),
        None, Some("35mm print")),
      new Event("Needful Things", "http://www.ifccenter.com/films/needful-things/",
        new DateTime(2016, 5, 14, 19, 0), Some(new DateTime(2016, 5, 14, 19, 0)),
        None, Some("35mm print")),

      new Event("ACE IN THE HOLE", "http://www.ifccenter.com/films/ace-in-the-hole/",
        new DateTime(2016, 5, 13, 19, 0), Some(new DateTime(2016, 5, 13, 19, 0)),
        None, Some("DCP projection")),
      new Event("ACE IN THE HOLE", "http://www.ifccenter.com/films/ace-in-the-hole/",
        new DateTime(2016, 5, 14, 19, 0), Some(new DateTime(2016, 5, 14, 19, 0)),
        None, Some("DCP projection")),
      new Event("ACE IN THE HOLE", "http://www.ifccenter.com/films/ace-in-the-hole/",
        new DateTime(2016, 5, 15, 19, 0), Some(new DateTime(2016, 5, 15, 19, 0)),
        None, Some("DCP projection")),

      new Event("Pelé: Birth of a Legend", "http://www.ifccenter.com/films/pele-birth-of-a-legend/",
        new DateTime(2016, 5, 13, 19, 0), Some(new DateTime(2016, 5, 13, 19, 0)),
        None, Some("Opening")),
      new Event("WHAT WE’VE BECOME", "http://www.ifccenter.com/films/what-weve-become/",
        new DateTime(2016, 5, 13, 19, 0), Some(new DateTime(2016, 5, 13, 19, 0)),
        None, Some("Opening"))
    )
    assertEquals(expectedEvents, events)
  }
}
