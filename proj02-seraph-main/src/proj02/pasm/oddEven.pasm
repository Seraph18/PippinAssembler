# Determine whether the value in a is odd or even 
#
# Algorithm:
#     acc = ((a/2) *2) - a
#     if (acc==0) acc=1
#     else acc=0
LOD a 	
DIV 2 	
MUL 2 	
SUB a 	
STO b 	
CMZ b 	
HLT
---data---
a = 397
