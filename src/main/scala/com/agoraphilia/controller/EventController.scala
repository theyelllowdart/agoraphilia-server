package com.agoraphilia.controller

import com.agoraphilia.service.SmallsJazzScraper
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.finatra.request.QueryParam
import org.joda.time.{DateTime, Interval}


case class EventsRequest(
  @QueryParam startDate: DateTime,
  @QueryParam endDate: DateTime
)

class EventController extends Controller {

  get("/events") { request: EventsRequest =>
    val smalls = new SmallsJazzScraper
    smalls.get(new Interval(request.startDate.withZone(smalls.timezone), request.endDate.withZone(smalls.timezone)))
  }
}
