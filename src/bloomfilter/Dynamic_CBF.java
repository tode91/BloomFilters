package bloomfilter;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.List;

import hashing.MurmurHash3;

import java.util.ArrayList;
import java.util.BitSet;


public class Dynamic_CBF {
	private List<CountingBloomFilter> cbfList;
	private int n_bloomFilter=0;
	private int indexActiveBF;
	private int c;
	private double fp_obj;
	private int numberOfAddedElement=0;
	private final Charset charset = Charset.forName("UTF-8");
	private Class<?> cl_type;
		
	/* Constructor */
	public Dynamic_CBF(double aFP,int capacity,Class<?> cl_type){
		c=capacity;
		fp_obj=aFP;
		this.cl_type=cl_type;
		cbfList=new ArrayList<CountingBloomFilter>();
		addBloomFilter();
	}
	
	public int getCapacity(){return c;}
	public int getNumberOfBloomFilter(){return n_bloomFilter;}
	public int getNumberOfAddedElement(){return this.numberOfAddedElement;}
	public int getNumberOfExpectedElement(){return c*n_bloomFilter;}
	public int getK(){return cbfList.get(0).getK();}	
	public int getM(){return cbfList.get(0).getM();}	
	public Class<?> getTypeCounter(){return cl_type;}
	
	/* method to add a standard bloom filter*/
	private boolean addBloomFilter(){
		cbfList.add(new CountingBloomFilter(fp_obj,c,cl_type));
		n_bloomFilter++;
		indexActiveBF=cbfList.size()-1;
		return true;
	}
	
	/*add operation */
	public void add(int c){this.addLocal(BigInteger.valueOf(c).toByteArray());}
	public void add(String c){this.addLocal(c.getBytes(charset));}
	public void addAll(List<?> c){for(Object el : c)this.addLocal(el.toString().getBytes(charset));}
	public void add(byte[] c){this.addLocal(c);}

	private void addLocal(byte[] element){
		if(cbfList.get(this.indexActiveBF).isFull()==false){
			cbfList.get(this.indexActiveBF).add(element);
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
		for(int j=0;j<this.cbfList.size();j++){
			if(cbfList.get(j).lookup(element)==true){
				return true;
			}
		}
		return false;
	}
	
	/* probability of false positive */
	public double getAverageProbabilityFP(){
		double fpProb=0;
		for(int i=0;i<cbfList.size();i++){
			fpProb=fpProb+cbfList.get(i).getProbabilityFP();
		}
		return fpProb/(double)cbfList.size();
		
	}
	
	public double getProbabilityFPReal(){
		double fpProb=0;
		if(this.c<=this.numberOfAddedElement)fpProb=1-Math.pow(1-Math.pow(1-Math.exp(((double)-1*this.getK()*this.getCapacity())/(double)this.getM()), this.getK()), this.getNumberOfBloomFilter());
		else fpProb=cbfList.get(0).getProbabilityFPReal();
		return fpProb;
	}
}
