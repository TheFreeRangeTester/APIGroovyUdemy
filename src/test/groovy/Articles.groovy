import io.restassured.response.Response
import org.testng.Assert
import org.testng.annotations.Test

import static io.restassured.RestAssured.get
import static io.restassured.RestAssured.given

class Articles extends Base {
    @Test
    void GetArticles_ReturnsList() {
        Response response = get("/articles")

        ArrayList<String> allArticles = response.path("data.title");
        Assert.assertTrue(allArticles.size() > 1, "No articles were returned")
    }

    @Test
    void CreateAndDeleteArticle() {
        File articleFile = new File(getClass().getResource("/article.json").toURI())

        Response createResponse =
                given()
                        .body(articleFile)
                        .when()
                        .post("/articles")

        String responseID = createResponse.jsonPath().getString("post.article_id")

        Response deleteResponse =
                given()
                        .body("{\n" +
                                "\t\"article_id\": " + responseID + "\n" +
                                "}")
                        .when()
                        .delete("/articles")

        Assert.assertEquals(deleteResponse.getStatusCode(), 200)
        Assert.assertEquals(deleteResponse.jsonPath().getString("message"),"Article successfully deleted")
    }

    @Test
    void DeleteNonExistingArticle_FailMessage() {
        String nonExistentArticleID = "456123"

        Response deleteResponse =
                given()
                        .body("{\n" +
                                "\t\"article_id\": " + nonExistentArticleID + "\n" +
                                "}")
                        .when()
                        .delete("/articles")

        Assert.assertEquals(deleteResponse.getStatusCode(), 400)
        Assert.assertEquals(deleteResponse.jsonPath().getString("error"), "Unable to find article id: " + nonExistentArticleID)
    }
}
