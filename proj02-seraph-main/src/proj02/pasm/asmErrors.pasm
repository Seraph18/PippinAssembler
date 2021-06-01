# Contains examples of all of the errors we expect to catch in the Assembler class

#
	CMH a # Checks to see if a is >0
	NOP a # Don't do anything on a
	AND   # And that with the accumulator
	ADD @@b # Add the indirect b value
	CMZ	# Compare that to zero
	STO @@a # And store that where a is pointing
	CML @12
	JMP	# Jump to accumulator
	JMZ @finish # If zero, jump to finish
	JMP happyplace # Go to your happy place
	
---data---

	0 = 12 30 # Initial location 0 to half past twelve
	c = 32 # And set b to the same thing
	12 > 12 # is a greater than b?
	10 = b # Set location 10 to the value of b
	
---data---

	a = 3
	
	