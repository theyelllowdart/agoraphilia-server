package com.agoraphilia.server

import com.agoraphilia.controller.EventController
import com.google.inject.Module
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.json.modules.FinatraJacksonModule
import com.twitter.finatra.json.utils.CamelCasePropertyNamingStrategy
import com.twitter.hello.AdminController

object AgoraphiliaMain extends AgoraphiliaServer

class AgoraphiliaServer extends HttpServer {

  override protected def jacksonModule: Module = new FinatraJacksonModule {
    override val propertyNamingStrategy = CamelCasePropertyNamingStrategy
  }

  override def configureHttp(router: HttpRouter) {
    router
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[CommonFilters]
      .add[EventController]
  }
}
