package hashing;

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class BFHashing {
	private final static int seedhash1=40;
	private final static int seedhash2=41;
	
	/**
	 * method to get the hash value of an element
	 * @param element element to be hashed
	 * @param i index of hashing function
	 * @return hashing value
	 */
	public static int getHashing(byte[] element, int i, int m){
		return getHashing(element,i,m,"Murmur3");
	}
	
	public static int getHashing(byte[] element, int i, int m, String type){
		int hash=(hash(element,type,seedhash1)+ hash(element,type,seedhash2) * i) % m;
		if( hash < 0 ) hash += m;
		return hash;
	}
	
	public static int getHashingMPHS(byte[] element, double power, String type){
		int hash=(int) (hash(element,type,seedhash1) % power);
	//	if( hash < 0 ) hash += m;
		return hash;
	}
	
	private static int hash( byte[] key, String type, int seed){ 
		int h=0;
		switch(type){
			case "Murmur3":{
				h = MurmurHash3.murmurhash3_x86_32(key, 0, key.length, seed);
				break;
			}
			case "Murmur2":{
				h = MurmurHash2.hash32(key, key.length, seed);
				break;
			}
			case "Murmur":{
				h = MurmurHash.hash(key, seed);
				break;
			}
		}
		return h;
	}


}
