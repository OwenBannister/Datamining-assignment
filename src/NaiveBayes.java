
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.classifiers.bayes.NaiveBayesUpdateable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class NaiveBayes {
	public static void main(String[] args) throws Exception {

		Instances train = new Instances(
				new BufferedReader(
						new FileReader("datasets/face-training.dat")));

		Instances test = new Instances(
				new BufferedReader(
						new FileReader("datasets/face-test.dat")));

		train.setClassIndex(0);
		test.setClassIndex(0);

		// train NaiveBayes
		NaiveBayesUpdateable nb = new NaiveBayesUpdateable();
		nb.buildClassifier(train);

		int truePositive = 0;
		int falseNegative = 0;
		int falsePositive = 0;
		int trueNegative = 0;


		// Classify each instance in the test set
		for(int i = 0 ; i < test.numInstances(); i ++){
			boolean classified = nb.classifyInstance(test.instance(i)) == 0.0;

			boolean real = test.instance(i).classValue() == 0.0;

			if(classified && real){
				truePositive++;
			}

			if(classified && !real){
				falsePositive++;
			}

			if(!classified && real){
				falseNegative++;
			}

			if(!classified && !real){
				trueNegative++;
			}
		}
		// Print out report
		System.out.println(nb);
		System.out.println();
		System.out.println("----------------------------");
		System.out.println("True\tPositive\t" + truePositive);
		System.out.println("False\tNegative\t" + falseNegative);

		System.out.println("True\tNegative\t" + trueNegative);
		System.out.println("False\tPositive\t" + falsePositive);

		System.out.println();
		System.out.println("True Accuracy\t" + ((double) truePositive / (truePositive + falseNegative)));
		System.out.println("False Accuracy\t" + ((double) trueNegative / (falsePositive + trueNegative)));

		double face = (((double) truePositive / (truePositive + falseNegative)));
		double nonFace = (((double) trueNegative / (falsePositive + trueNegative)));

		double accuracy = (face + nonFace)/2;
		System.out.println();
		System.out.println("Average Accuracy\t" + accuracy);
	}

}


