# gdc algorithm in Java pseudocode syntax

#gcd(a,b) {
#  while(b>0) {
#    if (a>b) {
#      temp=a;
#      a=b;
#      b=temp;
#    }
#    // a<=b
#    b=b-a
# }
#    gdc is in a
 
 start:
	CMZ b		# acc= b==0
  	NOT            	# acc= b!=0
  	JMZ finished 	# if (b==0) goto finished 
  	LOD a	 	# acc=a
  	SUB b	 	# acc=a-b
  	STO temp 	# temp=a-b
  	CML temp 	# if ((a-b)<0) or b>a
  	NOT            	# Invert for JMZ 
  	JMZ end-of-if	# if (b>a) goto end-of-if 
  	LOD a		# acc=a
 	STO temp	# temp=acc=a
 	LOD b		# acc=b
 	STO a  		# a=acc=b
 	LOD temp	# acc=temp
 	STO b		# b=temp
 end-of-if:  
 	LOD b		# acc=b
 	SUB a		# acc=b-a
 	STO b		# b=b-a
 	JMP start	# goto start=0,  
 finished:
 	LOD a		# Leave the answer in the accumulator
 	HLT
 ---data---
 a = 42
 b = 56