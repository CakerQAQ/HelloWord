Name: Ruiqi Wang
LSU ID: 898602592
email address: rwang26@lsu.edu
classes server login ID: cs4103an

my home contain 3 files, two input files from moodle and one java code file.
to compile and run my code:
javac *.java
java Raid5.java

my code will automatically generate 4 disk files which contain parts of information 
from the input file. (this is the write part)
it will also generate one output.txt file which is the result from these separate 
files. it used these separate files to restore the original input file
(this is the read part)
finally it generate the output2.txt file. this is I assumed the file 0 is missing,
and I used the rest three disk files to rebuild the original input file.
(this is the rebuild file)
all the disk file (disk.1, disk.2 ...) can opan and read. this is the 
separate information from input.
the output is the file read from separate files
the output2 is the file rebuilded from the separate files which file 0 is missing.