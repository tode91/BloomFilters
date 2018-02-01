package bloomfilter;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.List;

import hashing.BFHashing;


public class Compressed_CBF {
	private int[] setInt;
	private short[] setShort;
	private byte[] setByte;
	private int m;
	private int z;
	private int k;
	private int n;
	private double fp_obj;
	private int numberOfAddedElement=0;
	private String typeHashing="Murmur3";
	private final Charset charset = Charset.forName("UTF-8");
	private boolean dynamic=true;
	private boolean overflow=true;
	

	/* Constructor */
	public Compressed_CBF(CountingBloomFilter bf){
		initialize(bf.getProbabilityFPObj(),bf.getNumberOfExpectedElement(),bf.getTypeHashing(),bf.getTypeCounter());
		bf=null;
	}
	
	public Compressed_CBF(double aFP,List init,Class cl){
		initialize(aFP,init.size(),typeHashing,cl);
		this.addAll(init);
	}
	
	public Compressed_CBF(double aFP,List init,String typeHashing,Class cl){
		initialize(aFP,init.size(),typeHashing,cl);
		this.addAll(init);
	}
	
	public Compressed_CBF(double aFP,int expectedNElement,Class cl){
		initialize(aFP,expectedNElement,typeHashing,cl);
		
	}
	
	public Compressed_CBF(double aFP,int expectedNElement,String typeHashing,Class cl){
		initialize(aFP,expectedNElement,typeHashing,cl);
	}
	
	public Compressed_CBF(double aFP,List init,int expectedNElement,Class cl){
		initialize(aFP,expectedNElement,typeHashing,cl);
		this.addAll(init);
	}
	
	public Compressed_CBF(double aFP,List init,int expectedNElement,String typeHashing,Class cl){
		initialize(aFP,expectedNElement,typeHashing,cl);
		this.addAll(init);
	}
	
	public Compressed_CBF(int aM, int aN, int aK,double aFp_obj, String typeHashing,Object aSet,int numberOfAddedElement,Class cl){
		n=aN;
		fp_obj=aFp_obj;
		m=aM;
		k=aK;
		switch(cl.getSimpleName()){
		case "Byte":{setByte=(byte[])aSet;setShort=null;setInt=null;break;}
		case "Short":{setShort=(short[])aSet;setByte=null;setInt=null;break;}
		case "Integer":{setInt=(int[])aSet;setShort=null;setByte=null;break;}
	}
	cl=null;
		this.numberOfAddedElement=numberOfAddedElement;
		this.typeHashing=typeHashing;
	}
	
	private void initialize(double aFP,int expectedNElement,String typeHashing,Class cl){
		n=expectedNElement;
		fp_obj=aFP;
		m=(int)Math.round(-n * Math.log(fp_obj)/Math.pow(Math.log(2), 2));
		//h_p=-1*fp_obj*(Math.log(fp_obj) / Math.log(2))-(1-fp_obj)*(Math.log(1-fp_obj) / Math.log(2));
		//z= (int) Math.round(h_p*m);
		m = z = (int) Math.round(m*Math.log(2));
		k=(int)Math.round((double)-z/(double)n * Math.log(fp_obj));
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
		
	/* add operations */
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
		for(int j=0;j<k;j++){
			setByte[BFHashing.getHashing(element, j, m, this.typeHashing)]=(byte)((byte)setByte[BFHashing.getHashing(element, j, m, this.typeHashing)]+1);
		}
		numberOfAddedElement++;
	}
	private void addLocalShort(byte[] element){
		for(int j=0;j<k;j++){
			setShort[BFHashing.getHashing(element, j, m, this.typeHashing)]=(short)((short)setShort[BFHashing.getHashing(element, j, m, this.typeHashing)]+1);
		}
		numberOfAddedElement++;
	}
	private void addLocalInt(byte[] element){
		for(int j=0;j<k;j++){
			setInt[BFHashing.getHashing(element, j, m, this.typeHashing)]=(int)setInt[BFHashing.getHashing(element, j, m, this.typeHashing)]+1;
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
		for(int j=0;j<k;j++){
			setByte[BFHashing.getHashing(element, j, m, this.typeHashing)]=(byte)((byte)setByte[BFHashing.getHashing(element, j, m, this.typeHashing)]-1);
		}
		numberOfAddedElement--;
	}
	private void deleteLocalShort(byte[] element){
		for(int j=0;j<k;j++){
			setShort[BFHashing.getHashing(element, j, m, this.typeHashing)]=(short)((short)setShort[BFHashing.getHashing(element, j, m, this.typeHashing)]-1);
		}
		numberOfAddedElement--;
	}
	private void deleteLocalInt(byte[] element){
		for(int j=0;j<k;j++){
			setInt[BFHashing.getHashing(element, j, m, this.typeHashing)]=(int)setInt[BFHashing.getHashing(element, j, m, this.typeHashing)]-1;
		}
		numberOfAddedElement--;
	}
	
	
	/*probability of false positive */
	public double getProbabilityFPObj(){ return this.fp_obj;}
	
	public double getProbabilityFP(){
		return Math.exp((double)-m/(double)n*Math.log(2));
	}
	
	public double getProbabilityFPReal(){
		return Math.exp((double)-m/(double)this.numberOfAddedElement*Math.log(2));
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
	
}
