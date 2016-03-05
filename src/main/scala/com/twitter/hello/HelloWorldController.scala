package com.twitter.hello

import javax.inject.{Inject, Singleton}

import com.twitter.finagle.http.{MediaType, Request}
import com.twitter.finatra.http.Controller
import com.twitter.finagle.http
import com.twitter.finatra.httpclient.{RichHttpClient, HttpClient}
import com.twitter.finatra.httpclient.modules.HttpClientModule
import com.twitter.finatra.utils.FuturePools
import org.joda.time.DateTime

class HelloWorldController @Inject()(httpClient: HttpClient) extends Controller {

  val date = DateTime.now

  FuturePools.unboundedPool("yo")

  get("/hi")((request: Request) => {
    info("hi")
    "Hello " + request.params.getOrElse("name", "unnamed") + date
  })

  get("/admi:*") { request: Request =>
    //    val r = Request.apply(request.)
    //    r.accept_=("*/*")
    httpClient.execute(request)
  }
}
