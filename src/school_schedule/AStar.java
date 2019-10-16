
/*
 * Copyright (c) 2019, Lefteris Harteros, All rights reserved.
 *
 */

package school_schedule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
public class AStar {

	private ArrayList<Schedule> schedule;
	private HashSet<Schedule> closedSet;
	
	AStar(){
		this.schedule = null;
		this.closedSet = null;
	}
	/*this method gets as input : lessons = file with the lessons , teachers = file with the teachers ,
	 * number_of_classes = number of classes each grade will have 
	 * If the input is correct this method returns a schedule that satisfies all the hard constraints
	 * and most of the soft constraints
	 */
	public Schedule createProgram(String lessons,String teachers,int number_of_classes) throws IOException
	{
		if(number_of_classes<=0){
			System.out.println("Error. Number of classes per grade should be higher than 0.");
    		System.out.println("Program will now exit");
    		System.exit(1);
		}
		Schedule.NUMBER_OF_CLASSES_PER_GRADE=number_of_classes;//initialize the number of classes that each grade will have
		Schedule init=new Schedule(lessons,teachers);//creates initial random schedule 
	
		this.schedule = new ArrayList<Schedule>();
		this.closedSet = new HashSet<Schedule>();
		this.schedule.add(init);
		while(this.schedule.size() >0){
			
			Schedule currentState = this.schedule.remove(0);
			currentState.print();
			if(currentState.isTerminal())
			{
				return currentState;
			}
			
			if(!closedSet.contains(currentState)){
	            //generates the children and calculates their score
				this.schedule.addAll(currentState.getChildren());
				this.closedSet.add(currentState);
	            //sorts all the children according to their score and their depth in the tree we are searching
				Collections.sort(this.schedule);
			}
		}
		return null;
	}
}
