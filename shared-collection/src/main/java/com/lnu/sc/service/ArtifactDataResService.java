package com.lnu.sc.service;

import static com.lnu.sc.Application.artifactmappings;
import static com.lnu.sc.Application.artifacts;
import static com.lnu.sc.Application.collectionartifactrelations;
import static com.lnu.sc.Application.collectionmappings;
import static com.lnu.sc.Application.currentCollections;
import static com.lnu.sc.Application.collections;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import com.lnu.sc.entities.Artifact;
import com.lnu.sc.config.RestConstants;
import com.lnu.sc.entities.CollectionContent;
import com.lnu.sc.entities.Record;
import com.lnu.sc.file.FileIO;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;


@Component
public class ArtifactDataResService {
        
        FileIO fio = new FileIO();
	
	public Artifact search(String name){
		Artifact a = new Artifact();
		for(int i = 0; i < artifacts.size(); i++){
			if(name.equals(artifacts.get(i).getName())){
				a = artifacts.get(i);
				break;
			}
			else{
				continue;
			}
		}		
		return a;
	}
	
	/**
	 * returns a list of all artifacts
	 * 
	 * @return ArrayList<Artifact>
	 */
	public List<Artifact> getAllArtifact() {
            return artifacts;

	}

	/**
	 * returns an artifact according to its ID
	 * 
	 * @param name
	 * @return Artifact
	 */
	public Artifact getArtifact(String name) {
		
		
		return search(name);

	}

	public File getArtifactView(String name) {
		return search(name).getContent();		

	}

	/**
	 * adds an artifact
	 * 
	 * @param InputStream, FileName
	 * @return boolean
	 */
	public boolean uploadArtifact(InputStream is, String fileName) {
            
                String keypath = currentCollections.get("keys").toString();
                String namepath = currentCollections.get("names").toString();
                
                Date now = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String createtime = dateFormat.format(now);
            
                String[] collkeys = keypath.split("/");
                String currentCollKey = collkeys[collkeys.length-1];         
                String[] collnames = namepath.split("/");
                String currentCollName = collnames[collnames.length-1];

                boolean result = false;
                
                // check if fileName already exists in current collection
                // check collectionartifactrelations table
                boolean checkflag = false;
                for (int index=0; index < collectionartifactrelations.size(); index++) {
                    JSONObject ja = (JSONObject) collectionartifactrelations.get(index);
                    if (ja.get("key").toString().equals("currentCollKey") && ja.get("artiname").toString().equals(fileName)) {
                        checkflag = true;
                        break;
                    }
                }
                if (!checkflag) {
                    // check artifactmappings table
                    for (int index=0; index < artifactmappings.size(); index++) {
                        JSONObject jam = (JSONObject) artifactmappings.get(index);
                        if (jam.get("key").toString().equals("currentCollKey") && jam.get("artiname").toString().equals(fileName)) {
                            checkflag = true;
                            break;
                        }
                    }                         
                }
           
                if (checkflag) {
                    result = false;
                } else {
                    // if no this file, newfilename = currentcollectionnames + fileName
                    
                    String newfilename = namepath.replace("/", "_") + "_" + fileName;
                    
                    String filePath = RestConstants.PATH_ONE+"/"+newfilename;
                    File uploadDir = new File(RestConstants.PATH_ONE);
                    File uploadedFile = new File(filePath);
                    try {
                            if (!uploadDir.exists()) {
                                    uploadDir.mkdir();
                            }
                            if (!uploadedFile.exists()) {
                                    uploadedFile.createNewFile();
                            } else {
                                    return result;
                            }
                            OutputStream os = new FileOutputStream(uploadedFile);
                            int read = 0;
                            byte[] bytes = new byte[1024];
                            while ((read = is.read(bytes)) != -1) {
                                    os.write(bytes);
                                    bytes = new byte[1024];
                            }
                            os.flush();
                            os.close();
                            is.close();
                    } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                    }
                    if (uploadedFile.exists() && uploadedFile.length() > 0) {
                            result = true;
                    }
//                    Artifact art = new Artifact(artifacts.size() + 1, uploadedFile);
//                    artifacts.add(art);   
                    
                    JSONObject joNewCollArtifactRelation = new JSONObject();
                    
                    // adding new collection artifact relation to collectionartifactrelations
                    int sequenceCAR;
                    if (collectionartifactrelations.size() == 0) {
                        sequenceCAR = 1;
                    } else {
                        JSONObject jsonObject = (JSONObject) collectionartifactrelations.get(collectionartifactrelations.size()-1);
                        sequenceCAR = Integer.parseInt(jsonObject.get("sequence").toString()) + 1;                
                    }                
                    
                    joNewCollArtifactRelation.put("sequence", sequenceCAR);
                    joNewCollArtifactRelation.put("key", currentCollKey);
                    joNewCollArtifactRelation.put("artiname", fileName);
                    joNewCollArtifactRelation.put("originalkeypath", keypath);
                    joNewCollArtifactRelation.put("originalnamepath", namepath);
                    joNewCollArtifactRelation.put("createtime", createtime);
                    joNewCollArtifactRelation.put("tobedeleted", false);
                    
                    collectionartifactrelations.add(joNewCollArtifactRelation);
                    
                    Thread task = new Thread()
                    {
                        @Override
                        public void run()
                        {
                            fio.writeFile(RestConstants.FILECOLLECTIONARTIFACTRELATIONS, collectionartifactrelations);
                        }
                    };
                    task.start();                    
                }


		return result;
	}
	
	/**
	 * updates an artifact
	 * 
	 * @param InputStream, FileName
	 * @return boolean
	 */
	public boolean updateArtifact(InputStream is, String fileName) {
		boolean ok = false;
		String filePath = RestConstants.PATH_ONE+"/"+fileName;
		System.out.println("================================node 1=========================");
		File updatedDir = new File(RestConstants.PATH_ONE);
		File updatedFile = new File(filePath);
		try {
			if (!updatedDir.exists()) {
				updatedDir.mkdir();
			}
			if (!updatedFile.exists()) {
				System.err.println("No such file!!");
				return ok;
			} else {
				updatedFile.delete();
				updatedFile.createNewFile();
			}
			OutputStream os = new FileOutputStream(updatedFile);
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = is.read(bytes)) != -1) {
				os.write(bytes);
				bytes = new byte[1024];
			}
			os.flush();
			os.close();
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (updatedFile.exists() && updatedFile.length() > 0) {
			ok = true;
		}

		search(fileName).setContent(updatedFile);
		return ok;
	}


	/**
	 * deletes and artifact where the id is given
	 * 
	 * @param name
	 * @return
	 */
	public void deleteArtifact(String name) {
            Artifact searchResult = search(name);
            Integer id = searchResult.getId();
            searchResult.getContent().delete();
            artifacts.remove(searchResult);
	}
        
        
        /*
        *   author Nils
        */
        
        public CollectionContent getCurrentCollectionContent() {

            CollectionContent cl = new CollectionContent();
            String[] collnames = currentCollections.get("keys").toString().split("/");
            String currentkey = collnames[collnames.length-1];
            
            // root collection list
            if (currentkey.equals("root")) {
                for (int index=0; index < collections.size(); index++) {
                    JSONObject collection = (JSONObject) collections.get(index);
                    if (collection.get("root").toString().equals("true")) {
                        Record record = new Record();
                        record.setCollectionFlag(true);
                        record.setKey(collection.get("key").toString());
                        record.setName(collection.get("name").toString());
                        cl.addRecordList(record);
                    }       
                }                

            } else {
                // sub collection list
                String parentkey = currentkey;
                for (int index=0; index < collections.size(); index++) {
                    JSONObject collection = (JSONObject) collections.get(index);
                    if (collection.get("root").toString().equals("false") && collection.get("parentkey").toString().equals(parentkey)) {
                        Record record = new Record();
                        record.setCollectionFlag(true);
                        record.setKey(collection.get("key").toString());
                        record.setName(collection.get("name").toString());
                        cl.addRecordList(record);
                    }       
                }                  
            }
            
            // add artifact list
            for (int index=0; index < collectionartifactrelations.size(); index++) {
                JSONObject jocam = (JSONObject) collectionartifactrelations.get(index);
                if (jocam.get("key").equals(currentkey)) {
                    Record record = new Record();
                    record.setCollectionFlag(false);
                    record.setKey(jocam.get("key").toString());
                    record.setName(jocam.get("artiname").toString());
                    cl.addRecordList(record);                    
                }
            }
            
            return cl;
        }
        
        // Certain collection
        public void setCurrentCollection(String key, String name) {
            if (key.equals("root")) {
                currentCollections.put("keys", "/root");
                currentCollections.put("names", "/root");
            } else {
                
                String parentkey = key;
                String thiskey = key;
                String keys = "/" + key;
                String names = "/" + name;           
                
                // collection table, where the collections are orignial
                // mapped collection, using key, could be linked to original collection
                // find collection path to the root for getting keys path and names path
                while (!parentkey.equals("root")) { 
                    for (int index=0; index < collections.size(); index++) {
                        JSONObject jc = (JSONObject) collections.get(index);
                        if (jc.get("key").toString().equals(thiskey)) {
                            parentkey = jc.get("parentkey").toString();
                            keys = "/" + parentkey + keys;
                            names = "/" + jc.get("parentname").toString() + names;
                            thiskey = parentkey;
                            break;
                        }
                    }
                }
                
                currentCollections.put("keys", keys);
                currentCollections.put("names", names);
                
            }

        }
        
        // return to up collection
        public void setBackCurrentCollection() {
            String keys = currentCollections.get("keys").toString();
            String names = currentCollections.get("names").toString();
            
            // "/root" is splitted into 2 strings which are "" and "root"            
            String newkeys = "";
            String keystr[] = keys.split("/");
            if (keystr.length >= 3) {
                
                // not add last string of keystr[]
                for (int index=0; index<keystr.length-1; index++) {
                    if (!keystr[index].equals("")) 
                        newkeys = newkeys + "/" + keystr[index];
                    else 
                        continue;
                }                
            } else {
                newkeys = "/root";
            }

            // "/root" is splitted into 2 strings which are "" and "root"   
            String newnames = "";
            String namestr[] = names.split("/");
            if (namestr.length >= 3) {
                for (int index=0; index<namestr.length-1; index++) {
                    if (!namestr[index].equals("")) 
                        newnames = newnames + "/" + namestr[index];
                    else 
                        continue;
                }                
            } else {
                newnames = "/root";
            }

            currentCollections.put("keys", newkeys);
            currentCollections.put("names", newnames);
            
        }
        
        public boolean createCollection (String collname) {
            
            String[] collkeys = currentCollections.get("keys").toString().split("/");
            String[] collnames = currentCollections.get("names").toString().split("/");
            String currentCollKey = collkeys[collkeys.length-1];
            String currentCollName = collnames[collnames.length-1];
            
            // Tell if there is a collection that has the same name in the current collection
            boolean checkflag = false;
            // check original table
            for (int index=0; index<collections.size(); index++) {
                JSONObject jc = (JSONObject) collections.get(index);
                if (jc.get("parentkey").toString().equals(currentCollKey) && jc.get("name").toString().equals(collname)) {
                    checkflag = true;
                    break;
                }
            }
            if (!checkflag) {
                // check mapping table
                for (int index=0; index<collectionmappings.size(); index++) {
                    JSONObject jcm = (JSONObject) collectionmappings.get(index);
                    if (jcm.get("parentkey").toString().equals(currentCollKey) && jcm.get("name").toString().equals(collname)) {
                        checkflag = true;
                        break;
                    }
                }                
            }
            
            if (checkflag) {
                return false;
            } else {
                // time related data
                String key = Long.toString(System.currentTimeMillis(), 36).toString();
                Date now = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String createtime = dateFormat.format(now);

                // adding new collection to collections
                int sequenceC;
                if (collections.size() == 0) {
                    sequenceC = 1;
                } else {
                    JSONObject jsonObject = (JSONObject) collections.get(collections.size()-1);
                    sequenceC = Integer.parseInt(jsonObject.get("sequence").toString()) + 1;                
                }

                JSONObject jsonObjectNewColl = new JSONObject();
                jsonObjectNewColl.put("sequence", sequenceC);     
                jsonObjectNewColl.put("key", key);
                jsonObjectNewColl.put("name", collname);
                jsonObjectNewColl.put("createtime", createtime);
                jsonObjectNewColl.put("tobedeleted", false);

                if (currentCollKey.equals("root")) {
                    // adding new root collection to collections
                    jsonObjectNewColl.put("root", true);
                    jsonObjectNewColl.put("parentkey", "root");
                    jsonObjectNewColl.put("parentname", "root");
                } else {
                    // adding new sub collection to collections
                    jsonObjectNewColl.put("root", false);    
                    jsonObjectNewColl.put("parentkey", currentCollKey);    
                    jsonObjectNewColl.put("parentname", currentCollName); 
                }
                collections.add(jsonObjectNewColl); 

                Thread task = new Thread()
                {
                    @Override
                    public void run()
                    {
                        fio.writeFile(RestConstants.FILECOLLECTIONS, collections);
                    }
                };
                task.start();

                return true;                
            }
            

        }
	
}
