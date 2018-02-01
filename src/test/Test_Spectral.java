package test;

import java.io.FileWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bloomfilter.Compressed_CBF;
import bloomfilter.Compressed_SBF;
import bloomfilter.CountingBloomFilter;
import bloomfilter.Dynamic_CBF;
import bloomfilter.Dynamic_SBF;
import bloomfilter.SpectralBloomFilter_MI;
import bloomfilter.SpectralBloomFilter_RM;
import bloomfilter.StandardBloomFilter;
import data.Data;
import net.sourceforge.sizeof.SizeOf;

public class Test_Spectral {
	
	private static final String FILE_HEADER = "N.Element;"
			+ "CBF(byte);SPBF_MI(byte);SPBF_RM(byte);"
			+ "CBF(short);SPBF_MI(short);SPBF_RM(short);"
			+ "CBF(int);SPBF_MI(int);SPBF_RM(int);"
			;
	private static final String DELIMITER = ";";
	private static final String NEW_LINE_SEPARATOR = "\n";
	
	private static double fp=0.001;
	private static int initialSize=1000;
	private static int max=25000;
	private static int increment=100;
	
	
	public static void main(String [] args){
		add();
		//space();
		//frequency();
		//lookup();
		//frequencyCompare();
	}
	
	public static void add(){
		FileWriter fileWriter = null;	
		long start=0;
		long end=0;
		try {
			fileWriter = new FileWriter("test_add_spbf_csv.csv");
			fileWriter.append(FILE_HEADER.toString());
			fileWriter.append(NEW_LINE_SEPARATOR);
			
			for(int size=1000;size<max;size=size+increment){
					
				CountingBloomFilter cbf_b=new CountingBloomFilter(fp, size,"Murmur3",Byte.class);
				CountingBloomFilter cbf_s=new CountingBloomFilter(fp, size,"Murmur3",Short.class);
				CountingBloomFilter cbf_i=new CountingBloomFilter(fp, size,"Murmur3",Integer.class);
				
				SpectralBloomFilter_MI cbf_b_sp_mi=new SpectralBloomFilter_MI(fp, size,"Murmur3",Byte.class);
				SpectralBloomFilter_MI cbf_s_sp_mi=new SpectralBloomFilter_MI(fp, size,"Murmur3",Short.class);
				SpectralBloomFilter_MI cbf_i_sp_mi=new SpectralBloomFilter_MI(fp, size,"Murmur3",Integer.class);
				
				SpectralBloomFilter_RM cbf_b_sp_rm=new SpectralBloomFilter_RM(fp, size,"Murmur3",Byte.class);
				SpectralBloomFilter_RM cbf_s_sp_rm=new SpectralBloomFilter_RM(fp, size,"Murmur3",Short.class);
				SpectralBloomFilter_RM cbf_i_sp_rm=new SpectralBloomFilter_RM(fp, size,"Murmur3",Integer.class);

				List<String> addList=Data.getRandomString(20,size);
				
				
				
				start=System.nanoTime();
				for(String s: addList)cbf_b.add(s);
				end=System.nanoTime();
				double cbf_b_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_b_sp_mi.add(s);
				end=System.nanoTime();
				double cbf_b_sp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_b_sp_rm.add(s);
				end=System.nanoTime();
				double cbf_b_sp_rm_bench=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)cbf_s.add(s);
				end=System.nanoTime();
				double cbf_s_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_s_sp_mi.add(s);
				end=System.nanoTime();
				double cbf_s_sp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_s_sp_rm.add(s);
				end=System.nanoTime();
				double cbf_s_sp_rm_bench=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)cbf_i.add(s);
				end=System.nanoTime();
				double cbf_i_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_i_sp_mi.add(s);
				end=System.nanoTime();
				double cbf_i_sp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_i_sp_rm.add(s);
				end=System.nanoTime();
				double cbf_i_sp_rm_bench=Data.benchmark(start, end, size);
				
				
				String data=size+DELIMITER+
						cbf_b_bench+DELIMITER+
						cbf_b_sp_bench+DELIMITER+
						cbf_b_sp_rm_bench+DELIMITER+
						cbf_s_bench+DELIMITER+
						cbf_s_sp_bench+DELIMITER+
						cbf_s_sp_rm_bench+DELIMITER+
						cbf_i_bench+DELIMITER+
						cbf_i_sp_bench+DELIMITER+
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
	public static void space(){
		FileWriter fileWriter = null;		
		try {
			fileWriter = new FileWriter("test_space_spbf_csv.csv");
			fileWriter.append(FILE_HEADER.toString());
			fileWriter.append(NEW_LINE_SEPARATOR);
			
			for(int size=initialSize;size<max;size=size+increment){
				
				CountingBloomFilter cbf_b=new CountingBloomFilter(fp, size,"Murmur3",Byte.class);
				CountingBloomFilter cbf_s=new CountingBloomFilter(fp, size,"Murmur3",Short.class);
				CountingBloomFilter cbf_i=new CountingBloomFilter(fp, size,"Murmur3",Integer.class);
				
				SpectralBloomFilter_MI cbf_b_sp_mi=new SpectralBloomFilter_MI(fp, size,"Murmur3",Byte.class);
				SpectralBloomFilter_MI cbf_s_sp_mi=new SpectralBloomFilter_MI(fp, size,"Murmur3",Short.class);
				SpectralBloomFilter_MI cbf_i_sp_mi=new SpectralBloomFilter_MI(fp, size,"Murmur3",Integer.class);
				
				SpectralBloomFilter_RM cbf_b_sp_rm=new SpectralBloomFilter_RM(fp, size,"Murmur3",Byte.class);
				SpectralBloomFilter_RM cbf_s_sp_rm=new SpectralBloomFilter_RM(fp, size,"Murmur3",Short.class);
				SpectralBloomFilter_RM cbf_i_sp_rm=new SpectralBloomFilter_RM(fp, size,"Murmur3",Integer.class);
	
				List<String> addList=Data.getRandomString(20,size);
	
				cbf_b.addAll(addList);
				cbf_s.addAll(addList);
				cbf_i.addAll(addList);
				cbf_b_sp_mi.addAll(addList);
				cbf_s_sp_mi.addAll(addList);
				cbf_i_sp_mi.addAll(addList);
				cbf_b_sp_rm.addAll(addList);
				cbf_s_sp_rm.addAll(addList);
				cbf_i_sp_rm.addAll(addList);
	
			
				long cbf_b_size=SizeOf.deepSizeOf(cbf_b);
				long cbf_b_sp_size=SizeOf.deepSizeOf(cbf_b_sp_mi);
				long cbf_b_sp_rm_size=SizeOf.deepSizeOf(cbf_b_sp_rm);
				
				long cbf_s_size=SizeOf.deepSizeOf(cbf_s);
				long cbf_s_sp_size=SizeOf.deepSizeOf(cbf_s_sp_mi);
				long cbf_s_sp_rm_size=SizeOf.deepSizeOf(cbf_s_sp_rm);
				
				long cbf_i_size=SizeOf.deepSizeOf(cbf_i);
				long cbf_i_sp_size=SizeOf.deepSizeOf(cbf_i_sp_mi);
				long cbf_i_sp_rm_size=SizeOf.deepSizeOf(cbf_i_sp_rm);
				
				
				
				String data=size+DELIMITER+
						cbf_b_size+DELIMITER+
						cbf_b_sp_size+DELIMITER+
						cbf_b_sp_rm_size+DELIMITER+
						cbf_s_size+DELIMITER+
						cbf_s_sp_size+DELIMITER+
						cbf_s_sp_rm_size+DELIMITER+
						cbf_i_size+DELIMITER+
						cbf_i_sp_size+DELIMITER+
						cbf_i_sp_rm_size+DELIMITER+
						NEW_LINE_SEPARATOR;
				System.out.print(data);
				fileWriter.append(data);	
			
				System.out.println(size+"\t"+cbf_i_sp_rm.getNumberOfAddedElement()+"\t"+cbf_i_sp_rm.getUniqueItems());
			}
				
			fileWriter.flush();
			fileWriter.close();
			
			System.out.println("CSV scritto !!!");
			
		} catch (Exception e) {
			System.out.println("Error");
			e.printStackTrace();
		} 
	}
	
	public static void frequency(){
		FileWriter fileWriter = null;	
		long start=0;
		long end=0;
		try {
			fileWriter = new FileWriter("test_frequency_spbf_csv.csv");
			fileWriter.append(FILE_HEADER.toString());
			fileWriter.append(NEW_LINE_SEPARATOR);
			
			for(int size=initialSize;size<max;size=size+increment){
				
				CountingBloomFilter cbf_b=new CountingBloomFilter(fp, size,"Murmur3",Byte.class);
				CountingBloomFilter cbf_s=new CountingBloomFilter(fp, size,"Murmur3",Short.class);
				CountingBloomFilter cbf_i=new CountingBloomFilter(fp, size,"Murmur3",Integer.class);
				
				SpectralBloomFilter_MI cbf_b_sp_mi=new SpectralBloomFilter_MI(fp, size,"Murmur3",Byte.class);
				SpectralBloomFilter_MI cbf_s_sp_mi=new SpectralBloomFilter_MI(fp, size,"Murmur3",Short.class);
				SpectralBloomFilter_MI cbf_i_sp_mi=new SpectralBloomFilter_MI(fp, size,"Murmur3",Integer.class);
				
				SpectralBloomFilter_RM cbf_b_sp_rm=new SpectralBloomFilter_RM(fp, size,"Murmur3",Byte.class);
				SpectralBloomFilter_RM cbf_s_sp_rm=new SpectralBloomFilter_RM(fp, size,"Murmur3",Short.class);
				SpectralBloomFilter_RM cbf_i_sp_rm=new SpectralBloomFilter_RM(fp, size,"Murmur3",Integer.class);
	

				List<String> addList=new ArrayList<String>();
				List<String> search=Data.getRandomString(20,size);
				
				for(int j=0;j<search.size();j++){
					if(Math.random()>0.5)addList.add(search.get(j));
				}
				addList.addAll(Data.getRandomString(20,size));
				cbf_b.addAll(search);
				cbf_s.addAll(search);
				cbf_i.addAll(search);
				cbf_b_sp_mi.addAll(search);
				cbf_s_sp_mi.addAll(search);
				cbf_i_sp_mi.addAll(search);
				cbf_b_sp_rm.addAll(search);
				cbf_s_sp_rm.addAll(search);
				cbf_i_sp_rm.addAll(search);
				
				start=System.nanoTime();
				for(String s: addList)cbf_b.lookup(s);
				end=System.nanoTime();
				double cbf_b_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_b_sp_mi.frequency(s);
				end=System.nanoTime();
				double cbf_b_sp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_b_sp_rm.frequency(s);
				end=System.nanoTime();
				double cbf_b_sp_rm_bench=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)cbf_s.lookup(s);
				end=System.nanoTime();
				double cbf_s_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_s_sp_mi.frequency(s);
				end=System.nanoTime();
				double cbf_s_sp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_s_sp_rm.frequency(s);
				end=System.nanoTime();
				double cbf_s_sp_rm_bench=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)cbf_i.lookup(s);
				end=System.nanoTime();
				double cbf_i_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_i_sp_mi.frequency(s);
				end=System.nanoTime();
				double cbf_i_sp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_i_sp_rm.frequency(s);
				end=System.nanoTime();
				double cbf_i_sp_rm_bench=Data.benchmark(start, end, size);
				
				
				String data=size+DELIMITER+
						cbf_b_bench+DELIMITER+
						cbf_b_sp_bench+DELIMITER+
						cbf_b_sp_rm_bench+DELIMITER+
						cbf_s_bench+DELIMITER+
						cbf_s_sp_bench+DELIMITER+
						cbf_s_sp_rm_bench+DELIMITER+
						cbf_i_bench+DELIMITER+
						cbf_i_sp_bench+DELIMITER+
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
	
	public static void lookup(){
		FileWriter fileWriter = null;	
		long start=0;
		long end=0;
		try {
			fileWriter = new FileWriter("test_lookup_spbf_csv.csv");
			fileWriter.append(FILE_HEADER.toString());
			fileWriter.append(NEW_LINE_SEPARATOR);
			
			for(int size=initialSize;size<max;size=size+increment){
				
				CountingBloomFilter cbf_b=new CountingBloomFilter(fp, size,"Murmur3",Byte.class);
				CountingBloomFilter cbf_s=new CountingBloomFilter(fp, size,"Murmur3",Short.class);
				CountingBloomFilter cbf_i=new CountingBloomFilter(fp, size,"Murmur3",Integer.class);
				
				SpectralBloomFilter_MI cbf_b_sp_mi=new SpectralBloomFilter_MI(fp, size,"Murmur3",Byte.class);
				SpectralBloomFilter_MI cbf_s_sp_mi=new SpectralBloomFilter_MI(fp, size,"Murmur3",Short.class);
				SpectralBloomFilter_MI cbf_i_sp_mi=new SpectralBloomFilter_MI(fp, size,"Murmur3",Integer.class);
				
				SpectralBloomFilter_RM cbf_b_sp_rm=new SpectralBloomFilter_RM(fp, size,"Murmur3",Byte.class);
				SpectralBloomFilter_RM cbf_s_sp_rm=new SpectralBloomFilter_RM(fp, size,"Murmur3",Short.class);
				SpectralBloomFilter_RM cbf_i_sp_rm=new SpectralBloomFilter_RM(fp, size,"Murmur3",Integer.class);
	

				List<String> addList=new ArrayList<String>();
				List<String> search=Data.getRandomString(20,size);
				
				for(int j=0;j<search.size();j++){
					if(Math.random()>0.5)addList.add(search.get(j));
				}
				addList.addAll(Data.getRandomString(20,size));
				cbf_b.addAll(search);
				cbf_s.addAll(search);
				cbf_i.addAll(search);
				cbf_b_sp_mi.addAll(search);
				cbf_s_sp_mi.addAll(search);
				cbf_i_sp_mi.addAll(search);
				cbf_b_sp_rm.addAll(search);
				cbf_s_sp_rm.addAll(search);
				cbf_i_sp_rm.addAll(search);
				
				start=System.nanoTime();
				for(String s: addList)cbf_b.lookup(s);
				end=System.nanoTime();
				double cbf_b_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_b_sp_mi.lookup(s);
				end=System.nanoTime();
				double cbf_b_sp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_b_sp_rm.lookup(s);
				end=System.nanoTime();
				double cbf_b_sp_rm_bench=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)cbf_s.lookup(s);
				end=System.nanoTime();
				double cbf_s_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_s_sp_mi.lookup(s);
				end=System.nanoTime();
				double cbf_s_sp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_s_sp_rm.lookup(s);
				end=System.nanoTime();
				double cbf_s_sp_rm_bench=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)cbf_i.lookup(s);
				end=System.nanoTime();
				double cbf_i_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_i_sp_mi.lookup(s);
				end=System.nanoTime();
				double cbf_i_sp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_i_sp_rm.lookup(s);
				end=System.nanoTime();
				double cbf_i_sp_rm_bench=Data.benchmark(start, end, size);
				
				
				String data=size+DELIMITER+
						cbf_b_bench+DELIMITER+
						cbf_b_sp_bench+DELIMITER+
						cbf_b_sp_rm_bench+DELIMITER+
						cbf_s_bench+DELIMITER+
						cbf_s_sp_bench+DELIMITER+
						cbf_s_sp_rm_bench+DELIMITER+
						cbf_i_bench+DELIMITER+
						cbf_i_sp_bench+DELIMITER+
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

	public static void frequencyCompare(){
		try {
			
			int size=1000000;
				
				CountingBloomFilter cbf_i=new CountingBloomFilter(fp, size,"Murmur3",Integer.class);
				
				SpectralBloomFilter_MI cbf_i_sp_mi=new SpectralBloomFilter_MI(fp, size,"Murmur3",Integer.class);
				
				SpectralBloomFilter_RM cbf_i_sp_rm=new SpectralBloomFilter_RM(fp, size,"Murmur3",Integer.class);
	

				List<String> addList=Data.getRandomString(20,size);
				
				cbf_i.addAll(addList);
				cbf_i_sp_mi.addAll(addList);
				cbf_i_sp_rm.addAll(addList);			
			
		} catch (Exception e) {
			System.out.println("Error");
			e.printStackTrace();
		} 
	}
}
