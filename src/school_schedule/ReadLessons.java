
/*
 * Copyright (c) 2019, Lefteris Harteros, All rights reserved.
 *
 */

package school_schedule;

import java.io.FileNotFoundException;
import java.io.FileReader;

import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ReadLessons {
	
	ArrayList<Lesson> lecture= new ArrayList<Lesson>();//arraylist with the lessons from the json file
	
	public void setData(String filename) throws FileNotFoundException, IOException{
		
		if(!lecture.isEmpty()) lecture.clear();		
		JSONParser parser = new JSONParser();
		FileReader f=null;

		try {
			f = new FileReader(filename);
		} catch (FileNotFoundException ex) {
			System.out.println("File "+filename+" does not exist");
			System.out.println("Program will now exit");
			System.exit(1);
		}
		try{
			Object lesson_file = parser.parse(f); //parses the file and puts it into an object
			JSONArray lessons = (JSONArray)lesson_file; //casts object to JSON array
			//for each lesson creates a new Lesson object and adds it to the arraylist with the lessons
			for (int i=0; i<lessons.size(); i++){
				JSONObject lesson = getObjectFromArrayAtIndex(lessons,i); 
		      	JSONArray classes = getArrayOfObjectWithId(lesson,"class");
		      	Classroom[] room=new Classroom[classes.size()];
		      	for(int j=0; j<room.length; j++){
		      		room[j]=new Classroom((String)getObjectFromArrayAtIndex(classes,j).get("id"),Integer.parseInt((String) getObjectFromArrayAtIndex(classes,j).get("hours")));
		        }
		        lecture.add(new Lesson((String)lesson.get("id"),(String)lesson.get("name"),room));
			}
			
		}catch(ParseException p){
			System.out.println("Error reading file at position: " + p.getPosition());
			System.out.println(p);
		}
		try {			
           f.close();

        }
        catch (IOException e) {
            System.err.println("Error closing file.");
        }
	}
	public int size(){
		return lecture.size();
	}
	public Lesson get(int i){
		return lecture.get(i);
	}
	
	//returns a JSON Object at the index i of the JSON array
	public static JSONObject getObjectFromArrayAtIndex(JSONArray obj,int i){
		return (JSONObject)obj.get(i);
	}
	//returns a JSON array from a JSON object member with a specific id
	public static JSONArray getArrayOfObjectWithId(JSONObject obj,String id){
		return (JSONArray)obj.get(id);
	}
}
