package test;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import bloomfilter.Compressed_CBF;
import bloomfilter.CountingBloomFilter;
import bloomfilter.SpectralBloomFilter_RM;
import data.Data;

public class Test_Delete {
	private static final String FILE_HEADER = "N.Element;"
			+ "Array (String);"
			+ "CBF(byte);Compressed CBF(byte);SPBF_RM(byte);"
			+ "CBF(short);Compressed CBF(short);SPBF_RM(short);"
			+ "CBF(int);Compressed CBF(int);SPBF_RM(int);"
			;
	private static final String FILE_HEADER_FP = "FP Probability;"
			+ "Array (String);"
			+ "CBF(byte);Compressed CBF(byte);SPBF_RM(byte);"
			+ "CBF(short);Compressed CBF(short);SPBF_RM(short);"
			+ "CBF(int);Compressed CBF(int);SPBF_RM(int);"
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
			fileWriter = new FileWriter("test_delete_csv.csv");
			fileWriter.append(FILE_HEADER.toString());
			fileWriter.append(NEW_LINE_SEPARATOR);
			
			for(int size=initialSize;size<=max;size=size+increment){
				
				
				CountingBloomFilter cbf_b=new CountingBloomFilter(fp, size,"Murmur3",Byte.class);
				CountingBloomFilter cbf_s=new CountingBloomFilter(fp, size,"Murmur3",Short.class);
				CountingBloomFilter cbf_i=new CountingBloomFilter(fp, size,"Murmur3",Integer.class);
				
				Compressed_CBF cbf_b_cmp=new Compressed_CBF(cbf_b);
				Compressed_CBF cbf_s_cmp=new Compressed_CBF(cbf_s);
				Compressed_CBF cbf_i_cmp=new Compressed_CBF(cbf_i);
				
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
				
				cbf_b.addAll(search);
				cbf_s.addAll(search);
				cbf_i.addAll(search);
				cbf_b_cmp.addAll(search);
				cbf_s_cmp.addAll(search);
				cbf_i_cmp.addAll(search);
				cbf_b_sp_rm.addAll(search);
				cbf_s_sp_rm.addAll(search);
				cbf_i_sp_rm.addAll(search);
				
				start=System.nanoTime();
				for(String s: addList)compareListString.remove(s);
				end=System.nanoTime();
				double arrayString_bench=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)cbf_b.delete(s);
				end=System.nanoTime();
				double cbf_b_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_b_cmp.delete(s);
				end=System.nanoTime();
				double cbf_b_cmp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_b_sp_rm.delete(s);
				end=System.nanoTime();
				double cbf_b_sp_rm_bench=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)cbf_s.delete(s);
				end=System.nanoTime();
				double cbf_s_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_s_cmp.delete(s);
				end=System.nanoTime();
				double cbf_s_cmp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_s_sp_rm.delete(s);
				end=System.nanoTime();
				double cbf_s_sp_rm_bench=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)cbf_i.delete(s);
				end=System.nanoTime();
				double cbf_i_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_i_cmp.delete(s);
				end=System.nanoTime();
				double cbf_i_cmp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_i_sp_rm.delete(s);
				end=System.nanoTime();
				double cbf_i_sp_rm_bench=Data.benchmark(start, end, size);
				
				

				String data=size+DELIMITER+
						arrayString_bench+DELIMITER+
						cbf_b_bench+DELIMITER+
						cbf_b_cmp_bench+DELIMITER+
						cbf_b_sp_rm_bench+DELIMITER+
						cbf_s_bench+DELIMITER+
						cbf_s_cmp_bench+DELIMITER+
						cbf_s_sp_rm_bench+DELIMITER+
						cbf_i_bench+DELIMITER+
						cbf_i_cmp_bench+DELIMITER+
						cbf_i_sp_rm_bench+DELIMITER+
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
			fileWriter = new FileWriter("test_delete_fp_increment_csv.csv");
			fileWriter.append(FILE_HEADER_FP.toString());
			fileWriter.append(NEW_LINE_SEPARATOR);
			int size=max;
			for(int fppPercent=0;fppPercent<maxFP;fppPercent=fppPercent*2){
				if(fppPercent==0)  fp=1;
				else fp=1.0/((double)fppPercent);
				
				
				CountingBloomFilter cbf_b=new CountingBloomFilter(fp, size,"Murmur3",Byte.class);
				CountingBloomFilter cbf_s=new CountingBloomFilter(fp, size,"Murmur3",Short.class);
				CountingBloomFilter cbf_i=new CountingBloomFilter(fp, size,"Murmur3",Integer.class);
				
				Compressed_CBF cbf_b_cmp=new Compressed_CBF(cbf_b);
				Compressed_CBF cbf_s_cmp=new Compressed_CBF(cbf_s);
				Compressed_CBF cbf_i_cmp=new Compressed_CBF(cbf_i);
				
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
				
				cbf_b.addAll(search);
				cbf_s.addAll(search);
				cbf_i.addAll(search);
				cbf_b_cmp.addAll(search);
				cbf_s_cmp.addAll(search);
				cbf_i_cmp.addAll(search);
				cbf_b_sp_rm.addAll(search);
				cbf_s_sp_rm.addAll(search);
				cbf_i_sp_rm.addAll(search);
				
				start=System.nanoTime();
				for(String s: addList)compareListString.remove(s);
				end=System.nanoTime();
				double arrayString_bench=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)cbf_b.delete(s);
				end=System.nanoTime();
				double cbf_b_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_b_cmp.delete(s);
				end=System.nanoTime();
				double cbf_b_cmp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_b_sp_rm.delete(s);
				end=System.nanoTime();
				double cbf_b_sp_rm_bench=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)cbf_s.delete(s);
				end=System.nanoTime();
				double cbf_s_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_s_cmp.delete(s);
				end=System.nanoTime();
				double cbf_s_cmp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_s_sp_rm.delete(s);
				end=System.nanoTime();
				double cbf_s_sp_rm_bench=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)cbf_i.delete(s);
				end=System.nanoTime();
				double cbf_i_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_i_cmp.delete(s);
				end=System.nanoTime();
				double cbf_i_cmp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_i_sp_rm.delete(s);
				end=System.nanoTime();
				double cbf_i_sp_rm_bench=Data.benchmark(start, end, size);
				
				

				String data=fp+DELIMITER+
						arrayString_bench+DELIMITER+
						cbf_b_bench+DELIMITER+
						cbf_b_cmp_bench+DELIMITER+
						cbf_b_sp_rm_bench+DELIMITER+
						cbf_s_bench+DELIMITER+
						cbf_s_cmp_bench+DELIMITER+
						cbf_s_sp_rm_bench+DELIMITER+
						cbf_i_bench+DELIMITER+
						cbf_i_cmp_bench+DELIMITER+
						cbf_i_sp_rm_bench+DELIMITER+
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
