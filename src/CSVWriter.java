import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CSVWriter
{
   public static void main(String [] args)
   {

   }

   public static void generateCsvFile(ArrayList<ArrayList <Integer>> features,String fname)
   {
	try
	{
	    FileWriter writer = new FileWriter(fname);

	    writer.append("StdDev");
	    writer.append(',');
	    writer.append("Mean");
	    writer.append(',');
	    writer.append("Eyes");
	    writer.append(',');
	    writer.append("Mouth");
	    writer.append(',');
	    writer.append("Cheeks");
	    writer.append(',');
	    writer.append("Template");
	    writer.append(',');
	    writer.append("Face");
	    writer.append('\n');


	    for (ArrayList<Integer> feature: features){
	    	for(int i: feature){
	    	    writer.append(i+"");
	    		writer.append(',');
	    	}
		    writer.append('\n');
	    }

	    //generate whatever data you want

	    writer.flush();
	    writer.close();
	}
	catch(IOException e)
	{
	     e.printStackTrace();
	}
    }
}