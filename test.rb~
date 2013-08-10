 
require 'chunky_png'
 
class ChunkyPNG::Image
  def at(x,y)
    ChunkyPNG::Color.to_grayscale_bytes(self[x,y]).first
  end
end
 
img = ChunkyPNG::Image.from_file('test-pattern.png')
 
sobel_x = [[-1,0,1],
           [-2,0,2],
           [-1,0,1]]
 
sobel_y = [[-1,-2,-1],
           [0,0,0],
           [1,2,1]]
 
edge = ChunkyPNG::Image.new(img.width, img.height, ChunkyPNG::Color::TRANSPARENT)
 
for x in 1..img.width-2
  for y in 1..img.height-2
     #puts  "#{img.at(x,y)}"
    pixel_x = (sobel_x[0][0] * img.at(x-1,y-1)) + (sobel_x[0][1] * img.at(x,y-1)) + (sobel_x[0][2] * img.at(x+1,y-1)) +
              (sobel_x[1][0] * img.at(x-1,y))   + (sobel_x[1][1] * img.at(x,y))   + (sobel_x[1][2] * img.at(x+1,y)) +
              (sobel_x[2][0] * img.at(x-1,y+1)) + (sobel_x[2][1] * img.at(x,y+1)) + (sobel_x[2][2] * img.at(x+1,y+1))
 
    pixel_y = (sobel_y[0][0] * img.at(x-1,y-1)) + (sobel_y[0][1] * img.at(x,y-1)) + (sobel_y[0][2] * img.at(x+1,y-1)) +
              (sobel_y[1][0] * img.at(x-1,y))   + (sobel_y[1][1] * img.at(x,y))   + (sobel_y[1][2] * img.at(x+1,y)) +
              (sobel_y[2][0] * img.at(x-1,y+1)) + (sobel_y[2][1] * img.at(x,y+1)) + (sobel_y[2][2] * img.at(x+1,y+1))
    
    val = Math.sqrt((pixel_x * pixel_x) + (pixel_y * pixel_y)).ceil
print  "#{pixel_x}>>#{pixel_y} "
    edge[x,y] = ChunkyPNG::Color.grayscale(val)
    #puts edge[x,y]
  end
  puts ""
end
 
edge.save('engine_edge.png')