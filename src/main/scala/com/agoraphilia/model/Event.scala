package com.agoraphilia.model

import org.joda.time.DateTime


case class Event(
  title: String,
  href: String,
  start: DateTime,
  end: Option[DateTime],
  youtubeVideoId: Option[String] = None,
  descriptionOpt: Option[String] = None
) {
  override def toString: String = {
    val format = "MM/dd hh:mm"
    s"$title | ${start.toString(format)} | ${end.map(_.toString(format))} | $href | $descriptionOpt"
  }
}
