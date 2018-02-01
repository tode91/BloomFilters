package test;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import bloomfilter.StandardBloomFilter;
import bloomfilter.Compressed_CBF;
import bloomfilter.Compressed_SBF;
import bloomfilter.CountingBloomFilter;
import bloomfilter.Dynamic_CBF;
import bloomfilter.Dynamic_SBF;
import data.Data;

public class Test_FP {
		
	private static final String FILE_HEADER = "N.Element;"
			+ "SBF;Compressed SBF;Dynamic SBF;"
			+ "CBF(byte);Compressed CBF(byte);Dynamic CBF(byte);"
			+ "CBF(short);Compressed CBF(short);Dynamic CBF(short);"
			+ "CBF(int);Compressed CBF(int);Dynamic CBF(int);"
			;
	private static final String DELIMITER = ";";
	private static final String NEW_LINE_SEPARATOR = "\n";
	
	private static double fp=0.001;
	private static int initialSize=1000;
	private static int max=100000;
	private static int increment=100;
	private static int maxFPP;
	
	public static void main(String[] args){
		FileWriter fileWriter = null;		
		try {
			fileWriter = new FileWriter("test_fp_csv.csv");
			fileWriter.append(FILE_HEADER.toString());
			fileWriter.append(NEW_LINE_SEPARATOR);
			
			StandardBloomFilter sbf=new StandardBloomFilter(fp, initialSize,"Murmur3");
			CountingBloomFilter cbf_b=new CountingBloomFilter(fp, initialSize,"Murmur3",Byte.class);
			CountingBloomFilter cbf_s=new CountingBloomFilter(fp, initialSize,"Murmur3",Short.class);
			CountingBloomFilter cbf_i=new CountingBloomFilter(fp, initialSize,"Murmur3",Integer.class);
			
			Compressed_SBF sbf_cmp=new Compressed_SBF(sbf);
			Compressed_CBF cbf_b_cmp=new Compressed_CBF(cbf_b);
			Compressed_CBF cbf_s_cmp=new Compressed_CBF(cbf_s);
			Compressed_CBF cbf_i_cmp=new Compressed_CBF(cbf_i);
			
			Dynamic_SBF sbf_dy=new Dynamic_SBF(fp,initialSize);
			Dynamic_CBF cbf_b_dy=new Dynamic_CBF(fp,initialSize,Byte.class);
			Dynamic_CBF cbf_s_dy=new Dynamic_CBF(fp,initialSize,Short.class);
			Dynamic_CBF cbf_i_dy=new Dynamic_CBF(fp,initialSize,Integer.class);


			for(int size=0;size<max;size=size+increment){
				
				
				List<String> addList=Data.getRandomString(20,increment);
				
				sbf.addAll(addList);
				cbf_b.addAll(addList);
				cbf_s.addAll(addList);
				cbf_i.addAll(addList);
				sbf_cmp.addAll(addList);
				cbf_b_cmp.addAll(addList);
				cbf_s_cmp.addAll(addList);
				cbf_i_cmp.addAll(addList);
				sbf_dy.addAll(addList);
				cbf_b_dy.addAll(addList);
				cbf_s_dy.addAll(addList);
				cbf_i_dy.addAll(addList);

				
				double sbf_bench=sbf.getProbabilityFPReal();
				double sbf_cmp_bench=sbf_cmp.getProbabilityFPReal();
				double sbf_dy_bench=sbf_dy.getProbabilityFPReal();
				
				double cbf_b_bench=cbf_b.getProbabilityFPReal();
				double cbf_b_cmp_bench=cbf_b_cmp.getProbabilityFPReal();
				double cbf_b_dy_bench=cbf_b_dy.getProbabilityFPReal();

				double cbf_s_bench=cbf_s.getProbabilityFPReal();
				double cbf_s_cmp_bench=cbf_s_cmp.getProbabilityFPReal();
				double cbf_s_dy_bench=cbf_s_dy.getProbabilityFPReal();
				
				double cbf_i_bench=cbf_i.getProbabilityFPReal();
				double cbf_i_cmp_bench=cbf_i_cmp.getProbabilityFPReal();
				double cbf_i_dy_bench=cbf_i_dy.getProbabilityFPReal();

				
				String data=size+DELIMITER+
						sbf_bench+DELIMITER+
						sbf_cmp_bench+DELIMITER+
						sbf_dy_bench+DELIMITER+
						cbf_b_bench+DELIMITER+
						cbf_b_cmp_bench+DELIMITER+
						cbf_b_dy_bench+DELIMITER+
						cbf_s_bench+DELIMITER+
						cbf_s_cmp_bench+DELIMITER+
						cbf_s_dy_bench+DELIMITER+
						cbf_i_bench+DELIMITER+
						cbf_i_cmp_bench+DELIMITER+
						cbf_i_dy_bench+DELIMITER+
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
