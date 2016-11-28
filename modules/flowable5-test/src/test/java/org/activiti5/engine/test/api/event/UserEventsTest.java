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
package org.activiti5.engine.test.api.event;

import org.activiti.engine.common.api.delegate.event.ActivitiEntityEvent;
import org.activiti.engine.common.api.delegate.event.ActivitiEvent;
import org.activiti.idm.api.User;
import org.activiti.idm.api.event.ActivitiIdmEventType;
import org.activiti5.engine.impl.test.PluggableActivitiTestCase;

/**
 * Test case for all {@link ActivitiEvent}s related to users.
 * 
 * @author Frederik Heremans
 */
public class UserEventsTest extends PluggableActivitiTestCase {

	private TestActiviti6EntityEventListener listener;

	/**
	 * Test create, update and delete events of users.
	 */
	public void testUserEntityEvents() throws Exception {
		User user = null;
		try {
			user = identityService.newUser("fred");
			user.setFirstName("Frederik");
			user.setLastName("Heremans");
			identityService.saveUser(user);

			assertEquals(2, listener.getEventsReceived().size());
			ActivitiEntityEvent event = (ActivitiEntityEvent) listener.getEventsReceived().get(0);
			assertEquals(ActivitiIdmEventType.ENTITY_CREATED, event.getType());
			assertTrue(event.getEntity() instanceof User);
			User userFromEvent = (User) event.getEntity();
			assertEquals("fred", userFromEvent.getId());
			
			event = (ActivitiEntityEvent) listener.getEventsReceived().get(1);
			assertEquals(ActivitiIdmEventType.ENTITY_INITIALIZED, event.getType());
			listener.clearEventsReceived();

			// Update user
			user.setFirstName("Anna");
			identityService.saveUser(user);
			assertEquals(1, listener.getEventsReceived().size());
			event = (ActivitiEntityEvent) listener.getEventsReceived().get(0);
			assertEquals(ActivitiIdmEventType.ENTITY_UPDATED, event.getType());
			assertTrue(event.getEntity() instanceof User);
			userFromEvent = (User) event.getEntity();
			assertEquals("fred", userFromEvent.getId());
			assertEquals("Anna", userFromEvent.getFirstName());
			listener.clearEventsReceived();

			// Delete user
			identityService.deleteUser(user.getId());
			
			assertEquals(1, listener.getEventsReceived().size());
			event = (ActivitiEntityEvent) listener.getEventsReceived().get(0);
			assertEquals(ActivitiIdmEventType.ENTITY_DELETED, event.getType());
			assertTrue(event.getEntity() instanceof User);
			userFromEvent = (User) event.getEntity();
			assertEquals("fred", userFromEvent.getId());
			listener.clearEventsReceived();

		} finally {
			if (user != null && user.getId() != null) {
				identityService.deleteUser(user.getId());
			}
		}
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		listener = new TestActiviti6EntityEventListener(User.class);
		processEngineConfiguration.getEventDispatcher().addEventListener(listener);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		if (listener != null) {
		  processEngineConfiguration.getEventDispatcher().removeEventListener(listener);
		}
	}
}
