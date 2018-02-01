package bloomfilter;

import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.hash.Hashing;

import data.Data;
import it.unimi.dsi.bits.TransformationStrategies;
import it.unimi.dsi.sux4j.mph.GOVMinimalPerfectHashFunction;
import net.sourceforge.sizeof.SizeOf;

public class MPHS {
	private int n;
	private double fp_prob;
	private double n_bits;
	private int range;
	private GOVMinimalPerfectHashFunction<String> mph ;
	private int[] signature;
	
	public MPHS(List<String> aS,double fp){
		List<String> list=(List<String>)Arrays.asList(new HashSet<String>(aS).toArray(new String[0]));
		this.fp_prob=fp;
		n=list.size();
		n_bits=Math.log(1.0/this.fp_prob) / Math.log(2);
		range=(int)Math.ceil(Math.pow(2,n_bits));
		signature=new int[n];
		try {
			mph= new GOVMinimalPerfectHashFunction.Builder<String>().keys( list).transform( TransformationStrategies.utf32() ).signed( 32).build();
			for (int i=0;i<n;i++) signature[(int)mph.getLong(list.get(i))]=getSignature(list.get(i));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean lookup(String s){
		if(mph.getLong(s)!=-1)
			if (getSignature(s)==signature[(int) mph.getLong(s)]) return true;
			else return false;
		else return false;
	}
	

	private int getSignature(Object c){
		return (int) (c.hashCode() % range);
	}
	

}
