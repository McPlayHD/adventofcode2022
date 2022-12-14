// import the library
import com.hamoid.*;

// create a new VideoExport-object
VideoExport videoExport;

PShader shader;
PGraphics graphics;
PImage texture;
PFont consolas;

int PART = 1;

int SCALE = 4;
int XOFFSET = 300;
int XMAX = 700;
int WIDTH = 800;
int HEIGHT = 200;
int FRAMERATE = 60;

int movingSandColour = color(246, 215, 176);
int stationarySandColour = color(163, 59, 33);

void settings() {
  size(SCALE * (XMAX - XOFFSET), SCALE * HEIGHT, P2D);
}

void setup() {
  noStroke();
  graphics = createGraphics(WIDTH, HEIGHT, P2D);
  texture = loadImage("cave" + PART + ".png");
  shader = loadShader("cave.frag");
  consolas = createFont("consola.ttf", 128);
  background(0);
  frameRate(FRAMERATE);
  videoExport = new VideoExport(this, "Day14 Part" + PART + ".mp4");
  videoExport.setFrameRate(FRAMERATE);  
  videoExport.startMovie();
  textFont(consolas);
  textAlign(LEFT);
}

void spawnSand() {
  if (texture.get(500, 0) == color(0)) {
    texture.set(500, 0, movingSandColour);
  }
}

int countStationarySands() {
  int stationarySands = 0;
  texture.loadPixels();
  for (int colour : texture.pixels) {
    if (colour == stationarySandColour) {
      stationarySands ++;
    }
  }
  return stationarySands;
}

int spawnedSand = 0;

void draw() {
  if (spawnedSand-- == 0) {
     spawnSand();
     spawnedSand = 1;
  }
  
  shader.set("u_resolution", float(WIDTH), float(HEIGHT));
  shader.set("u_mouse", float(mouseX), float(mouseY));
  shader.set("u_time", millis() / 1000.0);
  shader.set("u_texture", texture);
  
  graphics.beginDraw();
  graphics.filter(shader);
  graphics.loadPixels();
  loadPixels();
  for (int y = 0; y < height; y ++) {
    int Y = y / SCALE;
    if (Y >= graphics.height)
      break;
    for (int x = 0; x < width; x ++) {
      int X = x / SCALE;
      if (X >= graphics.width)
        continue;
      pixels[y * width + x] = graphics.pixels[Y * graphics.width + X + XOFFSET];
    }
  }
  updatePixels();
  texture.loadPixels();
  System.arraycopy(graphics.pixels, 0, texture.pixels, 0, graphics.pixels.length);
  texture.updatePixels();
  graphics.endDraw();
  
  textSize(100);
  fill(stationarySandColour);
  text(countStationarySands() + "", 20, 100);
  textSize(30);
  fill(255);
  text("Shader file:\nhttps://github.com/McPlayHD/adventofcode2022/blob/master/processing/task14/cave.frag", 20, height - 60);
  
  videoExport.saveFrame();
}

void keyPressed() {
  if (key == 'q') {
    videoExport.endMovie();
    exit();
  }
}