package magento

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.http.HeaderNames._
import io.gatling.http.check.HttpCheck
import scala.concurrent.duration._
import util.Random
import Headers._
import ProductFromCategory._

object Category {

	val mCategoryChecks: Seq[HttpCheck] = Seq(
            )
    val mCategoryAction =
        exec(
            http("request_category")
                .get("/home-decor/bed-bath.html")
                .headers(headers_get)
                .check(mProductFromCategoryChecks:_*)
                )
        .pause(600 * MagentoSimulation.configRealtimeRatio milliseconds, 12 * MagentoSimulation.configRealtimeRatio seconds)

}
