
/*
 * Copyright (c) 2019, Lefteris Harteros, All rights reserved.
 *
 */

package school_schedule;

public class Lecture {
	
    private Lesson lesson;
    private Teacher teacher;
    //a Lecture is an object that represents a combination of (lesson,teacher) which will fit in each spot of the schedule array
    public Lecture(Lesson lesson, Teacher teacher){
        this.lesson=lesson;
        this.teacher=teacher;
    }

    public Lecture(Lecture lecture){
        this.lesson=lecture.lesson;
        this.teacher=lecture.teacher;
    }

    public Lecture(){
        this.lesson=new Lesson();
        this.teacher=new Teacher();
    }

    public Lesson getLesson(){
    	return lesson;
    }
    
    public Teacher getTeacher(){
    	return teacher;
    }
    
}
