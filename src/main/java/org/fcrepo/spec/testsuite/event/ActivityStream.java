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

/**
 * Helper class for ActivityStream validation
 *
 * @author whikloj
 */
public class ActivityStream {

    /**
     * Activity terms from https://www.w3.org/TR/activitystreams-vocabulary/#activity-types
     * 
     * @author whikloj
     */
    public enum Activities {
        Accept,
        Add,
        Announce,
        Arrive,
        Block,
        Create,
        Delete,
        Dislike,
        Flag,
        Follow,
        Ignore,
        Invite,
        Join,
        Leave,
        Like,
        Listen,
        Move,
        Offer,
        Question,
        Reject,
        Read,
        Remove,
        TentativeReject,
        TentativeAccept,
        Travel,
        Undo,
        Update,
        View;
    }


}
