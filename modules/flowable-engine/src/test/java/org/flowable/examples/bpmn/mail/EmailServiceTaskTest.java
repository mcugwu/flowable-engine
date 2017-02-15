/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.flowable.examples.bpmn.mail;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.flowable.engine.impl.test.PluggableFlowableTestCase;
import org.flowable.engine.test.Deployment;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

/**
 * @author Joram Barrez
 */
public class EmailServiceTaskTest extends PluggableFlowableTestCase {

    /* Wiser is a fake email server for unit testing */
    private Wiser wiser;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        wiser = new Wiser();
        wiser.setPort(5025);
        wiser.start();
    }

    @Override
    protected void tearDown() throws Exception {
        wiser.stop();
        super.tearDown();
    }

    @Deployment
    public void testSendEmail() throws Exception {

        String from = "ordershipping@flowable.org";
        boolean male = true;
        String recipientName = "John Doe";
        String recipient = "johndoe@flowable.com";
        Date now = new Date();
        String orderId = "123456";

        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("sender", from);
        vars.put("recipient", recipient);
        vars.put("recipientName", recipientName);
        vars.put("male", male);
        vars.put("now", now);
        vars.put("orderId", orderId);

        runtimeService.startProcessInstanceByKey("sendMailExample", vars);

        List<WiserMessage> messages = wiser.getMessages();
        assertEquals(1, messages.size());

        WiserMessage message = messages.get(0);
        MimeMessage mimeMessage = message.getMimeMessage();

        assertEquals("Your order " + orderId + " has been shipped", mimeMessage.getHeader("Subject", null));
        assertEquals(from, mimeMessage.getHeader("From", null));
        assertTrue(mimeMessage.getHeader("To", null).contains(recipient));
    }

}
