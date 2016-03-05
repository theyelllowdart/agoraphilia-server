package com.twitter.hello

import java.net.InetSocketAddress

import com.google.inject.Module
import com.twitter.app.Flag
import com.twitter.finagle.ListeningServer
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.httpclient.modules.HttpClientModule



class AdminClient(adminServer: => ListeningServer) extends HttpClientModule {
  lazy val port = adminServer.boundAddress.asInstanceOf[InetSocketAddress].getPort

  override def dest: String = ":" + port
}

object HelloWorldMain extends HelloWorldServer

class HelloWorldServer extends HttpServer {

  adminHttpServer
  override val disableAdminHttpServer = false

  override protected def modules: Seq[Module] = Vector(new AdminClient(adminHttpServer))

  override def configureHttp(router: HttpRouter) {
    router
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[CommonFilters]
      .add[HelloWorldController]
  }
}
