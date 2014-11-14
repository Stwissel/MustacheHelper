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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import lotus.domino.Document;
import lotus.domino.MIMEEntity;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;

/**
 * Convenience wrapper around Mustache for Java to be used with
 * Templates stored in Notes documents and to process Notes documents
 * and other Java collections
 * 
 * @author Stephan H. Wissel <stephan@wissel.net>
 */
public class MustacheHelper {
    private static final String         MIME_FIELD_NAME = "$NoteHasNativeMIME";
    private static final String         TEMPLATE_NAME   = "Subject";
    private static final String         TEMPLATE_BODY   = "Body";

    private boolean                     isInitialized   = false;
    private final Map<String, String>   templateStrings = new HashMap<String, String>();
    private final Map<String, Mustache> templates       = new HashMap<String, Mustache>();
    private MustacheFactory             factory         = null;

    public MustacheHelper() {
        // Constructor for access via managed bean
        this.isInitialized = false;
    }

    /**
     * Create the helper from a view object
     * 
     * @param templateView
     */
    public MustacheHelper(Session s, View templateView) {
        this.initFromView(s, templateView);
    }

    
    /**
     * Adds a template from a document with and without mime
     * 
     * @param doc
     */
    private void addTemplateFromNote(Session s, Document doc) {
        try {
            String templateName = doc.getItemValueString(TEMPLATE_NAME);
            String templateBody;
            if (doc.hasItem(MIME_FIELD_NAME)) {
                s.setConvertMime(false);
                MIMEEntity meBody = doc.getMIMEEntity(TEMPLATE_BODY);
                // TODO: is this good? Do we need to check for mimetype, attachments etc?
                templateBody = meBody.getContentAsText();                
                s.setConvertMime(true);
            } else {
                templateBody = doc.getItemValueString(TEMPLATE_BODY);
            }
            // Add it to the raw templates
            this.templateStrings.put(templateName, templateBody);
            
            // If it was compiled before, remove that
            if (this.templates.containsKey(templateName)) {
                this.templates.remove(templateName);
            }
        } catch (NotesException e) {
            e.printStackTrace();
        }
    }

    /**
     * Turns the document into an object that can be used in a mustache template
     * @param doc
     * @param dr DocumentResolver for special cases
     * @return
     */
    private Object getDocObject(Document doc, DocumentResolver dr) {
        DocumentResolver r = (dr==null) ? new DefaultDocumentResolver() : dr;
        return r.resolve(doc);
    }

    private Mustache getMustache(String templateName) throws MustacheError {
        if (!this.templateStrings.containsKey(templateName)) {
            throw new MustacheError("Template name " + templateName + " not found");
        }

        // If it wasn't used before compile it
        if (!this.templates.containsKey(templateName)) {
            Mustache m = this.factory.compile(new StringReader(this.templateStrings.get(templateName)), templateName);
            this.templates.put(templateName, m);
        }
        return this.templates.get(templateName);
    }
    
    /**
     * Clears the mustache template cache
     */
    public void reset() {
        this.templates.clear();
    }

    /**
     * Loads the templates from documents in a view The document either contains a plain text Body field or a
     * mime document, then we use the text/plain part of it The template name is in the subject field
     * 
     * @param templateView
     */
    public void initFromView(Session s, View templateView) {
        try {
            Document doc = templateView.getFirstDocument();
            while (doc != null) {
                Document nextDoc = templateView.getNextDocument(doc);
                    this.addTemplateFromNote(s, doc);
                Utils.shred(doc);
                doc = nextDoc;
            }
        } catch (NotesException e) {
            e.printStackTrace();
        }
        // Create the factory for our use
        this.factory = new DefaultMustacheFactory(new MustacheNotesResolver(this.templateStrings));

        // Memorize that we are done
        this.isInitialized = true;
    }

    /**
     * Closest method to a default Mustache. Takes any object - should be a collection map or bean
     * 
     * @param templateName
     * @param source
     * @param out
     * @throws MustacheError
     */
    public void render(String templateName, Object source, OutputStream out) throws MustacheError {
        if (!this.isInitialized || templateName == null || !this.templateStrings.containsKey(templateName)) {
            throw new MustacheError("Can't execute the template action");
        }
        Mustache mustache = this.getMustache(templateName);
        PrintWriter pw = new PrintWriter(out);
        mustache.execute(pw, source);
        pw.flush();
        pw.close();
    }

    /**
     * Renders a document into an output stream using a named template
     * 
     * @param templateName
     * @param doc
     * @param out
     * @throws MustacheError
     */
    public void renderDocument(String templateName, Document doc, OutputStream out) throws MustacheError {
        this.renderDocument(templateName, doc, out, null);
    }
    
    public void renderDocument(String templateName, Document doc, OutputStream out, DocumentResolver dr) throws MustacheError {
        Object docObject = this.getDocObject(doc, dr);
        this.render(templateName, docObject, out);
    }

    /**
     * Convenience method to render a document to a String
     * 
     * @param templateName
     * @param doc
     * @return
     * @throws IOException
     * @throws MustacheError
     */
    public String renderDocumentToString(String templateName, Document doc) throws IOException, MustacheError {
       return this.renderDocumentToString(templateName, doc, null);
    }
    
    public String renderDocumentToString(String templateName, Document doc, DocumentResolver dr) throws IOException, MustacheError {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        this.renderDocument(templateName, doc, result, dr);
        result.flush();
        result.close();
        return result.toString();
    }

    /**
     * Closest method to a default Mustache. Takes any object - should be a collection map or bean
     * 
     * @param templateName
     * @param source
     * @return a string
     * @throws IOException
     * @throws MustacheError
     */
    public String renderToString(String templateName, Object source) throws IOException, MustacheError {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        this.render(templateName, source, result);
        result.flush();
        result.close();
        return result.toString();
    }

}
