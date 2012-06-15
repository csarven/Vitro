/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.utils;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;

import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
//Returns the appropriate n3 based on data getter
public  class ProcessSparqlDataGetterN3 extends ProcessDataGetterAbstract {
	private static String classType = "java:edu.cornell.mannlib.vitro.webapp.utils.dataGetter.SparqlQueryDataGetter";
	private Log log = LogFactory.getLog(ProcessSparqlDataGetterN3.class);

	public ProcessSparqlDataGetterN3(){
		
	}
	//Pass in variable that represents the counter 

	//TODO: ensure correct model returned
	//We shouldn't use the ACTUAL values here but generate the n3 required
    public List<String> retrieveN3Required(int counter) {
    	String dataGetterVar = getDataGetterVar(counter);
    	String n3 = dataGetterVar + " a <" + classType + ">; \n" + 
    	"display:queryModel " + getN3VarName("queryModel", counter) + "; \n" + 
    	"display:saveToVar " + getN3VarName("saveToVar", counter) + "; \n" + 
    	"display:query " + getN3VarName("query", counter) + " .";
    	List<String> requiredList = new ArrayList<String>();
    	requiredList.add(getPrefixes() + n3);
    	return requiredList;
    	
    }
    public List<String> retrieveN3Optional(int counter) {
    	return null;
    }
  
    
    //Need to add method sfor returning the fields, literals on form, and all that
    /*
     * addLiteralsAndUrisOnForm(pn, counter);
			// Add fields
			addFields(pn, counter);
			//Add input values to submission
			addInputsToSubmission(pn, counter);
     */
    public List<String> retrieveLiteralsOnForm(int counter) {
    	List<String> literalsOnForm = new ArrayList<String>();
    	literalsOnForm.add(getVarName("saveToVar",counter));
    	literalsOnForm.add(getVarName("query", counter));
    	return literalsOnForm;
    	
    }
    
     
    public List<String> retrieveUrisOnForm(int counter) {
    	List<String> urisOnForm = new ArrayList<String>();
    	//We have no uris as far as I know.. well query Model is a uri
    	urisOnForm.add(getVarName("queryModel", counter));
    	return urisOnForm;
    	
    }
    
   public List<FieldVTwo> retrieveFields(int counter) {
	   List<FieldVTwo> fields = new ArrayList<FieldVTwo>();
	   
	   //An alternative way of doing this 
	   /*
	   List<String> allFieldsBase = new ArrayList<String>();
	   allFieldsBase.addAll(getLiteralVarNamesBase());
	   allFieldsBase.addAll(getUriVarNamesBase());
	   
	   for(String varName: allFieldsBase) {
		   fields.add(new FieldVTwo().setName(getVarName(varName, counter)));
	   } */
	   //For existing data getters
	   //fields.add(new FieldVTwo().setName(getVarName("dataGetter", counter)));
	   fields.add(new FieldVTwo().setName(getVarName("queryModel", counter)));
	   fields.add(new FieldVTwo().setName(getVarName("saveToVar", counter)));
	   fields.add(new FieldVTwo().setName(getVarName("query", counter)));

	   return fields;
   }
   
   public List<String> getLiteralVarNamesBase() {
	   return Arrays.asList("saveToVar", "query");   
   }

   //these are for the fields ON the form
   public List<String> getUriVarNamesBase() {
	   return Arrays.asList("queryModel");   
   }
   
   //Existing values
   //TODO: Correct
   
   public void populateExistingValues(String dataGetterURI, int counter, OntModel queryModel) {
	   //First, put dataGetterURI within scope as well
	   existingUriValues.put(this.getDataGetterVar(counter), new ArrayList<String>(Arrays.asList(dataGetterURI)));
	   //Sparql queries for values to be executed
	   //And then placed in the correct place/literal or uri
	   String querystr = getExistingValuesSparqlQuery(dataGetterURI);
	   QueryExecution qe = null;
       try{
           Query query = QueryFactory.create(querystr);
           qe = QueryExecutionFactory.create(query, queryModel);
           ResultSet results = qe.execSelect();
           while( results.hasNext()){
        	   QuerySolution qs = results.nextSolution();
        	   Literal saveToVarLiteral = qs.getLiteral("saveToVar");
        	   Literal htmlValueLiteral = qs.getLiteral("htmlValue");
        	   //Put both literals in existing literals
        	   existingLiteralValues.put(this.getVarName("saveToVar", counter),
        			   new ArrayList<Literal>(Arrays.asList(saveToVarLiteral, htmlValueLiteral)));
           }
       } catch(Exception ex) {
    	   log.error("Exception occurred in retrieving existing values with query " + querystr, ex);
       }
	   
	   
   }
  
   
   //?dataGetter a FixedHTMLDataGetter ; display:saveToVar ?saveToVar; display:htmlValue ?htmlValue .
   protected String getExistingValuesSparqlQuery(String dataGetterURI) {
	   String query = this.getSparqlPrefix() + "SELECT ?saveToVar ?htmlValue WHERE {" + 
			   "<" + dataGetterURI + "> display:saveToVar ?saveToVar . \n" + 
			   "<" + dataGetterURI + "> display:htmlValue ?htmlValue . \n" + 
			   "}";
	   return query;
   }


   
   public JSONObject getExistingValuesJSON(String dataGetterURI, OntModel queryModel) {
	   JSONObject jo = new JSONObject();
	   return jo;
   }

}

