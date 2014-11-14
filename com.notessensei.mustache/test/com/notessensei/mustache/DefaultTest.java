/**
 * 
 */
package com.notessensei.mustache;

import lotus.domino.NotesException;
import lotus.domino.NotesFactory;
import lotus.domino.NotesThread;
import lotus.domino.Session;
import lotus.domino.View;
import lotus.domino.Database;

/**
 * @author stw
 *
 */
public class DefaultTest {

    /**
     * @param args
     * @throws NotesException 
     */
    public static void main(String[] args) throws NotesException {
        DefaultTest dt = new DefaultTest();
        dt.run();

    }
    
    public void run() throws NotesException {
        System.out.println("\n\n ************* Test running ******************\n\n");
        NotesThread.sinitThread();
        Session s = NotesFactory.createSession();
        Database db = s.getDatabase("", "mustache.nsf");
        View templateView = db.getView("templateView");
        
        MustacheHelper mh = new MustacheHelper(s, templateView);
        
        //TODO: Sample Data here
        
        NotesThread.stermThread();
        System.out.println("\n\n ************* Test complete ******************\n\n");
    }

}
