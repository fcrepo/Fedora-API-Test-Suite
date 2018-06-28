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

import org.fcrepo.spec.testsuite.AbstractTest;
import org.fcrepo.spec.testsuite.TestInfo;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author awoods
 * @since 2018-06-28
 */
public class ExternalBinaryContent extends AbstractTest {

    /**
     * Authentication
     *
     * @param username
     * @param password
     */
    @Parameters({"param2", "param3"})
    public ExternalBinaryContent(final String username, final String password) {
        super(username, password);
    }

    /**
     * 3.9-A-1 PostCreate Copy
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void postCreateExternalBinaryContentCopy(final String uri) {
        final TestInfo info = setupTest("3.9-A-1", "postCreateExternalBinaryContentCopy",
                "Fedora servers should support the creation of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);
    }

    /**
     * 3.9-A-2 PostCreate Redirect
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void postCreateExternalBinaryContentRedirect(final String uri) {
        final TestInfo info = setupTest("3.9-A-2", "postCreateExternalBinaryContentRedirect",
                "Fedora servers should support the creation of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);
    }

    /**
     * 3.9-A-3 PostCreate Proxy
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void postCreateExternalBinaryContentProxy(final String uri) {
        final TestInfo info = setupTest("3.9-A-3", "postCreateExternalBinaryContentProxy",
                "Fedora servers should support the creation of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);
    }

    /**
     * 3.9-B-1 PutCreate Copy
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void putCreateExternalBinaryContentCopy(final String uri) {
        final TestInfo info = setupTest("3.9-B-1", "putCreateExternalBinaryContentCopy",
                "Fedora servers should support the creation of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);
    }

    /**
     * 3.9-B-2 PutCreate Redirect
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void putCreateExternalBinaryContentRedirect(final String uri) {
        final TestInfo info = setupTest("3.9-B-2", "putCreateExternalBinaryContentRedirect",
                "Fedora servers should support the creation of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);
    }

    /**
     * 3.9-B-3 PutCreate Proxy
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void putCreateExternalBinaryContentProxy(final String uri) {
        final TestInfo info = setupTest("3.9-B-3", "putCreateExternalBinaryContentProxy",
                "Fedora servers should support the creation of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);
    }

    /**
     * 3.9-C-1 PutUpdate Copy
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void putUpdateExternalBinaryContentCopy(final String uri) {
        final TestInfo info = setupTest("3.9-C-1", "putUpdateExternalBinaryContentCopy",
                "Fedora servers should support the update of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);
    }

    /**
     * 3.9-C-2 PutUpdate Redirect
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void putUpdateExternalBinaryContentRedirect(final String uri) {
        final TestInfo info = setupTest("3.9-C-2", "putUpdateExternalBinaryContentRedirect",
                "Fedora servers should support the update of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);
    }

    /**
     * 3.9-C-3 PutUpdate Proxy
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void putUpdateExternalBinaryContentProxy(final String uri) {
        final TestInfo info = setupTest("3.9-C-3", "putUpdateExternalBinaryContentProxy",
                "Fedora servers should support the update of LDP-NRs with content external to the " +
                        "request entity, as indicated by a link with " +
                        "rel=\"http://fedora.info/definitions/fcrepo#ExternalContent\"",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);
    }

    /**
     * 3.9-D-1 Unsupported status code
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void unsupportedExternalBinaryContentStatus(final String uri) {
        final TestInfo info = setupTest("3.9-D-1", "unsupportedExternalBinaryContentStatus",
                "Fedora servers that do not support the creation of LDP-NRs with content external must reject " +
                        "with a 4xx range status code",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);
    }

    /**
     * 3.9-D-2 Unsupported constraint
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void unsupportedExternalBinaryContentConstraint(final String uri) {
        final TestInfo info = setupTest("3.9-D-2", "unsupportedExternalBinaryContentConstraint",
                "Fedora servers that do not support the creation of LDP-NRs with content external must describe " +
                        "this restriction in a resource indicated by a " +
                        "rel=\"http://www.w3.org/ns/ldp#constrainedBy\" link in the Link response header.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);
    }

    /**
     * 3.9-E-1 handling attribute
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void externalBinaryContentHandlingAttribute(final String uri) {
        final TestInfo info = setupTest("3.9-E-1", "externalBinaryContentHandlingAttribute",
                "Fedora servers must use the handling attribute in the external content link to determine how to " +
                        "process the request. At least one of the following handling attributes must be supported: " +
                        "copy, redirect, and/or proxy.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);
    }

    /**
     * 3.9-E-2 handling status
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void externalBinaryContentHandlingStatus(final String uri) {
        final TestInfo info = setupTest("3.9-E-2", "externalBinaryContentHandlingStatus",
                "Fedora servers must reject with a 4xx range status code requests for which the handling attribute " +
                        "is not present or cannot be respected.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);
    }

    /**
     * 3.9-E-3 unsupported handling
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void unsupportedBinaryContentHandlingAttribute(final String uri) {
        final TestInfo info = setupTest("3.9-E-3", "unsupportedBinaryContentHandlingAttribute",
                "In the case that the specified handling cannot be respected, the restrictions causing the request " +
                        "to fail must be described in a resource indicated by a " +
                        "rel=\"http://www.w3.org/ns/ldp#constrainedBy\" link in the Link response header.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);
    }

    /**
     * 3.9-F-1 media type
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void binaryContentMediaType(final String uri) {
        final TestInfo info = setupTest("3.9-F-1", "binaryContentMediaType",
                "Fedora servers must use the value of the type attribute in the external content link as the media " +
                        "type of the external content, if provided.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);
    }

    /**
     * 3.9-F-2 no type attribute - external type
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MAY"})
    @Parameters({"param1"})
    public void binaryContentNoTypeExternalType(final String uri) {
        final TestInfo info = setupTest("3.9-F-2", "binaryContentNoTypeExternalType",
                "Fedora servers must use the value of the type attribute in the external content link as the media " +
                        "type of the external content, if provided. Servers may use the media type obtained when " +
                        "accessing the external content via the specified scheme (e.g. the Content-Type header for " +
                        "external content accessed via http).",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);
    }

    /**
     * 3.9-F-3 no type attribute - default
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MAY"})
    @Parameters({"param1"})
    public void binaryContentNoTypeDefault(final String uri) {
        final TestInfo info = setupTest("3.9-F-3", "binaryContentNoTypeDefault",
                "Fedora servers must use the value of the type attribute in the external content link as the media " +
                        "type of the external content, if provided. Servers may use a default media type.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);
    }

    /**
     * 3.9-F-4 no type attribute - unsupported
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MAY"})
    @Parameters({"param1"})
    public void binaryContentNoTypeUnsupported(final String uri) {
        final TestInfo info = setupTest("3.9-F-4", "binaryContentNoTypeUnsupported",
                "Fedora servers must use the value of the type attribute in the external content link as the media " +
                        "type of the external content, if provided. Servers may reject the request with a 4xx range " +
                        "status code.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);
    }

    /**
     * 3.9-F-5 Content-Type and type
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void binaryContentContentTypeAndType(final String uri) {
        final TestInfo info = setupTest("3.9-F-5", "binaryContentContentTypeAndType",
                "Fedora servers must use the value of the type attribute in the external content link as the media " +
                        "type of the external content, if provided. Any Content-Type header in the request should be " +
                        "ignored.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);
    }

    /**
     * 3.9-F-6 Content-Type and no type
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"SHOULD"})
    @Parameters({"param1"})
    public void binaryContentContentTypeAndNoType(final String uri) {
        final TestInfo info = setupTest("3.9-F-6", "binaryContentContentTypeAndNoType",
                "Fedora servers must use the value of the type attribute in the external content link as the media " +
                        "type of the external content, if provided. Any Content-Type header in the request should be " +
                        "ignored.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);
    }

    /**
     * 3.9-G-1 Guaranteed headers - describedby
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void binaryContentGuaranteeHeadersDescribedBy(final String uri) {
        final TestInfo info = setupTest("3.9-G-1", "binaryContentGuaranteeHeadersDescribedBy",
                "A Fedora server receiving requests that would create or update an LDP-NR with content external to " +
                        "the request entity must reject request if it cannot guarantee all of the response headers " +
                        "required by the LDP-NR interaction model in this specification.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);
    }

    /**
     * 3.9-G-2 Guaranteed headers - Content-Type
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void binaryContentGuaranteeHeadersContentType(final String uri) {
        final TestInfo info = setupTest("3.9-G-2", "binaryContentGuaranteeHeadersContentType",
                "A Fedora server receiving requests that would create or update an LDP-NR with content external to " +
                        "the request entity must reject request if it cannot guarantee all of the response headers " +
                        "required by the LDP-NR interaction model in this specification.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);
    }

    /**
     * 3.9-G-3 Guaranteed headers - Content-Length
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void binaryContentGuaranteeHeadersContentLength(final String uri) {
        final TestInfo info = setupTest("3.9-G-3", "binaryContentGuaranteeHeadersContentLength",
                "A Fedora server receiving requests that would create or update an LDP-NR with content external to " +
                        "the request entity must reject request if it cannot guarantee all of the response headers " +
                        "required by the LDP-NR interaction model in this specification.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);
    }

    /**
     * 3.9-G-4 Guaranteed headers - interaction model
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void binaryContentGuaranteeHeadersInteractionModel(final String uri) {
        final TestInfo info = setupTest("3.9-G-4", "binaryContentGuaranteeHeadersInteractionModel",
                "A Fedora server receiving requests that would create or update an LDP-NR with content external to " +
                        "the request entity must reject request if it cannot guarantee all of the response headers " +
                        "required by the LDP-NR interaction model in this specification.",
                "https://fcrepo.github.io/fcrepo-specification/#external-content", ps);
    }

    /**
     * 3.9.1 OPTIONS values
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void binaryContentOptions(final String uri) {
        final TestInfo info = setupTest("3.9.1", "binaryContentOptions",
                "Fedora servers supporting external content MUST include \"Accept-External-Content-Handling\" " +
                        "header in response to \"OPTIONS\" request.",
                "https://fedora.info/2018/06/25/spec/#external-content-options", ps);
    }

    /**
     * 3.9.3-A-1 redirect want-digest header
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void binaryContentRedirectWantDigest(final String uri) {
        final TestInfo info = setupTest("3.9.3-A-1", "binaryContentRedirectWantDigest",
                "Fedora servers supporting \"redirect\" external content types MUST correctly respond to the " +
                        "\"Want-Digest\" header.",
                "https://fedora.info/2018/06/25/spec/#redirect-and-proxy", ps);
    }

    /**
     * 3.9.3-A-2 proxy want-digest header
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void binaryContentProxyWantDigest(final String uri) {
        final TestInfo info = setupTest("3.9.3-A-2", "binaryContentProxyWantDigest",
                "Fedora servers supporting \"redirect\" external content types MUST correctly respond to the " +
                        "\"Want-Digest\" header.",
                "https://fedora.info/2018/06/25/spec/#redirect-and-proxy", ps);
    }

    /**
     * 3.9.3-B-1 redirect content status code on GET
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void binaryContentRedirectStatusGet(final String uri) {
        final TestInfo info = setupTest("3.9.3-B-1", "binaryContentRedirectStatusGet",
                "A successful response to a GET request for external content with handling of redirect " +
                        "must have status code of either 302 (Found) or 307 (Temporary Redirect)",
                "https://fedora.info/2018/06/25/spec/#redirect-and-proxy", ps);
    }

    /**
     * 3.9.3-B-2 redirect content status code on HEAD
     *
     * @param uri of base container of Fedora server
     */
    @Test(groups = {"MUST"})
    @Parameters({"param1"})
    public void binaryContentRedirectStatusHead(final String uri) {
        final TestInfo info = setupTest("3.9.3-B-2", "binaryContentRedirectStatusHead",
                "A successful response to a HEAD request for external content with handling of redirect " +
                        "must have status code of either 302 (Found) or 307 (Temporary Redirect)",
                "https://fedora.info/2018/06/25/spec/#redirect-and-proxy", ps);
    }

}
