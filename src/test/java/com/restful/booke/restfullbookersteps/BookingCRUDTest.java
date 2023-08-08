package com.restful.booke.restfullbookersteps;

import com.restful.booke.testbase.TestBase;
import com.restful.booke.utils.TestUtils;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.annotations.Title;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SerenityRunner.class)
public class BookingCRUDTest extends TestBase {
    static String firstName = "PrimUser" + TestUtils.getRandomValue();
    static String updateFirstName = "Update" + TestUtils.getRandomValue();
    static String lastName = "Testing" + TestUtils.getRandomValue();
    static String additionalNeeds = "Breakfast";
    static int price = 111;
    static boolean depositPaid = true;
    static int userId;

    static String token;


    @Steps
    RestFullBookerSteps restFullBookerSteps;

    @WithTag("restfulFeature:POSITIVE")
    @Title("This will create a new Token")
    @Test
    public void test001() {

        Response response = restFullBookerSteps.createToken();
        response.then().log().all().statusCode(200);
        String responseBody = response.getBody().asString();
        JsonPath jsonPath = new JsonPath(responseBody);
        token = jsonPath.getString("token");

    }

    @WithTags({
            @WithTag("restfulFeature:SMOKE"),
            @WithTag("restfulFeature:POSITIVE")
    })
    @Title("This will create a new Booking")
    @Test()
    public void test002() {

        Response response = restFullBookerSteps.createNewBooking(firstName, lastName, price, depositPaid, additionalNeeds, token);
        ValidatableResponse response1 = response.then().log().all().statusCode(200);
        userId = response1.extract().path("bookingid");
        int expectedPrice = response1.extract().path("booking.totalprice");


        Assert.assertEquals(firstName, response1.extract().path("booking.firstname"));
        Assert.assertEquals(lastName, response1.extract().path("booking.lastname"));
        Assert.assertEquals(price, expectedPrice);
        Assert.assertEquals(additionalNeeds, response1.extract().path("booking.additionalneeds"));


    }

    @WithTags({
            @WithTag("restfulFeature:SMOKE"),
            @WithTag("restfulFeature:NEGATIVE")
    })
    @Title("This will Update Booking")
    @Test
    public void test003() {

        Response response = restFullBookerSteps.updateNewBooking(updateFirstName, lastName, price, depositPaid, additionalNeeds, token, userId);
        response.then().log().all().statusCode(200);
        String responseBody = response.getBody().asString();
        JsonPath jsonPath = new JsonPath(responseBody);

        Assert.assertEquals(updateFirstName, jsonPath.getString("firstname"));


    }

    @WithTag("restfulFeature:POSITIVE")
    @Title("This will Get Booking by BookingId")
    @Test
    public void test004() {

        Response response = restFullBookerSteps.getBookingId(userId, token);
        response.then().log().all().statusCode(200);
        String responseBody = response.getBody().asString();
        JsonPath jsonPath = new JsonPath(responseBody);

        Assert.assertEquals(updateFirstName, jsonPath.getString("firstname"));
        Assert.assertEquals(lastName, jsonPath.getString("lastname"));
        Assert.assertEquals(price, jsonPath.getInt("totalprice"));
        Assert.assertEquals(additionalNeeds, jsonPath.getString("additionalneeds"));

    }

    @WithTags({
            @WithTag("restfulFeature:SMOKE"),
            @WithTag("restfulFeature:POSITIVE")
    })
    @Title("This will delete Booking by BookingId")
    @Test
    public void test005() {

        Response response = restFullBookerSteps.deleteBookingId(userId, token);
        response.then().log().all().statusCode(201);

        response = restFullBookerSteps.getBookingId(userId, token);
        response.then().log().all().statusCode(404);

    }
}
