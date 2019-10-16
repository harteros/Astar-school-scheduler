
/*
 * Copyright (c) 2019, Lefteris Harteros, All rights reserved.
 *
 */

package school_schedule;

public class Teacher {

	private String teacher_id;//id of the teacher
	private String teacher_name;//name of the teacher
	private Lesson[] lessons;//lessons that the teacher teaches
	private int max_hours_per_day;//maximum hours a teacher can teach per day
	private int max_hours_per_week;//maximum hours a teacher can teach per week
	private int hours_teached;//hours that the teacher have already teached
	
	public Teacher(String id, String name, Lesson[] lessons ,int max_day,int max_week){
		this.teacher_id=id;
		this.teacher_name=name;
		this.lessons=lessons;
		if(max_day<=0 || max_day>7){
			System.out.println("Error. Wrong number of maximum hours per day for teacher with id : "+id);
			System.exit(1);
		}else{
			this.max_hours_per_day=max_day;
		}
		if(max_week<=0 || max_day*Schedule.DAY<max_week){
			System.out.println("Error. Number of maximum hours per week exceed the maximum hours\n he can teach based on max hours per day for teacher with id : "+id);
			System.exit(1);
		}else{
			this.max_hours_per_week=max_week;
		}		
		this.hours_teached=0;
	}
	
	public Teacher(Teacher teacher){
		this.teacher_id=teacher.teacher_id;
		this.teacher_name=teacher.teacher_name;
		this.lessons=teacher.lessons;
		this.max_hours_per_day=teacher.max_hours_per_day;
		this.max_hours_per_week=teacher.max_hours_per_week;
		this.hours_teached=0;
	}
	
	public Teacher(){
		this.teacher_id="";
		this.teacher_name="";
		this.lessons=null;
		this.max_hours_per_day=-1;
		this.max_hours_per_week=-1;
		this.hours_teached=-1;
	}
	
	public String getTeacherID(){
		return teacher_id;
	}
	
	public String getTeacherName(){
		return teacher_name;
	}
	
	public Lesson[] getLessons(){
		return lessons;
	}
	
	public int getMaxHoursPerDay(){
		return max_hours_per_day;
	}
	
	public int getMaxHoursPerWeek(){
		return max_hours_per_week;
	}
	
	public void decreaseMaxHoursPerWeek(){
		this.max_hours_per_week--;
	}
	
	public int getHoursTeached(){
		return hours_teached;
	}
	
	public void increaseHoursTeached(){
		this.hours_teached++;
	}
	
}
