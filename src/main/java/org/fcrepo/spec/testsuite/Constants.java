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
package org.fcrepo.spec.testsuite;

/**
 * @author Daniel Bernstein
 */
public class Constants {

    public static final String NON_RDF_SOURCE_INTERACTION_MODEL = "http://www.w3.org/ns/ldp#NonRDFSource";
    public static final String RDF_SOURCE_LINK_HEADER = "<http://www.w3.org/ns/ldp#RDFSource>; rel=\"type\"";
    public static final String NON_RDF_SOURCE_LINK_HEADER = "<http://www.w3.org/ns/ldp#NonRDFSource>; rel=\"type\"";
    public static final String CONTAINER_LINK_HEADER = "<http://www.w3.org/ns/ldp#Container>; rel=\"type\"";
    public static final String BASIC_CONTAINER_LINK_HEADER = "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"";
    public static final String BASIC_CONTAINER_BODY = "@prefix ldp: <http://www.w3.org/ns/ldp#> ."
                                                      + "@prefix dcterms: <http://purl.org/dc/terms/> ."
                                                      + "<> dcterms:title 'Container class Container' ;"
                                                      +
                                                      "dcterms:description 'This is a crud container for the Fedora " +
                                                      "API Test Suite.' . ";

    public static final String DIRECT_CONTAINER_BODY = "@prefix ldp: <http://www.w3.org/ns/ldp#> ."
            + "@prefix dcterms: <http://purl.org/dc/terms/> ."
            + "<> dcterms:title 'A Direct Container' ; "
            + "dcterms:description 'This is a direct container for the Fedora API Test Suite' ;"
            + "ldp:membershipResource <%membershipResource%> ;"
            + "ldp:hasMemberRelation dcterms:hasPart ." ;

    public static final String INDIRECT_CONTAINER_BODY = "@prefix ldp: <http://www.w3.org/ns/ldp#> ."
                                                         + "@prefix dcterms: <http://purl.org/dc/terms/> ."
                                                         + "<> dcterms:title 'An Indirect Container' ; "
                                                         + "dcterms:description 'This is an indirect container for the "
                                                         + "Fedora API Test Suite ' ;"
                                                         + "ldp:membershipResource <%membershipResource%> ;"
                                                         + "ldp:hasMemberRelation dcterms:hasPart ;"
                                                         + "ldp:insertedContentRelation ldp:MemberSubject .";



    public static final String RDF_BODY = "@prefix dc: <http://purl.org/dc/terms/> . "
            + "@prefix foaf: <http://xmlns.com/foaf/0.1/> . "
            + "<> a foaf:Person; "
            + "foaf:name \"Pythagoras\" ; "
            + "foaf:based_near \"Croton\" ; "
            + "foaf:interest [ dc:title \"Geometry\" ] .";

    public static final String CONTENT_DISPOSITION = "Content-Disposition";
    public static final String SLUG = "slug";
    public static final String DIGEST = "Digest";

    public static final String LINK = "Link";
    public static final String APPLICATION_SPARQL_UPDATE = "application/sparql-update";

    public static final String MEMENTO_LINK_HEADER = "<http://mementoweb.org/ns#Memento>; rel=\"type\"";
    public static final String ORIGINAL_RESOURCE_LINK_HEADER = "<http://mementoweb.org/ns#OriginalResource>;" +
                                                               "rel=\"type\"";
    public static final String TIME_GATE_LINK_HEADER = "<http://mementoweb.org/ns#TimeGate>; " +
                                                               "rel=\"type\"";
    public static final String TIME_MAP_LINK_HEADER = "<http://mementoweb.org/ns#TimeMap>; " +
                                                       "rel=\"type\"";

    public static final String EXTERNAL_CONTENT_LINK_REL = "http://fedora.info/definitions/fcrepo#ExternalContent";

    public static final String MEMENTO_DATETIME_HEADER = "Memento-Datetime";

    private Constants() {

    }
}
