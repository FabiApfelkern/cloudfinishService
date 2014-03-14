package models;

import java.io.File;

import play.Play;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class Resource {
	
	public static File get(String id) {
		
		String path = Play.application().path().toString();
		File f = new File(path + "/public/resources/" + id + ".xml");
		
		if(f.exists()){
			return f;
		} else {
			return null;
		}
	
	}
	
}
