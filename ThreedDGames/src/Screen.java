import java.util.Random;

public class Screen extends BitMap {

	public Random r = new Random();
	
	public BitMap test;
	public BitMap3D perspectiveVision;
	
	public Screen(int width, int height){
		super(width, height);	
		test = new BitMap(50, 50);
		for(int i = 0; i < test.pixels.length; i++)
		  test.pixels[i] = r.nextInt();
		  perspectiveVision = new BitMap3D(width, height);
	}
	
	public void render(Game game) {		
		clear();
		perspectiveVision.render(game);
		perspectiveVision.renderFog();
		render(perspectiveVision,0,0);
		
	//  render(test,(width - 50) / 2 + ox ,(height - 50) / 2 + oy );
	}
	
	public void update(){		 
	}
	
}
