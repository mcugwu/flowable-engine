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

import java.util.Collection;
import java.util.List;

import org.flowable.common.engine.impl.persistence.entity.EntityManager;
import org.flowable.engine.impl.CommentQueryImpl;
import org.flowable.engine.task.Comment;
import org.flowable.engine.task.Event;

/**
 * @author Joram Barrez
 */
public interface CommentEntityManager extends EntityManager<CommentEntity> {

    List<Comment> findCommentsByTaskId(String taskId);

    List<Comment> findCommentsByTaskIdAndType(String taskId, String type);

    List<Comment> findCommentsByType(String type);

    List<Event> findEventsByTaskId(String taskId);

    List<Event> findEventsByProcessInstanceId(String processInstanceId);

    void deleteCommentsByTaskId(String taskId);

    void deleteCommentsByProcessInstanceId(String processInstanceId);
    
    void bulkDeleteCommentsForTaskIds(Collection<String> taskIds);
    
    void bulkDeleteCommentsForProcessInstanceIds(Collection<String> processInstanceIds);

    List<Comment> findCommentsByProcessInstanceId(String processInstanceId);

    List<Comment> findCommentsByProcessInstanceId(String processInstanceId, String type);

    Comment findComment(String commentId);

    Event findEvent(String commentId);

    List<Comment> findCommentsByQueryCriteria(CommentQueryImpl query);

    long findCommentCountByQueryCriteria(CommentQueryImpl query);

}