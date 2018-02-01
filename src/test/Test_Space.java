package test;

import java.io.FileWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bloomfilter.StandardBloomFilter;
import bloomfilter.Compressed_BF;
import bloomfilter.Compressed_CBF;
import bloomfilter.Compressed_SBF;
import bloomfilter.CountingBloomFilter;
import bloomfilter.Dynamic_CBF;
import bloomfilter.Dynamic_SBF;
import bloomfilter.MPHS;
import bloomfilter.SpectralBloomFilter_MI;
import bloomfilter.SpectralBloomFilter_RM;
import data.Data;
import net.sourceforge.sizeof.SizeOf;

public class Test_Space {
	
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
	private static int increment=100;
	private static int maxFP=5000;

	
	public static void main(String[] args){incrementSize();incrementFP();}
	public static void incrementSize(){
		double fp=0.001;
		FileWriter fileWriter = null;		
		try {
			fileWriter = new FileWriter("test_space_csv.csv");
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

				List<byte[]> compareListByte=new ArrayList<byte[]>();
				List<String> compareListString=new ArrayList<String>();

				List<String> addList=Data.getRandomString(20,size);
				for(int j=0;j<addList.size();j++){
					compareListByte.add(addList.get(j).getBytes(Charset.forName("UTF-8")));
					compareListString.add(addList.get(j));
				}
				
				MPHS mph=new MPHS(addList,fp);
				
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
				cbf_b_sp.addAll(addList);
				cbf_s_sp.addAll(addList);
				cbf_i_sp.addAll(addList);
				cbf_b_sp_rm.addAll(addList);
				cbf_s_sp_rm.addAll(addList);
				cbf_i_sp_rm.addAll(addList);

				long arrayString_size=SizeOf.deepSizeOf(compareListString);
				long arrayByte_size=SizeOf.deepSizeOf(compareListByte);
				
				long sbf_size=SizeOf.deepSizeOf(sbf);
				long sbf_cmp_size=SizeOf.deepSizeOf(sbf_cmp);
				long sbf_dy_size=SizeOf.deepSizeOf(sbf_dy);
				
				long cbf_b_size=SizeOf.deepSizeOf(cbf_b);
				long cbf_b_cmp_size=SizeOf.deepSizeOf(cbf_b_cmp);
				long cbf_b_dy_size=SizeOf.deepSizeOf(cbf_b_dy);
				long cbf_b_sp_size=SizeOf.deepSizeOf(cbf_b_sp);
				long cbf_b_sp_rm_size=SizeOf.deepSizeOf(cbf_b_sp_rm);
				
				long cbf_s_size=SizeOf.deepSizeOf(cbf_s);
				long cbf_s_cmp_size=SizeOf.deepSizeOf(cbf_s_cmp);
				long cbf_s_dy_size=SizeOf.deepSizeOf(cbf_s_dy);
				long cbf_s_sp_size=SizeOf.deepSizeOf(cbf_s_sp);
				long cbf_s_sp_rm_size=SizeOf.deepSizeOf(cbf_s_sp_rm);
				
				long cbf_i_size=SizeOf.deepSizeOf(cbf_i);
				long cbf_i_cmp_size=SizeOf.deepSizeOf(cbf_i_cmp);
				long cbf_i_dy_size=SizeOf.deepSizeOf(cbf_i_dy);
				long cbf_i_sp_size=SizeOf.deepSizeOf(cbf_i_sp);
				long cbf_i_sp_rm_size=SizeOf.deepSizeOf(cbf_i_sp_rm);
				
				long mph_size=SizeOf.deepSizeOf(mph);
				
				
				String data=size+DELIMITER+
						arrayString_size+DELIMITER+
						arrayByte_size+DELIMITER+
						mph_size+DELIMITER+
						sbf_size+DELIMITER+
						sbf_cmp_size+DELIMITER+
						sbf_dy_size+DELIMITER+
						cbf_b_size+DELIMITER+
						cbf_b_cmp_size+DELIMITER+
						cbf_b_dy_size+DELIMITER+
						cbf_b_sp_size+DELIMITER+
						cbf_b_sp_rm_size+DELIMITER+
						cbf_s_size+DELIMITER+
						cbf_s_cmp_size+DELIMITER+
						cbf_s_dy_size+DELIMITER+
						cbf_s_sp_size+DELIMITER+
						cbf_s_sp_rm_size+DELIMITER+
						cbf_i_size+DELIMITER+
						cbf_i_cmp_size+DELIMITER+
						cbf_i_dy_size+DELIMITER+
						cbf_i_sp_size+DELIMITER+
						cbf_i_sp_rm_size+DELIMITER+
						NEW_LINE_SEPARATOR;
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
		double fp=0.001;
		FileWriter fileWriter = null;		
		try {
			fileWriter = new FileWriter("test_space_increment_fp_csv.csv");
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

				List<byte[]> compareListByte=new ArrayList<byte[]>();
				List<String> compareListString=new ArrayList<String>();

				List<String> addList=Data.getRandomString(20,size);
				for(int j=0;j<addList.size();j++){
					compareListByte.add(addList.get(j).getBytes(Charset.forName("UTF-8")));
					compareListString.add(addList.get(j));
				}
				
				MPHS mph=new MPHS(addList,fp);
				
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
				cbf_b_sp.addAll(addList);
				cbf_s_sp.addAll(addList);
				cbf_i_sp.addAll(addList);
				cbf_b_sp_rm.addAll(addList);
				cbf_s_sp_rm.addAll(addList);
				cbf_i_sp_rm.addAll(addList);

				long arrayString_size=SizeOf.deepSizeOf(compareListString);
				long arrayByte_size=SizeOf.deepSizeOf(compareListByte);
				
				long sbf_size=SizeOf.deepSizeOf(sbf);
				long sbf_cmp_size=SizeOf.deepSizeOf(sbf_cmp);
				long sbf_dy_size=SizeOf.deepSizeOf(sbf_dy);
				
				long cbf_b_size=SizeOf.deepSizeOf(cbf_b);
				long cbf_b_cmp_size=SizeOf.deepSizeOf(cbf_b_cmp);
				long cbf_b_dy_size=SizeOf.deepSizeOf(cbf_b_dy);
				long cbf_b_sp_size=SizeOf.deepSizeOf(cbf_b_sp);
				long cbf_b_sp_rm_size=SizeOf.deepSizeOf(cbf_b_sp_rm);
				
				long cbf_s_size=SizeOf.deepSizeOf(cbf_s);
				long cbf_s_cmp_size=SizeOf.deepSizeOf(cbf_s_cmp);
				long cbf_s_dy_size=SizeOf.deepSizeOf(cbf_s_dy);
				long cbf_s_sp_size=SizeOf.deepSizeOf(cbf_s_sp);
				long cbf_s_sp_rm_size=SizeOf.deepSizeOf(cbf_s_sp_rm);
				
				long cbf_i_size=SizeOf.deepSizeOf(cbf_i);
				long cbf_i_cmp_size=SizeOf.deepSizeOf(cbf_i_cmp);
				long cbf_i_dy_size=SizeOf.deepSizeOf(cbf_i_dy);
				long cbf_i_sp_size=SizeOf.deepSizeOf(cbf_i_sp);
				long cbf_i_sp_rm_size=SizeOf.deepSizeOf(cbf_i_sp_rm);
				
				long mph_size=SizeOf.deepSizeOf(mph);
				
				
				String data=fp+DELIMITER+
						arrayString_size+DELIMITER+
						arrayByte_size+DELIMITER+
						mph_size+DELIMITER+
						sbf_size+DELIMITER+
						sbf_cmp_size+DELIMITER+
						sbf_dy_size+DELIMITER+
						cbf_b_size+DELIMITER+
						cbf_b_cmp_size+DELIMITER+
						cbf_b_dy_size+DELIMITER+
						cbf_b_sp_size+DELIMITER+
						cbf_b_sp_rm_size+DELIMITER+
						cbf_s_size+DELIMITER+
						cbf_s_cmp_size+DELIMITER+
						cbf_s_dy_size+DELIMITER+
						cbf_s_sp_size+DELIMITER+
						cbf_s_sp_rm_size+DELIMITER+
						cbf_i_size+DELIMITER+
						cbf_i_cmp_size+DELIMITER+
						cbf_i_dy_size+DELIMITER+
						cbf_i_sp_size+DELIMITER+
						cbf_i_sp_rm_size+DELIMITER+
						NEW_LINE_SEPARATOR;
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
