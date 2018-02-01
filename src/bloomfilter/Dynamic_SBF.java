package bloomfilter;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.List;

import hashing.MurmurHash3;

import java.util.ArrayList;
import java.util.BitSet;


public class Dynamic_SBF {
	private List<StandardBloomFilter> bfList;
	private int indexActiveBF;
	private int c;
	private double fp_obj;
	private int numberOfAddedElement=0;
	private final Charset charset = Charset.forName("UTF-8");
		
	/* Constructor */
	public Dynamic_SBF(double aFP,int capacity){
		bfList=new ArrayList<StandardBloomFilter>();
		c=capacity;
		fp_obj=aFP;
		addBloomFilter();
	}
	
	public int getCapacity(){return c;}
	public int getNumberOfBloomFilter(){return bfList.size();}
	public int getNumberOfAddedElement(){return this.numberOfAddedElement;}
	public int getNumberOfExpectedElement(){return c*bfList.size();}
	public int getK(){return bfList.get(0).getK();}	
	public int getM(){return bfList.get(0).getM();}
	
	/* method to add a standard bloom filter*/
	private boolean addBloomFilter(){
		bfList.add(new StandardBloomFilter(fp_obj,c));
		indexActiveBF=bfList.size()-1;
		return true;
	}
	
	/*add operation */
	public void add(byte[] c){this.addLocal(c);}
	public void add(int c){this.addLocal(BigInteger.valueOf(c).toByteArray());}
	public void add(String c){this.addLocal(c.getBytes(charset));}
	public void addAll(List c){for(Object el : c)this.addLocal(el.toString().getBytes(charset));}
	
	private void addLocal(byte[] element){
		if(bfList.get(this.indexActiveBF).isFull()==false){
			bfList.get(this.indexActiveBF).add(element);
			numberOfAddedElement++;
		}else{
			boolean b=addBloomFilter();
			addLocal(element);
		}
	}
	
	/* lookup operation */
	public boolean lookup(int c){return this.lookup(BigInteger.valueOf(c).toByteArray());}
	public boolean lookup(String c){return this.lookup(c.getBytes(charset));}
	public boolean lookup(byte[] element){
		for(int j=0;j<this.bfList.size();j++){
			if(bfList.get(j).lookup(element)==true)return true;
		}
		return false;
	}
	
	/* probability of false positive */
	public double getAverageProbabilityFP(){
		double fpProb=0;
		for(int i=0;i<bfList.size();i++){
			fpProb=fpProb+bfList.get(i).getProbabilityFP();
		}
		return fpProb/(double)bfList.size();
	}
	
	
public double getProbabilityFPReal(){
	if(this.c<=this.numberOfAddedElement)
		return 1 - (Math.pow(1-Math.pow(1-Math.exp(((double)(-1*this.getK()*this.c))/(double)this.getM()), this.getK()), Math.ceil(numberOfAddedElement/c)))*
			(1-Math.pow(1-Math.exp(-1.0*this.getK()*((double)(numberOfAddedElement-c*Math.ceil(numberOfAddedElement/c))/((double)this.getM()))), this.getK()));
	else return bfList.get(0).getProbabilityFPReal();
}
}
