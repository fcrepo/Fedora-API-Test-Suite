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

    public static String[] httpPost(){
        return new String[] {
                "HttpPost",
                "Any LDPC must support POST ([LDP] 4.2.3 / 5.2.3).",
                "http://fedora.info/2017/06/30/spec/#httpPOST"
        };
    }

    public static String[] constrainedByResponseHeader(){
        return new String[] {
                "HttpPost-ConstrainByResponseHeader",
                "The default interaction model that will be assigned when there is no explicit Link header in the request must be recorded in the constraints document referenced in the Link: rel=\"http://www.w3.org/ns/ldp#constrainedBy\" header ([LDP] 4.2.1.6 clarification).",
                "http://fedora.info/2017/06/30/spec/#httpPOST"
        };
    }

    public static String[] postNonRDFSource(){
        return new String[] {
                "NonRDFSource-PostNonRDFSource",
                "Any LDPC must support creation of LDP-NRs on POST ([LDP] 5.2.3.3 may becomes must).",
                "http://fedora.info/2017/06/30/spec/#httpPOST"
        };
    }

    public static String[] postResourceAndCheckAssociatedResource(){
        return new String[] {
                "NonRDFSource-PostResourceAndCheckAssociatedResource",
                "On creation of an LDP-NR an implementation must create an associated LDP-RS describing that LDP-NR ([LDP] 5.2.3.12 may becomes must).",
                "http://fedora.info/2017/06/30/spec/#httpPOST"
        };
    }

    public static String[] postDigestResponseHeaderAuthentication(){
        return new String[] {
                "NonRDFSource-PostDigestResponseHeaderAuthentication",
                "A HTTP POST request that would create a LDP-NR and includes a Digest header (as described in [RFC3230]) for which the instance-digest in that header does not match that of the new LDP-NR must be rejected with a 409 Conflict response.",
                "http://fedora.info/2017/06/30/spec/#httpPOSTLDPNR"
        };
    }

    public static String[] postDigestResponseHeaderVerification(){
        return new String[] {
                "NonRDFSource-PostDigestResponseHeaderVerification",
                "A HTTP POST request that includes an unsupported Digest type (as described in [RFC3230]), should be rejected with a 400 Bad Request response.",
                "http://fedora.info/2017/06/30/spec/#httpPOSTLDPNR"
        };
    }
    
    public static String[] httpPut(){
        return new String[] {
                "HttpPut",
				"When accepting a PUT request against an extant resource, an HTTP Link: rel=\"type\" header may be included. If that type is a value in the LDP namespace and is not either a current type of the resource or a subtype of a current type of the resource, the request must be rejected with a 409 Conflict response.",
                "http://fedora.info/2017/06/30/spec/#httpPUT"
        };
    }
    
    public static String[] httpPutNR(){
        return new String[] {
                "HttpPutNR",
                "Any LDP-NR must support PUT to replace the binary content of that resource.",
                "http://fedora.info/2017/06/30/spec/#httpPUTLDPNR"
        };
    }
    
    public static String[] putDigestResponseHeaderAuthentication(){
        return new String[] {
                "NonRDFSource-PutDigestResponseHeaderAuthentication",
                "A HTTP PUT request that includes a Digest header (as described in [RFC3230]) for which any instance-digest in that header does not match the instance it describes, must be rejected with a 409 Conflict response.",
                "http://fedora.info/2017/06/30/spec/#httpPUTLDPNR"
        };
    }

    public static String[] putDigestResponseHeaderVerification(){
        return new String[] {
                "NonRDFSource-PutDigestResponseHeaderVerification",
                "A HTTP PUT request that includes an unsupported Digest type (as described in [RFC3230]), should be rejected with a 400 Bad Request response.",
                "http://fedora.info/2017/06/30/spec/#httpPUTLDPNR"
        };
    }
	    
    public static String[] httpPutExternalBody(){
        return new String[] {
                "PutNonRDFSource-PutExternalBody",
                "Implementations may support Content-Type: message/external-body extensions for request bodies for HTTP PUT that would create LDP-NRs. This content-type requires a complete Content-Type header that includes the location of the external body, e.g Content-Type: message/external-body; access-type=URL; URL=\"http://www.example.com/file\", as defined in [RFC2017]. Requirements for this interaction are detailed in External LDP-NR Content.",
                "http://fedora.info/2017/06/30/spec/#httpPUTLDPNR"
        };
    }

    public static String[] responsePreferenceAppliedHeader(){
        return new String[] {
                "HttpGet-LDPRS-ResponsePreferenceAppliedHeader",
                "Responses to GET requests that apply a Prefer request header to any LDP-RS must include the Preference-Applied response header as defined in [RFC7240] section 3.",
                "http://fedora.info/2017/06/30/spec/#httpGETLDPRS"
        };
    }

    public static String[] responseDescribesHeader(){
        return new String[] {
                "HttpGet-LDPRS-ResponseDescribesHeader",
                "When the request is to the LDP-RS created to describe a LDP-NR, the response must include a Link: rel=\"describes\" header referencing the LDP-NR in question, as defined in [RFC6892].",
                "http://fedora.info/2017/06/30/spec/#httpGET"
        };
    }

    public static String[] httpHeadResponseNoBody(){
        return new String[] {
                "HttpHead-ResponseNoBody",
                "The HEAD method is identical to GET except that the server must not return a message-body in the response, as specified in [RFC7231] section 4.3.2.",
                "http://fedora.info/2017/06/30/spec/#httpHEAD"
        };
    }

    public static String[] httpHeadResponseHeadersSameAsHttpGet(){
        return new String[] {
                "HttpHead-ResponseHeadersSameAsHttpGet",
                "The server should send the same headers in response to a HEAD request as it would have sent if the request had been a GET, except that the payload headers (defined in [RFC7231] section 3.3) may be omitted.",
                "http://fedora.info/2017/06/30/spec/#httpHEAD"
        };
    }

    public static String[] httpDelete(){
        return new String[] {
                "HttpDelete",
                "The DELETE method is optional per [LDP] section 4.2.5 and this specification does not require Fedora servers to implement it. When a Fedora server supports this method, in addition to the requirements imposed on LDPRs within containers outlined in [LDP] section 5.2.5.",
                "http://fedora.info/2017/06/30/spec/#httpDELETE"
        };
    }

    public static String[] postCreateExternalBinaryContent(){
        return new String[] {
                "ExternalBinaryContent-PostCreate",
                "This specification describes the use of Content-Type: message/external-body values to signal, on POST or PUT, that the Fedora server should should not consider the request entity to be the LDP-NR's content, but that a Content-Type value will signal a name or address at which the content might be retreived.",
                "http://fedora.info/2017/06/30/spec/#external-content"
        };
    }

    public static String[] putCreateExternalBinaryContent(){
        return new String[] {
                "ExternalBinaryContent-PutCreate",
                "This specification describes the use of Content-Type: message/external-body values to signal, on POST or PUT, that the Fedora server should should not consider the request entity to be the LDP-NR's content, but that a Content-Type value will signal a name or address at which the content might be retreived.",
                "http://fedora.info/2017/06/30/spec/#external-content"
        };
    }

    public static String[] putUpdateExternalBinaryContent(){
        return new String[] {
                "ExternalBinaryContent-PutUpdate",
                "This specification describes the use of Content-Type: message/external-body values to signal, on POST or PUT, that the Fedora server should should not consider the request entity to be the LDP-NR's content, but that a Content-Type value will signal a name or address at which the content might be retreived.",
                "http://fedora.info/2017/06/30/spec/#external-content"
        };
    }

    public static String[] postCheckUnsupportedMediaType(){
        return new String[] {
                "ExternalBinaryContent-PostCheckUnsupportedMediaType",
                "Fedora servers receiving requests that would create or update a LDP-NR with a message/external-body with an unsupported type parameter must respond with HTTP 415 UNSUPPORTED MEDIA TYPE.",
                "http://fedora.info/2017/06/30/spec/#external-content"
        };
    }

    public static String[] putCheckUnsupportedMediaType(){
        return new String[] {
                "ExternalBinaryContent-PutCheckUnsupportedMediaType",
                "Fedora servers receiving requests that would create or update a LDP-NR with a message/external-body with an unsupported type parameter must respond with HTTP 415 UNSUPPORTED MEDIA TYPE.",
                "http://fedora.info/2017/06/30/spec/#external-content"
        };
    }

    public static String[] checkAcceptPostHeader(){
        return new String[] {
                "ExternalBinaryContent-CheckAcceptPostHeader",
                "Fedora servers that support LDP-NR with message/external-body must advertise that support in the Accept-Post response header for each supported type parameter of supported Content-Type values.",
                "http://fedora.info/2017/06/30/spec/#external-content"
        };
    }

    public static String[] getCheckContentLocationHeader() {
        return new String[] {
                "ExternalBinaryContent-HttpGetCheckContentLocationHeader",
                "LDP-NR GET and HEAD responses should include a Content-Location header with a URI representation of the location of the external content if the Fedora server is proxying the content.",
                "http://fedora.info/2017/06/30/spec/#external-content"
        };
    }

    public static String[] headCheckContentLocationHeader() {
        return new String[] {
                "ExternalBinaryContent-HttpHeadCheckContentLocationHeader",
                "LDP-NR GET and HEAD responses should include a Content-Location header with a URI representation of the location of the external content if the Fedora server is proxying the content.",
                "http://fedora.info/2017/06/30/spec/#external-content"
        };
    }

    public static String[] supportPatch() {
        return new String[] {
                "HttpPatch-SupportHttpPatch",
                "Any LDP-RS must support PATCH ([LDP] 4.2.7 may becomes must). [sparql11-update] must be an accepted content-type for PATCH. Other content-types (e.g. [ldpatch]) may be available.",
                "http://fedora.info/2017/06/30/spec/#httpPATCH"
        };
    }

    public static String[] failedPatch() {
        return new String[] {
                "HttpPatch-CheckPatch",
                "If an otherwise valid HTTP PATCH request is received that attempts to add statements to a resource that a server disallows (not ignores per [LDP] 4.2.4.1), the server must fail the request by responding with a 4xx range status code (e.g. 409 Conflict). The server must provide a corresponding response body containing information about which statements could not be persisted. ([LDP] 4.2.4.4 should becomes must). In that response the restrictions causing such a request to fail must be described in a resource indicated by a Link: rel=\"http://www.w3.org/ns/ldp#constrainedBy\" response header per [LDP] 4.2.1.6.",
                "http://fedora.info/2017/06/30/spec/#httpPATCH"
        };
    }
}
