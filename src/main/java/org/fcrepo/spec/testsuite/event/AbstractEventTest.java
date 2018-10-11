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

import static org.fcrepo.spec.testsuite.App.BROKER_URL_PARAM;
import static org.fcrepo.spec.testsuite.App.PERMISSIONLESS_USER_WEBID_PARAM;
import static org.fcrepo.spec.testsuite.App.QUEUE_NAME_PARAM;
import static org.fcrepo.spec.testsuite.App.ROOT_CONTROLLER_USER_WEBID_PARAM;
import static org.fcrepo.spec.testsuite.App.TOPIC_NAME_PARAM;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.fcrepo.spec.testsuite.AbstractTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Parameters;

/**
 * Abstract Event testing class.
 *
 * @author whikloj
 * @since 2018-09-20
 */
public class AbstractEventTest extends AbstractTest {

    private final ConnectionFactory connFactory;

    protected Connection connection;

    protected Session session = null;

    protected MessageConsumer consumer;

    private final String queueName;

    private final String topicName;

    /**
     * Constructor
     *
     * @param rootControllerUserWebId root container controller WebID
     * @param username username
     * @param jmsBroker URL of the JMS broker
     * @param queueName queue name (if applicable)
     * @param topicName topic name (if applicable)
     * @throws JMSException unable to create connection.
     */
    @Parameters({ROOT_CONTROLLER_USER_WEBID_PARAM, PERMISSIONLESS_USER_WEBID_PARAM, BROKER_URL_PARAM, QUEUE_NAME_PARAM,
                 TOPIC_NAME_PARAM})
    public AbstractEventTest(final String rootControllerUserWebId, final String username,
            final String jmsBroker, final String queueName, final String topicName)
            throws JMSException {
        super(rootControllerUserWebId, username);
        connFactory = new org.apache.activemq.ActiveMQConnectionFactory(jmsBroker);
        resetConnection();
        this.queueName = queueName.isEmpty() ? null : queueName;
        this.topicName = topicName.isEmpty() ? null : topicName;
    }

    /**
     * Create a new message consumer for the test.
     *
     * @throws JMSException
     */
    protected MessageConsumer getConsumer() throws JMSException {
        if (queueName != null) {
            return session.createConsumer(session.createQueue(queueName));
        } else if (topicName != null) {
            return session.createConsumer(session.createTopic(topicName));
        } else {
            throw new RuntimeException("Neither queue-name or topic-name given");
        }
    }

    /**
     * Reset the JMS connection and get a new one.
     *
     * @throws JMSException on problems connecting to the broker.
     */
    protected void resetConnection() throws JMSException {
        if (connection != null) {
            connection.close();
        }
        connection = connFactory.createConnection();
        connection.setClientID("Fedora-API-Test-Suite");
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    /**
     * Close JMS connections.
     *
     * @throws JMSException error closing connections.
     */
    @AfterClass
    public void closeConnection() throws JMSException {
        if (session != null) {
            session.close();
        }
        if (connection != null) {
            connection.close();
        }
    }

}
