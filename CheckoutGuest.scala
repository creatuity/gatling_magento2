package magento

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.http.HeaderNames._
import io.gatling.http.check.HttpCheck
import scala.concurrent.duration._
import util.Random
import Headers._

object CheckoutGuest {

    val mCheckoutGuestChecks: Seq[HttpCheck] = Seq(
                status.is(200),
                // Would use """(form_key.*?value="\K)([a-zA-Z0-9]*)(?=")""" but Java doesn't support \K
                regex("""(?<=form_key" type="hidden" value=")([a-zA-Z0-9]*)(?=")""").find.saveAs("checkoutKey")
            )
    val mCheckoutGuestAction =
        exec(
            http("checkout_onepage_guest")
                .post("${checkoutAction}saveMethod/")
                .headers(headers_ajax)
                .formParam("method","guest")
                .check(status.is(200))
            )
        .exitHereIfFailed
        .pause(10 * MagentoSimulation.configRealtimeRatio seconds, 30 * MagentoSimulation.configRealtimeRatio seconds)
        .exec(
            http("checkout_onepage_billing")
                .post("${checkoutAction}saveBilling/")
                .headers(headers_ajax)
                // Form fields required marked *
                .formParam("billing[address_id]","") // ?
                .formParam("billing[city]","dallas") // *
                .formParam("billing[company]","")
                .formParam("billing[confirm_password]","") // Presumably for creating a new account
                .formParam("billing[country_id]","US") // * Country code, presumably
                .formParam("billing[customer_password]","") // Again, new account?
                .formParam("billing[day]","") // day of DOB
                .formParam("billing[dob]","") // DOB
                .formParam("billing[email]","test@creatuity.com") // *
                .formParam("billing[fax]","")
                .formParam("billing[firstname]","test") // *
                .formParam("billing[gender]","")
                .formParam("billing[lastname]","order") // *
                .formParam("billing[month]","") // month of DOB
                .formParam("billing[postcode]","75238") // * 
                .formParam("billing[region]","") // * Either this or below, presumably
                .formParam("billing[region_id]","57") // * Texas == 57
                .formParam("billing[save_in_address_book]","1") // Option for logged in users?
                .formParam("billing[street][]","123 anystreet") // * Address line 1
                .formParam("billing[street][]","") // Address line 2
                .formParam("billing[telephone]","1234567890") // * Phone number
                .formParam("billing[use_for_shipping]","0") // * 0 for separate shipping address, 1 for use billing as shipping
                .formParam("billing[year]","") // year of DOB
                .check(regex(""""goto_section":"shipping"""").exists)
            )
        .exitHereIfFailed
        .pause(10 * MagentoSimulation.configRealtimeRatio seconds, 30 * MagentoSimulation.configRealtimeRatio seconds)
        .exec(
            http("checkout_onepage_shipping")
                .post("${checkoutAction}saveShipping/")
                .headers(headers_ajax)
                .formParam("shipping[address_id]","") // ?
                .formParam("shipping[city]","Richardson") // *
                .formParam("shipping[company]","") //
                .formParam("shipping[country_id]","US") // *
                .formParam("shipping[fax]","")
                .formParam("shipping[firstname]","tester") // *
                .formParam("shipping[lastname]","order") // *
                .formParam("shipping[postcode]","75080") // *
                .formParam("shipping[region]","") // *
                .formParam("shipping[region_id]","57") // *
                .formParam("shipping[save_in_address_book]","1")
                .formParam("shipping[street][]","456 someplace ave") // *
                .formParam("shipping[street][]","") //
                .formParam("shipping[telephone]","9876543210") // *
                .check(regex(""""goto_section":"shipping_method"""").exists)
            )
        .exitHereIfFailed
        .pause(1 * MagentoSimulation.configRealtimeRatio seconds, 10 * MagentoSimulation.configRealtimeRatio seconds)
        .exec(
            http("checkout_onepage_shipping_method")
                .post("${checkoutAction}saveShippingMethod/")
                .headers(headers_ajax)
//                .formParam("giftmessage[id][from]","test order") // Replace all the id with individual ID's for items from page, and for the overall cart
//                .formParam("giftmessage[id][message]","")         // I don't know an easy way to figure that out yet
//                .formParam("giftmessage[id][to]","tester order")
//                .formParam("giftmessage[id][type]","quote_item")
//                .formParam("giftoptions[id][type]","quote")
//                .formParam("giftwrapping[id][design]","") // set to blank for none or set to number based on the drop down box
//                .formParam("giftwrapping[id][add_printed_card]","1") // Select printed card (1) or not set 
//                .formParam("giftwrapping[id][allow_gift_receipt]","1") // Select allow gift receipt (1) or not set
//                .formParam("giftwrapping[{{id}}][design]","") // The overall giftwrap ? the {{id}} is what is posted ?
//                .formParam("allow_gift_messages_for_order","1") // If doing gift message for order, set to 1, otherwise vacant
//                .formParam("allow_gift_options","1") // If setting gift options, set to 1, otherwise vacant
                .formParam("shipping_method","flatrate_flatrate")
                .check(regex(""""goto_section":"payment"""").exists)
            )
        .exitHereIfFailed
        .pause(3 * MagentoSimulation.configRealtimeRatio seconds, 20 * MagentoSimulation.configRealtimeRatio seconds)
        .exec(
            http("checkout_onepage_payment_method")
                .post("${checkoutAction}savePayment/")
                .headers(headers_ajax)
//                .formParam("payment[method]","cashondelivery") // Select COD
                .formParam("payment[method]","checkmo") // Select Check / Money Order
                .check(regex(""""goto_section":"review"""").exists)
            )
        .exitHereIfFailed
        .pause(1 * MagentoSimulation.configRealtimeRatio seconds, 10 * MagentoSimulation.configRealtimeRatio seconds)
        .exec(
            http("checkout_onepage_place_order")
                .post("${checkoutAction}saveOrder/form_key/${checkoutKey}/")
                .headers(headers_post)
//                .formParam("payment[method]","cashondelivery") // Select payment method, again, or it fails.
                .formParam("payment[method]","checkmo") // Select Check / Money Order
                .check(regex(""""success":true""").exists)
            )
        .exitHereIfFailed
        .exec(
            http("checkout_onepage_success")
                .get("${checkoutAction}success/")
                .headers(headers_get)
                .check(status.is(200))
            )

}
