package test;

import java.util.List;
import bloomfilter.MPHS;
import data.Data;
import net.sourceforge.sizeof.SizeOf;


public class Test_MPHS {
	
	public static void main(String[] args){
		int size=10000;
		List<String> lst=Data.getArrayRandomString(10, 10000);
		MPHS mp=new MPHS(lst,0.0001);
		List<String> lst2=Data.getArrayRandomString(10, 10000);
		lst2.addAll(lst);
		int nfound=0;
		for(String s: lst2) if(mp.lookup(s)==false){System.out.println(s+"\t"+lst.indexOf(s)+"\t"+lst2.indexOf(s));nfound++;}
		System.out.println(nfound);
		System.out.println(SizeOf.humanReadable(SizeOf.deepSizeOf(mp)));
	}
	
	
}
