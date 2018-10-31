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

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.fcrepo.spec.testsuite.AbstractTest;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

/**
 * @author Jorge Abrego, Fernando Cardoza
 */
public class HttpDelete extends AbstractTest {

    /**
     * 3.8.1-A
     */
    @Test(groups = {"SHOULD"})
    public void httpDeleteOptionsCheck() {
        final TestInfo info = setupTest("3.8.1-A",
                                        "An implementation that cannot recurse should not advertise DELETE in " +
                                        "response to OPTIONS "
                                        + "requests for containers with contained resources.",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-delete-recursion",
                                        ps);

        final Response resourceOp = createBasicContainer(uri, info);
        final String locationHeader = getLocation(resourceOp);
        final Response resourceSonOp = createBasicContainer(locationHeader, "Delete-" + info.getId());

        final Headers headers = new Headers(
                new Header(SLUG, "Delete1-" + info.getId()),
                new Header(CONTENT_DISPOSITION, "attachment; filename=\"rdf01.txt\""));
        final Response rdf01 = doPost(locationHeader, headers, "TestString.");

        final String locationHeader2 = getLocation(resourceSonOp);

        final Headers headers2 = new Headers(
                new Header(SLUG, "Delete2-" + info.getId()),
                new Header(CONTENT_DISPOSITION, "attachment; filename=\"rdf02.txt\""));
        final Response rdf02 = doPost(locationHeader2, headers2, "TestString.");

        final Headers headers3 = new Headers(
                new Header(SLUG, "Delete3-" + info.getId()),
                new Header(CONTENT_DISPOSITION, "attachment; filename=\"rdf03.txt\""));
        final Response rdf03 = doPost(locationHeader2, headers3, "TestString.");

        final String rlocationHeader1 = getLocation(rdf01);
        final String rlocationHeader2 = getLocation(rdf02);
        final String rlocationHeader3 = getLocation(rdf03);

        ps.append("Request method:\tDELETE\n");
        ps.append("Request URI:\t").append(uri).append("\n");
        ps.append("Headers:\tAccept=*/*\n");
        ps.append("Body:\n");

        // Is DELETE supported?
        skipTestIfDeleteNotSupported(locationHeader);

        // Delete to resourceOp
        final Response response = doDelete(locationHeader);

        // Print headers and status
        final Headers responseHeaders = response.getHeaders();
        ps.append(response.getStatusLine());

        for (Header h : responseHeaders) {
            ps.append(h.getName()).append(": ").append(h.getValue()).append("\n");
        }

        //GET deleted resources
        doGetUnverified(locationHeader).then().statusCode(410);
        doGetUnverified(locationHeader2).then().statusCode(410);
        doGetUnverified(rlocationHeader1).then().statusCode(410);
        doGetUnverified(rlocationHeader2).then().statusCode(410);
        doGetUnverified(rlocationHeader3).then().statusCode(410);
    }

    /**
     * 3.8.1-C
     */
    @Test(groups = {"MUST"})
    public void httpDeleteStatusCheck() {
        final TestInfo info = setupTest("3.8.1-C",
                                        "An implementation must not return a 200 (OK) or 204 (No Content) response "
                                        + "unless the entire operation successfully completed.",
                                        "https://fcrepo.github.io/fcrepo-specification/#http-delete-recursion",
                                        ps);
        // Create resources
        final Response rootres = createBasicContainer(uri, info);
        final String locationHeader = getLocation(rootres);
        final Response resourceSon = createBasicContainer(locationHeader, "Delete-" + info.getId());
        final String locationHeader2 = getLocation(resourceSon);

        final Headers headers = new Headers(
                new Header(CONTENT_DISPOSITION, "attachment; filename=\"nrdf01.txt\""),
                new Header(SLUG, "Delete1-" + info.getId()));
        final Response nrdf01 = doPost(locationHeader, headers, "TestString.");

        final Headers headers2 = new Headers(
                new Header(CONTENT_DISPOSITION, "attachment; filename=\"nrdf02.txt\""),
                new Header(SLUG, "Delete2-" + info.getId()));
        final Response nrdf02 = doPost(locationHeader2, headers2, "TestString.");

        final Headers headers3 = new Headers(
                new Header(CONTENT_DISPOSITION, "attachment; filename=\"nrdf03.txt\""),
                new Header(SLUG, "Delete3-" + info.getId()));
        final Response nrdf03 = doPost(locationHeader2, headers3, "TestString.");

        final String rlocationHeader1 = getLocation(nrdf01);
        final String rlocationHeader2 = getLocation(nrdf02);
        final String rlocationHeader3 = getLocation(nrdf03);

        ps.append("Request method:\tDELETE\n");
        ps.append("Request URI:\t").append(uri).append("\n");
        ps.append("Headers:\tAccept=*/*\n");
        ps.append("Body:\n");

        // Is DELETE supported?
        skipTestIfDeleteNotSupported(locationHeader);

        // Delete root folder
        final Response response = doDelete(locationHeader);

        // Print headers and status
        final int statusDelete = response.getStatusCode();
        final Headers responseHeaders = response.getHeaders();
        ps.append(response.getStatusLine());

        for (Header h : responseHeaders) {
            ps.append(h.getName()).append(": ").append(h.getValue()).append("\n");
        }

        //GET deleted resources
        final Response resResource = doGetUnverified(locationHeader);
        final Response resResourceSon = doGetUnverified(locationHeader2);
        final Response resRdf01 = doGetUnverified(rlocationHeader1);
        final Response resRdf02 = doGetUnverified(rlocationHeader2);
        final Response resRdf03 = doGetUnverified(rlocationHeader3);

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
     */
    @Test(groups = {"MUST"})
    public void httpDeleteStatusCheckTwo() {
        final TestInfo info = setupTest("3.8.1-D",
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

        final Headers headers = new Headers(
                new Header(CONTENT_DISPOSITION, "attachment; filename=\"nrdf01.txt\""),
                new Header(SLUG, "Delete1-" + info.getId()));
        final Response nrdf01 = doPost(locationHeader, headers, "TestString.");

        final Headers headers2 = new Headers(
                new Header(CONTENT_DISPOSITION, "attachment; filename=\"nrdf02.txt\""),
                new Header(SLUG, "Delete2-" + info.getId()));
        final Response nrdf02 = doPost(locationHeader2, headers2, "TestString.");

        final Headers headers3 = new Headers(
                new Header(CONTENT_DISPOSITION, "attachment; filename=\"nrdf03.txt\""),
                new Header(SLUG, "Delete3-" + info.getId()));
        final Response nrdf03 = doPost(locationHeader2, headers3, "TestString.");

        final String rlocationHeader1 = getLocation(nrdf01);
        final String rlocationHeader2 = getLocation(nrdf02);
        final String rlocationHeader3 = getLocation(nrdf03);

        ps.append("Request method:\tDELETE\n");
        ps.append("Request URI:\t").append(uri).append("\n");
        ps.append("Headers:\tAccept=*/*\n");
        ps.append("Body:\n");

         // Is DELETE supported?
        skipTestIfDeleteNotSupported(locationHeader);

        // Delete root folder
        final Response response = doDelete(locationHeader);

        // Print headers and status
        final int statusDelete = response.getStatusCode();
        final Headers responseHeaders = response.getHeaders();
        ps.append(response.getStatusLine());

        for (Header h : responseHeaders) {
            ps.append(h.getName()).append(": ").append(h.getValue()).append("\n");
        }

        //GET deleted resources
        final Response resResource = doGetUnverified(locationHeader);
        final Response resResourceSon = doGetUnverified(locationHeader2);
        final Response resRdf01 = doGetUnverified(rlocationHeader1);
        final Response resRdf02 = doGetUnverified(rlocationHeader2);
        final Response resRdf03 = doGetUnverified(rlocationHeader3);

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

    private void skipTestIfDeleteNotSupported(final String location) {
        final Response response = doOptions(location);

        if (getHeaders(response, "Allow").noneMatch(h -> h.getValue().contains("DELETE"))) {
            throw new SkipException("DELETE not supported");
        }
    }

}
