/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lnu.sc.entities;

import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;

/**
 *
 * @author Nils
 */
public class CollectionContent {
//    private CurrentCollection currentCollection;
    private List<Record> recordList;
    
    // getter and setter are very import to deliver json data to frontend
    
    public CollectionContent() {
        recordList = new ArrayList<Record>();
    }
    
//    public void setCurrentCollection(CurrentCollection currentCollection) {
//        this.currentCollection = currentCollection;
//    }
//    
//    public CurrentCollection getCurrentCollection() {
//        return this.currentCollection;
//    }
    
    public void setSecordList(List<Record> recordList) {
        this.recordList = recordList;
    }
    
    public List<Record> getRecordList() {
        return this.recordList;
    }
    
    public void addRecordList(Record record) {
        recordList.add(record);
    }
    
    
}
