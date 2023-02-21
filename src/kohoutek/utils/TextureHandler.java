package kohoutek.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.PNGDecoder;
import org.newdawn.slick.opengl.TextureLoader;

public class TextureHandler {
	
	private TextureHandler(){}


	public static int loadTexture(String fileName) {
		ByteBuffer buf = null;
		int tWidth = 0;
		int tHeight = 0;

		try {
			// Open the PNG file as an InputStream
			InputStream in = new FileInputStream(fileName + ".PNG");
			// Link the PNG decoder to this stream
			PNGDecoder decoder = new PNGDecoder(in);
			

			// Get the width and height of the texture
			tWidth = decoder.getWidth();
			tHeight = decoder.getHeight();

			// Decode the PNG file in a ByteBuffer
			buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
			decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.RGBA);
			buf.flip();

			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		// 	Vytvoø novou texturu v pamìti a nahraj ji
		int textureId = GL11.glGenTextures();
		//GL13.glActiveTexture(textureId);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

		// Všechny RGB bajty jsou zarovnané k sobì a každý komponent je 1 B
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

		// Nahraj texturové data a generuj mipmapy (pro škálování)
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, tWidth, tHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,buf);
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

		// ST koordinace
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

		// Filtry
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D,EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT,8);

		return textureId;
	}
	
	
	public static int loadTexture(String name, String format) throws IOException {
		return TextureLoader.getTexture(format, new FileInputStream(name)).getTextureID();
	}
	
	public static Vector2f[] calculateUVMapping(int textureIndex, int atlasCols, int atlasRows){

		    int u = textureIndex % atlasCols;
		    int v = textureIndex / atlasRows;

		    float xOffset = 1f / atlasCols;
		    float yOffset = 1f / atlasRows;

		    float uOffset = (u * xOffset);
		    float vOffset = (v * yOffset);

		    Vector2f[] UVList = new Vector2f[4];

		    UVList[0] = new Vector2f(uOffset, vOffset); // 0,0
		    UVList[1] = new Vector2f(uOffset, vOffset + yOffset); // 0,1
		    UVList[2] = new Vector2f(uOffset + xOffset, vOffset + yOffset); // 1,1
		    UVList[3] = new Vector2f(uOffset + xOffset, vOffset); // 1,0


		    return UVList;
		}
		
	
}