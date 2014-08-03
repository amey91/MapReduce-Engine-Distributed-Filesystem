package testfiles;

import jarmanager.JarLoader;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import mapreduce.Mapper;
import commons.Logger;
class Club implements Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = -2869273211960451820L;


	String x;
	int y;
	Club(String s, int i){
		x=s;
		y=i;
	}
	@Override
	public String toString(){
		return x+y;
	}
}
public class Test3 {
	public static void main(String ar[]) throws Exception{
		
		Class<Mapper> mapperClass = (Class<Mapper >) JarLoader.getClassFromJar(
				"C:\\loc\\1/0.jar",	"temperature.Mapper1");
		
		String location = "C:/loc/t.tmp";
		ArrayList<Club> club1 = new ArrayList<Club>();
		club1.add(new Club("one",1));
		club1.add(new Club("two",2));
		club1.add(new Club("three",3));
		club1.add(new Club("four",4));

		ArrayList<Club> club2 = new ArrayList<Club>();
		club2.add(new Club("five", 5));
		club2.add(new Club("six", 6));
		club2.add(new Club("seven", 7));
		club2.add(new Club("eight", 8));
		club2.add(new Club("nine", 9));

		
		FileOutputStream fos = new FileOutputStream(location);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(club1);
		oos.writeObject(club2);
		oos.close();
		
		FileInputStream fis = new FileInputStream(location);
		ObjectInputStream ois = new ObjectInputStream(fis);
		ArrayList<Club> clubs1 = (ArrayList<Club>) ois.readObject();
		ArrayList<Club> clubs2 = (ArrayList<Club>) ois.readObject();
		ois.close();

		for(Club c: clubs1)
			Logger.log(c.toString());
		Logger.log("\nnext\n");
		for(Club c: clubs2)
			Logger.log(c.toString());
	}
}