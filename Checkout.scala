package magento

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.http.HeaderNames._
import io.gatling.http.check.HttpCheck
import scala.concurrent.duration._
import util.Random
import CheckoutGuest._
import Headers._

object Checkout {

	val mCheckoutChecks: Seq[HttpCheck] = Seq(
                css("button[class~=\"checkout\"]","onclick").find.saveAs("checkoutAction")
            )
    val mCheckoutAction =
        exec((session: Session) => {
            val checkoutActionRaw =  session("checkoutAction").as[String]
            val checkoutActionRegex = """(?<=location=')(.*)(?=')""".r
            val checkoutAction = (checkoutActionRegex findFirstMatchIn checkoutActionRaw).get
            println("Checkout URL : " + checkoutAction)
            session.set("checkoutAction",checkoutAction)
        })
        .pause(12 * MagentoSimulation.configRealtimeRatio seconds, 13 * MagentoSimulation.configRealtimeRatio seconds)
        .exec(
            http("checkout")
                .get("${checkoutAction}")
                .headers(headers_get)
                .check(mCheckoutGuestChecks:_*)
            )
        .exitHereIfFailed
        .pause(2 * MagentoSimulation.configRealtimeRatio seconds, 5 * MagentoSimulation.configRealtimeRatio seconds)
}
