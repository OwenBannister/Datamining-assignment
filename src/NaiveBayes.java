import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;


/**
 * @author bannisowen
 *
 */
/**
 * @author bannisowen
 *
 */
public class NaiveBayes {
	ArrayList<int[][]> testFaces = new ArrayList<int[][]>();
	ArrayList<int[][]> testNonFaces = new ArrayList<int[][]>();
	ArrayList<int[][]> trainFaces =  new ArrayList<int[][]>();
	ArrayList<int[][]> trainNonFaces = new ArrayList<int[][]>();

	private ArrayList <Integer> featuresInFace = new ArrayList <Integer>();
	private ArrayList <Integer> featuresInNonFace = new ArrayList <Integer>();

	BufferedImage bufferedImage;
	int[][] image;
	ImageProcessor processor;
	int stdDevOfset = 10;
	int stdDevThresh = 29;
	int meanPixelValue = 30;
	int meanPixelOfset = 100;

	public NaiveBayes(ImageProcessor ip) throws IOException{
		processor = ip;
		bufferedImage = processor.bufferedImage;
		image = processor.image;
		loadImagesForFaces();
		classify();
	}

	public double probOfFace(){
		double val = (double) trainFaces.size()/ (double)trainFaces.size();
		return val;
	}

	public double probOfNonFace(){
		double val = (double) trainNonFaces.size()/(double)(trainNonFaces.size());
		return val;
	}

	public void setUpTrainingFeatures(){
		// Setup values
		// Mean Pixel Value
		for (int array= 0;array< testNonFaces.size();array++){
			for (int x= 0;x< testNonFaces.get(0).length;x++){
				for (int y= 0;y< testNonFaces.get(0)[0].length;y++){
					meanPixelValue+= pixelAt(testNonFaces.get(array),x, y);
				}
			}
		}

		// Initialise Arrays
		for (int i = 0; i < 3; i++){
			featuresInFace.add(i,0);featuresInNonFace.add(i,0);
		}


		for (int[][] img: trainFaces){
			// Standard Deviation
			int val = standardDeviation(img);
			if (val > stdDevThresh - stdDevOfset && val < stdDevThresh + stdDevOfset)
				featuresInFace.set(0, featuresInFace.get(0)+1);
			// Mean
			val = meanPixelValue(img);
			if (val > meanPixelValue - meanPixelOfset && val < meanPixelValue + meanPixelOfset )
				featuresInFace.set(1, featuresInFace.get(1)+1);

			if(detectEyes(img))
				featuresInFace.set(2, featuresInFace.get(2)+1);


		}
		for (int[][] img: trainNonFaces){
			// Standard Deviation
			int val = standardDeviation(img);
			if (val > stdDevThresh - stdDevOfset && val < stdDevThresh + stdDevOfset)
				featuresInNonFace.set(0, featuresInNonFace.get(0)+1);
			// Mean
			val = meanPixelValue(img);
			if (val > meanPixelValue - meanPixelOfset && val < meanPixelValue + meanPixelOfset )
				featuresInNonFace.set(1, featuresInNonFace.get(1)+1);
			if(detectEyes(img))
				featuresInNonFace.set(2, featuresInNonFace.get(2)+1);
		}
	}


	private int meanPixelValue(int[][] img) {
		int mean = 0;
		int width = img[0].length;
		int height = img.length;
		for(int i =0; i<height; i++)
		{
			for(int j=0; j<width; j++)
			{
				mean+= pixelAt(i,j);
			}
		}
		mean /= width*height;
		return mean;
	}

	/**
	 * Basic eye detection based on the assumption that if a dark spot is found, there should be anouther further along in the image
	 * @param img
	 * @return
	 */
	public boolean detectEyes (int[][] img){
		int eyeThresh = 100;
		for (int y = 0; y< img.length/2;y++){
			for (int x = 0; x< img.length;x++){
				if (pixelAt(img,x,y) < eyeThresh ){
					int found = pixelAt(img,x,y);
					//System.out.print("eye ?: ");
					// should find a second eye of similar shade
				//	if (y+6 < 19)y+=6;
					if (x+6 < 19)x+=6;
					while(x<img.length/2){
						x++;
						if (pixelAt(img,x,y) < found + 10 && pixelAt(img,x,y) > found - 10 ){
							//System.out.println("Confirmed");
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public ArrayList<Integer> createFeatureVector(int[][] img){
		ArrayList<Integer> returnArray = new ArrayList<Integer>();
		for (int i = 0; i < 3; i++) returnArray.add(i,0);
		// Standard Deviation
		int val = standardDeviation(img);
		if (val > stdDevThresh - stdDevOfset && val < stdDevThresh + stdDevOfset){
			returnArray.set(0, 1);
		}
		// Mean Pixel Value
		val = meanPixelValue(img);
		if (val > meanPixelValue - meanPixelOfset && val < meanPixelValue + meanPixelOfset )
			returnArray.set(1, returnArray.get(1)+1);

		if(detectEyes(img))
			returnArray.set(2, returnArray.get(2)+1);

		return returnArray;

	}

	public void classify(){
		setUpTrainingFeatures();
		int faceCount =0;
		int nonFaceCount = 0;
		for (int[][] img: testFaces){
			ArrayList<Integer> features = createFeatureVector(img);
			double topEquationForNonFace= 0.0;
			double topEquationForFace = 0.0;
			// Iterate through features for face
			for (int i =0; i< features.size();i++){
				//System.out.println("test: "+features.get(i)+" Train feature: "+featuresInFace.get(i));
				//feature true
				if (features.get(i) == 1){
					if (i == 0) topEquationForFace = (double)featuresInFace.get(i);///(double)trainFaces.size();
					else topEquationForFace *= (double)featuresInFace.get(i)/(double)trainFaces.size();

				}
				//feature false
				else if (features.get(i) == 0){
					if (i == 0)topEquationForFace = (double)trainFaces.size() - (double)featuresInFace.get(i);///(double)trainFaces.size();
					else topEquationForFace *= (double)(trainFaces.size() - (double)featuresInFace.get(i))/(double)trainFaces.size();

				}
			}
			// Iterate through features for Non face
			for (int i =0; i< features.size();i++){
				//	System.out.println(features.size()+">>"+featuresInNonFace.size()+">>"+featuresInFace.size());
				//feature true
				if (features.get(i) == 1){
					if (i == 0) topEquationForNonFace = (double) featuresInNonFace.get(i);///trainNonFaces.size();
					else topEquationForNonFace *= (double) featuresInNonFace.get(i)/trainNonFaces.size();
				}
				//feature false
				else if (features.get(i) == 0){
					if (i == 0)  topEquationForNonFace = (double) (trainNonFaces.size() - featuresInNonFace.get(i));///trainNonFaces.size();
					else topEquationForNonFace *= (double) (trainNonFaces.size() - featuresInNonFace.get(i))/trainNonFaces.size();
				}
			}
			double bottomEquation = (double)topEquationForFace+(double)topEquationForNonFace;
			double face = (double)topEquationForFace/(double)bottomEquation;
			double nonFace = (double)topEquationForNonFace/(double)bottomEquation;
			//System.out.println(face+">>"+nonFace);
			if (face > nonFace)faceCount++;
			else nonFaceCount++;

		}
		System.out.println("TEST FACES: faceC: "+faceCount+" non: "+ nonFaceCount+"/"+testFaces.size());
		faceCount = 0;
		nonFaceCount = 0;
		//// Second Dataset
		for (int[][] img: testNonFaces){
			ArrayList<Integer> features = createFeatureVector(img);
			double topEquationForNonFace= 0.0;
			double topEquationForFace = 0.0;
			// Iterate through features for face
			for (int i =0; i< features.size();i++){
				//System.out.println("test: "+features.get(i)+" Train feature: "+featuresInFace.get(i));
				//feature true
				if (features.get(i) == 1){
					if (i == 0) topEquationForFace = (double)featuresInFace.get(i);///(double)trainFaces.size();
					else topEquationForFace *= (double)featuresInFace.get(i)/(double)trainFaces.size();

				}
				//feature false
				else if (features.get(i) == 0){
					if (i == 0)topEquationForFace = (double)trainFaces.size() - (double)featuresInFace.get(i);///(double)trainFaces.size();
					else topEquationForFace *= (double)(trainFaces.size() - (double)featuresInFace.get(i))/(double)trainFaces.size();

				}
			}
			// Iterate through features for Non face
			for (int i =0; i< features.size();i++){
				//	System.out.println(features.size()+">>"+featuresInNonFace.size()+">>"+featuresInFace.size());
				//feature true
				if (features.get(i) == 1){
					if (i == 0) topEquationForNonFace = (double) featuresInNonFace.get(i);///trainNonFaces.size();
					else topEquationForNonFace *= (double) featuresInNonFace.get(i)/trainNonFaces.size();
				}
				//feature false
				else if (features.get(i) == 0){
					if (i == 0)  topEquationForNonFace = (double) (trainNonFaces.size() - featuresInNonFace.get(i));///trainNonFaces.size();
					else topEquationForNonFace *= (double) (trainNonFaces.size() - featuresInNonFace.get(i))/trainNonFaces.size();
				}
			}
			double bottomEquation = (double)topEquationForFace+(double)topEquationForNonFace;
			double face = (double)topEquationForFace/(double)bottomEquation;
			double nonFace = (double)topEquationForNonFace/(double)bottomEquation;

			if (face > nonFace)faceCount++;
			else nonFaceCount++;
			//System.out.println(face+">>"+nonFace);
		}
		System.out.println("TEST NON FACES faceC: "+faceCount+" non: "+ nonFaceCount+"/"+testNonFaces.size());
	}

	public int standardDeviation(int[][] source)
	{
		image = source;
		int width = source.length;
		int height = source[0].length;
		int totalPixels = width * height;
		int pixel = 0;
		int VarianceSum = 0;
		double SumSquared = 0;
		int standardDeviation = 0;
		int mean = 0;
		for(int i =0; i<width; i++)
		{
			for(int j=0; j<height; j++)
			{
				mean+= pixelAt(i,j);
			}
		}
		mean /= width*height;

		//Loop through rast getting each grey level.
		for(int i =0; i<width; i++)
		{
			for(int j=0; j<height; j++)
			{
				pixel = pixelAt(i,j);
				VarianceSum = (pixel - mean);
				SumSquared += Math.pow(VarianceSum,2);
			}
		}
		standardDeviation = (int) Math.sqrt(SumSquared/totalPixels);
		return standardDeviation;
	}

	public int pixelAt(int x, int y){
		return image[x][y]& 0xFF;/////////will be the gray value;

	}
	public int pixelAt(int[][] img ,int x, int y){
		return img[x][y]& 0xFF;/////////will be the gray value;

	}
	public void loadImagesForFaces() throws IOException{
		// Populate test faces
		String fname = "datasets/test/face";
		File dir = new File(fname);
		for (File file : dir.listFiles()) {
			bufferedImage = ImageIO.read(file);
			image = processor.convertTo2D(bufferedImage);
			testFaces.add(image);
		}
		System.out.println("Done Test Faces");
		// Populate test non faces
		fname = "datasets/test/non-face";
		dir = new File(fname);
		for (File file : dir.listFiles()) {
			bufferedImage = ImageIO.read(file);
			image = processor.convertTo2D(bufferedImage);
			testNonFaces.add(image);
		}
		System.out.println("Done Test Non Faces");
		// Populate training faces
		fname = "datasets/train/face";
		dir = new File(fname);
		for (File file : dir.listFiles()) {
			bufferedImage = ImageIO.read(file);
			image = processor.convertTo2D(bufferedImage);
			trainFaces.add(image);
		}

		System.out.println("Loaded Training Faces");
		// Populate training non faces
		fname = "datasets/train/non-face";
		dir = new File(fname);
		for (File file : dir.listFiles()) {
			bufferedImage = ImageIO.read(file);
			image = processor.convertTo2D(bufferedImage);
			trainNonFaces.add(image);
		}
		System.out.println("Loaded Training Non Faces");
		System.out.println("Done Loading Faces");

	}
}
