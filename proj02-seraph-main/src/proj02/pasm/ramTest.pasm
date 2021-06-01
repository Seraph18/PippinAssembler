# A program to test the RAM memory chip of our Pippin computer
# The program goes through several tests...
#   First, writes all ones to 100 spaces in RAM, and reads all spaces
#   Then writes all zeroes to 100 spaces, and reads them
#

# REQUIRES a data size of at leasat 110 words.

# The Java pseudocode is as follows:
# i=base
# top=base+100
# while(i<top) { mem[i]=0; i++ }
# i=base
# while(i<top) { if (0!=mem[i]) { Acc=0; halt; } }
# i=base
# while(i<top) { mem[i]=-1; i++ } // -1 is 0xFFFFFFFFFF
# i=base
# while(i<top) { if (-1 != mem[i]) { Acc=0; halt; } }

	LOD base
	STO i
	ADD 100
	STO top
loop1:	LOD i
	SUB top
	STO temp
	CML temp
	JMZ init2
	LOD 0
	STO @i
	LOD i
	ADD 1
	STO i
	JMP loop1
init2:	LOD base
	STO i
loop2:	LOD i
	SUB top
	STO temp
	CML temp
	JMZ init3
	CMZ @i
	JMZ fail
	LOD i
	ADD 1
	STO i
	JMP loop2
init3:	LOD base
	STO i
loop3:	LOD i
	SUB top
	STO temp
	CML temp
	JMZ init4
	LOD -1
	STO @i
	LOD i
	ADD 1
	STO i
	JMP loop3
init4:	LOD base
	STO i
loop4:	LOD i
	SUB top
	STO temp
	CML temp
	JMZ finish
	LOD @i
	ADD 1
	STO temp
	CMZ temp
	JMZ fail
	LOD i
	ADD 1
	STO i
	JMP loop4
finish: LOD 1
	HLT
fail:	LOD 0
	HLT
	
---data---
   base = 10
   