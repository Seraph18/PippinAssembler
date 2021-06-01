# computes the factorial of the value in a
# Algorithm...
#    ans=1
#    while(a>0) {
#	ans = ans * a
#       a--
#    }

	LOD 1
	STO ans
while:
	CMZ a
	NOT
	JMZ finished
	LOD ans
	MUL a
	STO ans
	LOD a
	SUB 1
	STO a
	JMP while
finished:
	LOD ans
	HLT

---data---
a = 5