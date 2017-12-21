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
package org.flowable.idm.spring.authentication;

import org.flowable.idm.api.PasswordEncoder;
import org.flowable.idm.api.PasswordSalt;

public class SpringEncoder implements PasswordEncoder {

    private org.springframework.security.authentication.encoding.PasswordEncoder encodingPasswordEncoder;
    private org.springframework.security.crypto.password.PasswordEncoder cryptoPasswordEncoder;

    public SpringEncoder(org.springframework.security.authentication.encoding.PasswordEncoder passwordEncoder) {
        this.encodingPasswordEncoder = passwordEncoder;
    }

    public SpringEncoder(org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.cryptoPasswordEncoder = passwordEncoder;
    }

    @Override
    public String encode(CharSequence rawPassword, PasswordSalt passwordSalt) {
        if (null == encodingPasswordEncoder)
            return cryptoPasswordEncoder.encode(rawPassword);
        else return encodingPasswordEncoder.encodePassword(rawPassword.toString(), passwordSalt);
    }

    @Override
    public boolean isMatches(CharSequence rawPassword, String encodedPassword, PasswordSalt passwordSalt) {
        if (null == encodingPasswordEncoder)
            return cryptoPasswordEncoder.matches(rawPassword, encodedPassword);
        else
            return encodingPasswordEncoder.isPasswordValid(encodedPassword, rawPassword.toString(), passwordSalt);
    }

    public Object getSpringEncodingProvider() {
        return (null == encodingPasswordEncoder) ? cryptoPasswordEncoder : encodingPasswordEncoder;
    }
}
