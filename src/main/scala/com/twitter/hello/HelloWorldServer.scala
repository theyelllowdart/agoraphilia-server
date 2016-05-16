package com.twitter.hello

import java.net.InetSocketAddress
import javax.inject.Singleton

import com.google.inject.name.Named
import com.google.inject.{AbstractModule, Provides, Binder, Module}
import com.twitter.app.Flag
import com.twitter.finagle.{Service, ListeningServer}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.httpclient.{RichHttpClient, HttpClient}
import com.twitter.finatra.httpclient.modules.HttpClientModule
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.inject.TwitterModule


class AdminClientModule(adminServer: => ListeningServer) extends HttpClientModule {
  override def dest: String = ":" + adminServer.boundAddress.asInstanceOf[InetSocketAddress].getPort
}

//object Types {
//
//  trait _SmallsClient
//
//  type SmallsHttpClient = _SmallsClient with HttpClient
//}
//
//class AdminClientModule2(adminServer: => ListeningServer) extends TwitterModule {
//  @Provides
//  @Singleton
//  def provide(
//    mapper: FinatraObjectMapper
//  ): SmallsHttpClient = {
//    new HttpClient(
//      httpService = RichHttpClient.newClientService("yo"),
//      mapper = mapper
//    ) with SmallsHttpClient
//  }
//}

object HelloWorldMain extends HelloWorldServer

class HelloWorldServer extends HttpServer {

//  override protected def modules: Seq[Module] = Vector(new AdminClientModule(adminHttpServer), new AdminClientModule2(adminHttpServer))

  override def configureHttp(router: HttpRouter) {
    router
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[CommonFilters]
      .add[AdminController]
  }
}
