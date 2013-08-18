import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.JFrame;

import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.ConfusionMatrix;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.*;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;


public class DigitClassifier_Part3 {
	ArrayList<Integer> fac = new ArrayList<Integer>();
	ArrayList<Integer> kar = new ArrayList<Integer>();
	ArrayList<Integer> pix = new ArrayList<Integer>();
	ArrayList<Integer> zer = new ArrayList<Integer>();
	ArrayList<Integer> mor = new ArrayList<Integer>();
	boolean onlyMor = false;

	public static void main (String args[]){
		DigitClassifier_Part3 dc = new DigitClassifier_Part3();
		dc.readFiles();
		//dc.readOnlyMorph();
		dc.classify();
	}


	public void classify(){
		J48 j48 = new J48();

		try {
			Instances train = new Instances(
					new BufferedReader(
							new FileReader("digits/output/training.arff")));

			Instances test = new Instances(
					new BufferedReader(
							new FileReader("digits/output/test.arff")));
			test.setClassIndex(0);
			train.setClassIndex(0);
			j48.buildClassifier(train);
			int total = 0;
			int total9 = 0;
			for(int i = 0 ; i < test.numInstances(); i ++){
				double calculated = j48.classifyInstance(test.instance(i));

				if (test.instance(i).classValue() == calculated) total ++;
			}
			System.out.println("Total: " +total+"/"+test.numInstances() + " = "+(double)total/test.numInstances());

			// Draw the tree
			visualise(j48);

			 // Test the model
			 Evaluation eTest = new Evaluation(train);
			 eTest.evaluateModel(j48, test);
			 String strSummary = eTest.toSummaryString();

			 // Get the confusion matrix
			 double[][] cmMatrix = eTest.confusionMatrix();
			 System.out.println("\t0\t1\t2\t3\t4\t5\t6\t7\t8\t9");
			 System.out.println("\t_____________________________________________________________________________");
			 for (int i=0; i < cmMatrix.length;i++){
				 System.out.print(i+"\t|");
			 for (int j=0; j < cmMatrix[0].length;j++){
				 System.out.print((int)cmMatrix[i][j]+"\t");
			 }
			 System.out.println();
			 }
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void readOnlyMorph(){
		try {
			ArrayList<Digit> digits = new ArrayList<Digit>();
			BufferedReader morReader = new BufferedReader(new FileReader("/vol/courses/comp422/datasets/mfeat-digits/mfeat-mor"));

			for (int i = 0; i<2000 ; i++){
				Digit digit = new Digit();
				Scanner s = new Scanner(morReader.readLine());
				while (s.hasNext())
					digit.mor.add(Double.parseDouble(s.next()));
				s.close();

				if (i < 200) digit.classification = 0;
				else if (i < 400) digit.classification = 1;
				else if (i < 600) digit.classification = 2;
				else if (i < 800) digit.classification = 3;
				else if (i < 1000) digit.classification = 4;
				else if (i < 1200) digit.classification = 5;
				else if (i < 1400) digit.classification = 6;
				else if (i < 1600) digit.classification = 7;
				else if (i < 1800) digit.classification = 8;
				else if (i < 2000) digit.classification = 9;

				digits.add(digit);
			}
			System.out.println("Data Loaded In System");
			writeToFile(digits);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public void readFiles(){
		try {

			ArrayList<Digit> digits = new ArrayList<Digit>();
			BufferedReader fouReader = new BufferedReader(new FileReader("/vol/courses/comp422/datasets/mfeat-digits/mfeat-fou"));
			BufferedReader facReader = new BufferedReader(new FileReader("/vol/courses/comp422/datasets/mfeat-digits/mfeat-fac"));
			BufferedReader karReader = new BufferedReader(new FileReader("/vol/courses/comp422/datasets/mfeat-digits/mfeat-kar"));
			BufferedReader pixReader = new BufferedReader(new FileReader("/vol/courses/comp422/datasets/mfeat-digits/mfeat-pix"));
			BufferedReader zerReader = new BufferedReader(new FileReader("/vol/courses/comp422/datasets/mfeat-digits/mfeat-zer"));
			BufferedReader morReader = new BufferedReader(new FileReader("/vol/courses/comp422/datasets/mfeat-digits/mfeat-mor"));

			for (int i = 0; i<2000 ; i++){
				Digit digit = new Digit();
				Scanner s = new Scanner(fouReader.readLine());
				while (s.hasNext())
					digit.fou.add(Double.parseDouble(s.next()));
				s.close();

				s = new Scanner(facReader.readLine());
				while (s.hasNext())
					digit.fac.add(Double.parseDouble(s.next()));
				s.close();

				s = new Scanner(karReader.readLine());
				while (s.hasNext())
					digit.kar.add(Double.parseDouble(s.next()));
				s.close();

				s = new Scanner(pixReader.readLine());
				while (s.hasNext())
					digit.pix.add(Double.parseDouble(s.next()));
				s.close();

				s = new Scanner(zerReader.readLine());
				while (s.hasNext())
					digit.zer.add(Double.parseDouble(s.next()));
				s.close();

				s = new Scanner(morReader.readLine());
				while (s.hasNext())
					digit.mor.add(Double.parseDouble(s.next()));
				s.close();
				if (i < 200) digit.classification = 0;
				else if (i < 400) digit.classification = 1;
				else if (i < 600) digit.classification = 2;
				else if (i < 800) digit.classification = 3;
				else if (i < 1000) digit.classification = 4;
				else if (i < 1200) digit.classification = 5;
				else if (i < 1400) digit.classification = 6;
				else if (i < 1600) digit.classification = 7;
				else if (i < 1800) digit.classification = 8;
				else if (i < 2000) digit.classification = 9;

				digits.add(digit);
			}
			System.out.println("Data Loaded In System");
			writeToFile(digits);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void visualise(J48 classifier){
		TreeVisualizer tv;
		try {
			tv = new TreeVisualizer(null, classifier.graph(), new PlaceNode2());

			JFrame jf = new JFrame("Weka Classifier Tree Visualizer: J48");
			jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			jf.setSize(1600, 1000);
			jf.getContentPane().setLayout(new BorderLayout());
			jf.getContentPane().add(tv, BorderLayout.CENTER);
			jf.setVisible(true);
			// adjust tree
			tv.fitToScreen();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void writeToFile(ArrayList<Digit> digits) {
		try {
			Collections.shuffle(digits);

			ArrayList<Digit> testSet = new ArrayList<Digit>();
			ArrayList<Digit> trainingSet = new ArrayList<Digit>();


			boolean even = true;
			for (Digit d: digits){
				if (even){
					testSet.add(d);
					even = false;
				}
				else{
					trainingSet.add(d);
					even = true;
				}

			}
			System.out.println("Sorting");
			Collections.sort(testSet);
			Collections.sort(trainingSet);
			System.out.println("Done");
			FileWriter writer = new FileWriter("digits/test.csv");
			// Headings
			writer.append("class");
			writer.append(',');
			if (!onlyMor){
				for (int i= 0;i < 76;i++){
					writer.append("fou_"+i);
					writer.append(',');
				}
				for (int i= 0;i < 216;i++){
					writer.append("fac_"+i);
					writer.append(',');
				}
				for (int i= 0;i < 64;i++){
					writer.append("kar_"+i);
					writer.append(',');
				}
				for (int i= 0;i < 240;i++){
					writer.append("pix_"+i);
					writer.append(',');
				}
				for (int i= 0;i < 47;i++){
					writer.append("zer_"+i);
					writer.append(',');
				}
			}
			for (int i= 0;i < 5;i++){
				writer.append("mor_"+i);
				writer.append(',');
			}
			writer.append("mor_"+6);
			writer.append('\n');

			// Data
			for (Digit d: testSet){
				switch (d.classification) {
				case 0: writer.append("zero");break;
				case 1: writer.append("one");break;
				case 2: writer.append("two");break;
				case 3: writer.append("three");break;
				case 4: writer.append("four");break;
				case 5: writer.append("five");break;
				case 6: writer.append("six");break;
				case 7: writer.append("seven");break;
				case 8: writer.append("eight");break;
				case 9: writer.append("nine");break;
				}
				writer.append(',');
				if (!onlyMor){
					for (Double i : d.fou){
						writer.append(i+"");
						writer.append(',');
					}
					for (Double i : d.fac){
						writer.append(i+"");
						writer.append(',');
					}
					for (Double i : d.kar){
						writer.append(i+"");
						writer.append(',');
					}
					for (Double i : d.pix){
						writer.append(i+"");
						writer.append(',');
					}
					for (Double i : d.zer){
						writer.append(i+"");
						writer.append(',');
					}
				}
				for (int i = 0;i < d.mor.size()-1; i++){
					writer.append(d.mor.get(i)+"");
					writer.append(',');
				};
				writer.append(d.mor.get(d.mor.size()-1)+"");
				writer.append('\n');
			}

			//generate whatever data you want
			System.out.println("Data Outputted to File digits/digits.csv");
			writer.flush();
			writer.close();



			// Training Set
			writer = new FileWriter("digits/training.csv");
			// Headings
			writer.append("class");
			writer.append(',');
			if (!onlyMor){
				for (int i= 0;i < 76;i++){
					writer.append("fou_"+i);
					writer.append(',');
				}
				for (int i= 0;i < 216;i++){
					writer.append("fac_"+i);
					writer.append(',');
				}
				for (int i= 0;i < 64;i++){
					writer.append("kar_"+i);
					writer.append(',');
				}
				for (int i= 0;i < 240;i++){
					writer.append("pix_"+i);
					writer.append(',');
				}
				for (int i= 0;i < 47;i++){
					writer.append("zer_"+i);
					writer.append(',');
				}
			}
				for (int i= 0;i < 5;i++){
					writer.append("mor_"+i);
					writer.append(',');

			}
			writer.append("mor_"+6);
			writer.append('\n');

			// Data
			for (Digit d: trainingSet){
				switch (d.classification) {
				case 0: writer.append("zero");break;
				case 1: writer.append("one");break;
				case 2: writer.append("two");break;
				case 3: writer.append("three");break;
				case 4: writer.append("four");break;
				case 5: writer.append("five");break;
				case 6: writer.append("six");break;
				case 7: writer.append("seven");break;
				case 8: writer.append("eight");break;
				case 9: writer.append("nine");break;
				}
				writer.append(',');
				if (!onlyMor){
					for (Double i : d.fou){
						writer.append(i+"");
						writer.append(',');
					}
					for (Double i : d.fac){
						writer.append(i+"");
						writer.append(',');
					}
					for (Double i : d.kar){
						writer.append(i+"");
						writer.append(',');
					}
					for (Double i : d.pix){
						writer.append(i+"");
						writer.append(',');
					}
					for (Double i : d.zer){
						writer.append(i+"");
						writer.append(',');
					}
				}
				for (int i = 0;i < d.mor.size()-1; i++){
					writer.append(d.mor.get(i)+"");
					writer.append(',');
				};
				writer.append(d.mor.get(d.mor.size()-1)+"");
				writer.append('\n');
			}

			//generate whatever data you want
			System.out.println("Data Outputted to File digits/digits.csv");
			writer.flush();
			writer.close();

			convertToArff();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void convertToArff(){
		CSVLoader loader = new CSVLoader();
		try {
			// Training Set
			loader.setSource(new File("digits/training.csv"));
			Instances data = loader.getDataSet();

			// save ARFF
			ArffSaver saver = new ArffSaver();
			saver.setInstances(data);
			saver.setFile(new File("digits/output/training.arff"));
			saver.setDestination(new File("digits/output/training.arff"));
			saver.writeBatch();

			// Test Set
			loader.setSource(new File("digits/test.csv"));
			data = loader.getDataSet();

			// save ARFF
			saver = new ArffSaver();
			saver.setInstances(data);
			saver.setFile(new File("digits/output/test.arff"));
			saver.setDestination(new File("digits/output/test.arff"));
			saver.writeBatch();
			System.out.println("CSV Converted to arff");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public class Digit implements Comparable{

		int classification = -1;

		ArrayList<Double> fou = new ArrayList<Double>();
		ArrayList<Double> fac = new ArrayList<Double>();
		ArrayList<Double> kar = new ArrayList<Double>();
		ArrayList<Double> pix = new ArrayList<Double>();
		ArrayList<Double> zer = new ArrayList<Double>();
		ArrayList<Double> mor = new ArrayList<Double>();

		public int compareTo(Object o) {
			Digit d = (Digit)o;
			if (classification < d.classification) return -1;
			else if (classification == d.classification) return 0;
			return 1;
		}




	}
}
