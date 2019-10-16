
/*
 * Copyright (c) 2019, Lefteris Harteros, All rights reserved.
 *
 */

package school_schedule;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class Schedule implements Comparable<Schedule> {

	//variables that represent the dimensions of the array we will use for the schedule
	public static final int GRADES=3;//the depth of the 3D-array 
	public static final int DAY=5;//the columns of the 3D-array
	public static final int TIME=7;//the rows of the 3D-array
	public static int NUMBER_OF_CLASSES_PER_GRADE=1;//default value for number of classes per grade,
													//it is modified on the start of the algorithm based on the number of classes we want

	private int depth;//variable that defines the depth at which the child schedule is found
	private int score;//score of the schedule
	public Lecture[][][]  schedule;//the schedule
	private int[] firstRow;//array that represents for each dimension (depth of the array) the first row where a constraint was found
	private int[] firstColumn;//array that represents for each dimension (depth of the array) the first column where a constraint was found    

	private static final ReadLessons lessons = new ReadLessons(); 
	private static final ReadTeachers teachers = new ReadTeachers();

	public Schedule(String lessonfile,String teacherfile) throws FileNotFoundException, IOException{
		this.depth=0;
		this.score=0;
		this.schedule=new Lecture[GRADES*NUMBER_OF_CLASSES_PER_GRADE][DAY][TIME];
		this.firstRow=new int[GRADES*NUMBER_OF_CLASSES_PER_GRADE];
		this.firstColumn=new int[GRADES*NUMBER_OF_CLASSES_PER_GRADE];

		lessons.setData(lessonfile);
		teachers.setData(teacherfile,lessons);
		String grade="0";
		for (int i=0; i<GRADES*NUMBER_OF_CLASSES_PER_GRADE; i++){
			//for each dimension we initialize the 3d array so that we dont have null values
			for(int j=0; j<DAY; j++){
				for(int k=0; k<TIME; k++){
					this.schedule[i][j][k]=new Lecture();
				}
			}
			//calculate the grade based of the number of classes per grade
			if (i<1*NUMBER_OF_CLASSES_PER_GRADE){
				grade="A";		
			}else if(i<2*NUMBER_OF_CLASSES_PER_GRADE){
				grade="B";
			}else{
				grade="C";
			}
			
			for(int index=0; index<lessons.size(); index++){ //for each lesson
				Classroom[] classes =lessons.get(index).getClasses(); 
				for(int c=0; c<classes.length; c++){//for all the grades it is teached

					if(classes[c].getGrade().equals(grade)){//if the lesson is teached at the specific grade 

						boolean found=false;//variable to check if a teacher was found to teach the subject
						Teacher subject_teacher=new Teacher();
						for(int hours=0; hours<classes[c].getHours(); hours++){//for the hours that the subject needs to be taught

							if(!found){//if a teacher wasnt found, find one
								subject_teacher=getTeachersWithSameSubject(index);
								found=true;
							}
							subject_teacher.increaseHoursTeached(); 
							subject_teacher.decreaseMaxHoursPerWeek();
							if(subject_teacher.getMaxHoursPerWeek()==0) {//if the teacher reached the maximum hours he can teach a week,
								subject_teacher=getTeachersWithSameSubject(index); //find a new teacher                           
							}
							//allocate to a random spot the lecture 
							Random rnd = new Random(System.currentTimeMillis());                                  
							boolean good=false;
							while (!good) {

								int a = rnd.nextInt(TIME );
								int b = rnd.nextInt(DAY );
								if (schedule[i][b][a].getLesson().getLessonName().equals("")) {
									schedule[i][b][a] = new Lecture(lessons.get(index),subject_teacher);
									good=true;
								}
							} 
						}                                   			
					}
				}                      
			}
		}//grade
		this.heuristic();
	}
	//copy constructor
	public Schedule(Schedule schedule){

		this.depth=schedule.depth+1;
		this.score=0;
		this.schedule=new Lecture[GRADES*NUMBER_OF_CLASSES_PER_GRADE][DAY][TIME];
		this.firstRow=new int[GRADES*NUMBER_OF_CLASSES_PER_GRADE];
		this.firstColumn=new int[GRADES*NUMBER_OF_CLASSES_PER_GRADE];

		for (int i=0; i<GRADES*NUMBER_OF_CLASSES_PER_GRADE; i++){
			for(int j=0; j<DAY; j++){
				for(int k=0; k<TIME; k++){
					this.schedule[i][j][k]=schedule.schedule[i][j][k];
				}
			}
		}
	}
	//returns the 3D array with the schedule
	public  Lecture[][][] getSchedule() {
		return schedule;
	}
	//returns the score of the schedule
	public int getScore(){
		return score;
	}
	//returns the teacher (with the least hours teached) that teaches a specific subject
	public Teacher getTeachersWithSameSubject(int index){
		ArrayList<Teacher> teach=new ArrayList<Teacher>();
		for(int prof=0; prof<teachers.size(); prof++){//for all the teachers get those who teach the specific subject
			Lesson[] subjects = teachers.get(prof).getLessons();
			for(int m=0; m<subjects.length; m++){
				if(subjects[m].getLessonID().equals(lessons.get(index).getLessonID()) && teachers.get(prof).getMaxHoursPerWeek()!=0){
					teach.add(teachers.get(prof));
					break;
				}
			}

		}
		int id=-1;
		if(!teach.isEmpty()){//if there are teachers for the subject
			int min=1000;			
			for(int i=0; i<teach.size(); i++){//give the subject to the teacher who have teached the least hours from those who teach the subject		
				if(teach.get(i).getHoursTeached()<min){
					min=teach.get(i).getHoursTeached();
					id=i;
				}
			}
		}else{ //if no teacher exists for the subject
			System.out.println("Error. No more available teachers.");
			System.out.println("Program will now exit");
			System.exit(1);
		}
		return teach.get(id);
	}


	public void heuristic(){

		Map<String, Integer> map = new HashMap<String, Integer>();
		String grade="0";
		for(int i=0; i<GRADES*NUMBER_OF_CLASSES_PER_GRADE; i++){
			boolean first=false;
			if (i<1*NUMBER_OF_CLASSES_PER_GRADE){
				grade="A";		
			}else if(i<2*NUMBER_OF_CLASSES_PER_GRADE){
				grade="B";
			}else{
				grade="C";
			}
			for (int j = 0; j < DAY; j++) {
				//int a=-1;
				for (int k = 0; k < TIME; k++) {

					//1.hard constraint-------------------same teacher multiple classes----------------------
					for(int iplus=i+1; iplus<GRADES*NUMBER_OF_CLASSES_PER_GRADE; iplus++){                	
						if((schedule[i][j][k].getTeacher().getTeacherName().equals(schedule[iplus][j][k].getTeacher().getTeacherName()) && !schedule[i][j][k].getTeacher().getTeacherName().equals(""))){
							score+=1500;
							if(!first){
								firstRow[i]=j;
								firstColumn[i]=k;
								first=true;                				
							}              				
						}                   	
					}
					//-------------------end----------------------

					//2,3.hard constraint-------------------teacher more than max hours per day or else more than 2 hours continuously----------------------               	
					boolean exists=false;

					//check if slot is empty
					if(!schedule[i][j][k].getTeacher().getTeacherName().equals("")){

						//checks if teacher was examined
						for(int s=0; s<map.size(); s++){                		
							if (map.get(schedule[i][j][k].getTeacher().getTeacherID())!=null){
								if (map.get(schedule[i][j][k].getTeacher().getTeacherID())==j){
									exists=true;
									break;
								}                			
							}	
						}//end check for examined teacher                 	

						//if teacher was not examined
						if(!exists){
							ArrayList<Integer> hours_in_a_row=new ArrayList<Integer>();
							int counter=0;
							map.put(schedule[i][j][k].getTeacher().getTeacherID(),j);
							for(int w=0; w<GRADES*NUMBER_OF_CLASSES_PER_GRADE; w++){
								for(int g=0; g<TIME; g++){                			
									if( schedule[i][j][k].getTeacher().getTeacherName().equals(schedule[w][j][g].getTeacher().getTeacherName()) && !schedule[i][j][k].getTeacher().getTeacherName().equals("")){
										hours_in_a_row.add(g);
										counter++;
									}
								}
							}
							//checks if teacher exceeds max hours he can teach a day 
							if(counter>schedule[i][j][k].getTeacher().getMaxHoursPerDay()){                		
								score+=1400;
								if(!first){
									firstRow[i]=j;
									firstColumn[i]=k;
									first=true;
								}
								//checks if teacher has more than 2 hours continuously
							}else{
								Collections.sort(hours_in_a_row);
								int hours=0;
								for(int h=0; h<hours_in_a_row.size()-1; h++){
									if(hours_in_a_row.get(h)==hours_in_a_row.get(h+1)-1){
										hours++;
									}else{
										hours=0;
									}
									if(hours>=2){
										score+=8;
										if(!first){
											firstRow[i]=j;
											firstColumn[i]=k;
											first=true;
										}
									}
								}
							}//end check for continuous hours
						}//end check for unexamined teacher
					}//end check for empty slot
					//------------------end-------------------

					//4.--------same subject more than 2 hours a day----------
					int counter=0;
					for(int c=k+1; c<TIME; c++){
						if(!schedule[i][j][k].getLesson().getLessonName().equals("") && schedule[i][j][k].getLesson().getLessonName().equals(schedule[i][j][c].getLesson().getLessonName())){
							counter++;
							//a=c;
						}
					}
					if(!schedule[i][j][k].getLesson().getLessonName().equals("")){
						Classroom[] classes =schedule[i][j][k].getLesson().getClasses(); 
						for(int c=0; c<classes.length; c++){//for all the grades it is teached
							if(classes[c].getGrade().equals(grade)){
								if(classes[c].getHours()>2){
									if(counter>=2){
										score+=10;
										if(!first){
											firstRow[i]=j;
											firstColumn[i]=k;
											first=true;
										}
									}								
								}else{
									if(counter>=1){
										score+=10;
										if(!first){
											firstRow[i]=j;
											firstColumn[i]=k;
											first=true;
										}
									}
								}
								break;
							}
						}
					}
					//--------------end---------------              			
					//5.-------empty slot beetween hours or at the first hour------
					if(k<TIME-1 && schedule[i][j][k].getLesson().getLessonName().equals("")){
						score+=8;
						if(!first){
							firstRow[i]=j;
							firstColumn[i]=k;
							first=true;
						}
					}
					//----------end------------
				}//time
			}//day
		}//grades     
	}

	/*returns an array with all the children of the current schedule
	 * for each dimension the children are created by swapping the first index(x,y) that caused
	 * a constraint (first hard and then soft) with all the other spots of the dimension
	 */
	public ArrayList<Schedule> getChildren(){
		ArrayList<Schedule> children=new ArrayList<Schedule>();
		for(int index=0; index<GRADES*NUMBER_OF_CLASSES_PER_GRADE; index++){  			
			for (int k = 0; k < DAY; k++) {
				for (int j = 0; j < TIME; j++) {
					if(!(this.firstRow[index]==k && this.firstColumn[index]==j)){
						Schedule child=new Schedule(this);
						Lecture temp=child.schedule[index][this.firstRow[index]][this.firstColumn[index]];
						child.schedule[index][this.firstRow[index]][this.firstColumn[index]]=child.schedule[index][k][j];
						child.schedule[index][k][j]=temp;                
						child.heuristic();
						children.add(child);
					}
				}
			}	
		}
		return children;
	}
	
	
	public boolean isTerminal(){
		int max_search=165;
		if(this.score==0){
			System.out.println("Schedule created. All constaints fulfilled.");
			return true;
		}else{
			if(this.score<1400){				
				if(this.depth>max_search){
					System.out.println("Schedule created without fulfilling all the soft constaints");
					return true;
				}
			}
			
		}
		return false;
	}

	@Override
	public boolean equals(Object obj){

		if(this.score != ((Schedule)obj).score){
			return false;
		}		
		for(int i=0; i<GRADES*NUMBER_OF_CLASSES_PER_GRADE; i++){
			for (int j = 0; j < DAY; j++) {
				for (int k = 0; k < TIME; k++) {
					if(!(this.schedule[i][j][k].getLesson().getLessonID().equals(((Schedule)obj).schedule[i][j][k].getLesson().getLessonID()) && this.schedule[i][j][k].getTeacher().getTeacherID().equals(((Schedule)obj).schedule[i][j][k].getTeacher().getTeacherID()))){
						return false;
					}
				}
			}
		}
		return true;
	}


	@Override
	public int hashCode(){

		int hashcode = 0;
		for(int i=0; i<GRADES*NUMBER_OF_CLASSES_PER_GRADE; i++){
			for (int j = 0; j < DAY; j++) {
				for (int k = 0; k < TIME; k++) {
					hashcode += Objects.hash(this.schedule[i][j][k].getLesson().getLessonID(),this.schedule[i][j][k].getTeacher().getTeacherID());
				}
			}
		}
		return (hashcode+this.score);
	}

	@Override
	//We override the compareTo function of this class so that the sum of the heuristic score + the depth are compared
	public int compareTo(Schedule s){
		return Double.compare(this.score+this.depth, s.score+s.depth);
	}
	//prints the current schedule
	public void print(){
		String grade="0";
		for(int i=0; i<GRADES*NUMBER_OF_CLASSES_PER_GRADE; i++){
			if(i!=0){
				System.out.println();
				System.out.println();
			}       	
			if (i<1*NUMBER_OF_CLASSES_PER_GRADE){
				grade="A";		
			}else if(i<2*NUMBER_OF_CLASSES_PER_GRADE){
				grade="B";
			}else{
				grade="C";
			}
			System.out.println("-------------------------------------------------------------------------------------------------");
			System.out.format("%-45s%-51s%-1s","|",grade+((i%NUMBER_OF_CLASSES_PER_GRADE)+1), "|\n");
			for(int k=0; k<TIME; k++){
				if(k==0) {
					System.out.println("-------------------------------------------------------------------------------------------------");
					System.out.format("%-2s%-5s%-3s%-14s%-3s%-14s%-3s%-14s%-3s%-14s%-3s%-14s%-2s","|","wra\\mera ", "|","Deutera", "|","Triti", "|","Tetarti", "|","Pempti", "|","Paraskeui", "|\n");                		
				}else{
					System.out.println();
				}
				System.out.println("-------------------------------------------------------------------------------------------------");
				System.out.format("%-3s%-8s%-3s","|",(TIME+1+k)+" - "+(TIME+2+k), "|");               	            
				for(int j=0; j<DAY; j++){
					System.out.format("%-14s%-3s", schedule[i][j][k].getLesson().getLessonName(),"|");
				}	
				System.out.println();
				System.out.format("%-3s%-8s%-3s","|", "", "|");
				for(int j=0; j<DAY; j++){                        
					System.out.format("%-14s%-3s",schedule[i][j][k].getTeacher().getTeacherName()/*+schedule[i][j][k].getTeacher().getHoursTeached()*/,"|");
				}                    
			}
			System.out.println();   
			System.out.println("-------------------------------------------------------------------------------------------------");
		}
	}

	//creates a file with a specific name(filename) from the current schedule 
	public void write(String filename){

		BufferedWriter bw = null;    	
		try{
			bw = new BufferedWriter(new FileWriter(filename));
		}catch (IOException e) {
			System.err.println("Error opening file for writing!");
		}
		try {
			String grade="0";
			for(int i=0; i<GRADES*NUMBER_OF_CLASSES_PER_GRADE; i++){
				if(i!=0){
					bw.write("\n");
					bw.write("\n");
				}
				if (i<1*NUMBER_OF_CLASSES_PER_GRADE){
					grade="A";		
				}else if(i<2*NUMBER_OF_CLASSES_PER_GRADE){
					grade="B";
				}else{
					grade="C";
				}
				bw.write("-------------------------------------------------------------------------------------------------\n");
				bw.write(String.format("%-45s%-51s%-1s","|",grade+((i%NUMBER_OF_CLASSES_PER_GRADE)+1), "|\n"));
				for(int k=0; k<TIME; k++){
					if(k==0) {
						bw.write("-------------------------------------------------------------------------------------------------\n");
						bw.write(String.format("%-2s%-5s%-3s%-14s%-3s%-14s%-3s%-14s%-3s%-14s%-3s%-14s%-2s","|","wra\\mera ", "|","Deutera", "|","Triti", "|","Tetarti", "|","Pempti", "|","Paraskeui", "|\n"));

					}else{
						bw.write("\n");
					}
					bw.write("-------------------------------------------------------------------------------------------------\n");
					bw.write(String.format("%-3s%-8s%-3s","|",(TIME+1+k)+" - "+(TIME+2+k), "|"));
					for(int j=0; j<DAY; j++){
						bw.write(String.format("%-14s%-3s", schedule[i][j][k].getLesson().getLessonName(),"|"));
					}	
					bw.write("\n");
					bw.write(String.format("%-3s%-8s%-3s","|", "", "|"));
					for(int j=0; j<DAY; j++){                        
						bw.write(String.format("%-14s%-3s",schedule[i][j][k].getTeacher().getTeacherName(),"|"));
					}

				}
				bw.write("\n");   
				bw.write("-------------------------------------------------------------------------------------------------\n");

			}
			System.out.println("Schedule successfully writen to file: "+"\""+filename+"\"");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try{
			bw.close();
		}catch (IOException e) {
			System.err.println("Error closing file.");
		}
	}


}
