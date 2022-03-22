import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.BeforeTest;
import io.restassured.response.ValidatableResponse;
import org.testng.annotations.Test;
import org.testng.Assert;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import java.util.List;
import java.util.Objects;
import org.hamcrest.Matchers;

public class Beers {

    @BeforeTest
    public void Setup()
    {
        RestAssured.baseURI = "https://api.punkapi.com/v2/beers";
    }

    @Test(groups = "smoke")
    public void GetAllBeers()
    {
        Response response = given().get(baseURI)
                .then().assertThat().statusCode(200).extract().response();
        Assert.assertEquals(response.statusCode(),200);
        int resNameCount = response.jsonPath().getList("id").size();
        List<Objects> list;
        int totalCount = 0;
        String[] responseBodyParams = {"id", "name", "description", "abv"};
        for (String responseBodyParam : responseBodyParams) {
            list = response.getBody().jsonPath().getList(responseBodyParam);
            totalCount = list.size();
            Assert.assertEquals(totalCount, resNameCount);
        }
    }

    @Test(groups = "smoke")
            public void GetBeersBrewedBeforeCertainDate() {
        String givenBrewedBeforeDate = "02/2014";
        Response response = given().queryParam("brewed_before", givenBrewedBeforeDate).contentType("application/json")
                .get(baseURI)
                .then().assertThat().statusCode(200).extract().response();
        List<String> bearList = response.jsonPath().getList("first_brewed");
        System.out.println(bearList.size());
        int brewedBeforeYear = Integer.parseInt(givenBrewedBeforeDate.split("/")[1]);
        for (int i = 0; i < bearList.size(); i++) {
            String brewedDate = bearList.get(i);
            System.out.println(brewedDate);
            int actualBrewedYear = Integer.parseInt(brewedDate.split("/")[1]);
            Assert.assertTrue(brewedBeforeYear > actualBrewedYear);
        }
    }
    @Test(groups = "regression")
    public void getAllBearWithConditional_abv(){
        int conditional_abv = 6;
        Response response = given().queryParam("abv_gt", conditional_abv).contentType("application/json")
                .get(baseURI)
                .then().assertThat().statusCode(200).extract().response();
        List<Float> list = response.jsonPath().getList("abv");
        System.out.println(list.size());
        for (int i=0;i<list.size();i++){
            float actual_abv =  Float.parseFloat(String.valueOf(list.get(i)));
            System.out.println(actual_abv);
            Assert.assertTrue(actual_abv>conditional_abv);
        }
    }
    @Test(groups = "regression")
    public void getAllBearWithPagination(){
        int givenpage = 2;
        int givenperpage = 5;
        Response response = given().queryParam("page", givenpage).queryParam("per_page",givenperpage)
                .contentType("application/json")
                .get(baseURI)
                .then().assertThat().statusCode(200).extract().response();
        List<Integer> list = response.jsonPath().getList("id");
        System.out.println(list.size());
        Assert.assertEquals(list.size(),givenperpage);
    }
    private ValidatableResponse response(ValidatableResponse body) {
        return body
                .body("name", Matchers.notNullValue())
                .body("description", Matchers.notNullValue())
                .body("abv", Matchers.notNullValue());
    }
}
