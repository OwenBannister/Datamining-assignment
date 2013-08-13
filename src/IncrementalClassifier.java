
	import weka.core.Instance;
	import weka.core.Instances;
	import weka.core.converters.ArffLoader;
	import weka.classifiers.bayes.NaiveBayesUpdateable;

	import java.io.BufferedReader;
	import java.io.File;
	import java.io.FileReader;

	/**
	 * This example trains NaiveBayes incrementally on data obtained
	 * from the ArffLoader.
	 *
	 * @author FracPete (fracpete at waikato dot ac dot nz)
	 */
	public class IncrementalClassifier {

	  /**
	   * Expects an ARFF file as first argument (class attribute is assumed
	   * to be the last attribute).
	   *
	   * @param args        the commandline arguments
	   * @throws Exception  if something goes wrong
	   */
	  public static void main(String[] args) throws Exception {

	    // load unlabeled data
	    Instances train = new Instances(
	                            new BufferedReader(
	                              new FileReader("owen.dat")));

	    train.setClassIndex(0);

	    // load unlabeled data
	    Instances test = new Instances(
	                            new BufferedReader(
	                              new FileReader("owen-test.dat")));

	    test.setClassIndex(0);

	    // train NaiveBayes
	    NaiveBayesUpdateable nb = new NaiveBayesUpdateable();
	    nb.buildClassifier(train);

	    int TP = 0;
	    int FN = 0;
	    int FP = 0;
	    int TN = 0;

	    for(int i = 0 ; i < test.numInstances(); i ++){
	    	 boolean classified = nb.classifyInstance(test.instance(i)) == 0.0;

	    	 boolean isA = test.instance(i).classValue() == 0.0;

	    	 if(classified && isA){
	    		 TP++;
	    	 }

	    	 if(classified && !isA){
	    		 FP++;
	    	 }

	    	 if(!classified && !isA){
	    		 FN++;
	    	 }

	    	 if(!classified && isA){
	    		 TN++;
	    	 }


	    }

	    // output generated model
	    System.out.println(nb);

	    System.out.println();

		System.out.println("----------------------------");
		System.out.println("True\tPositive\t" + TP);
		System.out.println("False\tNegative\t" + FN);

		System.out.println("True\tNegative\t" + TN);
		System.out.println("False\tPositive\t" + FP);

		System.out.println();
		System.out.println("True Accuracy\t" + ((double) TP / (TP + TN)));
		System.out.println("False Accuracy\t" + ((double) FN / (FP + FN)));

		double correct = (TP + FN);
		double incorrect = (FP + TN);

		double accuracy = correct / (correct + incorrect);
		System.out.println();
		System.out.println("Overall Accuracy\t" + accuracy);
	  }

	}


