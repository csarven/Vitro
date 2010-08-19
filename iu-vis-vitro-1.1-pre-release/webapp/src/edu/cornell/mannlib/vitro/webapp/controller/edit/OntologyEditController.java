/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.controller.edit;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.beans.EditProcessObject;
import edu.cornell.mannlib.vedit.beans.FormObject;
import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.beans.Ontology;
import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.OntologyDao;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.jena.OntologyDaoJena;

public class OntologyEditController extends BaseEditController {
    private static final Log log = LogFactory.getLog(OntologyEditController.class.getName());

    public void doPost (HttpServletRequest req, HttpServletResponse response) {

    	VitroRequest request = new VitroRequest(req);
        if (!checkLoginStatus(request,response,(String)request.getAttribute("fetchURI")))
            return;

        try {
            super.doGet(request,response);
        } catch (Exception e) {
            log.error("OntologyEditController caught exception calling doGet()");
        }

        EditProcessObject epo = super.createEpo(request);
        request.setAttribute("epoKey", epo.getKey());

        OntologyDao oDao=getWebappDaoFactory().getOntologyDao();
        Ontology o=null;
        if (request.getParameter("uri")==null){
            log.error("doPost() expects non-null uri parameter");
        } else {
            o = (Ontology)oDao.getOntologyByURI(request.getParameter("uri"));
            if (o==null){
                if (!VitroVocabulary.vitroURI.equals(request.getParameter("uri"))) {
                    log.debug("doPost(): no ontology object found for the namespace "+request.getParameter("uri"));
                }
            } else {
                request.setAttribute("Ontology",o);
            }
        }
        ArrayList results = new ArrayList();
        results.add("Ontology");
        results.add("Namespace");
        results.add("Prefix");
        String name = o==null ? "" : (o.getName()==null) ? "" : o.getName();
        results.add(name);
        String namespace = o==null ? "" : (o.getURI()==null) ? "" : o.getURI();
        results.add(namespace);
        String prefix = o==null ? "" : (o.getPrefix()==null) ? "" : o.getPrefix();
        results.add(prefix);
        request.setAttribute("results", results);
        request.setAttribute("columncount", 3);
        request.setAttribute("suppressquery", "true");

        epo.setDataAccessObject(oDao);
        FormObject foo = new FormObject();
        HashMap OptionMap = new HashMap();

        HashMap formSelect = new HashMap(); // tells the JSP what select lists are populated, and thus should be displayed
        request.setAttribute("formSelect",formSelect);

        // add the options
        foo.setOptionLists(OptionMap);
        epo.setFormObject(foo);
        
        // funky hack because Ontology.getURI() will append a hash for a hash namespace
        // See OntologyDaoJena.ontologyFromOntologyResource() comments
        String realURI = OntologyDaoJena.adjustOntologyURI(o.getURI());
        request.setAttribute("realURI", realURI);
        request.setAttribute("exportURL", request.getContextPath() + Controllers.EXPORT_RDF);
        
        Portal portal = (new VitroRequest(request)).getPortal();
        RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);
        request.setAttribute("epoKey",epo.getKey());
        request.setAttribute("bodyJsp","/templates/edit/specific/ontologies_edit.jsp");
        request.setAttribute("portalBean",portal);
        request.setAttribute("title","Ontology Control Panel");
        request.setAttribute("css", "<link rel=\"stylesheet\" type=\"text/css\" href=\""+portal.getThemeDir()+"css/edit.css\"/>");

        try {
            rd.forward(request, response);
        } catch (Exception e) {
            log.error("OntologyEditController could not forward to view.");
            log.error(e.getMessage());
            log.error(e.getStackTrace());
        }

    }

    public void doGet (HttpServletRequest request, HttpServletResponse response) {
        doPost(request,response);
    }

}
