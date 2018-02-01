package bloomfilter;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.BitSet;
import java.util.List;

import hashing.BFHashing;

public class Compressed_SBF {
	
	private int m;
	private int k;
	private int n;
	private int z;
	//private double h_p;
	
	private double fp_obj;
	private int numberOfAddedElement;
	private String typeHashing;
	private BitSet set;
	private final Charset charset = Charset.forName("UTF-8");
	private boolean dynamic=true;
	private boolean overflow=true;

	
	public Compressed_SBF(StandardBloomFilter bf){
		initialize(bf.getProbabilityFPObj(),bf.getNumberOfExpectedElement(),bf.getTypeHashing());
		bf=null;
	}
	
	public Compressed_SBF(double aFP, List init){
		initialize(aFP,init.size(),typeHashing);
		this.addAll(init);
	}
	
	public Compressed_SBF(double aFP,List init,String typeHashing){
		initialize(aFP,init.size(),typeHashing);
		this.addAll(init);
	}
	
	public Compressed_SBF(double aFP,int expectedNElement){
		initialize(aFP,expectedNElement,typeHashing);
	}
	
	public Compressed_SBF(double aFP,int expectedNElement,String typeHashing){
		initialize(aFP,expectedNElement,typeHashing);
	}
	
	public Compressed_SBF(double aFP,List init,int expectedNElement, String typeHashing){
		initialize(aFP,expectedNElement,typeHashing);
		this.addAll(init);
	}
	
	public Compressed_SBF(double aFP,List init,int expectedNElement){
		initialize(aFP,expectedNElement,typeHashing);
		this.addAll(init);
	}
	
	private void initialize(double aFP,int expectedNElement, String typeHashing){
		n=expectedNElement;
		fp_obj=aFP;
		m=(int)Math.round(-n * Math.log(fp_obj)/Math.pow(Math.log(2), 2));
		m = z = (int) Math.round(m*Math.log(2));
		k=(int)Math.round((double)-z/(double)n * Math.log(fp_obj));
		this.typeHashing=typeHashing;
		set=new BitSet(m);
	}
			
	public boolean isOverflowEnable(){return this.overflow;}
	public boolean isDynamicEnable(){return this.dynamic;}
	public void setDynamic(boolean b){this.dynamic=b;}
	public void setOverflow(boolean b){this.overflow=b;}
	
	public BitSet getBitSet(){return this.set;}
	public int getK(){return k;}
	public int getM(){return set.size();}
	public String getTypeHashing(){return this.typeHashing;}
	public int getNumberOfAddedElement(){return this.numberOfAddedElement;}
	public int getNumberOfExpectedElement(){return this.n;}
	
	
	/* add operations */
	public void add(byte[] element){if(checkAdd()) addLocal(element);}
	public void add(int c){if(checkAdd()) this.addLocal(BigInteger.valueOf(c).toByteArray());}
	public void add(String c){if(checkAdd()) this.addLocal(c.getBytes(charset));}
	public void addAll(List c){if(checkAdd()) for(Object el : c){this.addLocal(el.toString().getBytes(charset));}}
	
	private void addLocal(byte[] element){
		for(int j=0;j<k;j++){
			set.set(BFHashing.getHashing(element, j, m, this.typeHashing), true);
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
		for(int j=0;j<k;j++){
			int hash=BFHashing.getHashing(element, j, m, this.typeHashing);
			if(set.get(hash)==false){
				return false;
			}
		}
		return true;
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
		this.set.clear();
		this.numberOfAddedElement=0;
	}
	
	/*
	 * method to check if bloom filter is full: the number of elements represented is > or = to the expected number of elements
	 * @return true if is full, false otherwise
	 */
	public boolean isFull(){
		if(numberOfAddedElement>=n){return true;}
		else{return false;}
	}
}
