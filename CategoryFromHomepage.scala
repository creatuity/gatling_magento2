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

object CategoryFromHomepage {

	val mCategoryFromHomepageChecks: Seq[HttpCheck] = Seq(
                css("A[role=menuitem]","href").findAll.optional.saveAs("categories"),
                regex("""(?<=href=")(.*?/category.*?)(?=")""").findAll.optional.saveAs("categories")
            )
    val mCategoryFromHomepageAction =
        exec((session: Session) => {
            val categories = session("categories").as[Seq[String]]
            val categoryURL = categories(Random.nextInt(categories.length))
            println("Chose " + categoryURL)
            session.set("categoryURL", categoryURL)
        })
        .exec(
            http("request_category")
                .get("${categoryURL}")
                .headers(headers_get)
                .check(mProductFromCategoryChecks:_*)
                )
        .pause(3 * MagentoSimulation.configRealtimeRatio seconds, 33 * MagentoSimulation.configRealtimeRatio seconds)
        .exitHereIfFailed

}
