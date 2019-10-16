
/*
 * Copyright (c) 2019, Lefteris Harteros, All rights reserved.
 *
 */

package school_schedule;

public class Lesson {

	private String lesson_id; //id of the lesson 
	private String lesson_name; //name of the lesson
	private Classroom[] classes; //grades where the lesson is teached
	
	public Lesson(String id, String name,Classroom[] classes){
		this.lesson_id=id;
		this.lesson_name=name;
		this.classes=classes;
	}
	
	public Lesson(Lesson lesson){
		this.lesson_id=lesson.lesson_id;
		this.lesson_name=lesson.lesson_name;
		this.classes=lesson.classes;
	}
	
	public Lesson(){
		this.lesson_id="";
		lesson_name="";
		classes=null;
	}
	
	public String getLessonID(){
		return lesson_id;
	}
	
	public String getLessonName(){
		return lesson_name;
	}
	
	public Classroom[] getClasses(){
		return classes;
	}
	
}
