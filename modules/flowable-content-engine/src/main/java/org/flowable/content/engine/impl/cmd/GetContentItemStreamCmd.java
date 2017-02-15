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
package org.flowable.content.engine.impl.cmd;

import java.io.InputStream;
import java.io.Serializable;

import org.flowable.content.api.ContentItem;
import org.flowable.content.api.ContentObject;
import org.flowable.content.api.ContentStorage;
import org.flowable.content.engine.impl.interceptor.Command;
import org.flowable.content.engine.impl.interceptor.CommandContext;
import org.flowable.engine.common.api.FlowableIllegalArgumentException;
import org.flowable.engine.common.api.FlowableObjectNotFoundException;

/**
 * @author Tijs Rademakers
 */
public class GetContentItemStreamCmd implements Command<InputStream>, Serializable {

    private static final long serialVersionUID = 1L;

    protected String contentItemId;

    public GetContentItemStreamCmd(String contentItemId) {
        this.contentItemId = contentItemId;
    }

    public InputStream execute(CommandContext commandContext) {
        if (contentItemId == null) {
            throw new FlowableIllegalArgumentException("contentItemId is null");
        }

        ContentItem contentItem = commandContext.getContentItemEntityManager().findById(contentItemId);
        if (contentItem == null) {
            throw new FlowableObjectNotFoundException("content item could not be found with id " + contentItemId);
        }

        ContentStorage contentStorage = commandContext.getContentEngineConfiguration().getContentStorage();
        ContentObject contentObject = contentStorage.getContentObject(contentItem.getContentStoreId());
        return contentObject.getContent();
    }

}
