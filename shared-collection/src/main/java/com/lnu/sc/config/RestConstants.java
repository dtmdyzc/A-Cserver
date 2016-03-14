/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lnu.sc.config;

/**
 *
 * @author Wang Bang
 */
public class RestConstants {
    	public static final String ARTIFACTS = "/artifacts"; //GET(list all files)
        public static final String ARTIFACT_NAME = "/artifact/{ArtifactName:.+}"; // GET(read one file) Post(file update)
	public static final String ARTIFACT_DELETE = "/artifactD/{ArtifactName:.+}"; // GET(In spring, methodtype of DELETE or PUT is OPTIONS, which does not work)
        public static final String ARTIFACT = "/artifact"; // POST(file upload)
        public static final String COLLECTION = "/collection"; // POST(create collection)
        public static final String CURRENTCOLLECTIONCONTENT = "/collectionlist"; // GET(list current collection content)
        public static final String SETCURRENTCOLLECTION = "/setcurrentcollection"; // POST(set current collection)
        public static final String SETBACKCURRENTCOLLECTION = "/setbackcurrentcollection"; // POST(go one back to the parent collection of the current collection)
        public static final String CURRENTCOLLECTION = "/currentcollection"; // GET(current collection)
        
        public static final String PATH_ONE = "C:\\artifacts";
	public static final String FILES = "files";
	public static final String DOWNLOAD = "download/{fileName}";

	public static final String PATH_TWO = "C:\\datafiles";
        public static final String FILECOLLECTIONS = "C:\\datafiles\\collections.txt";
        public static final String FILECOLLECTIONARTIFACTRELATIONS = "C:\\datafiles\\collectionartifactrelations.txt";
    
        
        
}
