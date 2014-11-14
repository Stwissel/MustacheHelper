/** ========================================================================= *
 * Copyright (C)  2014 Stephan H. Wissel <stephan@wissel.net>                 *
 *                            All rights reserved.                            *
 *                                                                            * 
 *  @author     Stephan H. Wissel <stephan@wissel.net>                        *   
 *                                                                            *
 * @version     1.0                                                           *
 * ========================================================================== *
 *                                                                            *
 * Licensed under the  Apache License, Version 2.0  (the "License").  You may *
 * not use this file except in compliance with the License.  You may obtain a *
 * copy of the License at <http://www.apache.org/licenses/LICENSE-2.0>.       *
 *                                                                            *
 * Unless  required  by applicable  law or  agreed  to  in writing,  software *
 * distributed under the License is distributed on an  "AS IS" BASIS, WITHOUT *
 * WARRANTIES OR  CONDITIONS OF ANY KIND, either express or implied.  See the *
 * License for the  specific language  governing permissions  and limitations *
 * under the License.                                                         *
 *                                                                            *
 * ========================================================================== */
package com.notessensei.mustache;

public class MustacheError extends Exception {

    public MustacheError(String errMessage) {
        super(errMessage);
    }

    private static final long serialVersionUID = 1L;

}
