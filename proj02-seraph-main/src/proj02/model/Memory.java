package proj02.model;

/**
 * Model a RAM memory for Pippin.
 *
 * A Pippin memory is simply an indexed array of data values.
 * The index is treated as the memory address.
 * Each value at each address may be written - replaced by a new integer value,
 * or read, return the value at that location.
 *
 * @author cs140
 */
public class Memory {

	/**
		Under the covers, we will simulate a RAM memory using a Java
		array. I have declared a field called "data" which
		is an array of integers.
	**/

	private int data[];

	/**
	 * Construct a new Pippin Memory whose size is specified by the parameter.
	 * @param length number of locations in the memory
	 */
	public Memory(int length) {
		data = new int[length];
	}

	/**
	 * @return the size of the memory
	 */
	public int size() {
		return data.length;
	}

	/**
	 * Writes the val parameter to the location in the loc parameter.
	 * If the location is invalid, prints an error message and does not update memory.
	 * @param loc index into the memory to write a new value
	 * @param val value to write to the specified location
	 */
	public void set(int loc,int val) {
		if (loc<0 || loc>data.length)
			System.out.println("Error... Invalid memory address: " + loc + " during set");
		else data[loc]=val;
	}

	/**
	 * Retrieves and returns the value at the specified location.
	 * If the loc is not valid, writes a message and returns a -1 value
	 * @param loc index into the memory to write a new value
	 * @return the value at the specified location
	 */
	public int get(int loc) {
		if (loc<0 || loc>data.length) {
			System.out.println("Error... Invalid memory address: " + loc + " during get");
			return -1;
		}
		else return data[loc];
	}

	/**
	 * Prints out the title and the contents of the entire memory.
	 * @param title a String to identify this dump of memory
	 */
	public void dump(String title) {
		dump(title,0,data.length-1);
	}

	/**
	 * Prints out the title and the contents of memory from the specified start location to the specified stop location.
	 * @param title a String to identify this dump of memory
	 * @param start index of the first location to dump
	 * @param stop index of the last location to dump
	 */
	public void dump(String title,int start,int stop) {
		boolean in0=false;
		int start0=start,stop0=start;
		System.out.println(title);
		for(int i=start;i<=stop;i++) {
			if (data[i]==0) {
				if (in0) { stop0=i; }
				else {
					in0=true;
					start0=stop0=i;
				}
			} else {
				if (in0) {
					if (start0==stop0) {
						System.out.println(String.format("   %08d           ",start0) + " : 0x00000000 =          0 = [NOP]");
					} else {
						System.out.println(String.format("   %08d",start0) + " - " + String.format("%08d",stop0) + " : 0x00000000 =        0 = [NOP]");
					}
					in0=false;
				}
				System.out.println(String.format("   %08d",i) + "            : " +String.format("0x%08x = %10d = %s",data[i],data[i],new Instruction(data[i]).toString()));
			}
		}
		if (in0) {
			if (start0==stop0) {
				System.out.println(String.format("   %08d",start0) + "           : 0x00000000 =          0 = [NOP]");
			} else {
				System.out.println(String.format("   %08d",start0) + " - " + String.format("%08d",stop0) + " : 0x00000000 =          0 = [NOP]");
			}
		}
	}

}
