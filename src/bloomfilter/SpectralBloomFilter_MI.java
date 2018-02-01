package bloomfilter;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hashing.BFHashing;


public class SpectralBloomFilter_MI {
	private int[] setInt;
	private short[] setShort;
	private byte[] setByte;
	private int m;
	private int k;
	private int n;
	private double fp_obj;
	private int numberOfAddedElement=0;
	private String typeHashing="Murmur3";
	private final Charset charset = Charset.forName("UTF-8");
	private boolean dynamic=true;
	private boolean overflow=true;
	

	/* Constructor */
	
	public SpectralBloomFilter_MI(double aFP,List<?> init,Class<?> cl){
		initialize(aFP,init.size(),typeHashing,cl);
		this.addAll(init);
	}
	
	public SpectralBloomFilter_MI(double aFP,List<?> init,String typeHashing,Class<?> cl){
		initialize(aFP,init.size(),typeHashing,cl);
		this.addAll(init);
	}
	
	public SpectralBloomFilter_MI(double aFP,int expectedNElement,Class<?> cl){
		initialize(aFP,expectedNElement,typeHashing,cl);
		
	}
	
	public SpectralBloomFilter_MI(double aFP,int expectedNElement,String typeHashing,Class<?> cl){
		initialize(aFP,expectedNElement,typeHashing,cl);
	}
	
	public SpectralBloomFilter_MI(double aFP,List<?> init,int expectedNElement,Class<?> cl){
		initialize(aFP,expectedNElement,typeHashing,cl);
		this.addAll(init);
	}
	
	public SpectralBloomFilter_MI(double aFP,List<?> init,int expectedNElement,String typeHashing,Class<?> cl){
		initialize(aFP,expectedNElement,typeHashing,cl);
		this.addAll(init);
	}
	
	public SpectralBloomFilter_MI(int aM, int aN, int aK,double aFp_obj, String typeHashing,Object aSet,int numberOfAddedElement,Class cl){
		n=aN;
		fp_obj=aFp_obj;
		m=aM;
		k=aK;
		switch(cl.getSimpleName()){
			case "Byte":{setByte=(byte[])aSet;setShort=null;setInt=null;break;}
			case "Short":{setShort=(short[])aSet;setByte=null;setInt=null;break;}
			case "Integer":{setInt=(int[])aSet;setShort=null;setByte=null;break;}
		}
		this.numberOfAddedElement=numberOfAddedElement;
		this.typeHashing=typeHashing;
	}
	
	private void initialize(double aFP,int expectedNElement,String typeHashing,Class<?> cl){
		n=expectedNElement;
		fp_obj=aFP;
		m=(int)Math.round(-n * Math.log(fp_obj)/Math.pow(Math.log(2), 2));
		k=(int)Math.round((double)m/(double)n* Math.log(2));
		this.typeHashing=typeHashing;
		switch(cl.getSimpleName()){
			case "Byte":{setByte=new byte[m];setShort=null;setInt=null;break;}
			case "Short":{setShort=new short[m];setByte=null;setInt=null;break;}
			case "Integer":{setInt=new int[m];setShort=null;setByte=null;break;}
		}
		cl=null;
	}
	
	
	public boolean isOverflowEnable(){return this.overflow;}
	public boolean isDynamicEnable(){return this.dynamic;}
	public void setDynamic(boolean b){this.dynamic=b;}
	public void setOverflow(boolean b){this.overflow=b;}
	
	public Object getBitSet(){
		if(setByte !=null)return (Object)setByte; 
		else if(setShort !=null)return (Object)setShort;
		else if(setInt !=null)return (Object)setInt;
		else return null;
		}
	public int getK(){return k;}
	public int getM(){return m;}
	public String getTypeHashing(){return this.typeHashing;}
	public int getNumberOfAddedElement(){return this.numberOfAddedElement;}
	public int getNumberOfExpectedElement(){return this.n;}
	public Class<?> getTypeCounter(){
		if(setByte !=null)return Byte.class; 
		else if(setShort !=null)return Short.class;
		else return Integer.class;
	}
		
	/* add operations with Minimum Increase Algorithm */
	public void add(byte[] element){if(checkAdd()) addLocal(element);}
	public void add(int c){if(checkAdd()) this.addLocal(BigInteger.valueOf(c).toByteArray());}
	public void add(String c){if(checkAdd()) this.addLocal(c.getBytes(charset));}
	public void addAll(List<?> c){if(checkAdd()) for(Object el : c){this.addLocal(el.toString().getBytes(charset));}}

	private void addLocal(byte[] element){
		if(setByte !=null)addLocalByte(element);
		else if(setShort !=null)addLocalShort(element);
		else if(setInt !=null)addLocalInt(element);
	}
	
	private void addLocalByte(byte[] element){
		HashMap<Byte,ArrayList<Integer>> map=new HashMap<Byte,ArrayList<Integer>>();
		for(int j=0;j<k;j++){
			int key=BFHashing.getHashing(element, j, m, typeHashing);
			//map.put(setByte[key],key);
			ArrayList<Integer> a= map.get(setByte[key]);
			try{
				a.add(key);
			}catch(Exception e){
				a=new ArrayList<Integer>();
				a.add(key);
			}
			map.put(setByte[key], a);
		}
		byte min= Collections.min(map.keySet());
		ArrayList<Integer> indexList=map.get(min);
		for(int j : indexList) setByte[j]=(byte)(min+1);
		//setByte[map.get(min)]=(byte)(min+1);
		numberOfAddedElement++;
		
	}
	private void addLocalShort(byte[] element){
		HashMap<Short,ArrayList<Integer>> map=new HashMap<Short,ArrayList<Integer>>();
		for(int j=0;j<k;j++){
			int key=BFHashing.getHashing(element, j, m, typeHashing);
			//map.put(setShort[key],key);
			ArrayList<Integer> a= map.get(setShort[key]);
			try{
				a.add(key);
			}catch(Exception e){
				a=new ArrayList<Integer>();
				a.add(key);
			}
			map.put(setShort[key], a);
		}
		short min= Collections.min(map.keySet());
		ArrayList<Integer> indexList=map.get(min);
		for(int j : indexList) setShort[j]=(short)(min+1);
		//setShort[map.get(min)]=(short)(min+1);
		numberOfAddedElement++;
	}
	private void addLocalInt(byte[] element){
		HashMap<Integer,ArrayList<Integer>> map=new HashMap<Integer,ArrayList<Integer>>();
		for(int j=0;j<k;j++){
			int key=BFHashing.getHashing(element, j, m, typeHashing);
			ArrayList<Integer> a= map.get(setInt[key]);
			try{
				a.add(key);
			}catch(Exception e){
				a=new ArrayList<Integer>();
				a.add(key);
			}
			map.put(setInt[key], a);
		}
		int min= Collections.min(map.keySet());
		ArrayList<Integer> indexList=map.get(min);
		for(int j : indexList) setInt[j]=(int)(min+1);
		//setInt[map.get(min)]=min+1;
		numberOfAddedElement++;
	}
	
	private boolean checkAdd(){
		if(dynamic){
			if(overflow){
				return true;
			}else if (isFull()){
				return false;
			}else{
				return true;
			}
		}else{
			return false;
		}		
	}
	
	/* lookup operation */
	public boolean lookup(String c){return this.lookup(c.getBytes(charset));}
	public boolean lookup(int c){return this.lookup(BigInteger.valueOf(c).toByteArray());}
	
	public boolean lookup(byte[] element){
		if(setByte !=null) return lookupLocalByte(element);
		else if(setShort !=null) return lookupLocalShort(element);
		else return lookupLocalInt(element);
	}
	
	private boolean lookupLocalByte(byte[] element){
		for(int j=0;j<k;j++){
			if(setByte[BFHashing.getHashing(element, j, m, this.typeHashing)]==0) return false;
		}
		return true;
	}
	private boolean lookupLocalShort(byte[] element){
		for(int j=0;j<k;j++){
			if(setShort[BFHashing.getHashing(element, j, m, this.typeHashing)]==0) return false;
		}
		return true;
	}
	private boolean lookupLocalInt(byte[] element){
		for(int j=0;j<k;j++){
			if(setInt[BFHashing.getHashing(element, j, m, this.typeHashing)]==0) return false;
		}
		return true;
	}
	
	/*  false positive probability */
	public double getProbabilityFP(){
		return Math.pow((1 - Math.exp(-k * (double) n / (double) m)), k);
	}
	
	public double getProbabilityFPObj(){return this.fp_obj;}
	
	public double getProbabilityFPReal(){
		return Math.pow((1 - Math.exp(-k * (double) numberOfAddedElement/ (double) m)), k);
	}
	
	/*
	 * method to clear bloom filter
	 */
	public void clearBloomFilter(){
		if(setByte !=null) setByte=new byte[m]; 
		else if(setShort !=null)setShort=new short[m];
		else if(setInt !=null)setInt=new int[m];
		this.numberOfAddedElement=0;
	}
	
	/*
	 * method to check if bloom filter is full: the number of elements represented is > or = to the expected number of elements
	 */
	public boolean isFull(){
		if(numberOfAddedElement>=n){return true;}
		else{return false;}
	}	
	
	/*
	 * method frequency
	 */
	public int frequency(String c){return this.frequency(c.getBytes(charset));}
	public int frequency(int c){return this.frequency(BigInteger.valueOf(c).toByteArray());}

	private int frequency(byte[] element){
		if(setByte !=null) return frequencyLocalByte(element);
		else if(setShort !=null) return frequencyLocalShort(element);
		else return frequencyLocalInt(element);
	}
	
	private int frequencyLocalByte(byte[] element){
		int minValue=0;
		for(int j=0;j<k;j++){
			int hash=setByte[BFHashing.getHashing(element, j, m, this.typeHashing)];
			if(hash==0)return 0;
			else if((minValue>hash || j==0 ) && hash!=0)minValue=hash;
		}
		return minValue;
	}
	
	private int frequencyLocalShort(byte[] element){
		int minValue=0;
		for(int j=0;j<k;j++){
			int hash=setShort[BFHashing.getHashing(element, j, m, this.typeHashing)];
			if(hash==0) return 0;
			else if((minValue>hash || j==0 ) && hash!=0)minValue=hash;
		}
		return minValue;
	}
	private int frequencyLocalInt(byte[] element){
		int minValue=0;
		for(int j=0;j<k;j++){
			int hash=setInt[BFHashing.getHashing(element, j, m, this.typeHashing)];
			if(hash==0) return 0;
			else if((minValue>hash || j==0 ) && hash!=0)minValue=hash;
		}
		return minValue;
	}
	
}
