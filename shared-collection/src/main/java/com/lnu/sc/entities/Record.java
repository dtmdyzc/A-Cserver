/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lnu.sc.entities;

/**
 *
 * @author Nils
 */
public class Record {
    private String name;
    private String key;
    private boolean collectionFlag;
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
    
    public void setCollectionFlag(boolean collectionFlag) {
        this.collectionFlag = collectionFlag;
    }

    public boolean getCollectionFlag() {
        return this.collectionFlag;
    }
    
    
    
}
