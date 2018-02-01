package test;

import java.io.FileWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

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

public class Test_Add {
	private static final String FILE_HEADER = "N.Element;"
			+ "Array (String);"
			+ "Array (byte);"
			+ "MPHS;"
			+ "SBF;Compressed SBF;Dynamic SBF;"
			+ "CBF(byte);Compressed CBF(byte);Dynamic CBF(byte);SPBF_MI(byte);SPBF_RM(byte);"
			+ "CBF(short);Compressed CBF(short);Dynamic CBF(short);SPBF_MI(short);SPBF_RM(short);"
			+ "CBF(int);Compressed CBF(int);Dynamic CBF(int);SPBF_MI(int);SPBF_RM(int);"
			;
	private static final String FILE_HEADER_FP = "FP Probability;"
			+ "Array (String);"
			+ "Array (byte);"
			+ "MPHS;"
			+ "SBF;Compressed SBF;Dynamic SBF;"
			+ "CBF(byte);Compressed CBF(byte);Dynamic CBF(byte);SPBF_MI(byte);SPBF_RM(byte);"
			+ "CBF(short);Compressed CBF(short);Dynamic CBF(short);SPBF_MI(short);SPBF_RM(short);"
			+ "CBF(int);Compressed CBF(int);Dynamic CBF(int);SPBF_MI(int);SPBF_RM(int);"
			;
	private static final String DELIMITER = ";";
	private static final String NEW_LINE_SEPARATOR = "\n";
	

	private static int initialSize=1000;
	private static int max=50000;
	private static int maxFP=1000;
	private static int increment=100;
	
	public static void main(String[] args){/*incrementSize();*/incrementFP();}
	public static void incrementSize(){
		double fp=0.001;
		FileWriter fileWriter = null;	
		long start=0;
		long end=0;
		try {
			fileWriter = new FileWriter("test_add_csv.csv");
			fileWriter.append(FILE_HEADER.toString());
			fileWriter.append(NEW_LINE_SEPARATOR);
			
			for(int size=1000;size<=max;size=size+increment){
				
				StandardBloomFilter sbf=new StandardBloomFilter(fp, size,"Murmur3");
				
				CountingBloomFilter cbf_b=new CountingBloomFilter(fp, size,"Murmur3",Byte.class);
				CountingBloomFilter cbf_s=new CountingBloomFilter(fp, size,"Murmur3",Short.class);
				CountingBloomFilter cbf_i=new CountingBloomFilter(fp, size,"Murmur3",Integer.class);
				
				Compressed_SBF sbf_cmp=new Compressed_SBF(sbf);
				Compressed_CBF cbf_b_cmp=new Compressed_CBF(cbf_b);
				Compressed_CBF cbf_s_cmp=new Compressed_CBF(cbf_s);
				Compressed_CBF cbf_i_cmp=new Compressed_CBF(cbf_i);
				Dynamic_SBF sbf_dy;
				Dynamic_CBF cbf_b_dy;
				Dynamic_CBF cbf_s_dy;
				Dynamic_CBF cbf_i_dy;
				
				if(size<initialSize){
					sbf_dy=new Dynamic_SBF(fp,size);
					 cbf_b_dy=new Dynamic_CBF(fp,size,Byte.class);
					 cbf_s_dy=new Dynamic_CBF(fp,size,Short.class);
					 cbf_i_dy=new Dynamic_CBF(fp,size,Integer.class);
				}else{
				
				 sbf_dy=new Dynamic_SBF(fp,initialSize);
				 cbf_b_dy=new Dynamic_CBF(fp,initialSize,Byte.class);
				 cbf_s_dy=new Dynamic_CBF(fp,initialSize,Short.class);
				 cbf_i_dy=new Dynamic_CBF(fp,initialSize,Integer.class);
				
				}
				SpectralBloomFilter_MI cbf_b_sp=new SpectralBloomFilter_MI(fp, size,"Murmur3",Byte.class);
				SpectralBloomFilter_MI cbf_s_sp=new SpectralBloomFilter_MI(fp, size,"Murmur3",Short.class);
				SpectralBloomFilter_MI cbf_i_sp=new SpectralBloomFilter_MI(fp, size,"Murmur3",Integer.class);
				SpectralBloomFilter_RM cbf_b_sp_rm=new SpectralBloomFilter_RM(fp, size,"Murmur3",Byte.class);
				SpectralBloomFilter_RM cbf_s_sp_rm=new SpectralBloomFilter_RM(fp, size,"Murmur3",Short.class);
				SpectralBloomFilter_RM cbf_i_sp_rm=new SpectralBloomFilter_RM(fp, size,"Murmur3",Integer.class);


				List<byte[]> compareListByte=new ArrayList<byte[]>();
				List<String> compareListString=new ArrayList<String>();

				List<String> addList=Data.getRandomString(20,size);
				
				start=System.nanoTime();
				MPHS mph=new MPHS(addList,fp);
				end=System.nanoTime();
				double mph_bench=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)compareListByte.add(s.getBytes(Charset.forName("UTF-8")));
				end=System.nanoTime();
				double arrayByte_bench=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)compareListString.add(s);
				end=System.nanoTime();
				double arrayString_bench=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)sbf.add(s);
				end=System.nanoTime();
				double sbf_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)sbf_cmp.add(s);
				end=System.nanoTime();
				double sbf_cmp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)sbf_dy.add(s);
				end=System.nanoTime();
				double sbf_dy_bench=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)cbf_b.add(s);
				end=System.nanoTime();
				double cbf_b_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_b_cmp.add(s);
				end=System.nanoTime();
				double cbf_b_cmp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_b_dy.add(s);
				end=System.nanoTime();
				double cbf_b_dy_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_b_sp.add(s);
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
				for(String s: addList)cbf_s_cmp.add(s);
				end=System.nanoTime();
				double cbf_s_cmp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_s_dy.add(s);
				end=System.nanoTime();
				double cbf_s_dy_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_s_sp.add(s);
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
				for(String s: addList)cbf_i_cmp.add(s);
				end=System.nanoTime();
				double cbf_i_cmp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_i_dy.add(s);
				end=System.nanoTime();
				double cbf_i_dy_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_i_sp.add(s);
				end=System.nanoTime();
				double cbf_i_sp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_i_sp_rm.add(s);
				end=System.nanoTime();
				double cbf_i_sp_rm_bench=Data.benchmark(start, end, size);
				
				
				String data=size+DELIMITER+
						arrayString_bench+DELIMITER+
						arrayByte_bench+DELIMITER+
						mph_bench+DELIMITER+
						sbf_bench+DELIMITER+
						sbf_cmp_bench+DELIMITER+
						sbf_dy_bench+DELIMITER+
						cbf_b_bench+DELIMITER+
						cbf_b_cmp_bench+DELIMITER+
						cbf_b_dy_bench+DELIMITER+
						cbf_b_sp_bench+DELIMITER+
						cbf_b_sp_rm_bench+DELIMITER+
						cbf_s_bench+DELIMITER+
						cbf_s_cmp_bench+DELIMITER+
						cbf_s_dy_bench+DELIMITER+
						cbf_s_sp_bench+DELIMITER+
						cbf_s_sp_rm_bench+DELIMITER+
						cbf_i_bench+DELIMITER+
						cbf_i_cmp_bench+DELIMITER+
						cbf_i_dy_bench+DELIMITER+
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
	
	public static void incrementFP(){
		double fp=1;
		FileWriter fileWriter = null;	
		long start=0;
		long end=0;
		try {
			fileWriter = new FileWriter("test_add_increment_fp_csv.csv");
			fileWriter.append(FILE_HEADER_FP.toString());
			fileWriter.append(NEW_LINE_SEPARATOR);
			int size=5000;
			for(int fppPercent=1;fppPercent<maxFP;fppPercent=fppPercent+5){
				fp=1.0/((double)fppPercent);
				
				StandardBloomFilter sbf=new StandardBloomFilter(fp, size,"Murmur3");
				
				CountingBloomFilter cbf_b=new CountingBloomFilter(fp, size,"Murmur3",Byte.class);
				CountingBloomFilter cbf_s=new CountingBloomFilter(fp, size,"Murmur3",Short.class);
				CountingBloomFilter cbf_i=new CountingBloomFilter(fp, size,"Murmur3",Integer.class);
				
				Compressed_SBF sbf_cmp=new Compressed_SBF(sbf);
				Compressed_CBF cbf_b_cmp=new Compressed_CBF(cbf_b);
				Compressed_CBF cbf_s_cmp=new Compressed_CBF(cbf_s);
				Compressed_CBF cbf_i_cmp=new Compressed_CBF(cbf_i);
				Dynamic_SBF sbf_dy;
				Dynamic_CBF cbf_b_dy;
				Dynamic_CBF cbf_s_dy;
				Dynamic_CBF cbf_i_dy;
				
				if(size<initialSize){
					sbf_dy=new Dynamic_SBF(fp,size);
					 cbf_b_dy=new Dynamic_CBF(fp,size,Byte.class);
					 cbf_s_dy=new Dynamic_CBF(fp,size,Short.class);
					 cbf_i_dy=new Dynamic_CBF(fp,size,Integer.class);
				}else{
				
				 sbf_dy=new Dynamic_SBF(fp,initialSize);
				 cbf_b_dy=new Dynamic_CBF(fp,initialSize,Byte.class);
				 cbf_s_dy=new Dynamic_CBF(fp,initialSize,Short.class);
				 cbf_i_dy=new Dynamic_CBF(fp,initialSize,Integer.class);
				
				}
	/*			SpectralBloomFilter_MI cbf_b_sp=new SpectralBloomFilter_MI(fp, size,"Murmur3",Byte.class);
				SpectralBloomFilter_MI cbf_s_sp=new SpectralBloomFilter_MI(fp, size,"Murmur3",Short.class);
				SpectralBloomFilter_MI cbf_i_sp=new SpectralBloomFilter_MI(fp, size,"Murmur3",Integer.class);
				SpectralBloomFilter_RM cbf_b_sp_rm=new SpectralBloomFilter_RM(fp, size,"Murmur3",Byte.class);
				SpectralBloomFilter_RM cbf_s_sp_rm=new SpectralBloomFilter_RM(fp, size,"Murmur3",Short.class);
				SpectralBloomFilter_RM cbf_i_sp_rm=new SpectralBloomFilter_RM(fp, size,"Murmur3",Integer.class); */


				List<byte[]> compareListByte=new ArrayList<byte[]>();
				List<String> compareListString=new ArrayList<String>();

				List<String> addList=Data.getRandomString(20,size);
				
				start=System.nanoTime();
			//	MPHS mph=new MPHS(addList,fp);
				end=System.nanoTime();
				double mph_bench=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)compareListByte.add(s.getBytes(Charset.forName("UTF-8")));
				end=System.nanoTime();
				double arrayByte_bench=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)compareListString.add(s);
				end=System.nanoTime();
				double arrayString_bench=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)sbf.add(s);
				end=System.nanoTime();
				double sbf_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)sbf_cmp.add(s);
				end=System.nanoTime();
				double sbf_cmp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)sbf_dy.add(s);
				end=System.nanoTime();
				double sbf_dy_bench=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)cbf_b.add(s);
				end=System.nanoTime();
				double cbf_b_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_b_cmp.add(s);
				end=System.nanoTime();
				double cbf_b_cmp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_b_dy.add(s);
				end=System.nanoTime();
				double cbf_b_dy_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
			//	for(String s: addList)cbf_b_sp.add(s);
				end=System.nanoTime();
				double cbf_b_sp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
			//	for(String s: addList)cbf_b_sp_rm.add(s);
				end=System.nanoTime();
				double cbf_b_sp_rm_bench=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)cbf_s.add(s);
				end=System.nanoTime();
				double cbf_s_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_s_cmp.add(s);
				end=System.nanoTime();
				double cbf_s_cmp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_s_dy.add(s);
				end=System.nanoTime();
				double cbf_s_dy_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
			//	for(String s: addList)cbf_s_sp.add(s);
				end=System.nanoTime();
				double cbf_s_sp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
			//	for(String s: addList)cbf_s_sp_rm.add(s);
				end=System.nanoTime();
				double cbf_s_sp_rm_bench=Data.benchmark(start, end, size);
				
				start=System.nanoTime();
				for(String s: addList)cbf_i.add(s);
				end=System.nanoTime();
				double cbf_i_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_i_cmp.add(s);
				end=System.nanoTime();
				double cbf_i_cmp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
				for(String s: addList)cbf_i_dy.add(s);
				end=System.nanoTime();
				double cbf_i_dy_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
		//		for(String s: addList)cbf_i_sp.add(s);
				end=System.nanoTime();
				double cbf_i_sp_bench=Data.benchmark(start, end, size);
				start=System.nanoTime();
		//		for(String s: addList)cbf_i_sp_rm.add(s);
				end=System.nanoTime();
				double cbf_i_sp_rm_bench=Data.benchmark(start, end, size);
				
				
				String data=fp+DELIMITER+
						arrayString_bench+DELIMITER+
						arrayByte_bench+DELIMITER+
						mph_bench+DELIMITER+
						sbf_bench+DELIMITER+
						sbf_cmp_bench+DELIMITER+
						sbf_dy_bench+DELIMITER+
						cbf_b_bench+DELIMITER+
						cbf_b_cmp_bench+DELIMITER+
						cbf_b_dy_bench+DELIMITER+
						cbf_b_sp_bench+DELIMITER+
						cbf_b_sp_rm_bench+DELIMITER+
						cbf_s_bench+DELIMITER+
						cbf_s_cmp_bench+DELIMITER+
						cbf_s_dy_bench+DELIMITER+
						cbf_s_sp_bench+DELIMITER+
						cbf_s_sp_rm_bench+DELIMITER+
						cbf_i_bench+DELIMITER+
						cbf_i_cmp_bench+DELIMITER+
						cbf_i_dy_bench+DELIMITER+
						cbf_i_sp_bench+DELIMITER+
						cbf_i_sp_rm_bench+DELIMITER+
						NEW_LINE_SEPARATOR;
				
				data=data.replace(".",",");
				System.out.print(fppPercent+";"+data);
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
