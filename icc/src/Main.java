import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Main {

	private static List<String> itemList;
	private static List<String> userList;
	private static Map<String,Map<String,Integer>> ratingMap;
	private static Map<String,Double> pretestMap; //userid, pretest
	private static Map<String,String> difficultyMap; //content_name,difficulty

	private static Map<String,List<String>> numMap;
	private static File file;
	private static FileWriter fw;
	private static BufferedWriter bw;	
	
	public static void main(String[] args)
	{
		//initiate list and map 
		userList = new ArrayList<String>();
		itemList = new ArrayList<String>();
		ratingMap = new HashMap<String,Map<String,Integer>>();
		numMap = new HashMap<String,List<String>>();
		//init variables for writing to file
		file = new File("./resources/icc_data.csv");
		createFile();
		//read data
		readItemList();
		readRatingMap();
		//remove items with only one rating
//		for (String item : numMap.keySet())
//		{
//			if (numMap.get(item).size() == 1)
//				itemList.remove(item);
//		}
		//write icc format data	
		readPretest("./resources/pretest_Q5_removed.csv");//user,pretest
		readDifficulty("./resources/difficulty.csv"); //content,difficulty
		writeRatings();
		closeWriter();
		file = new File("./resources/num.txt");
		createFile();
		writeNum();		
		closeWriter();

	}
	
	private static void readPretest(String path) {
		pretestMap = new HashMap<String,Double>();
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		boolean isHeader = true;
		try {
			br = new BufferedReader(new FileReader(path));
			String[] clmn;
			String user;
			double pretest;
			while ((line = br.readLine()) != null) {
				if (isHeader)
				{
					isHeader = false;
					continue;
				}
				clmn = line.split(cvsSplitBy);
				user = clmn[0];
				pretest = Double.parseDouble(clmn[1]);				
				pretestMap.put(user,pretest);
			}	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}		
		System.out.println("pretestMap:"+pretestMap.size());						
	}
	
	private static void readDifficulty(String path) {
		difficultyMap = new HashMap<String,String>();
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		boolean isHeader = true;
		try {
			br = new BufferedReader(new FileReader(path));
			String[] clmn;
			String content;
			String difficulty;
			while ((line = br.readLine()) != null) {
				if (isHeader)
				{
					isHeader = false;
					continue;
				}
				clmn = line.split(cvsSplitBy);
				content = clmn[0];
				difficulty = clmn[1];			
				difficultyMap.put(content,difficulty);
			}	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}		
		System.out.println("difficultyMap:"+difficultyMap.size());			
	}
	
	private static void createFile() {
		try {
			if (!file.exists())
				file.createNewFile();
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
				e.printStackTrace();
		}
	}
	private static void closeWriter() {
		try {
			
			if (fw != null) {
				fw.close();
			}
			if (bw != null) {
				bw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static void writeNum() {
		try {
			//write items
			for (String item : numMap.keySet())
			{
				bw.write(item+","+numMap.get(item).size());
				bw.newLine();
			    bw.flush();
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	
		
	}
	
	private static void writeRatings() {
		try {
//			//first column is empty
//			bw.write(",");
//			//write items
//			for (String item : itemList)
//				bw.write(item+",");
//			bw.newLine();
//		    bw.flush();
//			//write ratings
//			for (String user : ratingMap.keySet())
//			{
//				bw.write(user+",");
//				for (String item : itemList)
//				{
//					;
//					bw.write((ratingMap.get(user).get(item)==null?"":ratingMap.get(user).get(item))+",");
//				}
//				bw.newLine();
//			    bw.flush();
//			}
			//write ratings
			String[] pair;
			for (String item : itemList)
			{				
				pair = item.split("#");
				for (int u = 0; u < userList.size(); u++)
				{
					bw.write(item+","+userList.get(u)+","+(ratingMap.get(userList.get(u)).get(item)==null?"NA":""+ratingMap.get(userList.get(u)).get(item))+","+difficultyMap.get(pair[0])+","+pretestMap.get(userList.get(u)));
					bw.newLine();
				}
				bw.newLine();
			    bw.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private static void readRatingMap() {
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		boolean isHeader = true;
		try {
			br = new BufferedReader(new FileReader("./resources/ratings.csv"));
			String[] clmn;
			String item;
			String user;
			int rating;
			Map<String,Integer> map;
			List<String> list;
			while ((line = br.readLine()) != null) {
				if (isHeader)
				{
					isHeader = false;
					continue;
				}
				clmn = line.split(cvsSplitBy);
				item = clmn[0];	
				user = clmn[1];
				rating = Integer.parseInt(clmn[2]);
				//fill numMap
				if (numMap.containsKey(item) == false)
				{
					list = new ArrayList<String>();
					list.add(user);					
					numMap.put(item,list);
				}
				else
				{
					list = numMap.get(item);
					if (list.contains(user) == false)
						list.add(user);
				}
				//fill user list
				if (userList.contains(user) == false)
					userList.add(user);
				//fill rating map
				if (ratingMap.containsKey(user) == false)
				{
					map = new HashMap<String,Integer>();
					map.put(item, rating);
					ratingMap.put(user, map);
				}
				else
				{
					map = ratingMap.get(user);	
					map.put(item, rating);					
				}
			}
		}catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}	
		int count = 0;
		for (Map<String,Integer> m : ratingMap.values())
			count+=m.size();		
		System.out.println("ratingMap: "+count);
		int k = Integer.MAX_VALUE;
		for (String item : numMap.keySet())
			if (numMap.get(item).size() < k)
				k = numMap.get(item).size();
		System.out.println("k: "+k);
		System.out.println("userList: "+userList.size());
    }
	
	private static void readItemList() {
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		boolean isHeader = true;
		try {
			br = new BufferedReader(new FileReader("./resources/items.csv"));
			String[] clmn;
			String item;
			while ((line = br.readLine()) != null) {
				if (isHeader)
				{
					isHeader = false;
					continue;
				}
				clmn = line.split(cvsSplitBy);
				item = clmn[0];				
				if (itemList.contains(item) == false)
				{
					itemList.add(item);									
				}
			}
		}catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}		
		System.out.println("itemList: "+itemList.size());		
	}
}
