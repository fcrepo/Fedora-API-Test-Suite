/*
 * Licensed to DuraSpace under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.
 *
 * DuraSpace licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fcrepo.spec.testsuite.crud;

import static org.fcrepo.spec.testsuite.Constants.CONTENT_DISPOSITION;
import static org.fcrepo.spec.testsuite.Constants.SLUG;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.fcrepo.spec.testsuite.AbstractTest;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author Jorge Abrego, Fernando Cardoza
 */
public class HttpDelete extends AbstractTest {

    /**
     * Authentication
     *
     * @param username
     * @param password
     */
    @Parameters({"param2", "param3"})
    public HttpDelete(final String username, final String password) {
        super(username, password);
    }

    /**
     * 3.8.1-A
     *
     * @param uri
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void httpDeleteOptionsCheck(final String uri) {
        final TestInfo info = setupTest("3.8.1-A", "httpDeleteOptionsCheck",
                                        "An implementation that cannot recurse should not advertise DELETE in " +
                                        "response to OPTIONS "
                                        + "requests for containers with contained resources.",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-delete-recursion",
                                        ps);

        final Response resourceOp = createBasicContainer(uri, info);
        final String locationHeader = getLocation(resourceOp);
        final Response resourceSonOp = createBasicContainer(locationHeader, "Delete-" + info.getId());

        final Response rdf01 =
            createRequest().header(CONTENT_DISPOSITION, "attachment; filename=\"rdf01.txt\"")
                           .header(SLUG, "Delete1-" + info.getId())
                           .body("TestString.")
                           .when()
                           .post(locationHeader);

        final String locationHeader2 = getLocation(resourceSonOp);

        final Response rdf02 =
            createRequest().header(CONTENT_DISPOSITION, "attachment; filename=\"rdf02.txt\"")
                           .header(SLUG, "Delete2-" + info.getId())
                           .body("TestString.")
                           .when()
                           .post(locationHeader2);

        final Response rdf03 =
            createRequest().header(CONTENT_DISPOSITION, "attachment; filename=\"rdf03.txt\"")
                           .header(SLUG, "Delete3-" + info.getId())
                           .body("TestString.")
                           .when()
                           .post(locationHeader2);

        final String rlocationHeader1 = getLocation(rdf01);
        final String rlocationHeader2 = getLocation(rdf02);
        final String rlocationHeader3 = getLocation(rdf03);

        ps.append("Request method:\tDELETE\n");
        ps.append("Request URI:\t").append(uri).append("\n");
        ps.append("Headers:\tAccept=*/*\n");
        ps.append("Body:\n");

        // Options to resourceOp
        final Response responseOptions = createRequest().when()
                                                        .options(locationHeader);

        final String allowHeader = responseOptions.getHeader("Allow");

        // Delete to resourceOp
        final Response response = createRequest().when()
                                                 .delete(locationHeader);

        // Print headers and status
        final Headers headers = response.getHeaders();
        ps.append(response.getStatusLine());

        for (Header h : headers) {
            ps.append(h.getName()).append(": ").append(h.getValue()).append("\n");
        }

        //GET deleted resources
        final Response resResource = doGet(locationHeader);
        final Response resResourceSon = doGet(locationHeader2);
        final Response resRdf01 = doGet(rlocationHeader1);
        final Response resRdf02 = doGet(rlocationHeader2);
        final Response resRdf03 = doGet(rlocationHeader3);

        if (allowHeader.contains("OPTIONS")) {
            if (resResource.getStatusCode() != 410 && resResourceSon.getStatusCode() != 410 &&
                resRdf01.getStatusCode() != 410 && resRdf02.getStatusCode() != 410 &&
                resRdf03.getStatusCode() != 410) {
                Assert.fail();
            }
        } else if (resResource.getStatusCode() == 410 || resResourceSon.getStatusCode() == 410 ||
                resRdf01.getStatusCode() == 410 || resRdf02.getStatusCode() == 410 ||
                resRdf03.getStatusCode() == 410) {
                Assert.fail();
        }

    }

    /**
     * 3.8.1-C
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void httpDeleteStatusCheck(final String uri) {
        final TestInfo info = setupTest("3.8.1-C", "httpDeleteStatusCheck",
                                        "An implementation must not return a 200 (OK) or 204 (No Content) response "
                                        + "unless the entire operation successfully completed.",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-delete-recursion",
                                        ps);
        // Create resources
        final Response rootres = createBasicContainer(uri, info);
        final String locationHeader = getLocation(rootres);
        final Response resourceSon = createBasicContainer(locationHeader, "Delete-" + info.getId());
        final String locationHeader2 = getLocation(resourceSon);
        final Response nrdf01 =
            createRequest().header(CONTENT_DISPOSITION, "attachment; filename=\"nrdf01.txt\"")
                           .header(SLUG, "Delete1-" + info.getId())
                           .body("TestString.")
                           .when()
                           .post(locationHeader);

        final Response nrdf02 =
            createRequest().header(CONTENT_DISPOSITION, "attachment; filename=\"nrdf02.txt\"")
                           .header(SLUG, "Delete2-" + info.getId())
                           .body("TestString.")
                           .when()
                           .post(locationHeader2);

        final Response nrdf03 =
            createRequest().header(CONTENT_DISPOSITION, "attachment; filename=\"nrdf03.txt\"")
                           .header(SLUG, "Delete3-" + info.getId())
                           .body("TestString.")
                           .when()
                           .post(locationHeader2);
        final String rlocationHeader1 = getLocation(nrdf01);
        final String rlocationHeader2 = getLocation(nrdf02);
        final String rlocationHeader3 = getLocation(nrdf03);

        ps.append("Request method:\tDELETE\n");
        ps.append("Request URI:\t").append(uri).append("\n");
        ps.append("Headers:\tAccept=*/*\n");
        ps.append("Body:\n");

        // Delete root folder
        final Response response = createRequest().when()
                                                 .delete(locationHeader);

        // Print headers and status
        final int statusDelete = response.getStatusCode();
        final Headers headers = response.getHeaders();
        ps.append(response.getStatusLine());

        for (Header h : headers) {
            ps.append(h.getName()).append(": ").append(h.getValue()).append("\n");
        }

        //GET deleted resources
        final Response resResource = doGet(locationHeader);
        final Response resResourceSon = doGet(locationHeader2);
        final Response resRdf01 = doGet(rlocationHeader1);
        final Response resRdf02 = doGet(rlocationHeader2);
        final Response resRdf03 = doGet(rlocationHeader3);

        if (statusDelete == 200 || statusDelete == 204) {
            if (resResource.getStatusCode() != 410 && resResourceSon.getStatusCode() != 410 &&
                resRdf01.getStatusCode() != 410 && resRdf02.getStatusCode() != 410 &&
                resRdf03.getStatusCode() != 410) {
                Assert.fail();
            }
        } else {
            if (resResource.getStatusCode() == 410 || resResourceSon.getStatusCode() == 410 ||
                resRdf01.getStatusCode() == 410 || resRdf02.getStatusCode() == 410 ||
                resRdf03.getStatusCode() == 410) {
                Assert.fail();
            }
        }
    }

    /**
     * 3.8.1-D
     *
     * @param uri
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void httpDeleteStatusCheckTwo(final String uri) {
        final TestInfo info = setupTest("3.8.1-D", "httpDeleteStatusCheckTwo",
                                        "An implementation must not emit a message that implies the successful DELETE" +
                                        " of a resource until "
                                        + "the resource has been successfully removed.",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-delete-recursion",
                                        ps);
        // Create resources
        final Response rootres = createBasicContainer(uri, info);
        final String locationHeader = getLocation(rootres);
        final Response resourceSon = createBasicContainer(locationHeader, "Delete-" + info.getId());
        final String locationHeader2 = getLocation(resourceSon);
        final Response nrdf01 =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .header(CONTENT_DISPOSITION, "attachment; filename=\"nrdf01.txt\"")
                       .header(SLUG, "Delete1-" + info.getId())
                       .body("TestString.")
                       .when()
                       .post(locationHeader);

        final Response nrdf02 =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .header(CONTENT_DISPOSITION, "attachment; filename=\"nrdf02.txt\"")
                       .header(SLUG, "Delete2-" + info.getId())
                       .body("TestString.")
                       .when()
                       .post(locationHeader2);

        final Response nrdf03 =
            RestAssured.given()
                       .auth().basic(this.username, this.password)
                       .header(CONTENT_DISPOSITION, "attachment; filename=\"nrdf03.txt\"")
                       .header(SLUG, "Delete3-" + info.getId())
                       .body("TestString.")
                       .when()
                       .post(locationHeader2);

        final String rlocationHeader1 = getLocation(nrdf01);
        final String rlocationHeader2 = getLocation(nrdf02);
        final String rlocationHeader3 = getLocation(nrdf03);

        ps.append("Request method:\tDELETE\n");
        ps.append("Request URI:\t").append(uri).append("\n");
        ps.append("Headers:\tAccept=*/*\n");
        ps.append("Body:\n");

        // Delete root folder
        final Response response = createRequest().when()
                                                 .delete(locationHeader);

        // Print headers and status
        final int statusDelete = response.getStatusCode();
        final Headers headers = response.getHeaders();
        ps.append(response.getStatusLine());

        for (Header h : headers) {
            ps.append(h.getName()).append(": ").append(h.getValue()).append("\n");
        }

        //GET deleted resources
        final Response resResource = doGet(locationHeader);
        final Response resResourceSon = doGet(locationHeader2);
        final Response resRdf01 = doGet(rlocationHeader1);
        final Response resRdf02 = doGet(rlocationHeader2);
        final Response resRdf03 = doGet(rlocationHeader3);

        final String statusdeletestring = String.valueOf(statusDelete);
        if (statusdeletestring.charAt(0) == '2') {
            if (resResource.getStatusCode() != 410 && resResourceSon.getStatusCode() != 410 &&
                resRdf01.getStatusCode() != 410 && resRdf02.getStatusCode() != 410 &&
                resRdf03.getStatusCode() != 410) {
                Assert.fail();
            }
        } else {
            if (resResource.getStatusCode() == 410 || resResourceSon.getStatusCode() == 410 ||
                resRdf01.getStatusCode() == 410 || resRdf02.getStatusCode() == 410 ||
                resRdf03.getStatusCode() == 410) {
                Assert.fail();
            }
        }
    }

}
