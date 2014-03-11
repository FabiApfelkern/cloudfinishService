package models;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class Resource {
	
	public static Model get(String id) {
		
		Model model = ModelFactory.createDefaultModel();
		model.read("http://dbpedia.org/data/" + id + ".rdf");
		
		return model;
	}
	
}
