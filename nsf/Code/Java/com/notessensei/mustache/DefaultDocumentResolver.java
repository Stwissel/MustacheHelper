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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.NotesException;

/**
 * Turns a Notes document into an Object for easy use in a Mustache template
 * @author stw
 *
 */
public class DefaultDocumentResolver implements DocumentResolver {

    @Override
    /**
     * Single values turn into Strings, multi values into collections
     */
    public Map<String, Object> resolve(Document doc) {
 
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            for (Object o : doc.getItems()) {
                Item i = (Item) o;
                //TODO: Check for date values?
                if (i.getValues().size() > 1) {
                    Collection<String> c = new Vector<String>();
                    for (Object val : i.getValues()) {
                        c.add(val.toString());
                    }
                    result.put(i.getName(), c);
                } else {
                    result.put(i.getName(), i.getText());
                }
                
                Utils.shred(i);
            }
        } catch (NotesException e) {
            e.printStackTrace();
        }
        return result;
    }

}
