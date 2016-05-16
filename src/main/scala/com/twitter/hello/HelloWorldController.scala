package com.twitter.hello

import javax.inject.{Inject, Singleton}

import com.twitter.finagle.http.{MediaType, Request}
import com.twitter.finatra.http.Controller
import com.twitter.finagle.http
import com.twitter.finatra.httpclient.{RichHttpClient, HttpClient}
import com.twitter.finatra.httpclient.modules.HttpClientModule
import com.twitter.finatra.utils.FuturePools
import org.joda.time.DateTime

class AdminController @Inject()(httpClient: HttpClient) extends Controller {

  // NOTE(aaron): Use a wildcard in order to avoid being catalogued as an admin route in HttpRouter.partitionRoutesByType.
  get("/admi:*") { request: Request =>
    httpClient.execute(request)
  }
}
