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
package org.fcrepo.spec.testsuite.event;

import static org.awaitility.Awaitility.await;
import static org.awaitility.Duration.TEN_SECONDS;
import static org.fcrepo.spec.testsuite.App.BROKER_URL_PARAM;
import static org.fcrepo.spec.testsuite.App.QUEUE_NAME_PARAM;
import static org.fcrepo.spec.testsuite.App.TOPIC_NAME_PARAM;
import static org.junit.Assert.assertTrue;
import static java.util.stream.Collectors.toList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;

import org.fcrepo.spec.testsuite.TestInfo;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;

/**
 * API Spec event testing.
 *
 * @author whikloj
 * @since 2018-09-21
 */
public class NotificationTest extends AbstractEventTest {

    private static final String LDP_NAMESPACE = "http://www.w3.org/ns/ldp#";

    private static final Property RdfType = ResourceFactory.createProperty(
            "http://www.w3.org/1999/02/22-rdf-syntax-ns#type");

    private static final Resource ldpBasicContainer = ResourceFactory.createResource(LDP_NAMESPACE +
            "BasicContainer");

    private static final RDFNode ASupdate = ResourceFactory.createResource(ACTIVITY_STREAMS_NS + "Update");

    private static final RDFNode AScreate = ResourceFactory.createResource(ACTIVITY_STREAMS_NS + "Create");
    /**
     * Default constructor.
     *
     * @param adminUsername admin username
     * @param adminPassword admin password
     * @param username regular username
     * @param password regular user password
     * @param jmsBroker broker url
     * @param queueName queue name
     * @param topicName topic name
     * @throws JMSException on error connecting to broker
     */
    @Parameters({ "param2", "param3", "param4", "param5", BROKER_URL_PARAM, QUEUE_NAME_PARAM, TOPIC_NAME_PARAM })
    public NotificationTest(final String adminUsername, final String adminPassword, final String username,
            final String password, final String jmsBroker, final String queueName, final String topicName)
            throws JMSException {
        super(adminUsername, adminPassword, username, password, jmsBroker, queueName, topicName);
    }

    /**
     * 6.1 Notification Events
     *
     * @param uri the repository base uri
     * @throws JMSException problems connecting to broker
     * @throws InterruptedException interrupt the thread.sleep
     */
    @Test(groups = { "MUST" })
    @Parameters({ "param1" })
    public void testCatchMessage(final String uri) throws JMSException, InterruptedException {
        final TestInfo info = setupTest("6.1",
                "For every resource whose state is changed as a result of an HTTP operation, there MUST be a " +
                        "corresponding notification made available describing that change.",
                "https://fedora.info/2018/06/25/spec/#notification-events", ps);
        // Start with a clean JMS connection.
        resetConnection();
        // Get a new consumer.
        final MessageConsumer consumer = getConsumer();
        // Assign a message bank to capture the messages.
        consumer.setMessageListener(new MessageBank());
        // Start listening to the broker.
        connection.start();
        // Do your actions.
        final Response response = createBasicContainer(uri, info);
        final String location = getLocation(response);
        // Get the message bank back.
        final MessageBank listener = (MessageBank) consumer.getMessageListener();
        // POST emits 2 events
        await().atMost(TEN_SECONDS).until(() -> listener.stream()
                .count() == 2);

        // empty the message bank
        listener.clear();
        final Response doGet = doGet(location, new Header("Prefer",
                "return=representation; omit=\"http://fedora.info/definitions/v4/repository#ServerManaged\""));
        final String body = doGet.getBody().asString();

        doPut(location, new Headers(new Header("Content-type", "text/turtle"), new Header("Prefer",
                "handling=lenient; received=minimal")), body);
        // PUT emits 1 event
        await().atMost(TEN_SECONDS).until(() -> listener.stream()
                .count() == 1);

        listener.clear();
        doPatch(location, new Headers(new Header("Content-type", "application/sparql-update")),
                "prefix dc: <http://purl.org/dc/elements/1.1/> INSERT { <> dc:title \"This has been updated\"} " +
                        "WHERE {}");
        // PATCH emits one event
        await().atMost(TEN_SECONDS).until(() -> listener.stream()
                .count() == 1);

        listener.clear();
        doDelete(location);
        // Delete emits 2 events.
        await().atMost(TEN_SECONDS).until(() -> listener.stream()
                .count() == 2);

        consumer.close();

        session.close();
        connection.close();
    }

    /**
     * 6.2-A Event Serialization
     *
     * @param uri the repository base uri
     * @throws JMSException problems connecting to broker
     * @throws InterruptedException interrupt the thread.sleep
     * @throws UnsupportedEncodingException
     */
    @Test(groups = { "MUST" })
    @Parameters({ "param1" })
    public void testEventSerialization(final String uri) throws JMSException, InterruptedException,
            UnsupportedEncodingException {
        final TestInfo info = setupTest("6.2-A",
                "The notification serialization MUST conform to the [activitystreams-core] specification, " +
                        "and each event MUST contain the IRI of the resource and the event type.",
                "https://fedora.info/2018/06/25/spec/#notification-serialization", ps);
        // Start with a clean JMS connection.
        resetConnection();
        // Get a new consumer.
        final MessageConsumer consumer = getConsumer();
        // Assign a message bank to capture the messages.
        consumer.setMessageListener(new MessageBank());
        // Start listening to the broker.
        connection.start();
        // Do your actions.
        final Response response = createBasicContainer(uri, info);
        final String location = getLocation(response);
        // Get the message bank back.
        final MessageBank listener = (MessageBank) consumer.getMessageListener();
        await().atMost(TEN_SECONDS).until(() -> listener.stream()
                .count() == 2);

        final Resource locResource = ResourceFactory.createResource(location);
        final List<Message> message = listener.stream().collect(toList());
        for (final Message m : message) {
            if (m instanceof TextMessage) {
                final String body = ((TextMessage) m).getText();
                final InputStream is = new ByteArrayInputStream(body.getBytes("UTF-8"));
                final Model model = ModelFactory.createDefaultModel();

                model.read(is, location, "JSON-LD");
                if (model.contains(locResource, RdfType)) {
                    // This is the resource we created.
                    assertTrue("Event doesn't have the IRI of the resource", model.contains(locResource, null,
                            (RDFNode) null));
                    assertTrue("Event doesn't have an Activity Stream type", model.contains(null, RdfType,
                            AScreate));
                    assertTrue("Event doesn't have an Activity Stream type", model.contains(null, RdfType,
                            ASupdate));
                } else {
                    // Parent node is updated
                    assertTrue("Event doesn't have an Activity Stream type", model.contains(null, RdfType,
                            ASupdate));
                }
            }
        }
        consumer.close();

        session.close();
        connection.close();
    }

    /**
     * 6.2-B Event Serialization
     *
     * @param uri the repository base uri
     * @throws JMSException problems connecting to broker
     * @throws InterruptedException interrupt the thread.sleep
     * @throws UnsupportedEncodingException getBytes encoding fails
     */
    @Test(groups = { "SHOULD" })
    @Parameters({ "param1" })
    public void testEventSerializationShould(final String uri) throws JMSException, InterruptedException,
            UnsupportedEncodingException {
        final TestInfo info = setupTest("6.2-B",
                "Wherever possible, data SHOULD be expressed using the [activitystreams-vocabulary]. ",
                "https://fedora.info/2018/06/25/spec/#notification-serialization", ps);
        // Start with a clean JMS connection.
        resetConnection();
        // Get a new consumer.
        final MessageConsumer consumer = getConsumer();
        // Assign a message bank to capture the messages.
        consumer.setMessageListener(new MessageBank());
        // Get the message bank back.
        final MessageBank listener = (MessageBank) consumer.getMessageListener();
        // Start listening to the broker.
        connection.start();
        // Do your actions.
        final Response resp = createBasicContainer(uri, info);
        final String location = getLocation(resp);
        await().atMost(TEN_SECONDS).until(() -> listener.stream()
                .count() == 2);

        final Resource locResource = ResourceFactory.createResource(location);
        final List<Message> message = listener.stream().collect(toList());
        for (final Message m : message) {
            if (m instanceof TextMessage) {
                final String body = ((TextMessage) m).getText();
                final InputStream is = new ByteArrayInputStream(body.getBytes("UTF-8"));
                final Model model = ModelFactory.createDefaultModel();

                model.read(is, location, "JSON-LD");
                if (model.contains(locResource, RdfType)) {
                    // This is the resource we created.
                    assertTrue("Event doesn't have container type",
                            model.contains(locResource, RdfType, ldpBasicContainer));

                    // TODO: Need a test for the AS:actor
                    // TODO: Need a test for the ldp:inbox if it exists
                }
            }
        }
        consumer.close();

        session.close();
        connection.close();
    }
}
