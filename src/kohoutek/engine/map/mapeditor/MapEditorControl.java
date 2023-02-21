package kohoutek.engine.map.mapeditor;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import org.lwjgl.util.vector.Vector3f;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import kohoutek.engine.map.mapfile.MapFile;
import kohoutek.utils.geom.Lines;

public class MapEditorControl implements Initializable {
	private static final int[] ANGLES = new int[] { 0, 90, 180, 270, 360 };

	@FXML
	private AnchorPane			container;
	@FXML
	private TextField			texturePath;
	@FXML
	private Button				browseTextureButton;
	@FXML
	private GridPane			textureContainer;
	@FXML
	private Label				mousePos;
	@FXML
	private CheckBox			isDoorCheckBox;
	@FXML
	private CheckBox			trimCheckBox;
	@FXML
	private CheckBox			connectCheckBox;
	@FXML
	private CheckBox			nocullCheckBox;
	@FXML
	private Spinner<Integer>	heightSpinner;
	@FXML
	private Button				setAsCeiling;
	@FXML
	private Button				setAsFloor;
	@FXML
	private ComboBox<String>	actorsComboBox;

	private final Circle			player				= new Circle(16, 16, 4);
	private final ArrayList<Circle>	actors				= new ArrayList<>();
	private final ArrayList<Line>	map2D				= new ArrayList<>();
	private final ArrayList<String>	loadedTextures		= new ArrayList<>();
	private final ArrayList<String>	envData				= new ArrayList<>();
	private final ArrayList<String>	otherData			= new ArrayList<>();
	private final ArrayList<String>	actorData			= new ArrayList<>();
	private final FileChooser		textureFileChooser	= new FileChooser();
	private final FileChooser		exportFileChooser	= new FileChooser();
	private final ColorAdjust		effect				= new ColorAdjust();

	private String	selectedTexture;
	private String	floorTexture;
	private String	ceilingTexture;
	private String	selectedActor;
	private byte	col				= 0;
	private byte	row				= 0;
	private boolean	lineStarted		= false;
	private boolean	placingPlayer	= false;
	private File	output;

	@FXML
	void placingActor(ActionEvent event) {
		selectedActor = actorsComboBox.getSelectionModel().getSelectedItem();
		actors.add(0, new Circle(150, 150, 4, Color.YELLOW));
		container.getChildren().add(actors.get(0));

		if (lineStarted) {
			lineStarted = false;
			container.getChildren().remove(map2D.get(0));
			map2D.remove(0);
		}

	}

	@FXML
	void placingPlayer(ActionEvent event) {
		placingPlayer = true;

		if (lineStarted) {
			lineStarted = false;
			container.getChildren().remove(map2D.get(0));
			map2D.remove(0);
		}
	}

	@FXML
	void save(ActionEvent event) {
		if (output == null) {
			output = exportFileChooser.showSaveDialog(MapEditor.stage);
			if (output == null)
				return;

			// CREATING
			otherData.add("height " + heightSpinner.getValueFactory().getValue().intValue());
			otherData.add("player " + (float) player.getCenterX() + " 8 " + (float) player.getCenterY());

		} else {
			// OVERWRITING
			otherData.set(0, "height " + heightSpinner.getValueFactory().getValue().intValue());
			otherData.set(1, "player " + (float) player.getCenterX() + " 8 " + (float) player.getCenterY());
		}

		envData.add("c " + ceilingTexture);
		envData.add("f " + floorTexture);

		MapFile mf = new MapFile(otherData, envData, actorData);
		try {
			mf.export(output);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// remove ceiling and floor, expecting more wall data
		envData.remove(envData.size() - 2);
		envData.remove(envData.size() - 1);

	}

	void open(ActionEvent event) {

	}

	@FXML
	void textureContainerPressed(MouseEvent event) {
		for (Node node : textureContainer.getChildren()) {
			if (node instanceof ImageView) {
				if (node.getBoundsInParent().contains(event.getX(), event.getY())) {
					selectedTexture = loadedTextures.get((GridPane.getRowIndex(node) + 1) * (GridPane.getColumnIndex(node) + 1) - 1);
					// selectedTexture =
					// ((ImageView)node).getImage().impl_getUrl().substring(6);
					node.setEffect(null);
					setAsFloor.setDisable(false);
					setAsCeiling.setDisable(false);

					for (Node node2 : textureContainer.getChildren()) {
						if (node2 instanceof ImageView && node2 != node) {
							node2.setEffect(effect);
						}
					}
				}
			}
		}
	}

	@FXML
	void showTextureFileDialog(ActionEvent event) {
		List<File> textureFiles = textureFileChooser.showOpenMultipleDialog(MapEditor.stage);
		if (textureFiles == null)
			return;

		for (File file : textureFiles) {
			ImageView iv = new ImageView();

			try {
				iv.setImage(new Image(file.toURI().toURL().toString()));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			iv.setFitWidth(64);
			iv.setFitHeight(64);
			iv.setVisible(true);
			iv.setEffect(effect);

			textureContainer.add(iv, col, row);
			loadedTextures.add(file.getPath().substring(0, file.getPath().length() - 4));

			if ((col + 1) % 6 == 0) {
				col = 0;
				row++;
			} else {
				col++;
			}
		}
	}

	@FXML
	void onMousePressed(MouseEvent event) {

		Line line;
		if (!lineStarted) {
			if (placingPlayer) {
				placingPlayer = false;
				return;
			} else if (selectedActor != null) {
				actorData.add(selectedActor + " " + (float) actors.get(0).getCenterX() + " "
						+ (float) actors.get(0).getCenterY());
				System.out.println(actorData);
				selectedActor = null;
				return;
			}

			if (selectedTexture == null)
				return;

			map2D.add(0, new Line());
			container.getChildren().add(map2D.get(0));
			line = map2D.get(0);
			line.setStrokeWidth(2f);
			line.setStyle("-fx-stroke: red");

			long x = Math.round(event.getX());
			long y = Math.round(event.getY());

			if (connectCheckBox.isSelected() && map2D.size() > 1) {
				x = (long) map2D.get(1).getEndX();
				y = (long) map2D.get(1).getEndY();
			}

			line.setStartX(x);
			line.setStartY(y);
			line.setEndX(x);
			line.setEndY(y);
		} else {
			line = map2D.get(0);
			if (line.intersects(player.getBoundsInLocal()))
				return;
			if (trimCheckBox.isSelected()) {
				for (Line l : map2D) {
					if (l.intersects(line.getBoundsInLocal())) {
						double[] ip = Lines.intersection(l.getStartX(), l.getStartY(), l.getEndX(), l.getEndY(),
								line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
						if (ip != null && ip[0] != line.getStartX() && ip[1] != line.getStartY()) {
							line.setEndX(ip[0]);
							line.setEndY(ip[1]);
							break;
						}
					}

				}
			}
			int angle = fixLineAngle(line);

			double length = Lines.length(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
			line.setEndX(Math.round(line.getStartX() + Math.cos(Math.toRadians(angle)) * length));
			line.setEndY(Math.round(line.getStartY() + Math.sin(Math.toRadians(angle)) * length));

			char type;
			String nocull = new String();

			if (isDoorCheckBox.isSelected()) {
				type = 'd';
			} else {
				type = 'w';
			}
			if (nocullCheckBox.isSelected()) {
				nocull = " nocull";
			}
			envData.add(type + " " + (float) line.getStartX() + " " + (float) line.getStartY() + " " + (float) length
					+ " " + (-angle) + " " + selectedTexture + " " + "4 2 1" + nocull);
		}

		lineStarted = !lineStarted;
	}

	@FXML
	void onMouseMoved(MouseEvent event) {
		long x = Math.round(event.getX());
		long y = Math.round(event.getY());
		mousePos.setText("X: " + x + "  " + "Y: " + y);
		if (lineStarted) {
			Line line = map2D.get(0);
			line.setEndX(x);
			line.setEndY(y);
		} else if (placingPlayer) {
			player.setCenterX(x);
			player.setCenterY(y);
		} else if (selectedActor != null) {
			actors.get(0).setCenterX(x);
			actors.get(0).setCenterY(y);
		}
	}

	@FXML
	void setCeilingTexture(ActionEvent event) {
		ceilingTexture = selectedTexture;

	}

	@FXML
	void setFloorTexture(ActionEvent event) {
		floorTexture = selectedTexture;
	}

	private int fixLineAngle(Line line) {
		int currentAngle = (int) Lines.angle(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
		int newAngle = 0;
		int distance = Math.abs(ANGLES[0] - currentAngle);
		for (int i = 1; i < ANGLES.length; i++) {
			int idistance = Math.abs(ANGLES[i] - currentAngle);
			if (idistance < distance) {
				distance = idistance;
				newAngle = ANGLES[i];
			}
		}

		return newAngle;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		connectCheckBox.setSelected(true);

		player.setFill(Color.GREEN);
		container.getChildren().add(player);

		final SpinnerValueFactory<Integer> heights = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 256);

		heights.setValue(16);
		heightSpinner.setValueFactory(heights);

		ObservableList<String> actors = FXCollections.observableArrayList("Ettin");
		actorsComboBox.setItems(actors);
		actorsComboBox.getSelectionModel().select(0);

		textureFileChooser.setTitle("Browse for textures");
		textureFileChooser.setInitialDirectory(new File(".."));
		textureFileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG", "*.png"));

		exportFileChooser.setTitle("Save as...");
		exportFileChooser.setInitialDirectory(new File("."));
		exportFileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("MapFile Source", ".mfsrc"));
		effect.setBrightness(0.65);

	}

}