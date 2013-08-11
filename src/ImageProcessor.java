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
	public ImageProcessor() throws IOException{
		try{
			//callMethods();
			// Starts naive bayes
			NaiveBayes faceDetection = new NaiveBayes(this);
		}
		catch(IOException e){e.printStackTrace();}
	}

public void callMethods() throws IOException{
	int[][] img;
	// Edge Detection 1.1
	img = convertTo2D(ImageIO.read(new File("images/test-pattern.png")));
	img = sobelEdgeDetect(img);
	outputToFile(img,"edited/edited_test-pattern.png");

	// Noise Cancellation Mean Filter 1.2.1
	img = convertTo2D(ImageIO.read(new File("images/ckt-board-saltpep.tif")));
	img = meanFilter(img);
	outputToFile(img,"edited/edited_mean_filter_ckt-board-saltpep.png");

	// Noise Cancellation Median Filter 1.2.2
	img = convertTo2D(ImageIO.read(new File("images/ckt-board-saltpep.tif")));
	img = medianFilter(img);
	outputToFile(img,"edited/edited_median_filter_ckt-board-saltpep.png");

	// Image Enhancement Blury Moon 1.3
	img = convertTo2D(ImageIO.read(new File("images/blurry-moon.tif")));
	img = convolutionFilter(img);
	outputToFile(img,"edited/edited_blurry-moon.png");

	// Mining Space Images 2.1
	img = convertTo2D(ImageIO.read(new File("images/hubble.tif")));
	img = threshold(img); //part 1.3
	outputToFile(img,"edited/edited_hubble.png");
}

	public int[][] threshold(int[][] img) throws IOException{
		img = meanFilter(img);
		int threshold = 200;
		for (int x = 0; x < img.length; x++) {
			for (int y = 0; y < img[0].length; y++) {
				int pixel = pixelAt(img,x, y);
				if (pixel >= threshold) img[x][y] = 255;
				else img[x][y] = 0;
			}
		}
		return img;
	}

	public int[][] medianFilter(int[][] img) throws IOException{
		int[][] newImage = new int[img.length][img[0].length];
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
		for(int x = 1; x < img.length-1; x++)
			for(int y = 1; y < img[0].length-1; y++){
				//multiply every value of the filter with corresponding image pixel
				values.clear();
				for(int filterX = 0; filterX < 3; filterX++){
					for(int filterY = 0; filterY < 3; filterY++)
					{
						imageX = (x - 3 / 2 + filterX + img.length) % img.length;
						imageY = (y - 3 / 2 + filterY + img[0].length) % img[0].length;
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

	public int[][] meanFilter(int[][] img) throws IOException{
		int[][] newImage = new int[img.length][img[0].length];
		double filter[][] = new double[][]
				{
				{1, 1, 1},
				{1, 1, 1},
				{1, 1, 1}
				};
		double factor = 1.0/9 ;
		double bias = 0;

		//apply the filter
		for(int x = 0; x < img.length-0; x++)
			for(int y = 0; y < img[0].length-0; y++)
			{
				int val = 0;

				//multiply every value of the filter with corresponding image pixel
				for(int filterX = 0; filterX < 3; filterX++){
					for(int filterY = 0; filterY < 3; filterY++)
					{
						int imageX = (x - 3 / 2 + filterX + img.length) % img.length;
						int imageY = (y - 3 / 2 + filterY + img[0].length) % img[0].length;
						val += pixelAt(img,imageX,imageY) * filter[filterX][filterY];
					}
					int mag	 = (int) (factor * val + bias);
					if (mag<0) mag = 0;
					else if (mag>255)mag = 255;
					newImage[x][y]= mag;
				}
			}
		return newImage;
	}

	public int[][] convolutionFilter(int[][] img)throws IOException{
		double factor = 1.0/8 ;
		double bias = 0;
		int[][] newImage = new int[img.length][img[0].length];
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
		for(int x = 0; x < img.length; x++)
			for(int y = 0; y < img[0].length; y++)
			{
				int val = 0;
				//multiply every value of the filter with corresponding image pixel
				for(int filterX = 0; filterX < 5; filterX++){
					for(int filterY = 0; filterY < 5; filterY++)
					{
						int imageX = (x - 5 / 2 + filterX + img.length) % img.length;
						int imageY = (y - 5 / 2 + filterY + img[0].length) % img[0].length;
						val += pixelAt(img,imageX,imageY) * filter[filterX][filterY];
					}
					int mag	 = (int) (factor * val + bias);
					if (mag<0) mag = 0;
					else if (mag>255)mag = 255;
					newImage[x][y]= mag;
				}
			}
		return newImage;

	}




	public int[][] sobelEdgeDetect(int[][] img) throws IOException{
int threshold= 255/2;
		int[][] newImage = new int[img.length][img[0].length];
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
				if (val > 255-threshold) val = 255;
				if (val <threshold) val = 0;
				newImage[x][y] = val;
			}
		}
		return newImage;

	}

	public int pixelAt(int[][] img,int x, int y){
		return img[x][y]& 0xFF;/////////will be the gray value;
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

	public void outputToFile(int[][] img, String fname){
		try {

			BufferedImage bufferedImage = new BufferedImage(img.length, img[0].length, BufferedImage.TYPE_INT_RGB);
			for (int x = 0;x<img.length;x++){
				for (int y = 0;y<img[0].length;y++){
				//	bufferedImage.setRGB(x, y, new Color(pixelAt(x,y),pixelAt(x,y),pixelAt(x,y)).getRGB());
					bufferedImage.setRGB(x, y, level_to_greyscale(pixelAt(img,x,y)));
				}
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
