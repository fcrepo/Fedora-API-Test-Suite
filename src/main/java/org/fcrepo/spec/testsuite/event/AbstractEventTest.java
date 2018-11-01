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

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.fcrepo.spec.testsuite.AbstractTest;
import org.fcrepo.spec.testsuite.TestParameters;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * Abstract Event testing class.
 *
 * @author whikloj
 * @since 2018-09-20
 */
public class AbstractEventTest extends AbstractTest {

    private ConnectionFactory connFactory;

    protected Connection connection;

    protected Session session = null;

    protected MessageConsumer consumer;

    private String queueName;

    private String topicName;

    /**
     * Setup the messaging connection and queues/topics
     *
     * @throws JMSException on error
     */
    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws JMSException {
        final TestParameters params = TestParameters.get();
        final String queue = params.getQueueName();
        final String topic  = params.getTopicName();

        connFactory = new org.apache.activemq.ActiveMQConnectionFactory(params.getBrokerUrl());
        resetConnection();
        this.queueName = queue.isEmpty() ? null : queue;
        this.topicName = topic.isEmpty() ? null : topic;
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
    @AfterClass(alwaysRun = true)
    public void closeConnection() throws JMSException {
        if (session != null) {
            session.close();
        }
        if (connection != null) {
            connection.close();
        }
    }

}
