/**
 * @author Jorge Abrego, Fernando Cardoza
 */
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
package com.ibr.fedora;

public class TestsLabels {
    /**
     * Constructor
     */
    public TestsLabels() {
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] ldpnrCreationLinkType() {
        return new String[] {
            "3.1.2.-A - LDPNR-LDPNRCreationLinkType",
            "If, in a successful resource creation request, a Link: rel=\"type\" request header specifies"
            + " the LDP-NR interaction model (http://www.w3.org/ns/ldp#NonRDFSource, regardless of "
            + "Content-Type: value), then the server should handle subsequent requests to the newly "
            + "created resource as if it is an LDP-NR. ([LDP] 5.2.3.4 extension)",
            "https://fcrepo.github.io/fcrepo-specification/#ldpnr-ixn-model",
            "SHOULD"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] ldpnrCreationWrongLinkType() {
        return new String[] {
            "3.1.2.-B - LDPNR-LDPNRCreationWrongLinkType",
            "If, in a successful resource creation request, a Link: rel=\"type\" request header specifies"
            + " the LDP-NR interaction model (http://www.w3.org/ns/ldp#NonRDFSource, regardless of "
            + "Content-Type: value), then the server should handle subsequent requests to the newly "
            + "created resource as if it is an LDP-NR. ([LDP] 5.2.3.4 extension)",
            "https://fcrepo.github.io/fcrepo-specification/#ldpnr-ixn-model",
            "SHOULD"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] createLDPC() {
        return new String[] {
            "3.1.1-A - Container-CreateLDPC",
            "Implementations must support the creation and management of [LDP] Containers.",
            "https://fcrepo.github.io/fcrepo-specification/#ldpc",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] ldpcContainmentTriples() {
        return new String[] {
            "3.1.1-B - Container-ldpcContainmentTriples",
            "LDP Containers must distinguish [containment triples]",
            "https://fcrepo.github.io/fcrepo-specification/#ldpc",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] ldpcMembershipTriples() {
        return new String[] {
            "3.1.1-C - Container-ldpcMembershipTriples",
            "LDP Containers must distinguish [membership] triples.",
            "https://fcrepo.github.io/fcrepo-specification/#ldpc",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] ldpcMinimalContainerTriples() {
        return new String[] {
            "3.1.1-D - Container-ldpcMinimalContainerTriples",
            "LDP Containers must distinguish [minimal-container] triples.",
            "https://fcrepo.github.io/fcrepo-specification/#ldpc",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] supportPatch() {
        return new String[] {
            "3.7-A - HttpPatch-SupportHttpPatch",
            "Any LDP-RS must support PATCH ([LDP] 4.2.7 may becomes must). [sparql11-update] must be an accepted "
            + "content-type for PATCH.",
            "https://fcrepo.github.io/fcrepo-specification/#http-patch",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] ldpPatchContentTypeSupport() {
        return new String[] {
            "3.7-B - HttpPatch-LDPPatchContentTypeSupport",
            "Other content-types (e.g. [ldpatch]) may be available.",
            "https://fcrepo.github.io/fcrepo-specification/#http-patch"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] serverManagedPropertiesModification() {
        return new String[] {
            "3.7-C - HttpPatch-ServerManagedPropertiesModification",
            "If an otherwise valid HTTP PATCH request is received that attempts to modify "
            + "statements to a resource that a server disallows (not ignores per [LDP] "
            + "4.2.4.1), the server must fail the request by responding with a 4xx range"
            + " status code (e.g. 409 Conflict).",
            "https://fcrepo.github.io/fcrepo-specification/#http-patch",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] statementNotPersistedResponseBody() {
        return new String[] {
            "3.7-D - HttpPatch-StatementNotPersistedResponseBody",
            "The server must provide a corresponding response body containing information"
            + " about which statements could not be persisted."
            + " ([LDP] 4.2.4.4 should becomes must).",
            "https://fcrepo.github.io/fcrepo-specification/#http-patch",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] statementNotPersistedConstrainedBy() {
        return new String[] {
            "3.7-E - HttpPatch-StatementNotPersistedConstrainedByHeader",
            "In that response, the restrictions causing such a request to fail must be"
            + " described in a resource indicated by a Link: "
            + "rel=\"http://www.w3.org/ns/ldp#constrainedBy\" "
            + "response header per [LDP] 4.2.1.6.",
            "https://fcrepo.github.io/fcrepo-specification/#http-patch",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] successfulPatchStatusCode() {
        return new String[] {
            "3.7-F - HttpPatch-SuccessfulPatchStatusCode",
            "A successful PATCH request must respond with a 2xx status code; the "
            + "specific code in the 2xx range may vary according to the response "
            + "body or request state.",
            "https://fcrepo.github.io/fcrepo-specification/#http-patch",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] disallowPatchContainmentTriples() {
        return new String[] {
            "3.7.1 - HttpPatch-DisallowPatchContainmentTriples",
            "The server should not allow HTTP PATCH to update an LDPC’s containment triples; if"
            + " the server receives such a request, it should respond with a"
            + " 409 (Conflict) status code.",
            "https://fcrepo.github.io/fcrepo-specification/#http-patch-containment-triples",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] disallowChangeResourceType() {
        return new String[] {
            "3.7.2 - HttpPatch-DisallowChangeResourceType",
            "The server must disallow a PATCH request that would change the LDP"
            + " interaction model of a resource to a type that is not a subtype"
            + " of the current resource type. That request must be rejected"
            + " with a 409 Conflict response.",
            "https://fcrepo.github.io/fcrepo-specification/#http-patch-ixn-models",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] respondWantDigest() {
        return new String[] {
            "3.2.3-A - HttpGet-RespondWantDigest",
            "Testing for supported digest "
            + "GET requests to any LDP-NR must correctly respond to the Want-Digest "
            + "header defined in [RFC3230]",
            "https://fcrepo.github.io/fcrepo-specification/#http-get-ldpnr",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] httpPost() {
        return new String[] {
            "3.5-A - HttpPost",
            "Any LDPC (except Version Containers (LDPCv)) must support POST ([LDP] 4.2.3 / 5.2.3). ",
            "https://fcrepo.github.io/fcrepo-specification/#http-post",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] constrainedByResponseHeader() {
        return new String[] {
            "3.5-B - HttpPost-ConstrainByResponseHeader",
            "The default interaction model that will be assigned when there is no explicit Link "
            + "header in the request must be recorded in the constraints"
            + " document referenced in the Link: rel=\"http://www.w3.org/ns/ldp#constrainedBy\" "
            + "header ([LDP] 4.2.1.6 clarification).",
            "https://fcrepo.github.io/fcrepo-specification/#http-post",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] postNonRDFSource() {
        return new String[] {
            "3.5.1-A - NonRDFSource-PostNonRDFSource",
            "Any LDPC must support creation of LDP-NRs on POST ([LDP] 5.2.3.3 may becomes must).",
            "https://fcrepo.github.io/fcrepo-specification/#http-post",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] postResourceAndCheckAssociatedResource() {
        return new String[] {
            "3.5.1-B - NonRDFSource-PostResourceAndCheckAssociatedResource",
            "On creation of an LDP-NR, an implementation must create an associated LDP-RS describing"
            + " that LDP-NR ([LDP] 5.2.3.12 may becomes must).",
            "https://fcrepo.github.io/fcrepo-specification/#http-post",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] postDigestResponseHeaderAuthentication() {
        return new String[] {
            "3.5.1-C - NonRDFSource-PostDigestResponseHeaderAuthentication",
            "An HTTP POST request that would create an LDP-NR and includes a Digest header (as described"
            + " in [RFC3230]) for which the instance-digest in that header does not match that of the "
            + "new LDP-NR must be rejected with a 409 Conflict response.",
            "https://fcrepo.github.io/fcrepo-specification/#http-post-ldpnr",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] postDigestResponseHeaderVerification() {
        return new String[] {
            "3.5.1-D - NonRDFSource-PostDigestResponseHeaderVerification",
            "An HTTP POST request that includes an unsupported Digest type (as described in [RFC3230]), "
            + "should be rejected with a 400 Bad Request response.",
            "https://fcrepo.github.io/fcrepo-specification/#http-post-ldpnr",
            "SHOULD"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] httpPutSubtype() {
        return new String[] {
            "3.6-A - httpPutSubtype",
            "When accepting a PUT request against an existant resource, an HTTP Link: rel=\"type\" header "
            + "may be included. If the type in the Link header is a subtype of a current type of the resource, "
            + "and has an interaction model assigned to it by [LDP],hen the resource must be assigned the new "
            + "type and the interaction model of the resource must be changed to the interaction model assigned "
            + "to the new type by [LDP].",
            "https://fcrepo.github.io/fcrepo-specification/#http-put",
            "MAY"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] httpPut() {
        return new String[] {
            "3.6-B - HttpPut",
            "When accepting a PUT request against an existant resource, an HTTP Link: rel=\"type\" header "
            + "may be included. If that type is a value in the LDP namespace and is not either a current "
            + "type of the resource or a subtype of a current type of the resource, the request must be "
            + "rejected with a 409 Conflict response.",
            "https://fcrepo.github.io/fcrepo-specification/#http-put",
            "MAY"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] updateTriples() {
        return new String[] {
            "3.6.1-A - HttpPut-UpdateTriples",
            "Any LDP-RS must support PUT to update statements that are not server-managed triples (as defined "
            + "in [LDP] 2). [LDP] 4.2.4.1 and 4.2.4.3 remain in effect.",
            "https://fcrepo.github.io/fcrepo-specification/#http-put-ldprs",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] updateDisallowedTriples() {
        return new String[] {
            "3.6.1-B - HttpPut-UpdateDisallowedTriples",
            "If an otherwise valid HTTP PUT request is received that attempts to modify resource "
            + "statements that a server"
            + " disallows (not ignores per [LDP] 4.2.4.1), the server must fail the request by "
            + "responding with a 4xx "
            + "range status code (e.g. 409 Conflict).",
            "https://fcrepo.github.io/fcrepo-specification/#http-put-ldprs",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] updateDisallowedTriplesResponse() {
        return new String[] {
            "3.6.1-C - HttpPut-UpdateDisallowedTriplesResponse",
            "The server must provide a corresponding response body containing information "
            + "about which statements could"
            + " not be persisted. ([LDP] 4.2.4.4 shouldbecomes must).",
            "https://fcrepo.github.io/fcrepo-specification/#http-put-ldprs",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] updateDisallowedTriplesConstrainedByHeader() {
        return new String[] {
            "3.6.1-D - HttpPut-UpdateDisallowedTriplesConstrainedByHeader",
            "In that response, the restrictions causing such a request to fail must be "
            + "described in a resource indicated"
            + " by a Link: rel=\"http://www.w3.org/ns/ldp#constrainedBy\" response header per [LDP] 4.2.1.6.",
            "https://fcrepo.github.io/fcrepo-specification/#http-put-ldprs",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] httpPutNR() {
        return new String[] {
            "3.6.2-A - HttpPutNR",
            "Any LDP-NR must support PUT to replace the binary content of that resource.",
            "https://fcrepo.github.io/fcrepo-specification/#http-put-ldpnr",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] putDigestResponseHeaderAuthentication() {
        return new String[] {
            "3.6.2-B - NonRDFSource-PutDigestResponseHeaderAuthentication",
            "An HTTP PUT request that includes a Digest header (as described in [RFC3230]) for which any "
            + "instance-digest in that header does not match the instance it describes, must be rejected "
            + "with a 409 Conflict response.",
            "https://fcrepo.github.io/fcrepo-specification/#http-put-ldpnr",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] putDigestResponseHeaderVerification() {
        return new String[] {
            "3.6.2-C - NonRDFSource-PutDigestResponseHeaderVerification",
            "An HTTP PUT request that includes an unsupported Digest type (as described in [RFC3230]), should"
            + " be rejected with a 400 (Bad Request) response.",
            "https://fcrepo.github.io/fcrepo-specification/#http-put-ldpnr",
            "SHOULD"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] responseDescribesHeader() {
        return new String[] {
            "3.2.2-B - HttpGet-LDPRS-ResponseDescribesHeader",
            "When a GET request is made to an LDP-RS that describes an associated LDP-NR "
            + "(3.5 HTTP POST and [LDP]5.2.3.12),"
            + "the response must include a Link: rel=\"describes\" header referencing the LDP-NR "
            + "in question, as defined in [RFC6892].",
            "https://fcrepo.github.io/fcrepo-specification/#http-get-ldprs",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] additionalValuesForPreferHeader() {
        return new String[] {
            "3.2.1-A - HttpGet-AdditionalValuesForPreferHeader",
            "In addition to the requirements of [LDP], an implementation may support the value "
            + "http://www.w3.org/ns/oa#PreferContainedDescriptions and should support the value "
            + "http://fedora.info/definitions/fcrepo#PreferInboundReferences for the Prefer header when making GET "
            + "requests on LDPC resources.",
            "https://fcrepo.github.io/fcrepo-specification/#additional-prefer-values",
            "MAY"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] responsePreferenceAppliedHeader() {
        return new String[] {
            "3.2.2-A - HttpGet-LDPRS-ResponsePreferenceAppliedHeader",
            "Responses to GET requests that apply a Prefer request header to any LDP-RS must "
            + "include the Preference-Applied"
            + " response header as defined in [RFC7240] section 3.",
            "https://fcrepo.github.io/fcrepo-specification/#http-get-ldprs",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] httpHeadResponseNoBody() {
        return new String[] {
            "3.3-A - HttpHead-ResponseNoBody",
            "The HEAD method is identical to GET except that the server must not return a "
            + "message-body in the response, as "
            + "specified in [RFC7231] section 4.3.2.",
            "https://fcrepo.github.io/fcrepo-specification/#http-head",
            "MUST NOT"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] httpHeadResponseDigest() {
        return new String[] {
            "3.3-B - HttpHead-ResponseDigest",
            "The server must send the same Digest header in the response as it"
            + " would have sent if the request had been a GET (or omit it if it would have been omitted for a GET).",
            "https://fcrepo.github.io/fcrepo-specification/#http-head",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] httpHeadResponseHeadersSameAsHttpGet() {
        return new String[] {
            "3.3-C - HttpHead-ResponseHeadersSameAsHttpGet",
            "In other cases, The server should send the same headers in response to a HEAD request "
            + "as it would have sent if the request had "
            + "been a GET, except that the payload headers (defined in [RFC7231] section 3.3) may be omitted.",
            "https://fcrepo.github.io/fcrepo-specification/#http-head",
            "SHOULD"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] httpDelete() {
        return new String[] {
            "3.8 - HttpDelete",
            "The DELETE method is optional per [LDP] section 4.2.5 and this specification "
            + "does not require Fedora servers to "
            + "implement it. When a Fedora server supports this method, in addition to the "
            + "requirements imposed on LDPRs within"
            + " containers outlined in [LDP] section 5.2.5.",
            "https://fcrepo.github.io/fcrepo-specification/#http-delete",
            "MAY"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] httpDeleteOptionsCheck() {
        return new String[] {
            "3.8.1-A - httpDeleteOptionsCheck",
            "An implementation that cannot recurse should not advertise DELETE in response to OPTIONS "
            + "requests for containers with contained resources.",
            "https://fcrepo.github.io/fcrepo-specification/#http-delete-recursion",
            "SHOULD NOT"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] httpDeleteStatusCheck() {
        return new String[] {
            "3.8.1-C - httpDeleteStatusCheck",
            "An implementation must not return a 200 (OK) or 204 (No Content) response "
            + "unless the entire operation successfully completed.",
            "https://fcrepo.github.io/fcrepo-specification/#http-delete-recursion",
            "MUST NOT"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] httpDeleteStatusCheckTwo() {
        return new String[] {
            "3.8.1-D - httpDeleteStatusCheckTwo",
            "An implementation must not emit a message that implies the successful DELETE of a resource until "
            + "the resource has been successfully removed.",
            "https://fcrepo.github.io/fcrepo-specification/#http-delete-recursion",
            "MUST NOT"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] httpDeleteResource() {
        return new String[] {
            "4.2.6 - httpDeleteResource",
            "An implementation may support DELETE for LDPRms. If DELETE is supported,  "
            + "the server is responsible for all behaviors implied by the LDP-containment of the LDPRm.",
            "https://fcrepo.github.io/fcrepo-specification/#LDPRm-delete",
            "MAY"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] postCreateExternalBinaryContent() {
        return new String[] {
            "3.9-A - ExternalBinaryContent-PostCreate",
            "Fedora servers should support the creation of LDP-NRs with Content-Type "
            + "of message/external-body and"
            + " access-type parameter of url.",
            "https://fcrepo.github.io/fcrepo-specification/#external-content",
            "SHOULD"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] putCreateExternalBinaryContent() {
        return new String[] {
            "3.9-A - ExternalBinaryContent-PutCreate",
            "Fedora servers should support the creation of LDP-NRs with Content-Type "
            + "of message/external-body and"
            + " access-type parameter of url.",
            "https://fcrepo.github.io/fcrepo-specification/#external-content",
            "SHOULD"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] putUpdateExternalBinaryContent() {
        return new String[] {
            "3.9-A - ExternalBinaryContent-PutUpdate",
            "Fedora servers should support the creation of LDP-NRs with Content-Type "
            + "of message/external-body and"
            + " access-type parameter of url.",
            "https://fcrepo.github.io/fcrepo-specification/#external-content",
            "SHOULD"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] createExternalBinaryContentCheckAccesType() {
        return new String[] {
            "3.9-B - ExternalBinaryContent-createExternalBinaryContentCheckAccesType",
            "Fedora servers must advertise support in the Accept-Post response header for each supported access-type "
            + " parameter value of Content-Type: message/external-body.",
            "https://fcrepo.github.io/fcrepo-specification/#external-content",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] postCheckUnsupportedMediaType() {
        return new String[] {
            "3.9-C - ExternalBinaryContent-PostCheckUnsupportedMediaType",
            "Fedora servers receiving requests that would create or update a LDP-NR with "
            + "a message/external-body with an "
            + "unsupported type parameter must respond with HTTP 415 UNSUPPORTED MEDIA TYPE. "
            + "In the case that a Fedora"
            + " server does not support external LDP-NR content, all message/external-body messages must be rejected"
            + " with HTTP 415.",
            "https://fcrepo.github.io/fcrepo-specification/#external-content",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] putCheckUnsupportedMediaType() {
        return new String[] {
            "3.9-C - ExternalBinaryContent-PutCheckUnsupportedMediaType",
            "Fedora servers receiving requests that would create or update a LDP-NR with a "
            + "message/external-body with an "
            + "unsupported type parameter must respond with HTTP 415 UNSUPPORTED MEDIA TYPE. In the case that a Fedora"
            + " server does not support external LDP-NR content, all message/external-body messages must be rejected"
            + " with HTTP 415.",
            "https://fcrepo.github.io/fcrepo-specification/#external-content",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] checkUnsupportedMediaType() {
        return new String[] {
            "3.9-D - ExternalBinaryContent-checkUnsupportedMediaType",
            "In the case that a Fedora server does not support external LDP-NR content, "
            + "all message/external-body messages must be rejected with 415 (Unsupported Media Type).",
            "https://fcrepo.github.io/fcrepo-specification/#external-content",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] postCheckHeaders() {
        return new String[] {
            "3.9-E - ExternalBinaryContent-postCheckHeaders",
            "Fedora servers receiving requests that would create or update an LDP-NR with Content-Type: "
            + "message/external-body must not accept the request if it cannot guarantee all. "
            + "of the response headers required by the LDP-NR interaction model in this specification.",
            "https://fcrepo.github.io/fcrepo-specification/#external-content",
            "MUST NOT"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] putUpdateCheckHeaders() {
        return new String[] {
            "3.9-E - ExternalBinaryContent-putUpdateCheckHeaders",
            "Fedora servers receiving requests that would create or update an LDP-NR with Content-Type: "
            + "message/external-body must not accept the request if it cannot guarantee all. "
            + "of the response headers required by the LDP-NR interaction model in this specification.",
            "https://fcrepo.github.io/fcrepo-specification/#external-content",
            "MUST NOT"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] getCheckContentLocationHeader() {
        return new String[] {
            "3.9-F - ExternalBinaryContent-HttpGetCheckContentLocationHeader",
            "GET and HEAD responses for any external LDP-NR should include a Content-Location header with a URI  "
            + "representation of the location of the external content if the Fedora server is proxying the content.",
            "https://fcrepo.github.io/fcrepo-specification/#external-content",
            "SHOULD"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] headCheckContentLocationHeader() {
        return new String[] {
            "3.9-F - ExternalBinaryContent-HttpHeadCheckContentLocationHeader",
            "GET and HEAD responses for any external LDP-NR should include a Content-Location header with a URI  "
            + "representation of the location of the external content if the Fedora server is proxying the content.",
            "https://fcrepo.github.io/fcrepo-specification/#external-content",
            "SHOULD"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] httpOptionsSupport() {
        return new String[] {
            "3.4-A - HttpOptions-HttpOptionsSupport",
            "Any LDPR must support OPTIONS per [LDP] 4.2.8. "
            + "4.2.8.1 LDP servers must support the HTTP OPTIONS method.",
            "https://fcrepo.github.io/fcrepo-specification/#http-options",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] httpOptionsSupportAllow() {
        return new String[] {
            "3.4-B - HttpOptions-HttpOptionsSupportAllow",
            "Any LDPR must support OPTIONS per [LDP] 4.2.8. "
            + "LDP servers must indicate their support for HTTP Methods by responding to a"
            + " HTTP OPTIONS request on the LDPR’s URL with the HTTP Method tokens in the HTTP response header Allow.",
            "https://fcrepo.github.io/fcrepo-specification/#http-options",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] respondWantDigestTwoSupported() {
        return new String[] {
            "3.2.3-B - HttpGet-RespondWantDigestTwoSupported",
            "Testing for two supported digests with no weights"
            + " GET requests to any LDP-NR must correctly respond to the Want-Digest "
            + "header defined in [RFC3230]",
            "https://fcrepo.github.io/fcrepo-specification/#http-get-ldpnr",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] respondWantDigestTwoSupportedQvalueNonZero() {
        return new String[] {
            "3.2.3-C - HttpGet-RespondWantDigestTwoSupportedQvalueNonZero",
            "Testing for two supported digests with different weights"
            + "GET requests to any LDP-NR must correctly respond to the Want-Digest "
            + "header defined in [RFC3230]",
            "https://fcrepo.github.io/fcrepo-specification/#http-get-ldpnr",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] respondWantDigestTwoSupportedQvalueZero() {
        return new String[] {
            "3.2.3-D - HttpGet-RespondWantDigestTwoSupportedQvalueZero",
            "Testing for two supported digests with different weights q=0.3,q=0"
            + " GET requests to any LDP-NR must correctly respond to the Want-Digest"
            + " header defined in [RFC3230]",
            "https://fcrepo.github.io/fcrepo-specification/#http-get-ldpnr",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] respondWantDigestNonSupported() {
        return new String[] {
            "3.2.3-E - HttpGet-RespondWantDigestNonSupported",
            "Testing for one supported digest and one unsupported digest."
            + "GET requests to any LDP-NR must correctly respond to the Want-Digest "
            + "header defined in [RFC3230]",
            "https://fcrepo.github.io/fcrepo-specification/#http-get-ldpnr",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] respondWantDigestTwoSupportedExternalBinaryContent() {
        return new String[] {
            "3.9-H - ExternalBinaryContent-respondWantDigestTwoSupportedExternalBinaryContent",
            "GET and HEAD requests to any external LDP-NR must correctly respond to the "
            + "Want-Digest header defined in [RFC3230]. With two supported digests.",
            "https://fcrepo.github.io/fcrepo-specification/#external-content",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] respondWantDigestTwoSupportedExternalBinaryContentHead() {
        return new String[] {
            "3.9-H - ExternalBinaryContent-respondWantDigestTwoSupportedExternalBinaryContent",
            "GET and HEAD requests to any external LDP-NR must correctly respond to the "
            + "Want-Digest header defined in [RFC3230]. With two supported digests.",
            "https://fcrepo.github.io/fcrepo-specification/#external-content",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] respondWantDigestTwoSupportedQvalueNonZeroExternalBinaryContent() {
        return new String[] {
            "3.9-I - ExternalBinaryContent-respondWantDigestTwoSupportedQvalueNonZeroExternalBinaryContent",
            "GET and HEAD requests to any external LDP-NR must correctly respond to the "
            + "Want-Digest header defined in [RFC3230]. Two digests with different weights, q values.",
            "https://fcrepo.github.io/fcrepo-specification/#external-content",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] respondWantDigestTwoSupportedQvalueNonZeroExternalBinaryContentHead() {
        return new String[] {
            "3.9-I - ExternalBinaryContent-respondWantDigestTwoSupportedQvalueNonZeroExternalBinaryContentHead",
            "GET and HEAD requests to any external LDP-NR must correctly respond to the "
            + "Want-Digest header defined in [RFC3230]. Two digests with different weights, q values.",
            "https://fcrepo.github.io/fcrepo-specification/#external-content",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] respondWantDigestNonSupportedExternalBinaryContent() {
        return new String[] {
            "3.9-J - ExternalBinaryContent-respondWantDigestNonSupportedExternalBinaryContent",
            "GET and HEAD requests to any external LDP-NR must correctly respond to the "
            + "Want-Digest header defined in [RFC3230]. One supported and an unsupported Digest.",
            "https://fcrepo.github.io/fcrepo-specification/#external-content",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] respondWantDigestNonSupportedExternalBinaryContentHead() {
        return new String[] {
            "3.9-J - ExternalBinaryContent-respondWantDigestNonSupportedExternalBinaryContentHead",
            "GET and HEAD requests to any external LDP-NR must correctly respond to the "
            + "Want-Digest header defined in [RFC3230]. One supported and an unsupported Digest.",
            "https://fcrepo.github.io/fcrepo-specification/#external-content",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] respondWantDigestExternalBinaryContent() {
        return new String[] {
            "3.9-G - ExternalBinaryContent-respondWantDigestExternalBinaryContent",
            "GET and HEAD requests to any external LDP-NR must correctly respond to the "
            + "Want-Digest header defined in [RFC3230].",
            "https://fcrepo.github.io/fcrepo-specification/#external-content",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] respondWantDigestExternalBinaryContentHead() {
        return new String[] {
            "3.9-G - ExternalBinaryContent-respondWantDigestExternalBinaryContent",
            "GET and HEAD requests to any external LDP-NR must correctly respond to the "
            + "Want-Digest header defined in [RFC3230].",
            "https://fcrepo.github.io/fcrepo-specification/#external-content",
            "MUST"
        };
    }

    /**
     * Basic information for described test
     *
     * @return String[]
     */
    public String[] checkExpirationParameterDate() {
        return new String[] {
            "3.9-2-A - ExternalBinaryContent-checkExpirationParameterDate",
            "Per [RFC2046] section 5.2.3, all Content-Type: message/external-body values "
            + "may include an expiration parameter.",
            "https://fcrepo.github.io/fcrepo-specification/#external-content",
            "MAY"
        };
    }
}