
/*
 * Copyright (c) 2019, Lefteris Harteros, All rights reserved.
 *
 */

package school_schedule;

public class Classroom {

	private String grade;//grade where lesson is teached
	private int hours_per_week;//hours that the lesson is teached in the specific grade
		
	public Classroom(String grade,int hours){
		this.grade=grade;
		this.hours_per_week=hours;	
	}
	
	public Classroom(Classroom room){
		this.grade=room.grade;
		this.hours_per_week=room.hours_per_week;						
	}
	
	public String getGrade(){
		return grade;
	}
	
	public int getHours(){
		return hours_per_week;
	}

}
