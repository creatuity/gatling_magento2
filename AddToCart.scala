package magento

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.http.HeaderNames._
import io.gatling.http.check.HttpCheck
import scala.concurrent.duration._
import util.Random
import rapture.core._
import rapture.json._
import rapture.json.jsonBackends.scalaJson._
import Headers._
import Checkout._

object AddToCart {

	val mAddToCartChecks: Seq[HttpCheck] = Seq(
                css("#product_addtocart_form","action").find.saveAs("formAction"),
                css("input[name=form_key]","value").find.saveAs("formKey"),
                css("input[name=product]","value").find.saveAs("productSku"),
                css("input[name=related_product]","value").find.saveAs("relatedProduct"),
                // For configurable products
                regex("""(?<=\('#product_addtocart_form'\).configurable\()(.*)(?=\);)""").find.optional.saveAs("productConfig2"),
                // For bundled products
                // TODO: Add textarea and input detection for required text fields
                regex("""(?s)<select.*?select>""").findAll.optional.saveAs("bundleOptionSelectOpts"),
                css("""select[name*="bundle_option"]""","name").findAll.optional.saveAs("bundleOptionSelectName"),
                css("""input[name*="bundle_option_qty"]""","name").findAll.optional.saveAs("bundleOptionQty")
                // For product options
                // TODO: Add product options (haven't found one yet that had required options)
            )
    val mAddToCartAction =
        exec((session: Session) => {
            val formAction = session("formAction")
            val formKey = session("formKey")
            val bundleParams = collection.mutable.Map.empty[String,String]
            val configParams = collection.mutable.Map.empty[String,String]
            val optionParams = collection.mutable.Map.empty[String,String]

            // Configurable Products (Magento 2)
            if (session.contains("productConfig2"))
            {
                // We have a configurable product, let's extract the JSON and set the appropriate things
                val productConfigJson2 = session("productConfig2").as[String]
                val json: Json = Json.parse(productConfigJson2)

                // Some case classes to wrap up to take the JSON data
                case class ProductOptions2(
                    prices: Prices2,
                    label: String,
                    products: Any,
                    id: String
                )
                case class ProductAttributes2(
                    id: String,
                    code: String,
                    label: String,
                    options: List[ProductOptions2]
                )
                case class Price2(
                    amount: String//Float
                )
                case class Prices2(
                    oldPrice: Price2,
                    basePrice: Price2,
                    finalPrice: Price2
                )
                case class ProductConfig2(
                    attributes: Map[String,ProductAttributes2],
                    template: String,
                    prices: Prices2,
                    productId: String,
                    chooseText: String,
                    images: Map[String,Map[String,Map[String,String]]],
                    baseImage: String
                )
                case class PCWrapper2(
                    spConfig: ProductConfig2
                )

                // This maps the JSON as a ProductConfig2, including it's various children..
                val productConfiguration = json.as[PCWrapper2]
                // Now that we have the JSON into a nice structure, we can, for each attribute, build an
                // array of product IDs then pick one from it and assign it to an appropriately named
                // session variable

                // For each attribute
                productConfiguration.spConfig.attributes foreach
                {
                    case (productId, productAttributes) =>
                    {
                        val productIds = new scala.collection.mutable.ListBuffer[String]()
                        // For each option
                        productAttributes.options foreach
                        {
                            case (productOptions) =>
                            {
                                // Add to the list of option IDs
                                productIds += productOptions.id
                            }
                        }
                        val productIdsList = productIds.toList
                        // Add randomly chosen option ID for attribute as a form value
                        configParams += ("super_attribute[" + productId + "]") -> (productIdsList(Random.nextInt(productIdsList.length))).toString
                    }
                }
            }

            // Bundled products
            if (session.contains("bundleOptionSelectName"))
            {
                // We have bundled options. 
                val bundleOptionSelectNames = session("bundleOptionSelectName").as[Seq[String]]
                val bundleOptionSelectOptsRaw = session("bundleOptionSelectOpts").as[Seq[String]]
                val bundleOptionSelectOptsRegex = """(?s)(?<=<option value=")([\d]+?)(?=">)""".r
                val bundleOptionQtys = session("bundleOptionQty").as[Seq[String]]
                // For each named select, explode it's options and pick one and assign it to the Seq
                val i = 0;
                for ( i <- 0 until bundleOptionSelectNames.length)
                {
                    val bundleName = bundleOptionSelectNames(i)
                    val bundleOptions = bundleOptionSelectOptsRegex.findAllIn(bundleOptionSelectOptsRaw(i)).toList
                    bundleParams += bundleName -> (bundleOptions(Random.nextInt(bundleOptions.length-1)))
                }
                // For each bundle option quantity selection, assign it a random qty (or just 1 for now)
                for ( i <- 0 until bundleOptionQtys.length)
                {
                    bundleParams += bundleOptionQtys(i) -> (Random.nextInt(2)+1).toString
                }
            }

            //TODO: Add product options

            session.set("qty", Random.nextInt(4) + 1)
            .set("bundleParams", bundleParams.toMap)
            .set("configParams", configParams.toMap)
            .set("optionParams", optionParams.toMap)
        })
        .exec(
            http("add_to_cart")
                .post("${formAction}")
                .headers(headers_post)
                .formParam("form_key","${formKey}")
                .formParam("product","${productSku}")
                .formParam("qty","${qty}")
                .formParam("related_product","${relatedProduct}")
                .formParamMap("${bundleParams}")
                .formParamMap("${configParams}")
                .formParamMap("${optionParams}")
                .check(mCheckoutChecks:_*)
            )
        .exitHereIfFailed
        .pause(1 * MagentoSimulation.configRealtimeRatio seconds, 14 * MagentoSimulation.configRealtimeRatio seconds)
}
