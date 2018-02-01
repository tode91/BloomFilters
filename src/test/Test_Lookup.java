package test;

import java.io.FileWriter;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import bloomfilter.Compressed_CBF;
import bloomfilter.Compressed_SBF;
import bloomfilter.CountingBloomFilter;
import bloomfilter.Dynamic_CBF;
import bloomfilter.Dynamic_SBF;
import bloomfilter.MPHS;
import bloomfilter.SpectralBloomFilter_MI;
import bloomfilter.SpectralBloomFilter_RM;
import bloomfilter.StandardBloomFilter;
import data.Data;

public class Test_Lookup {
	private static final String FILE_HEADER = "N.Element;"
			+ "Array (String);Array (binary search);"
			+ "MPHS;"
			+ "SBF;Compressed SBF;Dynamic SBF;"
			+ "CBF(byte);Compressed CBF(byte);Dynamic CBF(byte);SPBF_MI(byte);SPBF_RM(byte);SPBF_RM(byte) frequency;"
			+ "CBF(short);Compressed CBF(short);Dynamic CBF(short);SPBF_MI(short);SPBF_RM(short);SPBF_RM(short) frequency;"
			+ "CBF(int);Compressed CBF(int);Dynamic CBF(int);SPBF_MI(int);SPBF_RM(int);SPBF_RM(int) frequency;"
			;
	private static final String FILE_HEADER_FP = "FP Probability;"
			+ "Array (String);Array (binary search);"
			+ "MPHS;"
			+ "SBF;Compressed SBF;Dynamic SBF;"
			+ "CBF(byte);Compressed CBF(byte);Dynamic CBF(byte);SPBF_MI(byte);SPBF_RM(byte);SPBF_RM(byte) frequency;"
			+ "CBF(short);Compressed CBF(short);Dynamic CBF(short);SPBF_MI(short);SPBF_RM(short);SPBF_RM(short) frequency;"
			+ "CBF(int);Compressed CBF(int);Dynamic CBF(int);SPBF_MI(int);SPBF_RM(int);SPBF_RM(int) frequency;"
			;
	private static final String DELIMITER = ";";
	private static final String NEW_LINE_SEPARATOR = "\n";
	
	private static int initialSize=1000;
	private static int max=50000;
	private static int increment=100;
	private static int maxFP=5000;
	
	public static void main(String[] args){incrementSize();incrementFP();}

	public static void incrementSize(){
		double fp=0.001;
		FileWriter fileWriter = null;	
		long start=0;
		long end=0;
		try {
			fileWriter = new FileWriter("test_lookup_csv.csv");
			fileWriter.append(FILE_HEADER.toString());
			fileWriter.append(NEW_LINE_SEPARATOR);
			
			for(int size=initialSize;size<=max;size=size+increment){
				
				StandardBloomFilter sbf=new StandardBloomFilter(fp, size,"Murmur3");
				
				CountingBloomFilter cbf_b=new CountingBloomFilter(fp, size,"Murmur3",Byte.class);
				CountingBloomFilter cbf_s=new CountingBloomFilter(fp, size,"Murmur3",Short.class);
				CountingBloomFilter cbf_i=new CountingBloomFilter(fp, size,"Murmur3",Integer.class);
				
				Compressed_SBF sbf_cmp=new Compressed_SBF(sbf);
				Compressed_CBF cbf_b_cmp=new Compressed_CBF(cbf_b);
				Compressed_CBF cbf_s_cmp=new Compressed_CBF(cbf_s);
				Compressed_CBF cbf_i_cmp=new Compressed_CBF(cbf_i);
				
				Dynamic_SBF sbf_dy=new Dynamic_SBF(fp,initialSize);
				Dynamic_CBF cbf_b_dy=new Dynamic_CBF(fp,initialSize,Byte.class);
				Dynamic_CBF cbf_s_dy=new Dynamic_CBF(fp,initialSize,Short.class);
				Dynamic_CBF cbf_i_dy=new Dynamic_CBF(fp,initialSize,Integer.class);
				SpectralBloomFilter_MI cbf_b_sp=new SpectralBloomFilter_MI(fp, size,"Murmur3",Byte.class);
				SpectralBloomFilter_MI cbf_s_sp=new SpectralBloomFilter_MI(fp, size,"Murmur3",Short.class);
				SpectralBloomFilter_MI cbf_i_sp=new SpectralBloomFilter_MI(fp, size,"Murmur3",Integer.class);
				SpectralBloomFilter_RM cbf_b_sp_rm=new SpectralBloomFilter_RM(fp, size,"Murmur3",Byte.class);
				SpectralBloomFilter_RM cbf_s_sp_rm=new SpectralBloomFilter_RM(fp, size,"Murmur3",Short.class);
				SpectralBloomFilter_RM cbf_i_sp_rm=new SpectralBloomFilter_RM(fp, size,"Murmur3",Integer.class);

				List<String> compareListString=new ArrayList<String>();

				List<String> addList=new ArrayList<String>();
				List<String> search=Data.getRandomString(20,size);
				
				for(int j=0;j<search.size();j++){
					if(new Random().nextInt(1)==0)addList.add(search.get(j));
				}
				addList.addAll(Data.getRandomString(20,size/2));
				//addList=search;

				for(String s: search)compareListString.add(s);
				Collections.sort(compareListString);
				
				sbf.addAll(search);
				cbf_b.addAll(search);
				cbf_s.addAll(search);
				cbf_i.addAll(search);
				sbf_cmp.addAll(search);
				cbf_b_cmp.addAll(search);
				cbf_s_cmp.addAll(search);
				cbf_i_cmp.addAll(search);
				sbf_dy.addAll(search);
				cbf_b_dy.addAll(search);
				cbf_s_dy.addAll(search);
				cbf_i_dy.addAll(search);
				cbf_b_sp.addAll(search);
				cbf_s_sp.addAll(search);
				cbf_i_sp.addAll(search);
				cbf_b_sp_rm.addAll(search);
				cbf_s_sp_rm.addAll(search);
				cbf_i_sp_rm.addAll(search);
				
				MPHS mph=new MPHS(search,fp);

				start=System.nanoTime();
				for(String s: addList)mph.lookup(s);
				end=System.nanoTime();
				double mph_bench=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)compareListString.contains(s);
				end=System.nanoTime();
				double arrayString_bench=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)Collections.binarySearch(compareListString, s);
				end=System.nanoTime();
				double arrayString_bench_binary=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)sbf.lookup(s);
				end=System.nanoTime();
				double sbf_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)sbf_cmp.lookup(s);
				end=System.nanoTime();
				double sbf_cmp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)sbf_dy.lookup(s);
				end=System.nanoTime();
				double sbf_dy_bench=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)cbf_b.lookup(s);
				end=System.nanoTime();
				double cbf_b_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_b_cmp.lookup(s);
				end=System.nanoTime();
				double cbf_b_cmp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_b_dy.lookup(s);
				end=System.nanoTime();
				double cbf_b_dy_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_b_sp.lookup(s);
				end=System.nanoTime();
				double cbf_b_sp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_b_sp_rm.lookup(s);
				end=System.nanoTime();
				double cbf_b_sp_rm_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_b_sp.frequency(s);
				end=System.nanoTime();
				double cbf_b_sp_fr_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_b_sp_rm.frequency(s);
				end=System.nanoTime();
				double cbf_b_sp_rm_fr_bench=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)cbf_s.lookup(s);
				end=System.nanoTime();
				double cbf_s_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_s_cmp.lookup(s);
				end=System.nanoTime();
				double cbf_s_cmp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_s_dy.lookup(s);
				end=System.nanoTime();
				double cbf_s_dy_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_s_sp.lookup(s);
				end=System.nanoTime();
				double cbf_s_sp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_s_sp_rm.lookup(s);
				end=System.nanoTime();
				double cbf_s_sp_rm_bench=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)cbf_s_sp.frequency(s);
				end=System.nanoTime();
				double cbf_s_sp_fr_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_s_sp_rm.frequency(s);
				end=System.nanoTime();
				double cbf_s_sp_rm_fr_bench=Data.benchmark(start, end, size);
				
				
				start=System.nanoTime();
				for(String s: addList)cbf_i.lookup(s);
				end=System.nanoTime();
				double cbf_i_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_i_cmp.lookup(s);
				end=System.nanoTime();
				double cbf_i_cmp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_i_dy.lookup(s);
				end=System.nanoTime();
				double cbf_i_dy_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_i_sp.lookup(s);
				end=System.nanoTime();
				double cbf_i_sp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_i_sp_rm.lookup(s);
				end=System.nanoTime();
				double cbf_i_sp_rm_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_i_sp.frequency(s);
				end=System.nanoTime();
				double cbf_i_sp_fr_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_i_sp_rm.frequency(s);
				end=System.nanoTime();
				double cbf_i_sp_rm_fr_bench=Data.benchmark(start, end, size);
				
				

				String data=size+DELIMITER+
						arrayString_bench+DELIMITER+
						arrayString_bench_binary+DELIMITER+
						mph_bench+DELIMITER+
						sbf_bench+DELIMITER+
						sbf_cmp_bench+DELIMITER+
						sbf_dy_bench+DELIMITER+
						cbf_b_bench+DELIMITER+
						cbf_b_cmp_bench+DELIMITER+
						cbf_b_dy_bench+DELIMITER+
						cbf_b_sp_bench+DELIMITER+
						cbf_b_sp_fr_bench+DELIMITER+
						cbf_b_sp_rm_bench+DELIMITER+
						cbf_b_sp_rm_fr_bench+DELIMITER+
						cbf_s_bench+DELIMITER+
						cbf_s_cmp_bench+DELIMITER+
						cbf_s_dy_bench+DELIMITER+
						cbf_s_sp_bench+DELIMITER+
						cbf_s_sp_fr_bench+DELIMITER+
						cbf_s_sp_rm_bench+DELIMITER+
						cbf_s_sp_rm_fr_bench+DELIMITER+
						cbf_i_bench+DELIMITER+
						cbf_i_cmp_bench+DELIMITER+
						cbf_i_dy_bench+DELIMITER+
						cbf_i_sp_bench+DELIMITER+
						cbf_i_sp_fr_bench+DELIMITER+
						cbf_i_sp_rm_bench+DELIMITER+
						cbf_i_sp_rm_fr_bench+DELIMITER+
						NEW_LINE_SEPARATOR;
				
				data=data.replace(".",",");
				System.out.print(data);
				fileWriter.append(data);	
				
			}
				
			fileWriter.flush();
			fileWriter.close();
			
			System.out.println("CSV scritto !!!");
			
		} catch (Exception e) {
			System.out.println("Error");
			e.printStackTrace();
		} 
	}
	
	public static void incrementFP(){
		double fp=1;
		FileWriter fileWriter = null;	
		long start=0;
		long end=0;
		try {
			fileWriter = new FileWriter("test_lookup_fp_increment_csv.csv");
			fileWriter.append(FILE_HEADER_FP.toString());
			fileWriter.append(NEW_LINE_SEPARATOR);
			int size=max;
			for(int fppPercent=0;fppPercent<maxFP;fppPercent=fppPercent*2){
				if(fppPercent==0)  fp=1;
				else fp=1.0/((double)fppPercent);
				StandardBloomFilter sbf=new StandardBloomFilter(fp, size,"Murmur3");
				
				CountingBloomFilter cbf_b=new CountingBloomFilter(fp, size,"Murmur3",Byte.class);
				CountingBloomFilter cbf_s=new CountingBloomFilter(fp, size,"Murmur3",Short.class);
				CountingBloomFilter cbf_i=new CountingBloomFilter(fp, size,"Murmur3",Integer.class);
				
				Compressed_SBF sbf_cmp=new Compressed_SBF(sbf);
				Compressed_CBF cbf_b_cmp=new Compressed_CBF(cbf_b);
				Compressed_CBF cbf_s_cmp=new Compressed_CBF(cbf_s);
				Compressed_CBF cbf_i_cmp=new Compressed_CBF(cbf_i);
				
				Dynamic_SBF sbf_dy=new Dynamic_SBF(fp,initialSize);
				Dynamic_CBF cbf_b_dy=new Dynamic_CBF(fp,initialSize,Byte.class);
				Dynamic_CBF cbf_s_dy=new Dynamic_CBF(fp,initialSize,Short.class);
				Dynamic_CBF cbf_i_dy=new Dynamic_CBF(fp,initialSize,Integer.class);
				SpectralBloomFilter_MI cbf_b_sp=new SpectralBloomFilter_MI(fp, size,"Murmur3",Byte.class);
				SpectralBloomFilter_MI cbf_s_sp=new SpectralBloomFilter_MI(fp, size,"Murmur3",Short.class);
				SpectralBloomFilter_MI cbf_i_sp=new SpectralBloomFilter_MI(fp, size,"Murmur3",Integer.class);
				SpectralBloomFilter_RM cbf_b_sp_rm=new SpectralBloomFilter_RM(fp, size,"Murmur3",Byte.class);
				SpectralBloomFilter_RM cbf_s_sp_rm=new SpectralBloomFilter_RM(fp, size,"Murmur3",Short.class);
				SpectralBloomFilter_RM cbf_i_sp_rm=new SpectralBloomFilter_RM(fp, size,"Murmur3",Integer.class);

				List<String> compareListString=new ArrayList<String>();

				List<String> addList=new ArrayList<String>();
				List<String> search=Data.getRandomString(20,size);
				
				for(int j=0;j<search.size();j++){
					if(new Random().nextInt(1)==0)addList.add(search.get(j));
				}
				addList.addAll(Data.getRandomString(20,size));
				//addList=search;

				for(String s: search)compareListString.add(s);
				Collections.sort(compareListString);
				
				sbf.addAll(search);
				cbf_b.addAll(search);
				cbf_s.addAll(search);
				cbf_i.addAll(search);
				sbf_cmp.addAll(search);
				cbf_b_cmp.addAll(search);
				cbf_s_cmp.addAll(search);
				cbf_i_cmp.addAll(search);
				sbf_dy.addAll(search);
				cbf_b_dy.addAll(search);
				cbf_s_dy.addAll(search);
				cbf_i_dy.addAll(search);
				cbf_b_sp.addAll(search);
				cbf_s_sp.addAll(search);
				cbf_i_sp.addAll(search);
				cbf_b_sp_rm.addAll(search);
				cbf_s_sp_rm.addAll(search);
				cbf_i_sp_rm.addAll(search);
				
				MPHS mph=new MPHS(search,fp);

				start=System.nanoTime();
				for(String s: addList)mph.lookup(s);
				end=System.nanoTime();
				double mph_bench=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)compareListString.contains(s);
				end=System.nanoTime();
				double arrayString_bench=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)Collections.binarySearch(compareListString, s);
				end=System.nanoTime();
				double arrayString_bench_binary=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)sbf.lookup(s);
				end=System.nanoTime();
				double sbf_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)sbf_cmp.lookup(s);
				end=System.nanoTime();
				double sbf_cmp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)sbf_dy.lookup(s);
				end=System.nanoTime();
				double sbf_dy_bench=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)cbf_b.lookup(s);
				end=System.nanoTime();
				double cbf_b_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_b_cmp.lookup(s);
				end=System.nanoTime();
				double cbf_b_cmp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_b_dy.lookup(s);
				end=System.nanoTime();
				double cbf_b_dy_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_b_sp.lookup(s);
				end=System.nanoTime();
				double cbf_b_sp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_b_sp_rm.lookup(s);
				end=System.nanoTime();
				double cbf_b_sp_rm_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_b_sp.frequency(s);
				end=System.nanoTime();
				double cbf_b_sp_fr_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_b_sp_rm.frequency(s);
				end=System.nanoTime();
				double cbf_b_sp_rm_fr_bench=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)cbf_s.lookup(s);
				end=System.nanoTime();
				double cbf_s_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_s_cmp.lookup(s);
				end=System.nanoTime();
				double cbf_s_cmp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_s_dy.lookup(s);
				end=System.nanoTime();
				double cbf_s_dy_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_s_sp.lookup(s);
				end=System.nanoTime();
				double cbf_s_sp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_s_sp_rm.lookup(s);
				end=System.nanoTime();
				double cbf_s_sp_rm_bench=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)cbf_s_sp.frequency(s);
				end=System.nanoTime();
				double cbf_s_sp_fr_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_s_sp_rm.frequency(s);
				end=System.nanoTime();
				double cbf_s_sp_rm_fr_bench=Data.benchmark(start, end, size);
				
				
				start=System.nanoTime();
				for(String s: addList)cbf_i.lookup(s);
				end=System.nanoTime();
				double cbf_i_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_i_cmp.lookup(s);
				end=System.nanoTime();
				double cbf_i_cmp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_i_dy.lookup(s);
				end=System.nanoTime();
				double cbf_i_dy_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_i_sp.lookup(s);
				end=System.nanoTime();
				double cbf_i_sp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_i_sp_rm.lookup(s);
				end=System.nanoTime();
				double cbf_i_sp_rm_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_i_sp.frequency(s);
				end=System.nanoTime();
				double cbf_i_sp_fr_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_i_sp_rm.frequency(s);
				end=System.nanoTime();
				double cbf_i_sp_rm_fr_bench=Data.benchmark(start, end, size);
				
				

				String data=fp+DELIMITER+
						arrayString_bench+DELIMITER+
						arrayString_bench_binary+DELIMITER+
						mph_bench+DELIMITER+
						sbf_bench+DELIMITER+
						sbf_cmp_bench+DELIMITER+
						sbf_dy_bench+DELIMITER+
						cbf_b_bench+DELIMITER+
						cbf_b_cmp_bench+DELIMITER+
						cbf_b_dy_bench+DELIMITER+
						cbf_b_sp_bench+DELIMITER+
						cbf_b_sp_fr_bench+DELIMITER+
						cbf_b_sp_rm_bench+DELIMITER+
						cbf_b_sp_rm_fr_bench+DELIMITER+
						cbf_s_bench+DELIMITER+
						cbf_s_cmp_bench+DELIMITER+
						cbf_s_dy_bench+DELIMITER+
						cbf_s_sp_bench+DELIMITER+
						cbf_s_sp_fr_bench+DELIMITER+
						cbf_s_sp_rm_bench+DELIMITER+
						cbf_s_sp_rm_fr_bench+DELIMITER+
						cbf_i_bench+DELIMITER+
						cbf_i_cmp_bench+DELIMITER+
						cbf_i_dy_bench+DELIMITER+
						cbf_i_sp_bench+DELIMITER+
						cbf_i_sp_fr_bench+DELIMITER+
						cbf_i_sp_rm_bench+DELIMITER+
						cbf_i_sp_rm_fr_bench+DELIMITER+
						NEW_LINE_SEPARATOR;
				
				data=data.replace(".",",");
				System.out.print(data);
				fileWriter.append(data);	
				
			}
				
			fileWriter.flush();
			fileWriter.close();
			
			System.out.println("CSV scritto !!!");
			
		} catch (Exception e) {
			System.out.println("Error");
			e.printStackTrace();
		} 
	}
}
