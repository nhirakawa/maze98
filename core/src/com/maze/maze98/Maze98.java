package com.maze.maze98;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.maze.maze.Cell;
import com.maze.maze.Maze;
import com.maze.maze.Wall;
//import com.maze.viewer.MazeViewer;

import java.util.LinkedList;
import java.util.List;

public class Maze98 extends ApplicationAdapter {
	// camera
	private PerspectiveCamera camera;
	private CameraInputController cameraInputController;

	// models
	private Model wallModel;
	private Model floorModel;
	private ModelInstance floorInstance;

	private ModelBatch modelBatch;

	private Environment environment;

	// containers
	private List<ModelInstance> walls;
	private List<ModelInstance> borders;

	private Cell current;
	private Cell next;

	// constants
	private static final float CELL_SIZE = 20.0f;
	private static final float WALL_SIZE = 2.0f;
	private static final float WALL_HEIGHT = 20.0f;
	private static final float CAMERA_HEIGHT = 10.0f;

	private long lastTick;
	private LinkedList<Cell> path;
	private int pathIndex = 0;


	@Override
	public void create () {
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		modelBatch = new ModelBatch();

		//load textures

		// set up camera
		camera = new PerspectiveCamera(90, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cameraInputController = new CameraInputController(camera);
		cameraInputController.translateUnits = 100.0f;
		Gdx.input.setInputProcessor(cameraInputController);
		camera.near = 1f;
		camera.far = 400f;
		camera.update();

		// generate maze
		Maze maze = new Maze(20, 20);
		maze.generate();
		path = (LinkedList) maze.solve();
//		new MazeViewer(maze);

		// set up maze models
		walls = new LinkedList<>();
		borders = new LinkedList<>();

		buildMazeWalls(maze);

		current = path.get(pathIndex);
		pathIndex += 1;
		next = path.get(pathIndex);
		camera.position.set(getCameraVector(current));
		camera.lookAt(getCameraVector(next));
		lastTick = System.currentTimeMillis();
	}

	private void buildMazeWalls(Maze maze) {
		Texture brick = new Texture("brick.jpg");
		brick.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		Texture sand = new Texture("sand.png");
		sand.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		float mazeModelWidth = convertMazeCoordinatesTo3D(maze.getWidth()) + WALL_SIZE + 1.0f;
		float mazeModelHeight = convertMazeCoordinatesTo3D(maze.getHeight()) + WALL_SIZE + 1.0f;
		float mazeWidthOffset = (mazeModelWidth / 2) - (3 * CELL_SIZE / 4) + WALL_SIZE;
		float mazeHeightOffset = (mazeModelHeight / 2) - (3 * CELL_SIZE / 4) + WALL_SIZE;

		ModelBuilder modelBuilder = new ModelBuilder();

		Material brickMaterial = new Material(new TextureAttribute(TextureAttribute.Diffuse, brick));

		Model borderModel = modelBuilder.createBox(mazeModelWidth, WALL_HEIGHT, WALL_SIZE,
				brickMaterial, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

		// top border
		ModelInstance borderInstance = new ModelInstance(borderModel);
		borderInstance.transform.translate(mazeWidthOffset, WALL_HEIGHT / 2, (mazeHeightOffset * 2) + CELL_SIZE / 2 + WALL_SIZE);
		borders.add(borderInstance);

		// bottom border
		borderInstance = new ModelInstance(borderModel);
		borderInstance.transform.translate(mazeWidthOffset, WALL_HEIGHT / 2, (-CELL_SIZE / 2) - (3 * WALL_SIZE / 2));
		borders.add(borderInstance);

		borderModel = modelBuilder.createBox(mazeModelHeight, WALL_HEIGHT, WALL_SIZE, brickMaterial,
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		// right border
		borderInstance = new ModelInstance(borderModel);
		borderInstance.transform.translate(convertMazeCoordinatesTo3D(maze.getWidth()) - CELL_SIZE / 2,
				WALL_HEIGHT/2, mazeHeightOffset);
		borderInstance.transform.rotate(Vector3.Y, 90.0f);
		borders.add(borderInstance);

		// left border
		borderInstance = new ModelInstance(borderModel);
		borderInstance.transform.translate(-(WALL_SIZE + CELL_SIZE / 2), WALL_HEIGHT / 2, mazeHeightOffset);
		borderInstance.transform.rotate(Vector3.Y, 90.0f);
		borders.add(borderInstance);

		wallModel = modelBuilder.createBox(CELL_SIZE, WALL_HEIGHT, WALL_SIZE, brickMaterial,
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		Model wallConnectorModel = modelBuilder.createBox(WALL_SIZE, WALL_HEIGHT, WALL_SIZE, brickMaterial,
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

		for(Wall wall : maze.getWalls()){
			boolean rotation = false;
			Cell cell1 = wall.getCell1();
			Cell cell2 = wall.getCell2();
			float x = (float) cell1.getX();
			float z = (float) cell1.getY();
			float connectorX1;
			float connectorZ1;
			float connectorX2;
			float connectorZ2;
			float connectorOffset = ((CELL_SIZE + WALL_SIZE) / 2);
			if(cell1.getX() == cell2.getX()){
				if(cell1.getY() > cell2.getY()){
					// draw bottom wall
					x = convertMazeCoordinatesTo3D(x) + WALL_SIZE;
					z = convertMazeCoordinatesTo3D(z);
					connectorX1 = x + connectorOffset;
					connectorX2 = x - connectorOffset;
					connectorZ1 = z;
					connectorZ2 = z;
				} else {
					// draw top wall
					x = convertMazeCoordinatesTo3D(x);
					z = convertMazeCoordinatesTo3D(z) + (WALL_SIZE / 2) + (CELL_SIZE / 2);
					connectorX1 = x + connectorOffset;
					connectorX2 = x - connectorOffset;
					connectorZ1 = z;
					connectorZ2 = z;
				}
			} else {
				rotation = true;
				if(cell1.getX() < cell2.getX()){
					// draw right wall
					x = convertMazeCoordinatesTo3D(x) + (WALL_SIZE / 2) + (CELL_SIZE / 2);
					z = convertMazeCoordinatesTo3D(z);
					connectorX1 = x;
					connectorX2 = x;
					connectorZ1 = z + connectorOffset;
					connectorZ2 = z - connectorOffset;
				} else {
					// draw left wall
					x = convertMazeCoordinatesTo3D(x);
					z = convertMazeCoordinatesTo3D(z) - WALL_SIZE;
					connectorX1 = x;
					connectorX2 = x;
					connectorZ1 = z + connectorOffset;
					connectorZ2 = z - connectorOffset;
				}
			}
			ModelInstance wallInstance = new ModelInstance(wallModel);
			wallInstance.transform.translate(x, WALL_HEIGHT/2, z);
			if(rotation){
				wallInstance.transform.rotate(Vector3.Y, 90);
			}
			walls.add(wallInstance);

			ModelInstance wallConnectorInstance = new ModelInstance(wallConnectorModel);
			wallConnectorInstance.transform.translate(connectorX1, WALL_HEIGHT/2, connectorZ1);
			walls.add(wallConnectorInstance);
			wallConnectorInstance = new ModelInstance(wallConnectorModel);
			wallConnectorInstance.transform.translate(connectorX2, WALL_HEIGHT/2, connectorZ2);
			walls.add(wallConnectorInstance);
		}
		Material sandMaterial = new Material(new TextureAttribute(TextureAttribute.Diffuse, sand));
		floorModel = modelBuilder.createBox(mazeModelWidth, 2.0f, mazeModelHeight,
				sandMaterial, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		floorInstance = new ModelInstance(floorModel);
		floorInstance.transform.translate(mazeWidthOffset, 0.0f, mazeHeightOffset);
	}

	@Override
	public void render () {
		// camera updates
		if(pathIndex < path.size()-1){
			long now = System.currentTimeMillis();
			if(now - lastTick >= 2000){
				lastTick = now;
				camera.position.set(getCameraVector(next));
				current = next;
				pathIndex += 1;
				next = path.get(pathIndex);
				camera.lookAt(getCameraVector(next));
			} else {
				float delta = Gdx.graphics.getDeltaTime() / 2;
				camera.translate(getTranslateDeltaVector(current, next, delta));
			}
		}
		cameraInputController.update();
		camera.update();

		// OpenGL clean up
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		// render models
		modelBatch.begin(camera);
		modelBatch.render(floorInstance, environment);
		for(ModelInstance border: borders){
			modelBatch.render(border, environment);
		}
		for(ModelInstance wall : walls){
			modelBatch.render(wall, environment);
		}
		modelBatch.end();
	}

	@Override
	public void dispose(){
		modelBatch.dispose();
		wallModel.dispose();
		floorModel.dispose();
	}

	private static Vector3 getTranslateDeltaVector(Cell current, Cell next, float delta){
		float currentX = convertMazeCoordinatesTo3D(current.getX());
		float currentZ = convertMazeCoordinatesTo3D(current.getY());
		float nextX = convertMazeCoordinatesTo3D(next.getX());
		float nextZ = convertMazeCoordinatesTo3D(next.getY());
		float deltaX = (nextX - currentX) * delta;
		float deltaZ = (nextZ - currentZ) * delta;
		return getMazeVector(deltaX, deltaZ);
	}

	private static Vector3 getRotateDeltaVector(Cell current, Cell next, float delta){
		return Vector3.Zero;
	}

	private static Vector3 getCameraVector(int x, int y){
		return new Vector3(x, CAMERA_HEIGHT, y);
	}

	private static Vector3 getCameraVector(float x, float y){
		return new Vector3(x, CAMERA_HEIGHT, y);
	}

	private static Vector3 getCameraVector(Cell cell){
		float x = convertMazeCoordinatesTo3D(cell.getX()) + WALL_SIZE;
		float z = convertMazeCoordinatesTo3D(cell.getY());
		return getCameraVector(x, z);
	}

	private static Vector3 getMazeVector(int x, int y){
		return new Vector3(x, 0.0f, y);
	}

	private static Vector3 getMazeVector(float x, float y){
		return new Vector3(x, 0.0f, y);
	}

	private static Vector3 getMazeVector(Cell cell){
		float x = convertMazeCoordinatesTo3D(cell.getX()) + WALL_SIZE;
		float z = convertMazeCoordinatesTo3D(cell.getY());
		return getMazeVector(x, z);
	}

	private static float convertMazeCoordinatesTo3D(int coord){
		return coord * (CELL_SIZE + WALL_SIZE);
	}

	private static float convertMazeCoordinatesTo3D(float coord){
		return coord * (CELL_SIZE + WALL_SIZE);
	}
}
