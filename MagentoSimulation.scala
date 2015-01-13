package magento

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.http.HeaderNames._
import scala.concurrent.duration._
import util.Random
import Headers._

import Homepage._
import CategoryFromHomepage._
import ProductFromCategory._
import AddToCart._
import Checkout._
import CheckoutGuest._

object MagentoSimulation {
    val configBaseUrl = System.getProperty("baseurl","http://localhost")
    val configRealtimeRatio = System.getProperty("realtimeratio", "1.0").toFloat
    val configAtOnceUsers = Integer.getInteger("atonceusers", 1)
    val configRampUsers = Integer.getInteger("rampusers", 10)
    val configRampSeconds = Integer.getInteger("rampseconds", 30)
}

class MagentoSimulation extends Simulation {

	val httpConf = http
        .baseURL(MagentoSimulation.configBaseUrl)
		.acceptCharsetHeader("ISO-8859-1,utf-8;q=0.7,*;q=0.7")
		.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
		.acceptEncodingHeader("gzip, deflate")
//		.acceptLanguageHeader("fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3")

	val scn = scenario("Select random product and checkout")
        .exec(mHomepageAction)
        .exec(mCategoryFromHomepageAction)
        .exec(mProductFromCategoryAction)
        .exec(mAddToCartAction)
        .exec(mCheckoutAction)
        .exec(mCheckoutGuestAction)

	setUp(scn.inject(nothingFor(1 seconds),
            atOnceUsers(MagentoSimulation.configAtOnceUsers),
            rampUsers(MagentoSimulation.configRampUsers) over (MagentoSimulation.configRampSeconds seconds)
            )).protocols(httpConf)
}
