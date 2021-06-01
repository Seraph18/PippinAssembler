# Project Part 2 - Enhancing Pippin

We have finally come to the final phase of the final project! You will work on the second phase of the project with the same teams you used in the first phase.

## Cloning your Repository Using Eclipse

To clone your repository using Eclipse, seen the Class Web Page, [Using Eclipse - Cloning a Repository](http://www.cs.binghamton.edu/~tbartens/CS140_Spring_2021/howto/usingEclipse.html#gitClone).

## Overall Process

The proj02 repository currently consists of almost the exact same code that you started of with for proj01. The only changes are some minor fixes to bugs discovered while working on proj01. 

Since not all teams have finished proj01, the initial version of Assembler.java in the proj02 repository doesn't work. You may copy your proj01 version of Assembler.java into proj02, or you may contact the Professor and get his version of Assembler.java (if you promise not to share it outside of your team.)

Your job for the final project is to: 

1. Run the Pippin GUI code (the top level is the PippinGUI class in proj02/view.) 

    - Try the "Load" button to select a .pexe file to load into that job region.
    - Try the "Step" button at the bottom to single step through the code.
    - Try the "Run/Pause" button to auto-step through the code. The slider bar at the side controls the speed of the auto-step.
    - Try the "Execute" button to run the program to completion before re-displaying.
    - Try the "Reload" button to re-initializes the current Pippin program.
    - Look at the bottom line that shows the state of the CPU.
    - Once you have a working Asssemble.java in proj02, try the "Assemble..." button that invokes your assemble method.
    - Try loading different programs in different jobs and running them concurrently.
  
2. Look at the code in the "proj02/view" package that implements the GUI

    - I had to make a couple of tweaks in the proj02/model package, but mostly, it's exactly what we've been using for proj01.
    - See how the various components are implemented.
  
3. Choose something that you would like to enhance in the Pippin GUI code

    - Is there anything that behaves incorrectly that you can fix?
    - What about enhancing the code? See some suggestions below, or come up with your own idea.
 
4. Make the enhancement or fix.

    - Actually change the code
    - Test your changes to make sure they work and don't break anything else.
    
5. Document your enhancement of fix in the enhancment.txt file. This should describe:

    - What your enhancement or fix is.
    - Why that enhancement or fix is important to improve or fix the Pippin GUI.
    - What parts of the Pippin GUI are affected by your enhancement or fix.
    - Any suggestions for more modifications or fixes that you didn't have time to work on.
  
6. Commit and push your changes into the team proj02 repository, and one team member, copy the hash code into the proj02 submission area on myCourses.
  
Then, you are done for the semester!

## Some possible enhancements

### Add Halted Flag to Processor View

Specify whether the CPU halted flag is true or false. This might be color coded, green if false, and red if true. 

Note that this is a very simple enhancement, not sufficiently complicated or useful enough to earn many points. Do this one as a warm-up along with another enhancement to earn full points.

### Modify Data in the Data View panel and ProcessorView panel

The data view panel displays what is in the Pippin Memory at the specified locations. You can edit the data and change it to something different, but currently, that doesn't really change the underlying data in Pippin Memory. Come up with a way of allowing users to change the data in the data panel and make those changes in the underlying Pippin Memory as well.

What about the CPU as well? Is it possible to allow the user to change the accumulator or the instructionPointer or the dataMemoryBase register values?

### Specify Data Size in .pasm and .pexe

Provide a methodology to request a specific data size in a .pasm file. If no explicit request is found, update the assembler to request as much data is it has reserved for variables or direct memory access.

Add data size information to the Program class, and to the .pexe file format. Make sure the Program class methods that read and write from .pexe files use this new value.

Use the data request size in the program itself when loading a Job into Pippin instead of requesting a size from the user. In other words, remove the fourth parameter from the Job constructor, and use the Program information in the CPU.loadJob method.

### Provide A Time-Slicing Version of Auto-Execute

When we were running the Pippin simulator command line in lab08, we had a runJobs method in the CPU class (now moved to the Pippin class) which executed a time slice for each job based on the parameter which specified the number of instructions to execute.

With the Pippin GUI, we can switch jobs manually by changing Job tabs, so time slicing doesn't make much sense when we are single stepping through the code using the Step button, but it would be nice to run multiple jobs via time-slicing when in auto-execute mode. Make a Run Jobs/Pause button which runs a time slice on one job, then automatically moves to the next job tab and runs a time slice on that job, and so on, continuing until all jobs are halted, or until the Run Jobs/Pause button is pushed again.

If any of the jobs halt, auto-run and time-slicing should stop. If there is a halted job in the list of jobs, then time-slicing should skip over that job. 

If the current job is halted, the Run/Pause Sliced button should be disabled. Otherwise, Run/Pause Sliced should be enabled.

One hint, the JTabbedPane `setSelectedIndex` method will automatically invoke anything registered as a change listener for the tabbed pane.

### Make the Remove Button work

When a job is loaded, there is a "Remove" button that currently doesn't do anything. The "Remove" button should clear the current job, remove the job tab, and return the memory used by the job to the Pippin model without breaking anything else.

The Pippin model currently uses a very very simple memory management algorithm... Start at 0, give each job requested as much memory as it needs, and make the next available memory at the end of the most recently requested job. This algorithm assumes that a job will own its memory forever, or at least until Pippin itself is shut down.

If we can remove jobs, now we need a more sophisticated memory management algorithm, because the removed job might be in the middle of used memory. Now we have to deal with fragmented memory. In the worst case, there may be enough free memory so that we could add another job, but if that free memory is not contiguous, we can't add a new job. (Note that it should be possible to put code memory in a separate block than data memory.) Can you come up with a memory management algorithm that optimizes the use of memory? In other words, is as flexible as possible in terms of adding new jobs, and prevents memory fragmentation as much as possible?

You may learn more about this kind of problem when you study Operating Systems' "heap memory management" algorithms.

### Display Symbols with the Instructions

Your assembler reads symbols, and converts them to binary instructions that no longer reference the symbols. When we show the code in the Code Display Panel, we can no longer show the symbols.

This enhancement consists of keeping the symbols and the location those symbols refer to with the program. That means you need to update the Program class to handle the symbol table, and you need to add reading and writing the symbols to the PEXE file.

Finally, you need to modify the displayed code to display labels instead of numeric values. If possible, do this for both data labels and code labels.

### Write a sophisticated Pippin Program

See if you can write a sophisticated program in the Pippin assembler language. For instance, can you write a program to perform the QuickSort algorithm on an array of an arbitrary number integer values that are pre-loaded into memory? This doesn't need GUI programming, so see if you can also come up with some GUI enhancement that makes it easier to understand how your algorithm works, you will earn more credit.

### Improve the Help

Currently the "help" capability consists of a panel of text... boring and hard to understand. How can the help be improved? For instance, give the user a list of topics and let them select what topic they want to look at. Or use ToolTips on the buttons and displays to give help instead of the help panel. 

What about a tutorial mode, where you show the user which buttons to push to do the next step in a standard tutorial scenario?

### Enable Characters in Pippin Data

Currently, the Pippin memory and instructions all deal with integer data. It's not too hard to translate 4 ASCII characters into a single integer, or a single integer back to 4 ASCII characters. If we do so, we can load and store character data.

Add a PRN (print) operation to Pippin Assembler, that takes an immediate, direct, or indirect argument, resolves that argument, then converts the revolved value into four ASCII characters, and "prints" them. When running outside the GUI, the characters should be written to the console. Inside the GUI, add a console display to the GUI that gets updated with the values printed by each PRN operation. There should be a separate console for each job so that the output of one job does not corrupt the output of another job.

You might also want a PRD (print decimal) operation, that resolves it's argument, and then converts that argument to an ASCII string that has the value of the number. For instance, if the argument resolves to 124, then print "124".

Add prints to the programs like gcd and factorial to print out the results of the computation.

  
## Submitting your Project

Only one member of the team needs to submit. The grade will be shared by all team members.
  
Get your hash code with: 

`git rev-parse HEAD`
  
or by using the Eclipse Git perspective. Copy the paste the hash code myCourses, CS140, Content, Project Submissions, proj02.

## Grading Criteria

This assignment is worth a total of 30 points. Those 30 points are broken down as follows:

- +10 for the utility of the enhancement you have implemented. The more useful your enhancements are to an end user, the more points you get. Most of the suggested enhancements above will earn the full 10 points for usefulness, except for the very simple ones like adding a haltedd flag.

- +10 points for the complexity of your enhancements, specifically, how much your changes demonstrate that you have learned about how to write Swing GUI code. If your enhancement uses Swing features that aren't already in the Pippin GUI, you will get more points on this criteria.

- +10 points if your implementation of the enhancement does everything it is supposed to do, works correctly, is documented in the enhancement.txt file, and does not break any other features in the Pippin GUI.

Warning: Since the deadline for this project is on the very last day of the semester, no extensions will be possible.
