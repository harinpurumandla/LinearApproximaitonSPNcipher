package linearapprox;
/*
 * Author @Harin Purumandla
 * */
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Date;
import java.util.concurrent.TimeUnit;
/*
 * Class LinearSPNapprox is used to find the 5-8 and 13-16 bits of subkey5 in SPN cipher.
 * */
public class LinearSPNapprox {
/*
 * linearApproximation function perform the linear approximation of SPN cipher.
 * */
	public static void linearApproximation(String ciphersample, String plainsample)
	 {
		System.out.println("Execution started ...\n --------------------------------------");
		long lStartTime = System.currentTimeMillis();
		int count=0,finalkey=0;
		String ciphertext="";
		String plaintext="";
		String v="";
		String u="";
		float[] bias=new float[256]; // bias values of guessed subkeys.
		SPNCipher spn=new SPNCipher(); 
		String[] keys=keyGeneration();
		for(int i=0;i<keys.length;i++)
		{
			String key=keys[i];
			count=0;
		BufferedReader cipherfile=fileRead(ciphersample);	// opening the ciphertextsample.txt file
		BufferedReader plainfile=fileRead(plainsample);		// opening the plaintextsample.txt file	
		try {
		 while( (ciphertext = cipherfile.readLine()) != null && (plaintext = plainfile.readLine())!= null){ // reading the files till the end simultaneously
			 if(ciphertext.length()==key.length())
			v=Xor(ciphertext,key); // step 1: performing Xor of the cipher text and guessed subkey
			 u=spn.substitute(v, false); // step 2: performing substitute function backwards.
			 if(appoxValue(u,plaintext) == 0) // step 3: computing the approximation value using the formula
			 {
				 count++; // incrementing the counter if the approximation value returns 0
			 }
            }
		 cipherfile.close();
		 plainfile.close();
		}catch (Exception e) {
            System.err.println("Opps Something went wrong, Please check the sample files and re-run the program");
		}
		bias[i]=computeBias(count); // step 4: computing the bias value
	}

		finalkey=minArray(bias); // finding the key using the bias value
		finalSubkey(finalkey); // printing the subkey
		long lEndTime = System.currentTimeMillis();
		long difference = lEndTime - lStartTime;
		System.out.println("--------------------------------------\n ... **... Execution completed in "+TimeUnit.MILLISECONDS.toSeconds(difference)+" seconds  ... **... ");
	}
	/*
	 * Computing bias and also subtracting the bias value from the constant (1/32) derived by the application
	 * Piling-Up Lemma just to improve the correctness of the value.
	 * */
	private static float computeBias(int count)
	{
		return Math.abs(Math.abs((count-5000.0f)/10000.0f)-(1/32.0f));
		
	}
	/*
	 * fileRead function open the file and returns the bufferedReader object
	 * */
	private static BufferedReader fileRead(String filename)
	{
		  BufferedReader br = null;
        String strLine = "";
        try {
            br = new BufferedReader( new FileReader(filename));
            return br;
        } catch (Exception e) {
            System.err.println("Unable to find the file: ");
            return null;
        }
	}
	/*
	 * keyGeneration function generates the all possible keys (256) by changing hex2 and hex4 values.
	 * */
	private static String[] keyGeneration()
	{
		String[] subkeys = new String[256];
		int count=0;
        for(int i=0;i<16;i++) // 16*16 = 256
        {
            String hex1="0000"+String.format("%4s",Integer.toBinaryString(i)).replace(' ','0'); // hex1 of length 8 bits is created with first four bits as all zero's
            for(int j=0;j<16;j++)
            {
                String hex2="0000"+String.format("%4s",Integer.toBinaryString(j)).replace(' ','0');// hex2 of length 8 bits is created with first four bits as all zero's
                subkeys[count]=hex1+hex2;
                count++;
            }
		}
		return subkeys;
	}
	/*
	 * Xor function xor's 2 string bit values of length 16 bits and returns the 16 bit xored value
	 * */
    private static String Xor(String ciphertext,String subkey)
    {
			char[] cipherarray = ciphertext.toCharArray();
			char[] keyarray = subkey.toCharArray();
			String out="";
			for(int i=0;i<16;i++)
			{
				out=out+(Integer.parseInt(""+cipherarray[i])^Integer.parseInt(""+keyarray[i]));
			}
			return out;
		
    }
    /*
     *approxvalue function computes the formula  U4,6 ⊕U4,8 ⊕U4,14 ⊕U4,16 ⊕P5 ⊕P7 ⊕P8 and returns the value 
     * */
    private static int appoxValue(String U,String P)
    {
			char[] Uarray = U.toCharArray();
			char[] Parray = P.toCharArray();
			int out=(Integer.parseInt(""+Uarray[5])^Integer.parseInt(""+Uarray[7])^Integer.parseInt(""+Uarray[13])^Integer.parseInt(""+Uarray[15])^Integer.parseInt(""+Parray[4])^Integer.parseInt(""+Parray[6])^Integer.parseInt(""+Parray[7]));
			return out;
    }
    /*
     * minarray function returns the index of the array which has the minimum value in the array
     * */
    private static int minArray(float[] array)
    {
		float min=1;
		int key=0;
		for (int i = 0; i < array.length; i++)
		{
			if (array[i] < min)
			{
				min = array[i];
				key = i;
			}
		}
		return key;
	}
    /*
     * finalsubkey function takes integer value and converts it to hex values and also prints the integer in a guessed subkey value ie. 0000xxxx0000xxxx format.
     * */
	private static void finalSubkey(int key)
	{
		System.out.print("Integer value of Extracted bits from subkey5 is ");
		System.out.println(key);
		System.out.print("Hex value of Extracted bits from subkey5 is ");
		String hex = Integer.toHexString(key);
		System.out.println(hex.toUpperCase());
		String bin = String.format("%8s", Integer.toBinaryString(key)).replace(' ', '0');
		System.out.println("Extracted bits of subkey5 in the location (5-8) are "+bin.substring(0,4));
		System.out.println("Extracted bits of subkey5 in the location (13-16) are "+bin.substring(4,8));
	}

}
