package com.lnu.sc;

import com.lnu.sc.config.RestConstants;
import com.lnu.sc.data.RepositoryData;
import com.lnu.sc.entities.Artifact;
import com.lnu.sc.file.FileIO;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import javax.servlet.MultipartConfigElement;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.ResourceUtils;

@ComponentScan
@EnableAutoConfiguration
public class Application extends SpringBootServletInitializer {
        @Bean
        public MultipartConfigElement multipartConfigElement() {
                MultipartConfigFactory factory = new MultipartConfigFactory();
                factory.setMaxFileSize("5120KB");
                factory.setMaxRequestSize("5120KB");
                return factory.createMultipartConfig();
        }
        
	@Value("${keystore.file}") private String keystoreFile;
	@Value("${keystore.pass}") private String keystorePass;
        
        
        public static List<Artifact> artifacts = RepositoryData.getArtifacts();
        public static JSONObject currentCollections = RepositoryData.getCurrentCollections();
        public static JSONArray collections = RepositoryData.getCollections();
        public static JSONArray collectionartifactrelations = RepositoryData.getCollectionArtifactRelations();
        
        public static JSONArray artifactmappings = RepositoryData.getArtifactMappings();
        public static JSONArray collectionmappings = RepositoryData.getCollectionMappings();
        public static JSONArray sharedcollections = RepositoryData.getSharedcollections();        
        
    public static void main(String[] args) {
        FileIO fio = new FileIO();
        
        currentCollections.put("keys", "/root");
        currentCollections.put("names", "/root");
        collections = fio.readFile(RestConstants.FILECOLLECTIONS);
        collectionartifactrelations = fio.readFile(RestConstants.FILECOLLECTIONARTIFACTRELATIONS);
        
        File[] folder1 = new File(RestConstants.PATH_ONE).listFiles();
        int i = 0;
        for (File f : folder1) {
            // f is only file path string
            artifacts.add(new Artifact(i, f));
            i++;
        }
        
        SpringApplication.run(Application.class, args);
    }
    
	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer()  throws FileNotFoundException {
		final String absoluteKeystoreFile = ResourceUtils.getFile(keystoreFile).getAbsolutePath();

		return new EmbeddedServletContainerCustomizer() {
			@Override
			public void customize(	ConfigurableEmbeddedServletContainer factory) {
				if (factory instanceof TomcatEmbeddedServletContainerFactory) {
					TomcatEmbeddedServletContainerFactory containerFactory = (TomcatEmbeddedServletContainerFactory) factory;
					containerFactory.addConnectorCustomizers(new TomcatConnectorCustomizer() {
							@Override
							public void customize(Connector connector) {
								connector.setPort(8443);
								connector.setSecure(true);
								connector.setScheme("https");
								Http11NioProtocol proto = (Http11NioProtocol) connector.getProtocolHandler();
					            proto.setSSLEnabled(true);
					            proto.setKeystoreFile(absoluteKeystoreFile);
					            proto.setKeystorePass(keystorePass);
					            proto.setKeystoreType("PKCS12");
					            proto.setKeyAlias("tomcat");
							}
						});
				}
			}
		};
	}
}
