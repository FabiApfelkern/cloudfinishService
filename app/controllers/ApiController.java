package controllers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;



import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.Resource;

import play.Logger;
import play.api.mvc.AnyContent;
import play.libs.Json;
import play.api.mvc.Action;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.BodyParser;

public class ApiController extends Controller {
	
	public static Result getResource(String id){	
		File model = models.Resource.get(id);
		if(model == null){
			return notFound();
		}
		response().setHeader("Content-disposition","inline; filename=\"" + model.toString() + "\"");
		return ok(model);
	}
	
	public static Result status(){
		ObjectNode result = Json.newObject();
		result.put("status", "ok");
		return ok(result);
	}
	

	@BodyParser.Of(BodyParser.Json.class)
	public static Result recognition() throws JsonParseException, JsonMappingException, IOException {
		ObjectNode result = Json.newObject();
		String objectsKey = "objects";
		
		Logger.info("Recognition called");
		JsonNode json = request().body().asJson();
		ObjectMapper mapper = new ObjectMapper();

		if(!json.has(objectsKey)) {
			result.put("status", "Malformed Request - Key 'Object' is missing");
			return ok(result);
		}
		
		JsonNode objects = json.get(objectsKey);
		
	    TypeReference<List<String>> typeRef = new TypeReference<List<String>>(){};
        List<String> resourcesList = mapper.readValue(objects.traverse(), typeRef);
		
        for(String s : resourcesList) {
        	Logger.info("Processing Resource: " + s);
    		ObjectNode resource = Json.newObject();
        	Model model = ModelFactory.createDefaultModel();
        	try {
            	model.read(s);
            	StmtIterator stmts = model.listStatements();
             	result.put("status", "ok");
        
        		ObjectNode properties = Json.newObject();
        		
            	while (stmts.hasNext()) {
            		Statement stmt = stmts.next();
            		Resource subject = stmt.getSubject();
            		Property predicate = stmt.getPredicate();
            		RDFNode object = stmt.getObject();

            		if(predicate.getNameSpace().equals("http://dbpedia.org/ontology/MeanOfTransportation/")) {
            			String datatype = object.asLiteral().getDatatypeURI();
            			datatype = datatype.substring(datatype.lastIndexOf('/') + 1);
            			String name = predicate.getLocalName();
            			String value = object.asLiteral().getString();
            			properties.put(name, value + " " + datatype);
            			Logger.info(predicate.getLocalName() + ": " + object.asLiteral().getString());
            			
            		}
            		
              		if(predicate.toString().equals("http://dbpedia.org/property/name")) {
            			resource.put("name", object.asLiteral().getString());
            			Logger.info(object.toString());
            		}
              		
            		if(predicate.toString().equals("http://localhost:9000/property/connection")) {
            			resource.put("connection", object.toString());
            			Logger.info(object.toString());
            		}
            		
            		if(properties.size() > 0) {
            			resource.put("properties",properties);
            		}
            		result.put(s,resource);
            	}
            	
			} catch (Exception e) {
				result.put("status", "Error");
				result.put("exception", e.getMessage());
			}

        	
        }
        
        return ok(result);	
	}
	
	
	public static Result sandbox(){
		ObjectNode result = Json.newObject();
		
		Model model = ModelFactory.createDefaultModel();
		model.read("http://localhost:9000/resource/wheel");
		
		
		result.put("status", "ok");
		result.put("message", model.toString());
		return ok(result);
	}
	

}
