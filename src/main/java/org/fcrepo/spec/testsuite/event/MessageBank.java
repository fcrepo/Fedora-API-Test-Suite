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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * Collection messages from topic or queue and save them
 *
 * @author whikloj
 */
public class MessageBank implements MessageListener {

    private final List<Message> messages = new ArrayList<>();

    @Override
    public void onMessage(final Message message) {
        if (message instanceof TextMessage) {
            messages.add(message);
        }
    }

    /**
     * Get the messages as a stream.
     *
     * @return all messages.
     */
    public Stream<Message> stream() {
        return messages.stream();
    }

    /**
     * Clear message list.
     */
    public void clear() {
        messages.clear();
    }
}
