/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 *
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

/*
 * Copyright 2021 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */
package org.opensearch.commons.notifications.model

import com.fasterxml.jackson.core.JsonParseException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.opensearch.commons.utils.createObjectFromJsonString
import org.opensearch.commons.utils.getJsonString
import org.opensearch.commons.utils.recreateObject

internal class EmailTests {

    @Test
    fun `Email serialize and deserialize transport object should be equal`() {
        val sampleEmail = Email(
            "sampleAccountId",
            listOf(
                EmailRecipient("email1@email.com"),
                EmailRecipient("email2@email.com")
            ),
            listOf("sample_group_id_1", "sample_group_id_2")
        )
        val recreatedObject = recreateObject(sampleEmail) { Email(it) }
        assertEquals(sampleEmail, recreatedObject)
    }

    @Test
    fun `Email serialize and deserialize using json object should be equal`() {
        val sampleEmail = Email(
            "sampleAccountId",
            listOf(
                EmailRecipient("email1@email.com"),
                EmailRecipient("email2@email.com")
            ),
            listOf("sample_group_id_1", "sample_group_id_2")
        )
        val jsonString = getJsonString(sampleEmail)
        val recreatedObject = createObjectFromJsonString(jsonString) { Email.parse(it) }
        assertEquals(sampleEmail, recreatedObject)
    }

    @Test
    fun `Email should deserialize json object using parser`() {
        val sampleEmail = Email(
            "sampleAccountId",
            listOf(
                EmailRecipient("email1@email.com"),
                EmailRecipient("email2@email.com")
            ),
            listOf("sample_group_id_1", "sample_group_id_2")
        )
        val jsonString = """
            {
                "email_account_id":"${sampleEmail.emailAccountID}",
                "recipient_list":[
                    {"recipient":"${sampleEmail.recipients[0].recipient}"},
                    {"recipient":"${sampleEmail.recipients[1].recipient}"}
                ],
                "email_group_id_list":[
                    "${sampleEmail.emailGroupIds[0]}",
                    "${sampleEmail.emailGroupIds[1]}"
                ]
             }"
        """.trimIndent()
        val recreatedObject = createObjectFromJsonString(jsonString) { Email.parse(it) }
        assertEquals(sampleEmail, recreatedObject)
    }

    @Test
    fun `Email should throw exception when invalid json object is passed`() {
        val jsonString = "sample message"
        assertThrows<JsonParseException> {
            createObjectFromJsonString(jsonString) { Email.parse(it) }
        }
    }

    @Test
    fun `Email should throw exception when emailAccountID is replaced with emailAccountID2 in json object`() {
        val sampleEmail = Email(
            "sampleAccountId",
            listOf(
                EmailRecipient("email1@email.com"),
                EmailRecipient("email2@email.com")
            ),
            listOf("sample_group_id_1", "sample_group_id_2")
        )
        val jsonString = """
            {
                "email_account_id2":"${sampleEmail.emailAccountID}",
                "recipient_list":[
                    {"recipient":"${sampleEmail.recipients[0]}"},
                    {"recipient":"${sampleEmail.recipients[1]}"}
                ],
                "email_group_id_list":[
                    "${sampleEmail.emailGroupIds[0]}",
                    "${sampleEmail.emailGroupIds[1]}"
                ]
             }"
        """.trimIndent()
        assertThrows<IllegalArgumentException> {
            createObjectFromJsonString(jsonString) { Email.parse(it) }
        }
    }

    @Test
    fun `Email should accept without defaultRecipients and defaultEmailGroupIds in json object`() {
        val sampleEmail = Email("sampleAccountId", listOf(), listOf())
        val jsonString = """
            {
                "email_account_id":"${sampleEmail.emailAccountID}"
            }"
        """.trimIndent()
        val recreatedObject = createObjectFromJsonString(jsonString) { Email.parse(it) }
        assertEquals(sampleEmail, recreatedObject)
    }

    @Test
    fun `Email should safely ignore extra field in json object`() {
        val sampleEmail = Email("sampleAccountId", listOf(), listOf())
        val jsonString = """
            {
                "email_account_id":"${sampleEmail.emailAccountID}",
                "recipient_list2":[
                    "email1@email.com",
                    "email2@email.com"
                ],
                "email_group_id_list2":[
                    "sample_group_id_1"
                ],
                "another":"field"
            }"
        """.trimIndent()
        val recreatedObject = createObjectFromJsonString(jsonString) { Email.parse(it) }
        assertEquals(sampleEmail, recreatedObject)
    }
}
