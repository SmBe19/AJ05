package com.smeanox.games.aj05.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.MathUtils;

public class GameScreen implements Screen {

    private float time;

    private ModelLoader modelLoader;
    private SpriteBatch batch;
    private ModelBatch modelBatch;
    private Camera camera;
    private CameraInputController cameraInputController;

    private Model modelTest1;
    private ModelInstance test1;

    private Model modelSphere;
    private ModelInstance moon;
    private ModelInstance sun;

    public GameScreen() {
        batch = new SpriteBatch();

        String vertexShader = Gdx.files.internal("shd/grid.vertex.glsl").readString();
        String fragmentShader = Gdx.files.internal("shd/grid.fragment.glsl").readString();
        modelBatch = new ModelBatch(vertexShader, fragmentShader);

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0, 0, 17);
        camera.lookAt(0, 0, 0);
        camera.near = 1;
        camera.far = 300;
        camera.update();

        cameraInputController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(cameraInputController);

        modelLoader = new ObjLoader();

        modelTest1 = modelLoader.loadModel(Gdx.files.internal("obj/test1.obj"));
        test1 = new ModelInstance(modelTest1);

        modelSphere = modelLoader.loadModel(Gdx.files.internal("obj/sphere.obj"));
        moon = new ModelInstance(modelSphere);
        moon.transform.translate(10, 10, 0);
        sun = new ModelInstance(modelSphere);
    }

    @Override
    public void show() {

    }

    private void update(float delta) {
        time += delta;

        moon.transform.setToTranslation(40*MathUtils.sin(time), 40*MathUtils.cos(time), -150);
        moon.transform.rotate(0, 1, 0, -100*time);
        moon.transform.scale(10, 10, 10);

        sun.transform.setToTranslation(100*MathUtils.sin(time*0.2f), 100*MathUtils.cos(time*0.2f), -200);
        sun.transform.rotate(0, 1, 0, -200*time);
        sun.transform.scale(50, 50, 50);
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        modelBatch.begin(camera);
        modelBatch.render(test1);
        modelBatch.render(sun);
        modelBatch.render(moon);
        modelBatch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        modelTest1.dispose();
    }
}
