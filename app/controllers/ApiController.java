package controllers;

import models.Resource;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.BodyParser;

public class ApiController extends Controller {
	
	public static Result getResource(String id){
		Model model = Resource.get(id);
		
		ObjectNode result = Json.newObject();
		result.put("status", "ok");
		return ok(model.toString());
		
	}
	
	public static Result status(){
		ObjectNode result = Json.newObject();
		result.put("status", "ok");
		return ok(result);
	}
	

	@BodyParser.Of(BodyParser.Json.class)
	public static Result recognition() {
		Logger.info("Recognition called");
		JsonNode json = request().body().asJson();
		ObjectNode result = Json.newObject();
		result.put("status", json.get("name"));
		return ok(result);	
	}
	
	
	public static Result sandbox(){
		ObjectNode result = Json.newObject();
		
		Model model = ModelFactory.createDefaultModel();
		model.read("http://dbpedia.org/data/ICE_1.rdf");
		
		
		result.put("status", "ok");
		result.put("message", model.toString());
		return ok(result);
	}
	

}
