import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;


/**
 * @author bannisowen
 *
 */
/**
 * @author bannisowen
 *
 */
public class FaceClassifier_Part2 {
	// ArrayLists for Images
	CSVWriter writer = new CSVWriter();
	ArrayList<int[][]> testFaces = new ArrayList<int[][]>();
	ArrayList<int[][]> testNonFaces = new ArrayList<int[][]>();
	ArrayList<int[][]> trainFaces =  new ArrayList<int[][]>();
	ArrayList<int[][]> trainNonFaces = new ArrayList<int[][]>();
	BufferedImage bufferedImage =new BufferedImage(19, 19, BufferedImage.TYPE_INT_ARGB);// new BufferedImage(19, 19, BufferedImage.TYPE_INT_ARGB


	int count= 0;
	int maxBlob = 0;
	int meanBlob = 0;
	int stdDevOfset = 10;
	int stdDevThresh = 29;
	int meanPixelValue = 0;
	int meanPixelOfset = 25;
	int blobCount = 0;

public static void main(String[] args) throws Exception{
	FaceClassifier_Part2 classifier = new FaceClassifier_Part2();
	classifier.loadImagesForFaces();
	classifier.classify();
	NaiveBayes.main(new String[0]);
}

	/**
	 *  ArrayList for feature vectors to be outputted to CSV
	 */
	ArrayList<ArrayList<Integer>> outputFile = new ArrayList<ArrayList<Integer>>();

	/**
	 * Eye Image for templating
	 */
	int[][] eye;
	int[][] bigTemplate;
	// ArrayList for summed up features
	private ArrayList <Integer> featuresInFace = new ArrayList <Integer>();
	private ArrayList <Integer> featuresInNonFace = new ArrayList <Integer>();
	ArrayList<ArrayList<Integer>> testArray = new ArrayList<ArrayList<Integer>>();
	public ArrayList<Integer> createFeatureVector(int[][] img, boolean test){
		ArrayList<Integer> returnArray = new ArrayList<Integer>();
		ArrayList<Integer> vector = new ArrayList<Integer>();
		int val = 0;
		for (int i = 0; i < 12; i++){returnArray.add(i,0);vector.add(i,0);}
		vector.add(12,0);
		// Standard Deviation
		val = standardDeviation(img);
		if (val > stdDevThresh - stdDevOfset && val < stdDevThresh + stdDevOfset){
			returnArray.set(0, 1);
			vector.set(0,1);
		}
		// Mean Pixel Value
		val = getMean(img);
		if (val > meanPixelValue - meanPixelOfset && val < meanPixelValue + meanPixelOfset ){
			returnArray.set(1, returnArray.get(1)+1);
			vector.set(1,1);
		}

		if (detectEyes(img)){
			returnArray.set(2, returnArray.get(2)+1);
			vector.set(2,1);
		}

		if (detectMouth(img)){
			returnArray.set(3, returnArray.get(3)+1);
			vector.set(3,1);
		}
		if (detectCheeks(img)){
			returnArray.set(4, returnArray.get(4)+1);
			vector.set(4,1);
		}
		if (detectEyeTemplate(img)){
			returnArray.set(5, returnArray.get(5)+1);
			vector.set(5,1);
		}
		if (detectNose(img)){
			returnArray.set(6, returnArray.get(6)+1);
			vector.set(6,1);
		}
		if (detectBigTemplate(img)){
			returnArray.set(7, returnArray.get(7)+1);
			vector.set(7,1);
		}
		if (compareCheeksToEyes(img)){
			returnArray.set(8, returnArray.get(8)+1);
			vector.set(8,1);
		}
		if (compareNoseToEyes(img)){
			returnArray.set(9, returnArray.get(9)+1);
			vector.set(9,1);
		}
		if (compareEyesToMouth(img)){
			returnArray.set(10, returnArray.get(10)+1);
			vector.set(10,1);
		}
		if (pixelCheck(img)){
			returnArray.set(11, returnArray.get(11)+1);
			vector.set(11,1);
		}
		if (test)
		vector.set(vector.size()-1,1);
		testArray.add(vector);
		return returnArray;

	}

public void setUpTrainingFeatures(){
	int co = 0;
	// Setup values
	// Mean Pixel Value
	for (int array= 0;array< trainFaces.size();array++){
		for (int x= 0;x< trainFaces.get(0).length;x++){
			for (int y= 0;y< trainFaces.get(0)[0].length;y++){
				meanPixelValue+= pixelAt(trainFaces.get(array),x, y);
			}
		}
	}
	meanPixelValue/= trainFaces.size()*trainFaces.get(0).length * trainFaces.get(0)[0].length;

	// Initialise Arrays
	for (int i = 0; i < 12; i++){
		featuresInFace.add(i,0);featuresInNonFace.add(i,0);
	}

	for (int[][] img: trainFaces){

		ArrayList<Integer> vector = new ArrayList<Integer>();
		// Initialise return array
		for(int i = 0;i< featuresInFace.size()+1;i++) vector.add(i,0);
		// Standard Deviation
		int val = standardDeviation(img);
		if (val > stdDevThresh - stdDevOfset && val < stdDevThresh + stdDevOfset){
			featuresInFace.set(0, featuresInFace.get(0)+1);
			vector.set(0,1);
		}

		// Mean
		val = getMean(img);
		if (val > meanPixelValue - meanPixelOfset && val < meanPixelValue + meanPixelOfset ){
			featuresInFace.set(1, featuresInFace.get(1)+1);
			vector.set(1,1);
		}

		if (detectEyes(img)){
			featuresInFace.set(2, featuresInFace.get(2)+1);
			vector.set(2,1);
		}

		if (detectMouth(img)){
			featuresInFace.set(3, featuresInFace.get(3)+1);
			vector.set(3,1);
		}
		if (detectCheeks(img)){
			featuresInFace.set(4, featuresInFace.get(4)+1);
			vector.set(4,1);
		}
		if (detectEyeTemplate(img)){
			featuresInFace.set(5, featuresInFace.get(5)+1);
			vector.set(5,1);
		}
		if (detectNose(img)){
			featuresInFace.set(6, featuresInFace.get(6)+1);
			vector.set(6,1);
		}
		if (detectBigTemplate(img)){
			featuresInFace.set(7, featuresInFace.get(7)+1);
			vector.set(7,1);
		}
		if (compareCheeksToEyes(img)){
			featuresInFace.set(8, featuresInFace.get(8)+1);
			vector.set(8,1);
		}
		if (compareNoseToEyes(img)){
			featuresInFace.set(9, featuresInFace.get(9)+1);
			vector.set(9,1);
		}
		if (compareEyesToMouth(img)){
			featuresInFace.set(10, featuresInFace.get(10)+1);
			vector.set(10,1);
		}
		if (pixelCheck(img)){
			featuresInFace.set(11, featuresInFace.get(11)+1);
			vector.set(11,1);
		}
		vector.set(vector.size()-1,1);
		outputFile.add(vector);
	}
	System.out.println("Faces Correct StdDev: "+(double)featuresInFace.get(0)/trainFaces.size());
	System.out.println("Faces Correct Mean: "+(double)featuresInFace.get(1)/trainFaces.size());
	System.out.println("Faces Correct Eyes: "+(double)featuresInFace.get(2)/trainFaces.size());
	System.out.println("Faces Correct Mouth: "+(double)featuresInFace.get(3)/trainFaces.size());
	System.out.println("Faces Correct Cheeks: "+(double)featuresInFace.get(4)/trainFaces.size());
	System.out.println("Faces Correct Template: "+(double)featuresInFace.get(5)/trainFaces.size());
	System.out.println("Faces Correct Nose: "+(double)featuresInFace.get(6)/trainFaces.size());
	System.out.println("Faces Correct Big Template: "+(double)featuresInFace.get(7)/trainFaces.size());
	System.out.println("Faces Correct Eyes to Cheeks "+(double)featuresInFace.get(8)/trainFaces.size());
	System.out.println("Faces Correct Eyes to Nose "+(double)featuresInFace.get(9)/trainFaces.size());
	System.out.println("Faces Correct Mouth to Cheeks "+(double)featuresInFace.get(10)/trainFaces.size());
	System.out.println("Faces Correct Pixel"+(double)featuresInFace.get(11)/trainFaces.size());
	System.out.println();


	for (int[][] img: trainNonFaces){
		ArrayList<Integer> vector = new ArrayList<Integer>();
		// Initialise return array
		for(int i = 0;i< featuresInFace.size()+1;i++) vector.add(i,0);
		// Standard Deviation
		int val = standardDeviation(img);
		if (val > stdDevThresh - stdDevOfset && val < stdDevThresh + stdDevOfset){
			featuresInNonFace.set(0, featuresInNonFace.get(0)+1);
			vector.set(0,1);
		}

		// Mean
		val = getMean(img);
		if ((val > meanPixelValue - meanPixelOfset && val < meanPixelValue + meanPixelOfset )){
			featuresInNonFace.set(1, featuresInNonFace.get(1)+1);
			vector.set(1,1);
		}

		if (detectEyes(img)){
			featuresInNonFace.set(2, featuresInNonFace.get(2)+1);
			vector.set(2,1);
		}

		if (detectMouth(img)){
			featuresInNonFace.set(3, featuresInNonFace.get(3)+1);
			vector.set(3,1);
		}
		if (detectCheeks(img)){
			featuresInNonFace.set(4, featuresInNonFace.get(4)+1);
			vector.set(4,1);
		}
		if (detectEyeTemplate(img)){
			featuresInNonFace.set(5, featuresInNonFace.get(5)+1);
			vector.set(5,1);
		}
		if (detectNose(img)){
			featuresInNonFace.set(6, featuresInNonFace.get(6)+1);
			vector.set(6,1);
		}
		if (detectBigTemplate(img)){
			featuresInNonFace.set(7, featuresInNonFace.get(7)+1);
			vector.set(7,1);
		}
		if (compareCheeksToEyes(img)){
			featuresInNonFace.set(8, featuresInNonFace.get(8)+1);
			vector.set(8,1);
		}
		if (compareNoseToEyes(img)){
			featuresInNonFace.set(9, featuresInNonFace.get(9)+1);
			vector.set(9,1);
		}
		if (compareEyesToMouth(img)){
			featuresInNonFace.set(10, featuresInNonFace.get(10)+1);
			vector.set(10,1);
		}
		if (pixelCheck(img)){
			featuresInNonFace.set(11, featuresInNonFace.get(11)+1);
			vector.set(11,1);
		}
		vector.set(vector.size()-1,0);
		outputFile.add(vector);
	}
	System.out.println("Faces Incorrect StdDev: "+(double)featuresInNonFace.get(0)/trainNonFaces.size());
	System.out.println("Faces Incorrect Mean: "+(double)featuresInNonFace.get(1)/trainNonFaces.size());
	System.out.println("Faces Incorrect Eyes: "+(double)featuresInNonFace.get(2)/trainNonFaces.size());
	System.out.println("Faces Incorrect Mouth: "+(double)featuresInNonFace.get(3)/trainNonFaces.size());
	System.out.println("Faces Incorrect Cheeks: "+(double)featuresInNonFace.get(4)/trainNonFaces.size());
	System.out.println("Faces Incorrect Template: "+(double)featuresInNonFace.get(5)/trainNonFaces.size());
	System.out.println("Faces Incorrect Nose: "+(double)featuresInNonFace.get(6)/trainNonFaces.size());
	System.out.println("Faces Incorrect Big Template: "+(double)featuresInNonFace.get(7)/trainNonFaces.size());
	System.out.println("Faces Incorrect Eyes to Cheeks "+(double)featuresInNonFace.get(8)/trainNonFaces.size());
	System.out.println("Faces Incorrect Eyes to Nose "+(double)featuresInNonFace.get(9)/trainNonFaces.size());
	System.out.println("Faces Incorrect Mouth to Cheeks "+(double)featuresInNonFace.get(10)/trainNonFaces.size());
	System.out.println("Faces Incorrect Pixel "+(double)featuresInNonFace.get(11)/trainNonFaces.size());
	writer.writeFile(outputFile, "datasets/face-training.dat");

}


private int getMean(int[][] img) {
	int mean = 0;
	int width = img[0].length;
	int height = img.length;
	for(int i =0; i<height; i++)
	{
		for(int j=0; j<width; j++)
		{
			mean+= pixelAt(img,i,j);
		}
	}
	mean /= width*height;
	return mean;
}

public int getMax(int[][] img) {
	int max = 0;
	for(int x =0; x<19; x++)
	{
		for(int y=0; y<19; y++)
		{
			if (pixelAt(img,x,y) > max) max = pixelAt(img,x,y);
		}
	}
	return max;
}
public int getMedian(int[][] img){
	ArrayList<Integer> values = new ArrayList<Integer>();
	int median = 0;
	for(int x =0; x<19; x++){
		for(int y=0; y<19; y++){
			values.add(pixelAt(img,x,y));
		}
	}
	Collections.sort(values);
	if (values.size()%2 == 1)
		median = values.get(values.size() / 2);

	else
		median = values.get(values.size() / 2) + values.get(values.size() / 2 + 1)/2;
	return median;
}
public int[][] adaptiveThreshold(int[][] img){
	img = noiseFilterMedian(img);
	//int max = getMax(img);
	//	int threshold = (int) ((int) max - (getMean(img)/6));
	int threshold = getMedian(img);
	for (int x = 1; x < 18; x++) {
		for (int y = 1; y < 18; y++) {
			int pixel = pixelAt(img,x, y);
			if (pixel >= threshold) img[x][y] = 0;
			else img[x][y] = 255;
		}
	}
	return img;
}



public int[][] connectedComponentAlgorithmEyes(int[][] img){
	img = adaptiveThreshold(img);
	int [][] labels = new int[19][19];
	int label = 1;


	for (int y = 1; y< img.length-1;y++){
		for (int x = 1; x< img.length-1;x++){
			if (pixelAt(img,x,y) == 255 ){
				if (labels[x-1][y-1] == 0 && labels[x][y-1] == 0 && labels[x+1][y-1] == 0 && labels[x-1][y] == 0){
					labels[x][y] = label;
					label++;
				}
				else{
					// Get the minimum value from neigbours thats not 0
					int min = label+1;
					if (labels[x-1][y-1] < min && labels[x-1][y-1]>0) min = labels[x-1][y-1];
					if (labels[x][y-1] < min && labels[x][y-1]>0) min = labels[x][y-1];
					if (labels[x+1][y-1] < min && labels[x+1][y-1]>0) min = labels[x+1][y-1];
					if (labels[x-1][y] < min && labels[x-1][y]>0) min = labels[x-1][y];
					// For all neighbours and current pixel that are foreground make label min
					if (pixelAt(img,x-1,y-1) == 255) labels[x-1][y-1] = min;
					if (pixelAt(img,x,y-1) == 255) labels[x][y-1] = min;
					if (pixelAt(img,x+1,y-1) == 255) labels[x+1][y-1] = min;
					if (pixelAt(img,x-1,y) == 255) labels[x-1][y] = min;
					if (pixelAt(img,x,y) == 255) labels[x][y] = min;
				}
			}
		}
	}
	for(int x = 0; x < 19; x++){
		labels[x][0]= pixelAt(img,x,0);
	}
	for(int x = 0; x < 19; x++){
		labels[x][18]= pixelAt(img,x,18);
	}
	for(int x = 0; x < 19; x++){
		labels[0][x]= pixelAt(img,0,x);
	}
	for(int x = 0; x < 19; x++){
		labels[18][x]= pixelAt(img,18,x);
	}
	return labels;

}



private int[][]  noiseFilterMedian(int[][] img){
	int[][] newImage = new int[19][19];
	int filterWidth = 3;
	int filterHeight = 3;
	ArrayList<Integer> values = new ArrayList<Integer>();
	//apply the filter
	int imageX =0;
	int imageY =0;

	for(int x = 0; x < 19; x++){
		newImage[x][0]= img[x][0];
	}
	for(int x = 0; x < 19; x++){
		newImage[x][18]= img[x][18];
	}
	for(int x = 0; x < 19; x++){
		newImage[0][x]= img[0][x];
	}
	for(int x = 0; x < 19; x++){
		newImage[18][x]= img[18][x];
	}

	for(int x = 1; x < 19-1; x++)
		for(int y = 1; y < 19-1; y++){
			//multiply every value of the filter with corresponding image pixel
			values.clear();
			for(int filterX = 0; filterX < 3; filterX++){
				for(int filterY = 0; filterY < 3; filterY++)
				{
					imageX = (x - 3 / 2 + filterX + 19) % 19;
					imageY = (y - 3 / 2 + filterY + 19) % 19;
					values.add(pixelAt(img,imageX,imageY));

				}
			}
			Collections.sort(values);
			if((filterWidth * filterHeight) % 2 == 1)
			{
				int result =  values.get(filterWidth * filterHeight / 2);
				if (result<0) result = 0;
				else if (result>255)result = 255;
				newImage[x][y] = result;
			}
			else if(filterWidth >= 2)
			{
				int result =(values.get(filterWidth * filterHeight / 2) + values.get(filterWidth * filterHeight / 2 + 1) / 2);
				if (result<0) result = 0;
				else if (result>255)result = 255;
				newImage[x][y]= result;

			}

		}
	return newImage;
}
public boolean detectEyes (int[][] img){
	int[][] labels = connectedComponentAlgorithmEyes(img);
	int numKeys = 0;
	HashMap<Integer, Integer> blobs = new HashMap<Integer, Integer>();
	for (int y = 1; y< img.length/3;y++){
		for (int x = 1; x< img.length-1;x++){
			if (labels[x][y] == 0) continue;
			if (blobs.get(labels[x][y]) == null){
				blobs.put(labels[x][y], 1);
				numKeys++;
			}
			else blobs.put(labels[x][y], blobs.get(labels[x][y])+1);
		}
	}
	int acceptableBlobs = 0;
	if (numKeys >= 2){
		for (int i: blobs.keySet()){
			if (blobs.get(i) > 13) acceptableBlobs++;
		}
	}
	if (acceptableBlobs >= 1 && acceptableBlobs <= 3 )
		return true;
	else return false;
}
/**
 * Should be detected as a dark spot
 * @param img
 * @return
 */
public boolean detectMouth(int[][] img){
	int[][] labels = connectedComponentAlgorithmEyes(img);
	int numKeys = 0;
	HashMap<Integer, Integer> blobs = new HashMap<Integer, Integer>();
	for (int y = img.length/3+img.length/3; y< img.length-1;y++){
		for (int x = 1; x< img.length-1;x++){
			if (labels[x][y] == 0) continue;
			if (blobs.get(labels[x][y]) == null){
				blobs.put(labels[x][y], 1);
				numKeys++;
			}
			else blobs.put(labels[x][y], blobs.get(labels[x][y])+1);
		}
	}
	int acceptableBlobs = 0;
	if (numKeys >= 1){
		for (int i: blobs.keySet()){
			if (blobs.get(i) > 25) acceptableBlobs++;
		}
	}
	if (acceptableBlobs >= 1 && acceptableBlobs <= 2 )
		return true;
	else return false;
}

public boolean compareCheeksToEyes(int[][] img){
	img = img.clone();
	int cheekMean = 0;
	int eyeMean = 0;
	int eyeCount = 0;
	int cheekCount = 0;
	// Eyes
	for (int y = 0; y< img.length/3;y++){
		for (int x = 0; x< img.length/3;x++){
			eyeMean += pixelAt(img,  (img.length/3+img.length/3)+x, y);
			eyeMean += pixelAt(img, x, y);
			eyeCount++;
			eyeCount++;
		}
		}
	// Cheeks
	for (int y = img.length/3; y< img.length/3+img.length/3;y++){
		for (int x = 0; x< img.length/3;x++){
			cheekMean += pixelAt(img,  (img.length/3+img.length/3)+x, y);
			cheekMean += pixelAt(img, x, y);
			cheekCount++;
			cheekCount++;
		}
		}
	eyeMean/=eyeCount;
	cheekMean/=cheekCount;

	if (eyeMean + 6 < cheekMean) return true;
	return false;
}

public boolean compareEyesToMouth(int[][] img){
	img = img.clone();
	int cheekMean = 0;
	int mouthMean = 0;
	int mouthCount = 0;
	int cheekCount = 0;
	// Mouth
	for (int y = img.length/3+img.length/3; y< img.length-1;y++){
		for (int x = img.length/3; x< img.length/3+img.length/3;x++){
			mouthMean += pixelAt(img,  (img.length/3+img.length/3), y);
			mouthMean += pixelAt(img, x, y);
			mouthCount++;
			mouthCount++;
		}
		}
	// Cheeks
	for (int y = img.length/3; y< img.length/3+img.length/3;y++){
		for (int x = 0; x< (img.length/3);x++){
			cheekMean += pixelAt(img,  ((img.length/3)*2)+x, y);
			cheekMean += pixelAt(img, x, y);
			cheekCount++;
			cheekCount++;
		}
		}
	mouthMean/=mouthCount;
	cheekMean/=cheekCount;

	if (mouthMean + 25 < cheekMean) return true;
	return false;
}
public boolean compareNoseToEyes(int[][] img){
	img = img.clone();
	int noseMean = 0;
	int noseCount = 0;
	int eyeMean = 0;
	int eyeCount = 0;

	// Eyes
	for (int y = 0; y< img.length/3;y++){
		for (int x = 0; x< img.length/3;x++){
			eyeMean += pixelAt(img,  (img.length/3+img.length/3)+x, y);
			eyeMean += pixelAt(img, x, y);
			eyeCount++;
			eyeCount++;
		}
		}
	// Nose
	for (int y = 0; y< (img.length/3)*2;y++){
		for (int x = (img.length/5)*2;x< (img.length/5)*3;x++){
			noseMean += pixelAt(img, x, y);
			noseCount++;
		}
		}
	eyeMean/=eyeCount;
	noseMean/=noseCount;

	if (eyeMean + 6 < noseMean) return true;
	return false;
}

public int[][] threshold(int[][] img, int thresh){
	img = img.clone();
	for (int y = 0; y< img.length-1;y++){
		for (int x = 0; x< img.length-1;x++){
			if (pixelAt(img, x, y)> thresh) img[x][y] = 255;
			else img[x][y] = 0;
		}
		}
	return img;
}

public boolean pixelCheck(int[][] img){

	if (pixelAt(img, 9, 8) > 180 && pixelAt(img, 9, 6) > 170 ) return true;
	 return false;

}

public int[][] sobelEdgeDetect(int[][] img) throws IOException{
int threshold= getMedian(img);
System.out.println(threshold);
		int[][] newImage = img.clone();
		int[][] sobel_x = new int[][]{{-1,-2,-1},{0,0,0}, {1,2,1}};
		int[][] sobel_y = new int[][]{{-1,0,1},{-2,0,2}, {-1,0,1}};
		int pixelX = 0;
		int pixelY = 0;
		int val = 0;
		for (int x = 1;x<img.length-1;x++){
			for (int y = 1;y<img[0].length-1;y++){

				pixelX = (sobel_x[0][0] * pixelAt(img,x-1,y-1)) + (sobel_x[0][1] * pixelAt(img,x,y-1)) + (sobel_x[0][2] * pixelAt(img,x+1,y-1)) +
						(sobel_x[1][0] * pixelAt(img,x-1,y))   + (sobel_x[1][1] * pixelAt(img,x,y))   + (sobel_x[1][2] * pixelAt(img,x+1,y)) +
						(sobel_x[2][0] * pixelAt(img,x-1,y+1)) + (sobel_x[2][1] * pixelAt(img,x,y+1)) + (sobel_x[2][2] * pixelAt(img,x+1,y+1));
				pixelY = (sobel_y[0][0] * pixelAt(img,x-1,y-1)) + (sobel_y[0][1] *pixelAt(img,x,y-1)) + (sobel_y[0][2] * pixelAt(img,x+1,y-1)) +
						(sobel_y[1][0] * pixelAt(img,x-1,y))   + (sobel_y[1][1] * pixelAt(img,x,y))   + (sobel_y[1][2] * pixelAt(img,x+1,y)) +
						(sobel_y[2][0] * pixelAt(img,x-1,y+1)) + (sobel_y[2][1] * pixelAt(img,x,y+1)) + (sobel_y[2][2] * pixelAt(img,x+1,y+1));
				val =  (int) (Math.sqrt((pixelX * pixelX) + (pixelY * pixelY)));
				if (val > 255) val = 255;
				if (val < 0) val = 0;
				newImage[x][y] = val;
			}
		}
		outputToFile(newImage,"te");
		return newImage;

	}

/**
 * Should be detected as either a dark spot from the side of the nose or white spot from top of nose
 * @param img
 * @return
 */
public boolean detectNose(int[][] img){
	int[][] labels = connectedComponentAlgorithmEyes(img);
	HashMap<Integer, Integer> blobs = new HashMap<Integer, Integer>();
	for (int y = img.length/3; y< img.length/3+img.length/3-1;y++){
		for (int x = 0; x< img.length;x++){
			if (labels[x][y] == 0) continue;
			if (blobs.get(labels[x][y]) == null){
				blobs.put(labels[x][y], 1);
			}
			else blobs.put(labels[x][y], blobs.get(labels[x][y])+1);
		}
	}

	int acceptableBlobs = 0;
	for (int i: blobs.keySet()){
		if (blobs.get(i) > 5) acceptableBlobs++;
	}
	if (acceptableBlobs <= 1  )
		return true;
	else {

		return false;}
}

/**
 * Cheeks should reflect the most light and so not be detected as dark spots
 * @param img
 * @return
 */
public boolean detectCheeks(int[][] img){
	int[][] labels = connectedComponentAlgorithmEyes(img);
	HashMap<Integer, Integer> blobs = new HashMap<Integer, Integer>();
	for (int y = (int) ((int)img.length/3); y< img.length/3+img.length/3-1;y++){
		for (int x = 1; x< img.length/3;x++){
			if (labels[x][y] == 0) continue;
			if (blobs.get(labels[x][y]) == null){
				blobs.put(labels[x][y], 1);
			}
			else blobs.put(labels[x][y], blobs.get(labels[x][y])+1);
		}
	}
	for (int y = (int) ((int)img.length/3); y< img.length/3+img.length/3-1;y++){
		for (int x = img.length/3+img.length/3; x< img.length-1;x++){
			if (labels[x][y] == 0) continue;
			if (blobs.get(labels[x][y]) == null){
				blobs.put(labels[x][y], 1);
			}
			else blobs.put(labels[x][y], blobs.get(labels[x][y])+1);
		}
	}

	int acceptableBlobs = 0;
	for (int i: blobs.keySet()){
		if (blobs.get(i) > 13) acceptableBlobs++;
	}
	if (acceptableBlobs <= 0  )
		return true;
	else return false;
}

public boolean detectEyeTemplate (int[][] img){
	img = noiseFilterMedian(img);
	double minSAD = 100000;
	for (int y= 0;y<(img.length/2)-eye.length;y++){
		for (int x= 0;x<img.length-eye[0].length;x++){
			double SAD = 0.0;
			for(int j = 0;j < eye[0].length;j++){
			for(int i = 0;i < eye.length;i++){
					int imgPixel  = pixelAt(img, x+i, y+j);
					int templatePixel  = pixelAt(eye, i, j);
					   SAD += Math.abs( imgPixel - templatePixel);
			}
			}
			  if ( minSAD > SAD ) {
		            minSAD = SAD;
		        }

		}
	}
	if (minSAD <= 600) return true;
	return false;
}
public boolean detectBigTemplate (int[][] img){
	double minSAD = 100000;
			double SAD = 0.0;
			for(int j = 0;j < bigTemplate[0].length;j++){
			for(int i = 0;i < bigTemplate.length;i++){
					int imgPixel  = pixelAt(img, 0+i, 0+j);
					int templatePixel  = pixelAt(bigTemplate, i, j);
					   SAD += Math.abs( imgPixel - templatePixel);
			}
			}

	if (SAD <= 16000){return true;}

	return false;
}




public void classify(){
	System.out.println("Classifying");
	setUpTrainingFeatures();
	int faceCount =0;
	int nonFaceCount = 0;
	for (int[][] img: testFaces){

		ArrayList<Integer> features = createFeatureVector(img, true);
	}
	for (int[][] img: testNonFaces){
		ArrayList<Integer> features = createFeatureVector(img, false);
	}
	System.out.println("Generating files");
	writer.writeFile(testArray, "datasets/face-test.dat");
	//writer.writeFile(testArray);
	System.out.println("Files Generated");
}

public int standardDeviation(int[][] img){
	int width = img.length;
	int height = img[0].length;
	int totalPixels = width * height;
	int pixel = 0;
	int VarianceSum = 0;
	double SumSquared = 0;
	int standardDeviation = 0;
	int mean = 0;
	for(int i =0; i<width; i++)
		for(int j=0; j<height; j++)
		mean+= pixelAt(img,i,j);

	mean /= width*height;
	for(int i =0; i<width; i++)
	{
		for(int j=0; j<height; j++)
		{
			pixel = pixelAt(img,i,j);
			VarianceSum = (pixel - mean);
			SumSquared += Math.pow(VarianceSum,2);
		}
	}
	standardDeviation = (int) Math.sqrt(SumSquared/totalPixels);
	return standardDeviation;
}

public int pixelAt(int[][] img ,int x, int y){
	return img[x][y]& 0xFF;/////////will be the gray value;

}
public void loadImagesForFaces() throws IOException{

	int[][] img;

	// Load eye
	String fname = "images/eye.png";
	File f = new File (fname);
	bufferedImage = ImageIO.read(f);
	eye = convertTo2D(bufferedImage);

	// Load eye
	 fname = "images/bigTemplate.png";
	 f = new File (fname);
	bufferedImage = ImageIO.read(f);
	bigTemplate = convertTo2D(bufferedImage);


	// Populate test faces
	fname = "datasets/test/face";
	File dir = new File(fname);
	for (File file : dir.listFiles()) {
		bufferedImage = ImageIO.read(file);
		img = convertTo2D(bufferedImage);
		testFaces.add(img);
	}
	System.out.println("Done Test Faces");
	// Populate test non faces
	fname = "datasets/test/non-face";
	dir = new File(fname);
	for (File file : dir.listFiles()) {
		bufferedImage = ImageIO.read(file);
		img = convertTo2D(bufferedImage);
		testNonFaces.add(img);
	}
	System.out.println("Done Test Non Faces");
	// Populate training faces
	fname = "datasets/train/face";
	dir = new File(fname);
	for (File file : dir.listFiles()) {
		bufferedImage = ImageIO.read(file);
		img = convertTo2D(bufferedImage);
		trainFaces.add(img);
	}

	System.out.println("Loaded Training Faces");
	// Populate training non faces
	fname = "datasets/train/non-face";
	dir = new File(fname);
	for (File file : dir.listFiles()) {
		bufferedImage = ImageIO.read(file);
		img = convertTo2D(bufferedImage);
		trainNonFaces.add(img);
	}
	System.out.println("Loaded Training Non Faces");
	System.out.println("Done Loading Faces");
}

public void outputBlobs(int[][] labels,int[][] img,String fname) throws IOException {
	BufferedImage returnImage = new BufferedImage(38, 19, BufferedImage.TYPE_INT_ARGB);
	for (int x = 0;x<img.length;x++){
		for (int y = 0;y<img[0].length;y++){
			int val = labels[x][y];
			if (x!=18 && y!=18 && x!=0 && y!=0){
				returnImage.setRGB(x, y, new Color(val*20,val*20,val*20).getRGB());}
			else returnImage.setRGB(x, y, new Color(val,val,val).getRGB());
			returnImage.setRGB(img.length+x, y, img[x][y]);
		}
	}
	File outputfile = new File(fname);
	ImageIO.write(returnImage, "png", outputfile);
}

public void outputToFile(int[][] img,String fname){
	try {
		BufferedImage returnImage = new BufferedImage(19, 19, BufferedImage.TYPE_INT_ARGB);
		for (int x = 0;x<img.length;x++){
			for (int y = 0;y<img[0].length;y++){
				returnImage.setRGB(x, y, new Color(pixelAt(img,x,y),pixelAt(img,x,y),pixelAt(img,x,y)).getRGB());}
		}
		Graphics g = returnImage.createGraphics();
		g.setColor(Color.RED);
		g.fillOval(50, 50, 50, 50);
		ImageIcon icon = new ImageIcon();
		icon.setImage(returnImage);
		JOptionPane.showMessageDialog(null, icon);
		File outputfile = new File(fname);
		ImageIO.write(returnImage, "png", outputfile);
	} catch (Exception e) {e.printStackTrace();}
}

public static int level_to_greyscale(int level) {
	return (level << 16) | (level << 8) | level;
}
public class CSVWriter
{


   public void writeFile(ArrayList<ArrayList <Integer>> features,String fname)
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
	    writer.append("@attribute MouthToCheeks numeric");
	    writer.append('\n');
	    writer.append("@attribute Pixel numeric");
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
	    writer.flush();
	    writer.close();
	}


	catch(IOException e)
	{
	     e.printStackTrace();
	}
    }
	public void writeFile (ArrayList<ArrayList <Integer>> features){
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
public int[][] convertTo2D(BufferedImage image) {
	int width = image.getWidth();
	int height = image.getHeight();
	int[][] result = new int[width][height];
	for (int x = 0; x < width; x++) {
		for (int y = 0; y < height; y++) {
			result[x][y] = image.getRGB(x, y);
		}
	}
	return result;
}
}
