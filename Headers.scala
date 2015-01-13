package magento

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.http.HeaderNames._
import scala.concurrent.duration._

object Headers {

	val headers_get = Map(
		"Keep-Alive" -> "115")

	val headers_post = Map(
		"Keep-Alive" -> "115",
		"Content-Type" -> "application/x-www-form-urlencoded")

	val headers_ajax = Map(
		"Accept" -> "application/json, text/javascript, */*; q=0.01",
		"Keep-Alive" -> "115",
		"X-Requested-With" -> "XMLHttpRequest",
        "X-Prototype-Version" -> "1.7",
        "Content-Type" -> "application/x-www-form-urlencoded")

}
