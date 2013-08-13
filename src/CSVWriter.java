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
	    writer.append("@relation 'owens data'");
	    writer.append('\n');
	    writer.append("@attribute class-att {1, 0}");
	    writer.append('\n');
	    writer.append("@attribute StdDev numeric");
	    writer.append('\n');
	    writer.append("@attribute Mean numeric");
	    writer.append('\n');
	    writer.append("@attribute Eyes numeric");
	    writer.append('\n');
	    writer.append("@attribute Mouth numeric");
	    writer.append('\n');
	    writer.append("@attribute Cheeks numeric");
	    writer.append('\n');
	    writer.append("@attribute Template numeric");
	    writer.append('\n');
	    writer.append("@attribute Nose numeric");
	    writer.append('\n');
	    writer.append("@attribute BigTemplate numeric");
	    writer.append('\n');
	    writer.append("@attribute EyesToCheeks numeric");
	    writer.append('\n');
	    writer.append("@attribute EyesToNose numeric");
	    writer.append('\n');
	    writer.append("@data");
	    writer.append('\n');


	    for (ArrayList<Integer> feature: features){
	    	writer.append(feature.get(feature.size()-1)+"");
		    writer.append(',');

	    	for(int i = 0; i < feature.size()-1; i++){
	    	    writer.append(feature.get(i)+"");
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
	public static void test (ArrayList<ArrayList <Integer>> features){
		 FileWriter writer;
		try {
			writer = new FileWriter("Test.dat");

		    for (ArrayList<Integer> feature: features){
		    	writer.append(feature.get(feature.size()-1)+"");
			    writer.append(' ');
			    if (feature.get(feature.size()-1) == 1)
			    writer.append(5+"");
			    else
			    	 writer.append(1+"");
			    writer.append('\n');
		    }

		    writer.flush();
		    writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}