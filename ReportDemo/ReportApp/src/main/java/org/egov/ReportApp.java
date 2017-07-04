package org.egov;
import java.io.File;

import org.egov.domain.model.ReportDefinitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;



@SpringBootApplication
public class ReportApp{

	public static final Logger LOGGER = LoggerFactory.getLogger(ReportApp.class);

	
    @Autowired
    public static ResourceLoader resourceLoader;
    
   
    @Autowired
    private Environment env;
    

	/*@Autowired
	public ReportDefinitions reportDefintions;
	*/
    
  


    public ReportApp(ResourceLoader resourceLoader) {
    	this.resourceLoader = resourceLoader;
    }
    
	public static void main(String[] args) throws Exception {
		SpringApplication.run(ReportApp.class, args);
	}
	
	@Bean
    public MappingJackson2HttpMessageConverter jacksonConverter() {
        final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        final ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        converter.setObjectMapper(mapper);
        return converter;
    }
	 
	  
	  @Bean("reportDefinitions")
	  public static ReportDefinitions loadYaml() {
	      ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    try {
        /*ReportDefinitions reportDefinitions = mapper.readValue(new File("/home/senthilkumar/application.yml"), ReportDefinitions.class);*/
    	Resource resource = resourceLoader.getResource("file:/home/senthilkumar/application.yml");
    	//Resource resource = resourceLoader.getResource("file:"+env.getProperty("report.yaml.path"));
        File yamlFile = resource.getFile();
        ReportDefinitions reportDefinitions = mapper.readValue(yamlFile, ReportDefinitions.class);
        LOGGER.info(reportDefinitions.toString());
        return reportDefinitions;
    } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    return null;
    }
	  

	
}