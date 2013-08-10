import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;


public class ImageProcessor{
	BufferedImage bufferedImage;
	int[][] image;

	public ImageProcessor() throws IOException{
		try{
//			sobelEdgeDetect("images/test-pattern.png"); // part 1.1
//			outputToFile("edited/test-pattern.png");
//			noiseFilterMean("images/ckt-board-saltpep.png");	//part 1.2.1
//			outputToFile("edited/edited_mean_filter_ckt-board-saltpep.png");
//			noiseFilterMedian();	//part 1.2.2
//			outputToFile("edited/edited_median_filter_ckt-board-saltpep.png");
//			convolutionFilter(); //part 1.3
//			outputToFile("edited/edited_blurry-moon.png");
		//	mineSpaceImage();
		//	outputToFile("edited/edited_hubble.png");
			detectFace();
		}
		catch(IOException e){e.printStackTrace();}
	}



	public void detectFace() throws IOException{
		new NaiveBayes(this);

	}



	public void mineSpaceImage() throws IOException{
		noiseFilterMean("images/hubble.png");
		//noiseFilterMean("datasets/train/face/face00035.pgm");
		int[][] newImage = image.clone();

		int threshold = 200;
		for (int x = 0; x < bufferedImage.getWidth(); x++) {
			for (int y = 0; y < bufferedImage.getHeight(); y++) {

				int pixel = pixelAt(x, y);
				if (pixel >= threshold) newImage[x][y] = 255;
				else newImage[x][y] = 0;

			}
		}

		image= newImage;


	}

	private void noiseFilterMedian() throws IOException{
		File file = new File("datasets/train/face/face00001.pgm");
		//File file = new File("images/ckt-board-saltpep.png");
		System.out.println("Loading: "+file.getName());
		System.out.println("Start: Filtering by Median");
		bufferedImage = ImageIO.read(file);
		image = convertTo2D(bufferedImage);
		int[][] newImage = new int[bufferedImage.getWidth()][bufferedImage.getHeight()];
		double filter[][] = new double[][]
				{
				{1/9, 1/9, 1/9},
				{1/9, 1/9, 1/9},
				{1/9, 1/9, 1/9}
				};
		int filterWidth = 3;
		int filterHeight = 3;
		double factor = 1.0 ;
		double bias = 0;
		ArrayList<Integer> values = new ArrayList<Integer>();
		//apply the filter
		int imageX =0;
		int imageY =0;
		for(int x = 1; x < bufferedImage.getWidth()-1; x++)
			for(int y = 1; y < bufferedImage.getHeight()-1; y++){
				//multiply every value of the filter with corresponding image pixel
				values.clear();
				for(int filterX = 0; filterX < 3; filterX++){
					for(int filterY = 0; filterY < 3; filterY++)
					{
						imageX = (x - 3 / 2 + filterX + bufferedImage.getWidth()) % bufferedImage.getWidth();
						imageY = (y - 3 / 2 + filterY + bufferedImage.getHeight()) % bufferedImage.getHeight();
						values.add(pixelAt(imageX,imageY));

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
		image= newImage;
		System.out.println("End: Filtering by Median");
	}

	public void noiseFilterMean(String s) throws IOException{

		File file = new File(s);
		System.out.println("Loading: "+file.getName());
		System.out.println("Start: Filtering by Mean");
		bufferedImage = ImageIO.read(file);
		image = convertTo2D(bufferedImage);
		int[][] newImage = new int[bufferedImage.getWidth()][bufferedImage.getHeight()];
		double filter[][] = new double[][]
				{
				{1, 1, 1},
				{1, 1, 1},
				{1, 1, 1}
				};
		double factor = 1.0/9 ;
		double bias = 0;

		//apply the filter
		for(int x = 0; x < bufferedImage.getWidth()-0; x++)
			for(int y = 0; y < bufferedImage.getHeight()-0; y++)
			{
				int val = 0;

				//multiply every value of the filter with corresponding image pixel
				for(int filterX = 0; filterX < 3; filterX++){
					for(int filterY = 0; filterY < 3; filterY++)
					{
						int imageX = (x - 3 / 2 + filterX + bufferedImage.getWidth()) % bufferedImage.getWidth();
						int imageY = (y - 3 / 2 + filterY + bufferedImage.getHeight()) % bufferedImage.getHeight();
						val += pixelAt(imageX,imageY) * filter[filterX][filterY];
					}
					int mag	 = (int) (factor * val + bias);
					if (mag<0) mag = 0;
					else if (mag>255)mag = 255;
					newImage[x][y]= mag;

				}
			}
		image= newImage;
		System.out.println("End: Filtering by Mean");

	}

	public void convolutionFilter()throws IOException{

		double factor = 1.0/8 ;
		double bias = 0;
		File file = new File("images/blurry-moon.png");
		System.out.println("Loading: "+file.getName());
		System.out.println("Start: Sharpening Moon");
		bufferedImage = ImageIO.read(file);
		image = convertTo2D(bufferedImage);
		int[][] newImage = new int[bufferedImage.getWidth()][bufferedImage.getHeight()];
		//				double filter[][] = new double[][]
		//						{
		//						{-1, -1, -1},
		//						{-1, 9, -1},
		//						{-1, -1, -1}
		//						};
		double filter[][] = new double[][]
				{
				{-1,-1,-1,-1,-1},
				{-1,2,2,2,-1},
				{-1,2,8,2,-1},
				{-1,-1,-1,-1,-1},
				{-1,2,2,2,-1}
				};
		//apply the filter
		for(int x = 0; x < bufferedImage.getWidth(); x++)
			for(int y = 0; y < bufferedImage.getHeight(); y++)
			{
				int val = 0;

				//multiply every value of the filter with corresponding image pixel
				for(int filterX = 0; filterX < 5; filterX++){
					for(int filterY = 0; filterY < 5; filterY++)
					{
						int imageX = (x - 5 / 2 + filterX + bufferedImage.getWidth()) % bufferedImage.getWidth();
						int imageY = (y - 5 / 2 + filterY + bufferedImage.getHeight()) % bufferedImage.getHeight();
						val += pixelAt(imageX,imageY) * filter[filterX][filterY];
					}
					int mag	 = (int) (factor * val + bias);
					if (mag<0) mag = 0;
					else if (mag>255)mag = 255;
					newImage[x][y]= mag;

				}
			}
		image= newImage;
		System.out.println("End: Sharpening Moon");

	}




	public void sobelEdgeDetect(String string) throws IOException{
int threshold= 255/2;
		File file = new File(string);
		System.out.println("Loading: "+file.getName());
		System.out.println("Start: Detecting Edges");
		bufferedImage = ImageIO.read(file);
		image = convertTo2D(bufferedImage);
		int[][] newImage = new int[image.length][image[0].length];
		int[][] sobel_x = new int[][]{{-1,-2,-1},{0,0,0}, {1,2,1}};
		int[][] sobel_y = new int[][]{{-1,0,1},{-2,0,2}, {-1,0,1}};

		int pixelX = 0;
		int pixelY = 0;
		int val = 0;

		for (int x = 1;x<bufferedImage.getWidth()-1;x++){
			for (int y = 1;y<bufferedImage.getHeight()-1;y++){

				pixelX = (sobel_x[0][0] * pixelAt(x-1,y-1)) + (sobel_x[0][1] * pixelAt(x,y-1)) + (sobel_x[0][2] * pixelAt(x+1,y-1)) +
						(sobel_x[1][0] * pixelAt(x-1,y))   + (sobel_x[1][1] * pixelAt(x,y))   + (sobel_x[1][2] * pixelAt(x+1,y)) +
						(sobel_x[2][0] * pixelAt(x-1,y+1)) + (sobel_x[2][1] * pixelAt(x,y+1)) + (sobel_x[2][2] * pixelAt(x+1,y+1));
				pixelY = (sobel_y[0][0] * pixelAt(x-1,y-1)) + (sobel_y[0][1] *pixelAt(x,y-1)) + (sobel_y[0][2] * pixelAt(x+1,y-1)) +
						(sobel_y[1][0] * pixelAt(x-1,y))   + (sobel_y[1][1] * pixelAt(x,y))   + (sobel_y[1][2] * pixelAt(x+1,y)) +
						(sobel_y[2][0] * pixelAt(x-1,y+1)) + (sobel_y[2][1] * pixelAt(x,y+1)) + (sobel_y[2][2] * pixelAt(x+1,y+1));
				val =  (int) (Math.sqrt((pixelX * pixelX) + (pixelY * pixelY)));
				if (val > 255-threshold) val = 255;
				if (val <threshold) val = 0;
				newImage[x][y] = val;
			}
		}
		image= newImage;
		System.out.println("End: Detecting Edges");

	}

	public int pixelAt(int x, int y){
		return image[x][y]& 0xFF;/////////will be the gray value;

	}

	public int[][] convertTo2D(BufferedImage image) {
		//System.out.println(image);
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
	public void outputToFile(String fname){
		try {

			 bufferedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
			for (int x = 0;x<image.length;x++){
				for (int y = 0;y<image[0].length;y++){
					//System.out.print( new Color(pixelAt(x,y),pixelAt(x,y),pixelAt(x,y)).getRGB()+" ");
				//	bufferedImage.setRGB(x, y, new Color(pixelAt(x,y),pixelAt(x,y),pixelAt(x,y)).getRGB());
					bufferedImage.setRGB(x, y, level_to_greyscale(pixelAt(x,y)));
				}
				//System.out.println();
			}

			File outputfile = new File(fname);
			ImageIO.write(bufferedImage, "png", outputfile);
			System.out.println("DONE\n");
		} catch (IOException e) {}

	}
	public static int level_to_greyscale(int level) {
		return (level << 16) | (level << 8) | level;
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException{
		ImageProcessor main = new ImageProcessor();
	}


}
