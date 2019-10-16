
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

public class ReadTeachers {

	ArrayList<Teacher> professors= new ArrayList<Teacher>();//arraylist with the lessons from the json file
	
	public void setData(String filename,ReadLessons array) throws FileNotFoundException, IOException{
		if(!professors.isEmpty()) professors.clear();
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
		   Object teacher_file = parser.parse(f);//parses the file and puts it into an object
		   JSONArray teachers = (JSONArray)teacher_file;//casts object to JSON array
			//for each teacher creates a new Teacher object and adds it to the arraylist with the teachers
		   for (int i=0; i<teachers.size(); i++){
			   JSONObject teacher = getObjectFromArrayAtIndex(teachers,i);
			   JSONArray teacher_lessons = getArrayOfObjectWithId(teacher,"lesson_id");
			   Lesson[] lessons=new Lesson[teacher_lessons.size()];
			   //checks if the lessons that they teach exists in the lessons
			   for(int j=0; j<lessons.length; j++){
				   boolean found=false;
				   String id=(String)getObjectFromArrayAtIndex(teacher_lessons,j).get("id");
				   for (int k=0; k<array.size(); k++){					   
					   if(id.equals(array.get(k).getLessonID())){
						   lessons[j]=new Lesson(array.get(k));
						   found=true;
						   break;
					   }
				   }
				   if(!found){
					   System.out.println("Error at teacher with id "+(String)teacher.get("id")+". Lesson with id "+id+" does not exist");
					   System.exit(1);
				   }
			   }//end check
			   professors.add(new Teacher((String)teacher.get("id"),(String)teacher.get("name"),lessons,Integer.parseInt((String)teacher.get("max_day")),Integer.parseInt((String)teacher.get("max_week"))));
		   }
		}catch(ParseException pe){
			System.out.println("position: " + pe.getPosition());
			System.out.println(pe);
		}
		try {			
			f.close();

	    }catch (IOException e) {
	    	System.err.println("Error closing file.");
	    }
	}
	
	public int size(){
		return professors.size();
	}
	public Teacher get(int i){
		return professors.get(i);
	}	
	public static JSONObject getObjectFromArrayAtIndex(JSONArray obj,int i){
		return (JSONObject)obj.get(i);
	}
	public static JSONArray getArrayOfObjectWithId(JSONObject obj,String id){
		return (JSONArray)obj.get(id);
	}
}
