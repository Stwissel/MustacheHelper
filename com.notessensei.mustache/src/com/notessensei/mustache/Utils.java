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

import lotus.domino.Base;
import lotus.domino.NotesException;

/**
 * @author stw
 */
public class Utils {
    /**
     * Get rid of all Notes objects
     * 
     * @param morituri
     */
    public static void shred(Base... morituri) {

        for (Base obsoleteObject : morituri) {
            if (obsoleteObject != null) {
                try {
                    obsoleteObject.recycle();
                } catch (NotesException e) {
                    // We don't care we want go get
                    // rid of it anyway
                } finally {
                    obsoleteObject = null;
                }
            }
        }

    }
}
