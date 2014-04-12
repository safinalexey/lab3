package ru.eltech.csa.semweb.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.NoSuchElementException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Writer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;

import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

@Controller
public class OntologyController {

    @Autowired
    OntModel diseaseOntModelService;

	@RequestMapping(value="/", method=RequestMethod.GET)
    public String welcomeAction(Model model) {

        OntClass diseaseClass = diseaseOntModelService.getOntClass("http://www.semanticweb.org/alexey/ontologies/2014/2/medicines#Disease");
        OntClass organClass = diseaseOntModelService.getOntClass("http://www.semanticweb.org/alexey/ontologies/2014/2/medicines#Organ");
        OntClass medicineClass = diseaseOntModelService.getOntClass("http://www.semanticweb.org/alexey/ontologies/2014/2/medicines#Medicine");

        List<String> diseaseIndividuals = new ArrayList<String>();
        List<String> organIndividuals = new ArrayList<String>();
        List<String> medicineIndividuals = new ArrayList<String>();


        for (ExtendedIterator iter = diseaseClass.listInstances();iter.hasNext();) 
        {
            Individual inst = (Individual) iter.next();
            diseaseIndividuals.add(inst.getLocalName());
        }

        for (ExtendedIterator iter = organClass.listInstances();iter.hasNext();) 
        {
            Individual inst = (Individual) iter.next();
            organIndividuals.add(inst.getLocalName());
        }

        for (ExtendedIterator iter = medicineClass.listInstances();iter.hasNext();) 
        {
            Individual inst = (Individual) iter.next();
            medicineIndividuals.add(inst.getLocalName());
        }      

        model.addAttribute("diseaseIndividuals", diseaseIndividuals);
        model.addAttribute("organIndividuals", organIndividuals);
        model.addAttribute("medicineIndividuals", medicineIndividuals);

        return "welcomeView";
    }

    @RequestMapping(value="/edit/{name}", method=RequestMethod.GET)
    public String editIndividualAction(Model model, @PathVariable("name") String name) {

        String path = "http://www.semanticweb.org/alexey/ontologies/2014/2/medicines#";

        Individual inst = diseaseOntModelService.getIndividual(path+name);

        List<String> sourceList  = new ArrayList<String>();
        List<String> symptomList = new ArrayList<String>();
        List<String> objPropList = new ArrayList<String>();

        OntProperty sourceProp  = diseaseOntModelService.getOntProperty("http://www.semanticweb.org/alexey/ontologies/2014/2/medicines#source");
        OntProperty symptomProp = diseaseOntModelService.getOntProperty("http://www.semanticweb.org/alexey/ontologies/2014/2/medicines#symptoms");
        ObjectProperty objProp  = diseaseOntModelService.getObjectProperty("http://www.semanticweb.org/alexey/ontologies/2014/2/medicines#has_influence");
        System.out.println(objProp.getLocalName());

        for (NodeIterator iter2 = inst.listPropertyValues(sourceProp);iter2.hasNext();) {
            String aa = new String(iter2.next().toString());
            sourceList.add(aa);
        }
        for (NodeIterator iter2 = inst.listPropertyValues(symptomProp);iter2.hasNext();) {
            String aa = new String(iter2.next().toString());
            symptomList.add(aa);
        }
        //for (NodeIterator iter2 = inst.listPropertyValues(objProp);iter2.hasNext();) {
            //String aa = new String(iter2.next().toString());
            //System.out.println(iter2);
            //objPropList.add(aa);
        //}

        System.out.println(inst.listPropertyValues(objProp).toList().toArray()[0]);

        model.addAttribute("name", name);
        model.addAttribute("sourceList", sourceList);
        model.addAttribute("symptomList", symptomList);

        return "editView";
    }

    @RequestMapping(value="/add/{ontClass}/{name}", method=RequestMethod.POST)
    public @ResponseBody String addIndividualsAction(Model model,
                                            @PathVariable("ontClass") String ontClass,
                                            @PathVariable("name") String name) {

        String path = "http://www.semanticweb.org/alexey/ontologies/2014/2/medicines#";

        OntClass addNewIndividualClass = diseaseOntModelService.getOntClass(path+ontClass);

        Individual insta = diseaseOntModelService.createIndividual(path+name, addNewIndividualClass);

        return new String("ok");
    }

    @RequestMapping(value="/delete/{name}", method=RequestMethod.POST)
    public @ResponseBody String deleteIndividualsAction(Model model, @PathVariable("name") String name) {

        String path = "http://www.semanticweb.org/alexey/ontologies/2014/2/medicines#";

        Individual instToDel = diseaseOntModelService.getIndividual(path + name);
        instToDel.remove();

        return new String("ok");
    }

    @RequestMapping(value="/saveProperty/{ind}/{name}/{value}", method=RequestMethod.POST)
    public @ResponseBody String savePropertyAction(Model model, @PathVariable("ind") String ind, @PathVariable("name") String name, @PathVariable("value") String value) {
        
        String origPath = "http://www.semanticweb.org/alexey/ontologies/2014/2/medicines#";
        String path = "http://www.semanticweb.org/alexey/ontologies/2014/2/medicines#Disease";

        OntClass diseaseClass = diseaseOntModelService.getOntClass(path);

        Individual inst = diseaseOntModelService.getIndividual(origPath+ind);

        OntProperty prop = diseaseOntModelService.getOntProperty(origPath+name);

        inst.addProperty(prop, ResourceFactory.createPlainLiteral(value));

        return new String("ok");
    }

    @RequestMapping(value="/deleteProperty/{ind}/{name}/{value}", method=RequestMethod.POST)
    public @ResponseBody String deletePropertyAction(Model model, @PathVariable("ind") String ind, @PathVariable("name") String name, @PathVariable("value") String value) {
        
        String origPath = "http://www.semanticweb.org/alexey/ontologies/2014/2/medicines#";
        String path = "http://www.semanticweb.org/alexey/ontologies/2014/2/medicines#Disease";

        OntClass diseaseClass = diseaseOntModelService.getOntClass(path);

        Individual inst = diseaseOntModelService.getIndividual(origPath+ind);

        OntProperty prop = diseaseOntModelService.getOntProperty(origPath+name);

        inst.removeProperty(prop, ResourceFactory.createPlainLiteral(value));

        return new String("ok");
    }

    @RequestMapping(value="/save", method=RequestMethod.POST)
    public @ResponseBody String saveAction(Model model) {

        try{
            //File file = new File("C:\\1\\med.owl");
            File file = new File("1.owl");
            diseaseOntModelService.write(new FileOutputStream(file));    

            return new String("ok");
        }
        catch (FileNotFoundException e) {
            return new String("error");
        }
    }
}