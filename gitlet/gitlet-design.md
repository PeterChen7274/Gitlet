# Gitlet Design Document
author: Peter Chen

## Design Document Guidelines

Please use the following format for your Gitlet design document. Your design
document should be written in markdown, a language that allows you to nicely 
format and style a text file. Organize your design document in a way that 
will make it easy for you or a course-staff member to read.  

## 1. Classes and Data Structures

Include here any class definitions. For each class list the instance
variables and static variables (if any). Include a ***brief description***
of each variable and its purpose in the class. Your explanations in
this section should be as concise as possible. Leave the full
explanation to the following sections. You may cut this section short
if you find your document is too wordy.

###Commit:
A class that represents a commit
####time: A string representation of the time the commit was made
####Message: The commit message
####Files: All of the files
####Parent: The previous commit
####Code: the commit code used for 
###Commit Tree (node):
A tree of commits
####Init: a default root node
####HEAD: The current commit node
####Master: The master branch
####split: a boolean indicator to show if a node splits
###Blobs:
The actual file
####Content: the string content of the file
###stage:
The stage that contains add files
  


## 2. Algorithms

This is where you tell us how your code works. For each class, include
a high-level description of the methods in that class. That is, do not
include a line-by-line breakdown of your code, but something you would
write in a javadoc comment above a method, ***including any edge cases
you are accounting for***. We have read the project spec too, so make
sure you do not repeat or rephrase what is stated there.  This should
be a description of how your code accomplishes what is stated in the
spec.


The length of this section depends on the complexity of the task and
the complexity of your design. However, simple explanations are
preferred. Here are some formatting tips:

* For complex tasks, like determining merge conflicts, we recommend
  that you split the task into parts. Describe your algorithm for each
  part in a separate section. Start with the simplest component and
  build up your design, one piece at a time. For example, your
  algorithms section for Merge Conflicts could have sections for:

   * Checking if a merge is necessary.
   * Determining which files (if any) have a conflict.
   * Representing the conflict in the file.
  
* Try to clearly mark titles or names of classes with white space or
  some other symbols.

###Commit:
####gets: all the get methods that return variables of commit
####change: all the change methods that edit variables of commit

###Commit tree:
####add branch: add a new branch

###Stage:
####add: add a blob to the stage
####clear: clear the stage after a commit

###Main:
####commit: make a commit, which copies previous commit files and update files that are on the stage
####checkout: return a file or the entire status back to a certain status
####status: prints out the current branches, stage, untracked files and unadded changes
####init: make a hidden gitlet folder, initiate an empty commit


## 3. Persistence

Describe your strategy for ensuring that you don’t lose the state of your program
across multiple runs. Here are some tips for writing this section:

* This section should be structured as a list of all the times you
  will need to record the state of the program or files. For each
  case, you must prove that your design ensures correct behavior. For
  example, explain how you intend to make sure that after we call
       `java gitlet.Main add wug.txt`,
  on the next execution of
       `java gitlet.Main commit -m “modify wug.txt”`, 
  the correct commit will be made.
  
* A good strategy for reasoning about persistence is to identify which
  pieces of data are needed across multiple calls to Gitlet. Then,
  prove that the data remains consistent for all future calls.
  
* This section should also include a description of your .gitlet
  directory and any files or subdirectories you intend on including
  there.
####There will be a commit folder storing all the serialized commits for checkout
####There will be a file to store all actual files in their different state

## 4. Design Diagram

Attach a picture of your design diagram illustrating the structure of your
classes and data structures. The design diagram should make it easy to 
visualize the structure and workflow of your program.

![](/Users/cte./Downloads/IMG_3302.JPG)
