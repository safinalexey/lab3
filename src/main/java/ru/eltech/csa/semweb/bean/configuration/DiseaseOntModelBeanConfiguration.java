package ru.eltech.csa.semweb.bean.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;


@Configuration
public class DiseaseOntModelBeanConfiguration {

    private final static String ontPath = "file:///C:/1/med.owl";

    @Bean
    public OntModel diseaseOntModelService()
    {
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
        model.read(ontPath);

        return model;
    }

}