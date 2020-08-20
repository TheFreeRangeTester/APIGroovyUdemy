import io.restassured.response.Response
import org.testng.Assert
import org.testng.annotations.Test

import static io.restassured.RestAssured.get
import static io.restassured.RestAssured.given
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath

class Books extends Base {
    @Test
    void GetBooks_ReturnsList() {
        Response response = get("/books")

        ArrayList<String> allBooks = response.path("data.title");
        Assert.assertTrue(allBooks.size() > 1, "No books were returned")
    }

    @Test
    void GetBooks_SchemaIsValid() {
        get("/books")
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("booksSchema.json"))
    }

    @Test
    void CreateAndDeleteBook() {
        File bookFile = new File(getClass().getResource("/book.json").toURI())

        Response createResponse =
                given()
                        .body(bookFile)
                        .when()
                        .post("/books")

        String responseID = createResponse.jsonPath().getString("post.book_id")

        Response deleteResponse =
                given()
                        .body("{\n" +
                                "\t\"book_id\": " + responseID + "\n" +
                                "}")
                        .when()
                        .delete("/books")

        Assert.assertEquals(deleteResponse.getStatusCode(), 200)
        Assert.assertEquals(deleteResponse.jsonPath().getString("message"), "Book successfully deleted")
    }

    @Test
    void DeleteNonExistingBook_FailMessage() {
        String nonExistentBookID = "456123"

        Response deleteResponse =
                given()
                        .body("{\n" +
                                "\t\"book_id\": " + nonExistentBookID + "\n" +
                                "}")
                        .when()
                        .delete("/books")

        Assert.assertEquals(deleteResponse.getStatusCode(), 400)
        Assert.assertEquals(deleteResponse.jsonPath().getString("error"), "Unable to find book id: " + nonExistentBookID)
    }
}
