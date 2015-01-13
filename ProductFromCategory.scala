package magento

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.http.HeaderNames._
import io.gatling.http.check.HttpCheck
import scala.concurrent.duration._
import util.Random
import Headers._
import AddToCart._

object ProductFromCategory {

	val mProductFromCategoryChecks: Seq[HttpCheck] = Seq(
                css(".product-item-photo","href").findAll.saveAs("products")
            )
    val mProductFromCategoryAction =
        exec((session: Session) => {
            val products = session("products").as[Seq[String]]
            val productURL = products(Random.nextInt(products.length))
            println("Chose " + productURL)
            session.set("productURL", productURL)
        })
        .exec(
            http("request_product")
                .get("${productURL}")
                .headers(headers_get)
                .check(mAddToCartChecks:_*)
                )
        .pause(3 * MagentoSimulation.configRealtimeRatio seconds, 33 * MagentoSimulation.configRealtimeRatio seconds)
        .exitHereIfFailed

}
