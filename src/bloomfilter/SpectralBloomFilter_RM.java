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


public class SpectralBloomFilter_RM {
	private int[] setInt;
	private short[] setShort;
	private byte[] setByte;
	private int[] setInt2;
	private short[] setShort2;
	private byte[] setByte2;
	private int m;
	private int m2;
	private int k;
	private int k2;
	private int n2;
	private int n;
	private double fp_obj;
	private int numberOfAddedElement=0;
	private String typeHashing="Murmur3";
	private final Charset charset = Charset.forName("UTF-8");
	private boolean dynamic=true;
	private boolean overflow=true;
	private int uniqueItems=0;
	

	/* Constructor */
	
	public SpectralBloomFilter_RM(double aFP,List<?> init,Class<?> cl){
		initialize(aFP,init.size(),typeHashing,cl);
		this.addAll(init);
	}
	
	public SpectralBloomFilter_RM(double aFP,List<?> init,String typeHashing,Class<?> cl){
		initialize(aFP,init.size(),typeHashing,cl);
		this.addAll(init);
	}
	
	public SpectralBloomFilter_RM(double aFP,int expectedNElement,Class<?> cl){
		initialize(aFP,expectedNElement,typeHashing,cl);
		
	}
	
	public SpectralBloomFilter_RM(double aFP,int expectedNElement,String typeHashing,Class<?> cl){
		initialize(aFP,expectedNElement,typeHashing,cl);
	}
	
	public SpectralBloomFilter_RM(double aFP,List<?> init,int expectedNElement,Class<?> cl){
		initialize(aFP,expectedNElement,typeHashing,cl);
		this.addAll(init);
	}
	
	public SpectralBloomFilter_RM(double aFP,List<?> init,int expectedNElement,String typeHashing,Class<?> cl){
		initialize(aFP,expectedNElement,typeHashing,cl);
		this.addAll(init);
	}
	
	public SpectralBloomFilter_RM(int aM, int aN, int aK,double aFp_obj, String typeHashing,Object aSet,int numberOfAddedElement,Class cl){
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
		n2 = (int)(n*(20.0f/100.0f));
		fp_obj=aFP;
		m=(int)Math.round(-n * Math.log(fp_obj)/Math.pow(Math.log(2), 2));
		k=(int)Math.round((double)m/(double)n* Math.log(2));
		m2=(int)Math.round(-n2 * Math.log(fp_obj)/Math.pow(Math.log(2), 2));
		k2=(int)Math.round((double)m2/(double)n2* Math.log(2));
		this.typeHashing=typeHashing;
		switch(cl.getSimpleName()){
			case "Byte":{setByte=new byte[m];setShort=null;setInt=null;setByte2=new byte[m2];setShort2=null;setInt2=null;break;}
			case "Short":{setShort=new short[m];setByte=null;setInt=null;setShort2=new short[m2];setByte2=null;setInt2=null;break;}
			case "Integer":{setInt=new int[m];setShort=null;setByte=null;setInt2=new int[m2];setShort2=null;setByte2=null;break;}
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
	public int getK(){return k+k2;}
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
			setByte[key]=(byte) (setByte[key]+1);
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
	//	for(int j : indexList) setByte[j]=(byte)(min+1);
		if(indexList.size()==1){
			/*		int increment;
			if(check2) increment=1;
			else increment=min;*/
			byte increment=min;
			for(int j=0;j<k2;j++){
				if(setByte2[BFHashing.getHashing(element, j, m2, typeHashing)]==0){
					increment=1;
					break;
				}
			}
			for(int j=0;j<k2;j++){
				int key=BFHashing.getHashing(element, j, m2, typeHashing);
				setByte2[key]=(byte) (setByte2[key]+increment);
			}
			uniqueItems++;
		}
		numberOfAddedElement++;
		
	}
	private void addLocalShort(byte[] element){
		HashMap<Short,ArrayList<Integer>> map=new HashMap<Short,ArrayList<Integer>>();
		for(int j=0;j<k;j++){
			int key=BFHashing.getHashing(element, j, m, typeHashing);
			setShort[key]=(short) (setShort[key]+1);
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
	//	for(int j : indexList) setShort[j]=(short)(min+1);
		//setShort[map.get(min)]=(short)(min+1);
		if(indexList.size()==1){
			/*		int increment;
			if(check2) increment=1;
			else increment=min;*/
			short increment=min;
			for(int j=0;j<k2;j++){
				if(setShort2[BFHashing.getHashing(element, j, m2, typeHashing)]==0){
					increment=1;
					break;
				}
			}
			for(int j=0;j<k2;j++){
				int key=BFHashing.getHashing(element, j, m2, typeHashing);
				setShort2[key]=(short) (setShort2[key]+increment);
			}
			uniqueItems++;
		}
		numberOfAddedElement++;
	}
	private void addLocalInt(byte[] element){
		HashMap<Integer,ArrayList<Integer>> map=new HashMap<Integer,ArrayList<Integer>>();
		for(int j=0;j<k;j++){
			int key=BFHashing.getHashing(element, j, m, typeHashing);
			setInt[key]=setInt[key]+1;
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
		if(indexList.size()==1){
	/*		int increment;
			if(check2) increment=1;
			else increment=min;*/
			int increment=min;
			for(int j=0;j<k2;j++){
				if(setInt2[BFHashing.getHashing(element, j, m2, typeHashing)]==0){
					increment=1;
					break;
				}
			}
			for(int j=0;j<k2;j++){
				int key=BFHashing.getHashing(element, j, m2, typeHashing);
				setInt2[key]=(setInt2[key]+increment);
			}
			uniqueItems++;
		}
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
	
	/* delete operation */
	public void delete(byte[] c){if(dynamic)this.deleteLocal(c);}
	public void delete(String c){if(dynamic)this.deleteLocal(c.getBytes(charset));}
	public void delete(int c){if(dynamic)this.deleteLocal(BigInteger.valueOf(c).toByteArray());}
	public void deleteAll(List<?> c){if(dynamic)for(Object el : c){this.deleteLocal(el.toString().getBytes(charset));}}
	
	private void deleteLocal(byte[] element){
		if(setByte !=null)deleteLocalByte(element);
		else if(setShort !=null)deleteLocalShort(element);
		else if(setInt !=null)deleteLocalInt(element);
	}
	
	private void deleteLocalByte(byte[] element){
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
		for(int j : indexList) setByte[j]=(byte)(min-1);
		if(indexList.size()==1){
			/*		int increment;
			if(check2) increment=1;
			else increment=min;*/
			byte increment=min;
			for(int j=0;j<k2;j++){
				if(setByte2[BFHashing.getHashing(element, j, m2, typeHashing)]==0){
					increment=1;
					break;
				}
			}
			for(int j=0;j<k2;j++){
				int key=BFHashing.getHashing(element, j, m2, typeHashing);
				setByte2[key]=(byte)(setByte2[key]-increment);
			}
			this.uniqueItems--;
		}
		numberOfAddedElement--;
		
	}
	private void deleteLocalShort(byte[] element){
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
		for(int j : indexList) setShort[j]=(short)(min-1);
		//setShort[map.get(min)]=(short)(min+1);
		if(indexList.size()==1){
			/*		int increment;
			if(check2) increment=1;
			else increment=min;*/
			short increment=min;
			for(int j=0;j<k2;j++){
				if(setShort2[BFHashing.getHashing(element, j, m2, typeHashing)]==0){
					increment=1;
					break;
				}
			}
			for(int j=0;j<k2;j++){
				int key=BFHashing.getHashing(element, j, m2, typeHashing);
				setShort2[key]=(short)(setShort2[key]-increment);
			}
			this.uniqueItems--;
		}
		numberOfAddedElement--;
	}
	private void deleteLocalInt(byte[] element){
		HashMap<Integer,ArrayList<Integer>> map=new HashMap<Integer,ArrayList<Integer>>();
		for(int j=0;j<k;j++){
			int key=BFHashing.getHashing(element, j, m, typeHashing);
			//map.put(setInteger[key],key);
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
		for(int j : indexList) setInt[j]=(int)(min-1);
		if(indexList.size()==1){
			/*		int increment;
			if(check2) increment=1;
			else increment=min;*/
			int increment=min;
			for(int j=0;j<k2;j++){
				if(setInt2[BFHashing.getHashing(element, j, m2, typeHashing)]==0){
					increment=1;
					break;
				}
			}
			for(int j=0;j<k2;j++){
				int key=BFHashing.getHashing(element, j, m2, typeHashing);
				setInt2[key]=(byte)(setInt2[key]-increment);
			}
			this.uniqueItems--;
		}
		numberOfAddedElement--;
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
	
	private byte frequencyLocalByte(byte[] element){
		HashMap<Byte,ArrayList<Integer>> map=new HashMap<Byte,ArrayList<Integer>>();
		for(int j=0;j<k;j++){
			int key=BFHashing.getHashing(element, j, m, typeHashing);
			if(setByte[key]==0) return 0;
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
		
		if(indexList.size()==1){
			byte min2=0;
			for(int j=0;j<k2;j++){
				byte val=setByte2[BFHashing.getHashing(element, j, m2, typeHashing)];
				if(val==0) return min;
				else if(val<min2) min2=val;
				else if(j==0) min2=val;
			}
			return min2;
		}else{
			return min;
		} 
	}
	
	private short frequencyLocalShort(byte[] element){
		HashMap<Short,ArrayList<Integer>> map=new HashMap<Short,ArrayList<Integer>>();
		for(int j=0;j<k;j++){
			int key=BFHashing.getHashing(element, j, m, typeHashing);
			//map.put(setShorteger[key],key);
			if(setShort[key]==0) return 0;
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
		
		if(indexList.size()==1){
			short min2=0;
			for(int j=0;j<k2;j++){
				short val=setShort2[BFHashing.getHashing(element, j, m2, typeHashing)];
				if(val==0) return min;
				else if(val<min2) min2=val;
				else if(j==0) min2=val;
			}
			return min2;
		}else{
			return min;
		} 
	}
	private int frequencyLocalInt(byte[] element){
		HashMap<Integer,ArrayList<Integer>> map=new HashMap<Integer,ArrayList<Integer>>();
		for(int j=0;j<k;j++){
			int key=BFHashing.getHashing(element, j, m, typeHashing);
			//map.put(setInteger[key],key);
			if(setInt[key]==0) return 0;
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
		
		if(indexList.size()==1){
			int min2=0;
			for(int j=0;j<k2;j++){
				int val=setInt2[BFHashing.getHashing(element, j, m2, typeHashing)];
				if(val==0) return min;
				else if(val<min2) min2=val;
				else if(j==0) min2=val;
			}
			return min2;
		}else{
			return min;
		} 
		
	}

	public int getUniqueItems(){return this.uniqueItems;}
}
