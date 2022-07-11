package utils;

import org.json.JSONObject;

import env.ApplicationConfigProperties;
import env.Resource;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.core.annotations.Step;
import utilities.ApplicationConfigConstants;

public class RequestProcessor {

    ApplicationConfigProperties appProps = Resource.INSTANCE.getApplicationProperties();
    private static String authToken;

    public RequestSpecification getCommonSpec(String path) {
        RequestSpecification rSpec = SerenityRest.given();
        String basePath = appProps.getPropertyValue(path);
        rSpec.contentType(ContentType.JSON).baseUri(appProps.getBaseURL() + basePath);
        return rSpec;
    }

    protected JSONObject createJSONPayload(Object pojo) {
        return new JSONObject(pojo);
    }

    public String getAuthorizationToken() {
        if (authToken == null || authToken.length() < 1) {
            JSONObject jsonObj = new JSONObject().put(ApplicationConfigConstants.APPLICATION_USERNAME, appProps.getUsername())
                    .put(ApplicationConfigConstants.APPLICATION_PASSWORD, appProps.getPassword());
            RequestSpecification rspec = getCommonSpec("auth")
                    .body(jsonObj.toString());
            Response response = sendRequest(rspec, Constants.RequestType.POST_REQUEST, null);
            authToken = response.jsonPath().getString("token");
        }

        return authToken;
    }

    @Step
    public Response sendRequest(RequestSpecification request, int requestType, Object pojo) {
        Response response;
        if (pojo != null) {
            String payload = createJSONPayload(pojo).toString();
            request.body(payload);
        }
        switch (requestType) {
            case Constants.RequestType.POST_REQUEST:
                if (request == null) {
                    response = SerenityRest.when().post();
                } else {
                    response = request.post();
                }
                break;
            case Constants.RequestType.DELETE_REQUEST:
                if (request == null) {
                    response = SerenityRest.when().delete();
                } else {
                    response = request.delete();
                }
                break;
            case Constants.RequestType.PUT_REQUEST:
                if (request == null) {
                    response = SerenityRest.when().put();
                } else {
                    response = request.put();
                }
                break;
            case Constants.RequestType.PATCH_REQUEST:
                if (request == null) {
                    response = SerenityRest.when().patch();
                } else {
                    response = request.patch();
                }
                break;
            case Constants.RequestType.GET_REQUEST:
            default:
                if (request == null) {
                    response = SerenityRest.when().get();
                } else {
                    response = request.get();
                }
                break;
        }
        return response;
    }

}
