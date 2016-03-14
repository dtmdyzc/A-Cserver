package com.lnu.sc.entities;

import java.io.File;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * this class demonstrates what properties an artifact has
 * 
 * @author Bang
 *
 */
@XmlRootElement
public class Artifact {
    	private Integer id; // artifact id in repository
	private File content;// the artifact it self regardless the format
	private String path;
	private String name;
	/**
	 * no-argument constructor
	 */
	public Artifact() {

	}

	/**
	 * artifact model constructor with arguments
	 * 
	 * @param id
	 * @param file
	 */
	public Artifact(Integer id, File file) {
		super();
		this.id = id;
		this.content = file;
		this.path = file.getAbsolutePath();
		this.name = this.content.getName();
	}

	/**
	 * returns artifact id in repository
	 * 
	 * @return  id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * sets artifact id
	 * 
	 * @param id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * returns the artifact itself
	 * 
	 * @return artifact artifact
	 */
	public File getContent() {
		return content;
	}

	/**
	 * sets artifact itself
	 * 
	 * @param content
	 */
	public void setContent(File content) {
		this.content = content;
	}
	
	public String getPath(){
		return path;
	}
	
	public String getName(){
		return name;
	}
	
	public static String getFileNameNoEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}
}
