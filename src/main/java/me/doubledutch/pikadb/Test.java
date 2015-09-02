package me.doubledutch.pikadb;

import java.io.*;
import java.util.*;
import org.json.*;

public class Test{
	public static void main(String args[]){
		String filename="./test.data";
		int RECORDS=100000;
		try{
			
			System.out.println("Running test "+RECORDS);


			long pre=System.currentTimeMillis();
			PageFile f=new PageFile(filename);
			Page p1=f.createPage();
			Soup soup=new Soup("users",f,p1.getId());

			for(int i=0;i<RECORDS;i++){
				JSONObject obj=new JSONObject();
				obj.put("id",i);
				obj.put("username","kasper.jeppesen"+i);
				obj.put("firstName","Kasper");
				obj.put("lastName","Jeppesen");
				obj.put("image","jerk-"+i+".png");
				obj.put("number",3.1415f);
				soup.add(i,obj);
			}
			f.saveChanges();
			f.close();

			long post=System.currentTimeMillis();

			System.out.println(" Write in "+(post-pre)+"ms "+(int)(RECORDS/((post-pre)/1000.0))+" obj/s");

			File ftest=new File(filename);
			System.out.println("length:"+ftest.length());

			pre=System.currentTimeMillis();
			f=new PageFile(filename);
			soup=new Soup("users",f,p1.getId());

			
			
			System.out.println("Scanning partial objects");
			List<String> columns=new ArrayList<String>();
			columns.add("id");
			columns.add("username");
			List<JSONObject> list=soup.scan(columns);
			for(int i=0;i<RECORDS;i++){
				JSONObject obj=list.get(i);
			}
			f.close();
			post=System.currentTimeMillis();
			System.out.println(" Read in "+(post-pre)+"ms "+(int)(RECORDS/((post-pre)/1000.0))+" obj/s");
		

			System.out.println("Scanning full objects");
			pre=System.currentTimeMillis();
			f=new PageFile(filename);
			soup=new Soup("users",f,p1.getId());
			list=soup.scan();
			System.out.println("Records returned by scan "+list.size());
			for(int i=0;i<RECORDS;i++){
				JSONObject obj=list.get(i);
			}
			f.close();
			post=System.currentTimeMillis();
			System.out.println(" Read in "+(post-pre)+"ms "+(int)(RECORDS/((post-pre)/1000.0))+" obj/s");
			
			System.out.println("Scanning partial objects");
			pre=System.currentTimeMillis();
			f=new PageFile(filename);
			soup=new Soup("users",f,p1.getId());
			columns=new ArrayList<String>();
			columns.add("id");
			columns.add("username");
			list=soup.scan(columns);
			for(int i=0;i<RECORDS;i++){
				JSONObject obj=list.get(i);
			}
			f.close();
			post=System.currentTimeMillis();
			System.out.println(" Read in "+(post-pre)+"ms "+(int)(RECORDS/((post-pre)/1000.0))+" obj/s");
		
			System.out.println("Scanning full objects");
			pre=System.currentTimeMillis();
			f=new PageFile(filename);
			soup=new Soup("users",f,p1.getId());
			list=soup.scan();
			System.out.println("Records returned by scan "+list.size());
			for(int i=0;i<RECORDS;i++){
				JSONObject obj=list.get(i);
				// System.out.println(obj.toString());
			}
			f.close();
			post=System.currentTimeMillis();
			System.out.println(" Read in "+(post-pre)+"ms "+(int)(RECORDS/((post-pre)/1000.0))+" obj/s");
			
			System.out.println("Scanning partial objects 50%");
			pre=System.currentTimeMillis();
			f=new PageFile(filename);
			soup=new Soup("users",f,p1.getId());
			columns=new ArrayList<String>();
			columns.add("id");
			columns.add("username");
			Map<Integer,JSONObject> objMap=new HashMap<Integer,JSONObject>();
			for(int i=0;i<RECORDS;i++){
				if(i%2==0){
					JSONObject obj=new JSONObject();
					objMap.put(i,obj);
				}
			}
			list=soup.scan(objMap,columns);
			for(int i=0;i<list.size();i++){
				JSONObject obj=list.get(i);
				// System.out.println(obj.toString());
			}
			f.close();
			post=System.currentTimeMillis();
			System.out.println(" Read in "+(post-pre)+"ms "+(int)(RECORDS/((post-pre)/1000.0))+" obj/s");
		
			System.out.println("Picking out single object");
			pre=System.currentTimeMillis();
			f=new PageFile(filename);
			soup=new Soup("users",f,p1.getId());
			System.out.println(soup.scan(6,columns).toString());
			f.close();
			post=System.currentTimeMillis();
			System.out.println(" Read in "+(post-pre)+"ms");

		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			File ftest=new File(filename);
			if(!ftest.delete()){
				System.out.println("Couldn`t delete file");
			}
		}catch(Exception e){}
	}
}