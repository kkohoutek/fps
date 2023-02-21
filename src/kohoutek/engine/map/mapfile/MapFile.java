package kohoutek.engine.map.mapfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.util.vector.Vector3f;
import kohoutek.engine.Player;
import kohoutek.engine.map.Map;
import kohoutek.engine.thing.actor.Actor;
import kohoutek.engine.thing.env.Wall;
import kohoutek.utils.TextureHandler;

/**
 * MapFile represents game map information contained in a strictly formatted
 * mfsrc file.<br>
 * MapFile object can eventually be converted into a Map object that can be used
 * in-engine.
 * 
 * @author Kamil Kohoutek
 * @version 1.0
 */
public class MapFile {
	/** First two lines */
	public final List<String>	otherData;
	/** Lines under ENVIRONMENT tag. Required. */
	public final List<String>	envData;
	/** Lines under ACTORS tag. Not required. */
	public final List<String>	actorData;

	private PrintWriter logger;

	/**
	 * MapFile from source file.
	 * @param path path to file
	 */
	public MapFile(String path) {
		final File FILE = new File(path);
		final List<String> DATA = readData(FILE);

		otherData = DATA.subList(0, 2);
		envData = find("ENVIRONMENT", DATA);
		actorData = find("ACTORS", DATA);

		logger = null;
		try {
			logger = new PrintWriter(new FileWriter(".\\DATA\\logger_output.log", true));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			validateSource();
		} catch (MapFileSourceFormatMismatchException | FileNotFoundException e) {
			System.err.println(FILE.getName() + " is invalid!");
			System.err.println("ERROR: " + e.getMessage());
			record(e.getMessage());
			System.exit(-1);
		}
	}

	/**
	 * MapFile from raw data.
	 */
	public MapFile(final List<String> otherData, final List<String> envData, final List<String> actorData) {
		this.otherData = otherData;
		this.envData = envData;
		this.actorData = actorData;

		try {
			validateSource();
		} catch (FileNotFoundException | MapFileSourceFormatMismatchException e) {
			System.err.println("Data is invalid!");
			System.err.println("ERROR: " + e.getMessage());
			record("ERROR: " + e.getMessage());
			System.exit(-1);
		}
	}

	/**
	 * Export the information into a .mfsrc file.
	 * 
	 * @param outputPath
	 * @throws IOException
	 */
	public void export(String outputPath) throws IOException {
		File outputFile = new File(outputPath);

		outputFile.delete();

		PrintWriter writer = new PrintWriter(new FileWriter(outputFile, false));

		final ArrayList<String> data = new ArrayList<>();
		data.addAll(otherData);
		data.add(new String());
		data.add("ENVIRONMENT");
		data.addAll(envData);
		if (actorData != null) {
			data.add(new String());
			data.add("ACTORS");
			data.addAll(actorData);
		}
		for (String line : data) {
			writer.println(line);
			writer.flush();
		}
		writer.close();

	}

	public void export(File output) throws IOException {
		export(output.getPath());
	}

	/**
	 * Turn this into a Map object.
	 */
	public Map compile() {
		long startTime = System.currentTimeMillis(); // pro mìøící úèely

		float height = Float.parseFloat(otherData.get(0).split(" ")[1]);

		String[] line = otherData.get(1).split(" ");

		float playerX = Float.parseFloat(line[1]);
		float playerY = Float.parseFloat(line[2]);
		float playerZ = Float.parseFloat(line[3]);

		Wall[] walls = new Wall[envData.size() - 2];
		int[] textures = new int[envData.size() - 2];
		Vector3f[] textureScaleVecs = new Vector3f[envData.size() - 2];

		int i;
		for (i = 0; i < envData.size() - 2; i++) {
			line = envData.get(i).split(" ");

			boolean isDoor = false;
			if (line[0].equalsIgnoreCase("d")) {
				isDoor = true;
			}

			boolean noCullBackFace = false;
			if (line.length == 10) {
				if (line[9].equalsIgnoreCase("nocull")) {
					noCullBackFace = true;
				}
			}

			float x = Float.parseFloat(line[1]);
			float z = Float.parseFloat(line[2]);
			float width = Float.parseFloat(line[3]);
			float rotation = Float.parseFloat(line[4]);
			String texturePath = line[5];
			Vector3f textureScaleVec = new Vector3f(Float.parseFloat(line[6]), Float.parseFloat(line[7]),
					Float.parseFloat(line[8]));

			walls[i] = new Wall(x, 0, z, width, height, rotation, noCullBackFace, isDoor);
			textures[i] = TextureHandler.loadTexture(texturePath);
			textureScaleVecs[i] = textureScaleVec;
		}

		line = envData.get(i).split(" ");
		int ceilingTexture = TextureHandler.loadTexture(line[1]);

		line = envData.get(i + 1).split(" ");
		int floorTexture = TextureHandler.loadTexture(line[1]);

		Map map = new Map(walls, textures, textureScaleVecs, ceilingTexture, floorTexture,
				new Player(100, 100, playerX, playerY, playerZ));

		if (actorData != null) {

			for (i = 0; i < actorData.size(); i++) {

				line = actorData.get(i).split(" ");

				String actorName = line[0];

				try {
					map.spawn(actorInstanceByName(actorName, Float.parseFloat(line[1]), Float.parseFloat(line[2]),
							height));
				} catch (Exception e) {
					e.printStackTrace();
				} // will NEVER happen doe
			}
		}

		long endTime = System.currentTimeMillis() - startTime;
		System.out.println("Compiled in " + endTime + " ms.");
		return map;
	}

	/**
	 * Check if the source file is valid.
	 * 
	 * @throws MapFileSourceFormatMismatchException
	 *             file is not a mapfile
	 * @throws FileNotFoundException
	 *             some texture was not found
	 */
	private void validateSource() throws MapFileSourceFormatMismatchException, FileNotFoundException {
		long startTime = System.currentTimeMillis();

		String[] line = otherData.get(0).split(" ");

		if (!line[0].equalsIgnoreCase("height")) {
			throw new MapFileSourceFormatMismatchException("Height not found");
		}

		if (line.length < 2) {
			throw new MapFileSourceFormatMismatchException("Not enough information; first line");
		}
		if (line.length > 2) {
			throw new MapFileSourceFormatMismatchException("Too much enough information; first line");
		}

		try {
			new Float(line[1]);
		} catch (NumberFormatException e) {
			throw new MapFileSourceFormatMismatchException("Expected a number; line 1");
		}

		line = otherData.get(1).split(" ");

		if (!line[0].equalsIgnoreCase("player")) {
			throw new MapFileSourceFormatMismatchException("Player data not found");
		}

		if (line.length < 4) {
			throw new MapFileSourceFormatMismatchException("Not enough information; second line");
		}
		if (line.length > 4) {
			throw new MapFileSourceFormatMismatchException("Too much enough information; second line");
		}

		try {
			for (int i = 1; i <= 3; i++) {
				new Float(line[i]);
			}
		} catch (NumberFormatException e) {
			throw new MapFileSourceFormatMismatchException("Expected a number; line 2");
		}

		if (envData == null)
			throw new MapFileSourceFormatMismatchException("Environment data not found");
		if (envData.size() <= 2)
			throw new MapFileSourceFormatMismatchException("Not enough ENVIRONMENT information");

		int i;
		for (i = 0; i < envData.size() - 2; i++) {
			line = envData.get(i).split(" ");
			if (line.length < 9) {
				throw new MapFileSourceFormatMismatchException("Not enough information; line" + (i + 1));
			} else if (line.length > 10) {
				throw new MapFileSourceFormatMismatchException("Too much information; line " + (i + 1));
			}

			if (!line[0].equalsIgnoreCase("w") && !line[0].equalsIgnoreCase("d")) {
				throw new MapFileSourceFormatMismatchException("Expected w or d; line " + (i + 1));
			}

			if (!new File(line[5] + ".png").exists())
				throw new FileNotFoundException("Texture file " + line[5] + " not found; line " + (i + 1));

			for (int j = 1; j <= 4; j++) {
				try {
					new Float(line[j]);
				} catch (NumberFormatException e) {
					throw new MapFileSourceFormatMismatchException("Expected a number; line " + (i + 1));
				}
			}
			for (int j = 6; j <= 8; j++) {
				try {
					new Float(line[j]);
				} catch (NumberFormatException e) {
					throw new MapFileSourceFormatMismatchException("Expected a number; line " + (i + 1));
				}
			}
		}

		line = envData.get(i).split(" ");

		if (!line[0].equalsIgnoreCase("c"))
			throw new MapFileSourceFormatMismatchException("Ceiling data not found; line " + (i + 1));
		if (!new File(line[1] + ".png").exists())
			throw new FileNotFoundException("Ceiling texture " + line[1] + " not found");

		line = envData.get(i + 1).split(" ");

		if (!line[0].equalsIgnoreCase("f"))
			throw new MapFileSourceFormatMismatchException("Floor data not found; line " + (i + 1));
		if (!new File(line[1] + ".png").exists())
			throw new FileNotFoundException("Floor texture " + line[1] + " not found");

		if (actorData != null) {
			for (i = 0; i < actorData.size(); i++) {
				line = actorData.get(i).split(" ");
				if (line.length < 3) {
					throw new MapFileSourceFormatMismatchException("Not enough information; line" + (i + 1));
				}
				if (line.length > 3) {
					throw new MapFileSourceFormatMismatchException("Too much information; line" + (i + 1));
				}

				try {
					new Float(line[1]);
					new Float(line[2]);
				} catch (NumberFormatException e) {
					throw new MapFileSourceFormatMismatchException("Expected a number; line " + (i + 1));
				}

				try {
					Class.forName("kohoutek.engine.thing.actor." + line[0]);

				} catch (ClassNotFoundException e) {
					throw new MapFileSourceFormatMismatchException(line[0] + " does not exist; line " + (i + 1));
				}

			}
		}

		long endTime = System.currentTimeMillis() - startTime;

		System.out.println("Validated in " + endTime + " ms.");
	}

	/**
	 * Get data under the tag.
	 * 
	 * @return sublist from data
	 */
	private List<String> find(String tag, List<String> data) {
		int dataStartIndex = data.indexOf(tag) + 1;

		if (dataStartIndex == 0 || dataStartIndex == data.size() || data.get(dataStartIndex).isEmpty())
			return null;

		int toIndex;
		for (toIndex = dataStartIndex; toIndex < data.size() && !data.get(toIndex).isEmpty(); ++toIndex);
		return data.subList(dataStartIndex, toIndex);
	}

	private List<String> readData(File FILE) {
		try {
			return Files.readAllLines(Paths.get(FILE.getAbsolutePath()));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);

		}
		return null;
	}

	private Actor actorInstanceByName(String actorName, float x, float z, float height)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, ClassNotFoundException {

		Class<?> actorClass = Class.forName("kohoutek.engine.thing.actor." + actorName);
		Actor actor = ((Actor) (actorClass.getConstructor(Float.TYPE, Float.TYPE, Float.TYPE).newInstance(x, height,z)));

		return actor;
	}

	private void record(String text) {
		if (logger == null)
			return;

		LocalDateTime now = LocalDateTime.now();

		logger.println(now.format(DateTimeFormatter.ofPattern("dd MM yyyy HH:mm")) + " - " + text);
		logger.flush();

	}

}
