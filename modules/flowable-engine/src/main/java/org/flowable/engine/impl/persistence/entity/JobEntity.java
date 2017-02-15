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
package org.flowable.engine.impl.persistence.entity;

import java.util.Date;

/**
 * Represents an async job: a piece of logic that needs to be executed asynchronously.
 * 
 * @author Tijs Rademakers
 * @author Joram Barrez
 */
public interface JobEntity extends AbstractJobEntity {

    String getLockOwner();

    void setLockOwner(String claimedBy);

    Date getLockExpirationTime();

    void setLockExpirationTime(Date claimedUntil);
}
