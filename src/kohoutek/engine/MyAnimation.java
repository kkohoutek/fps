package kohoutek.engine;

import java.util.ArrayList;

import org.newdawn.slick.Image;

public class MyAnimation {
	private ArrayList<Frame> frames;
	private int frameIndex;
	private int movieTime;
	private long totalTime;
	
	public MyAnimation(){
		frames = new ArrayList<>();
		totalTime = 0;
		start();
	}
	
	public synchronized void addFrame(Image i, long time){
		totalTime += time;
		frames.add(new Frame(i, totalTime));
	}

	public synchronized void start() {
		movieTime = 0;
		frameIndex = 0;
		
	}
	
	public synchronized void update(long timePassed){
		if(frames.size()>1){
			movieTime += timePassed;
			if(movieTime >= totalTime){
				movieTime = 0;
				frameIndex = 0;
			}
			while(movieTime > getFrame(frameIndex).endTime){
				frameIndex++;
			}
		}
	}
	
	public synchronized Image getCurrentImage(){
		if(frames.size() == 0){
			return null;
		} else {
			return getFrame(frameIndex).pic;
		}
	}
	
	private Frame getFrame(int x){
		return frames.get(x);
	}
	
	public synchronized int getCurrentFrame(){
		return frameIndex;
	}
	
	private class Frame {
		Image pic;
		long endTime;
		
		public Frame(Image pic, long endTime){
			this.pic = pic;
			this.endTime = endTime;
		}
	}
}
