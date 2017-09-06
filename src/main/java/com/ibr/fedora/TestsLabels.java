package com.ibr.fedora;

public class TestsLabels {

    public static String[] httpPost(){
        return new String[] {
                "HttpPost",
                "Any LDPC must support POST ([LDP] 4.2.3 / 5.2.3).",
                "http://fedora.info/2017/06/30/spec/#httpPOST"
        };
    }

    public static String[] constrainByResponseHeader(){
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
                "On creation of an LDP-NR an implementation must create an associated LDP-RS describing that LDP-NR ([LDP] 5.2.3.12 may becomes must).",
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
}
