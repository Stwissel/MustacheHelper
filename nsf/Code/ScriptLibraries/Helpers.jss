function applyTemplate(doc, templateName) {
	try {
		// Check for init
		if (!Mustache.isInitialized()) {
			var templateView:NotesView = database.getView("templates");
			Mustache.initFromView(session, templateView);
			templateView.recycle();	
		}
		return Mustache.renderDocumentToString(templateName,doc);
	} catch (e) {
		return e.message;
	}
}