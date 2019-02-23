package com.smeanox.games.aj05.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {

    private float time;

    private ModelLoader modelLoader;
    private SpriteBatch spriteBatch;
    private ModelBatch modelBatch;
    private FrameBuffer frameBuffer;
    private TextureRegion frameBufferTexture;
    private ShaderProgram postShader;
    private Camera camera;
    private CameraInputController cameraInputController;

    private Model modelTest1;
    private ModelInstance test1;

    private Model modelSphere;
    private ModelInstance moon;
    private ModelInstance sun;

    private Model modelTree;
    private List<ModelInstance> trees;

    public GameScreen() {
        String vertexShader = Gdx.files.internal("shd/grid.vertex.glsl").readString();
        String fragmentShader = Gdx.files.internal("shd/grid.fragment.glsl").readString();
        modelBatch = new ModelBatch(vertexShader, fragmentShader);

        String postVertexShader = Gdx.files.internal("shd/post.vertex.glsl").readString();
        String postFragmentShader = Gdx.files.internal("shd/post.fragment.glsl").readString();
        postShader = new ShaderProgram(postVertexShader, postFragmentShader);
        if (!postShader.isCompiled()) {
            throw new GdxRuntimeException("Could not compile post shader: " + postShader.getLog());
        }
        if (postShader.getLog().length() > 0) {
            Gdx.app.log("Post Shader", postShader.getLog());
        }
        spriteBatch = new SpriteBatch(1, postShader);

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0, 0, 17);
        camera.lookAt(0, 0, 0);
        camera.near = 1;
        camera.far = 300;
        camera.update();

        frameBuffer = new FrameBuffer(Pixmap.Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        frameBufferTexture = new TextureRegion(frameBuffer.getColorBufferTexture());
        frameBufferTexture.flip(false, true);

        cameraInputController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(cameraInputController);

        modelLoader = new ObjLoader();

        modelTest1 = modelLoader.loadModel(Gdx.files.internal("obj/test1.obj"));
        test1 = new ModelInstance(modelTest1);

        modelSphere = modelLoader.loadModel(Gdx.files.internal("obj/sphere.obj"));
        moon = new ModelInstance(modelSphere);
        sun = new ModelInstance(modelSphere);

        modelTree = modelLoader.loadModel(Gdx.files.internal("obj/tree.obj"));
        trees = new ArrayList<ModelInstance>();
        for(int x = -4; x < 5; x++) {
            for (int z = -4; z < 5; z++) {
                ModelInstance tree = new ModelInstance(modelTree);
                tree.transform.translate(x*20, 0, z*20);
                trees.add(tree);
            }
        }
    }

    @Override
    public void show() {

    }

    private void update(float delta) {
        time += delta;

        moon.transform.setToTranslation(40*MathUtils.sin(time), 40*MathUtils.cos(time), -150);
        moon.transform.rotate(0, 1, 0, -100*time);
        moon.transform.scale(10, 10, 10);

        sun.transform.setToTranslation(-200, 100*MathUtils.cos(time*0.2f), 100*MathUtils.sin(time*0.2f));
        sun.transform.rotate(0, 1, 0, -200*time);
        sun.transform.scale(50, 50, 50);
    }

    @Override
    public void render(float delta) {
        update(delta);

        frameBuffer.begin();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        modelBatch.begin(camera);
        modelBatch.render(test1);
        for (ModelInstance tree : trees) {
            modelBatch.render(tree);
        }
        modelBatch.render(sun);
        modelBatch.render(moon);
        modelBatch.end();

        frameBuffer.end();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.begin();
        postShader.setUniformf("u_time", time);
        postShader.setUniformf("u_wobble", 1);
        spriteBatch.draw(frameBufferTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.end();
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
        modelSphere.dispose();
        modelTree.dispose();
        postShader.dispose();
    }
}
