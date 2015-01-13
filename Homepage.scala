package magento

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.http.HeaderNames._
import io.gatling.http.check.HttpCheck
import scala.concurrent.duration._
import Headers._
import CategoryFromHomepage._

object Homepage {

	val mHomepageChecks: Seq[HttpCheck] = Seq(
            )
    val mHomepageAction =
        exec(
            http("request_homepage")
                .get("/")
                .headers(headers_get)
                .check(mCategoryFromHomepageChecks:_*)
                )
        .pause(600 * MagentoSimulation.configRealtimeRatio milliseconds, 12 * MagentoSimulation.configRealtimeRatio seconds)

}
