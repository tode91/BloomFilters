package data;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Data {
	
	private static final char[] charSet="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

	/**
	 * method to get a list of random string
	 * @param length length of each string
	 * @param size number of string to create
	 * @return list of string
	 */
	public static List<String> getRandomString(int length, int size){
		return getArrayRandomString(length,size);
	}
	/**
	 * method to get a list of random string
	 * @return list of string
	 */
	public static List<String> getRandomString(){
		return getArrayRandomString(20,(int) Math.pow(10, 5));
	}
	
	/**
	 * method to generate an array of alphanumeric string
	 * @param size dimension of the array
	 * @return array of string
	 */
	public static List<String> getArrayRandomString(int length,int size){
		ArrayList<String> str=new ArrayList<String>();
		for (int i=0;i<size;i++){
			str.add(i, getRandomString(length));
		}
		return str;
	}
	
	/**
	 * method to generate an array of alphanumeric string
	 * @param size dimension of the array
	 * @return array of string
	 */
	public static List<byte[]> getArrayRandomByteString(int length,int size){
		ArrayList<byte[]> str=new ArrayList<byte[]>();
		for (int i=0;i<size;i++){
			str.add(i, getRandomString(length).getBytes(Charset.forName("UTF-8")));
		}
		return str;
	}
	
	/**
	 * method to generate an alphanumeric string
	 * @param size dimension of the string
	 * @return alphanumeric string
	 */
	public static String getRandomString(int strSize){
		Random r = new Random();
	    char[] rs = new char[strSize];
	    for (int i = 0; i < rs.length; i++) {
	        int randIndexChar = r.nextInt(charSet.length);
	        rs[i] = charSet[randIndexChar];
	    }
		return new String(rs);
	}
	
	/**
	 * method to print some statistics
	 * @param name title
	 * @param start start time in milliseconds
	 * @param end end time in milliseconds
	 * @param elementCount number of elements analyzed
	 */
	public static void printStat(String name,long start, long end,int elementCount) {
        double diff = (end - start) / 1000.0000;
        System.out.println(name+", "+elementCount+" element: "+diff + "s, " + (elementCount / diff) + " elements/s");
    }
	
	public static double benchmark(long start, long end, int size){
		if(size!=0)return ((double)(end-start))/((double)size);
		else return 0.0;
	}
}	

