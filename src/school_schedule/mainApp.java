
/*
 * Copyright (c) 2019, Lefteris Harteros, All rights reserved.
 *
 */

package school_schedule;

import java.io.IOException;

public class mainApp {

	public static void main(String[] args) throws IOException {

		AStar a= new AStar();
		System.out.println("Creating schedule ...");
		long start = System.currentTimeMillis();
		Schedule final_schedule=a.createProgram("lessons.json","teachers.json",3);
		long end = System.currentTimeMillis();
		System.out.println("Schedule created in: " + (double)(end - start) / 1000 + " sec.");
		//final_schedule.print(); //uncomment to print schedule on terminal
		final_schedule.write("schedule.txt");
		
	}
}


