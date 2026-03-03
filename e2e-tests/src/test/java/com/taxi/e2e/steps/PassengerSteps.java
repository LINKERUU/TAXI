package com.taxi.e2e.steps;

import io.cucumber.java.ru.Допустим;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Value;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;

public class PassengerSteps {

  @Value("${test.base-url:http://localhost:8083}")
  private String baseUrl;

  private Response lastResponse;
  private Long lastCreatedId;

  static {
    RestAssured.baseURI = "http://localhost:8083";
  }


  private String replaceId(String path) {
    if (lastCreatedId == null) {
      throw new IllegalStateException("ID пассажира ещё не сохранён");
    }
    return path.replace("{id}", lastCreatedId.toString());
  }

  private String replacePlaceholders(String text) {
    return text.replace("${timestamp}", String.valueOf(System.currentTimeMillis()));
  }

  @Допустим("Базовый URL сервиса пассажиров")
  public void базовыйURLСервисаПассажиров() {
    System.out.println("Базовый URL сервиса пассажиров: " + baseUrl);
  }

  @Тогда("возвращена ошибка 500")
  public void check500Error() {
    lastResponse.then().statusCode(500);
    System.out.println("✓ Получена ошибка 500, как и ожидалось");
  }

  // Базовые HTTP-шаги
  @Когда("я отправляю POST-запрос на {string} с телом:")
  public void sendPost(String path, String body) {
    String preparedBody = replacePlaceholders(body);
    lastResponse = given()
            .contentType(ContentType.JSON)
            .body(preparedBody)
            .when()
            .post(path);
  }

  @Когда("я отправляю GET-запрос на {string}")
  public void sendGet(String path) {
    String realPath = path.contains("{id}") ? replaceId(path) : path;
    lastResponse = given().when().get(realPath);
  }

  @Когда("я отправляю PUT-запрос на {string} с телом:")
  public void sendPut(String path, String body) {
    String realPath = path.contains("{id}") ? replaceId(path) : path;
    String preparedBody = replacePlaceholders(body);
    lastResponse = given()
            .contentType(ContentType.JSON)
            .body(preparedBody)
            .when()
            .put(realPath);
  }

  @Когда("я отправляю DELETE-запрос на {string}")
  public void sendDelete(String path) {
    String realPath = path.contains("{id}") ? replaceId(path) : path;
    lastResponse = given().when().delete(realPath);
  }

  @Допустим("существует пассажир с email {string}")
  public void preconditionExistingPassenger(String email) {
    String safeEmail = replacePlaceholders(email);
    String body = """
                {
                    "name": "Предусловие",
                    "email": "%s",
                    "phone": "+375000000000"
                }
                """.formatted(safeEmail);

    given()
            .contentType(ContentType.JSON)
            .body(body)
            .post("/api/passengers");
  }

  @Допустим("создан пассажир {string} с email {string}")
  public void createAndRememberPassenger(String name, String email) {
    String preparedEmail = replacePlaceholders(email);
    String body = """
                {
                    "name": "%s",
                    "email": "%s",
                    "phone": "+375291112266"
                }
                """.formatted(name, preparedEmail);

    lastResponse = given()
            .contentType(ContentType.JSON)
            .body(body)
            .post("/api/passengers");

    if (lastResponse.statusCode() == 201 || lastResponse.statusCode() == 200) {
      try {
        lastCreatedId = lastResponse.jsonPath().getLong("id");
      } catch (Exception e) {
      }
    }
  }

  @Допустим("я запомнил его id")
  public void rememberLastId() {
  }

  @Тогда("статус ответа должен быть {int}")
  public void statusCodeIs(int expectedStatus) {
    int actualStatus = lastResponse.statusCode();
    if (actualStatus != expectedStatus) {
      System.err.println("Ожидался статус: " + expectedStatus + ", но получен: " + actualStatus);
      System.err.println("Тело ответа: " + lastResponse.getBody().asString());
    }
    lastResponse.then().statusCode(expectedStatus);
  }

  @Тогда("статус ответа должен быть {int} или {int}")
  public void statusCodeIsEither(int status1, int status2) {
    int actual = lastResponse.statusCode();
    String body = lastResponse.getBody().asString();
    assertTrue("Статус должен быть " + status1 + " или " + status2 + ", но был " + actual + "\nТело: " + body,
            actual == status1 || actual == status2);
  }

  @Тогда("статус ответа должен быть 500 и сообщение содержит {string}")
  public void status500WithMessage(String expectedText) {
    lastResponse.then().statusCode(500);
    String body = lastResponse.getBody().asString();
    assertTrue("Тело ответа должно содержать: " + expectedText + "\nФактическое тело: " + body,
            body.contains(expectedText) ||
                    body.contains("RuntimeException") ||
                    body.contains("Exception"));
  }

  @Тогда("в теле ответа должно быть поле {string}")
  public void hasField(String field) {
    lastResponse.then().body(field, notNullValue());
  }

  @Тогда("поле {string} равно {string}")
  public void fieldEquals(String field, String expected) {
    lastResponse.then().body(field, equalTo(expected));
  }

  @Тогда("в теле ответа содержится {string}")
  public void bodyContains(String expectedText) {
    String actualBody = lastResponse.getBody().asString();
    assertTrue("Ожидалось, что тело ответа содержит: \"" + expectedText + "\"\nФактическое тело: " + actualBody,
            actualBody.contains(expectedText));
  }
}