package com.smeanox.games.aj05.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.smeanox.games.aj05.Consts;
import com.smeanox.games.aj05.world.Animal;
import com.smeanox.games.aj05.world.Bullet;
import com.smeanox.games.aj05.world.Tree;

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

    private Vector3 vec3;

    private Model modelTest1;
    private ModelInstance test1;
    private int test1VerticesSize;
    private List<float[]> test1Vertices;

    private Model modelSphere;
    private ModelInstance moon;
    private ModelInstance sun;

    private Model modelTree;
    private List<Tree> trees;

    private List<Model> modelAnimals;
    private List<Animal> animals;

    private List<Bullet> bullets;

    private float speed;
    private float downspeed;
    private boolean inAir;

    private float spellCharge;
    private boolean didBoom;

    // TODO menu item to adjust mouse sensitivity

    public GameScreen() {
        vec3 = new Vector3();

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
        camera.far = 700;
        camera.update();

        frameBuffer = new FrameBuffer(Pixmap.Format.RGB565, Gdx.graphics.getWidth() * Consts.ANTIALIASING, Gdx.graphics.getHeight() * Consts.ANTIALIASING, true);
        frameBufferTexture = new TextureRegion(frameBuffer.getColorBufferTexture());
        frameBufferTexture.flip(false, true);

        modelLoader = new ObjLoader();

        modelTest1 = modelLoader.loadModel(Gdx.files.internal("obj/test1.obj"));
        modelTest1.materials.get(0).set(ColorAttribute.createDiffuse(0, 0, 0.5f, 1));
        test1 = new ModelInstance(modelTest1);
        test1.calculateTransforms();

        test1Vertices = new ArrayList<float[]>();
        for (Node node : test1.nodes) {
            for (NodePart part : node.parts) {
                Mesh mesh = part.meshPart.mesh;
                test1VerticesSize = mesh.getVertexSize()/4;
                float[] vertices = new float[mesh.getNumVertices()*test1VerticesSize];
                mesh.getVertices(vertices);
                test1Vertices.add(vertices);
            }
        }

        modelSphere = modelLoader.loadModel(Gdx.files.internal("obj/sphere.obj"));
        modelSphere.materials.get(0).set(ColorAttribute.createDiffuse(0, 0, 0, 1));
        moon = new ModelInstance(modelSphere);
        sun = new ModelInstance(modelSphere);

        modelTree = modelLoader.loadModel(Gdx.files.internal("obj/tree.obj"));
        modelTree.materials.get(0).set(ColorAttribute.createDiffuse(0, 0, 0, 1));
        trees = new ArrayList<Tree>();
        for (int i = 0; i < 10; i++) {
            addTree();
        }

        modelAnimals = new ArrayList<Model>();
        for (int i = 0; i < 3; i++) {
            Model model = modelLoader.loadModel(Gdx.files.internal("obj/animal" + (i + 1) + ".obj"));
            model.materials.get(0).set(ColorAttribute.createDiffuse(0, 0, 0, 1));
            modelAnimals.add(model);
        }

        animals = new ArrayList<Animal>();
        for (int i = 0; i < 100; i++) {
            addAnimal();
        }

        bullets = new ArrayList<Bullet>();

        Gdx.input.setCursorCatched(true);

        reset();
    }

    public void reset() {
        speed = 0;
        downspeed = 100;
        inAir = false;
        spellCharge = 0;
        didBoom = false;
        camera.position.setZero();
        camera.update();
    }

    public void addTree(float x, float z) {
        Tree tree = new Tree(modelTree, x, getFloorHeight(x, z)+5, z);
        trees.add(tree);
    }

    public void addTree() {
        addTree(MathUtils.random(-Consts.FIELD_WIDTH, Consts.FIELD_WIDTH), MathUtils.random(-Consts.FIELD_HEIGHT, Consts.FIELD_HEIGHT));
    }

    public void addAnimal() {
        int random = MathUtils.random(modelAnimals.size() - 1);
        animals.add(new Animal(this, modelAnimals.get(random), random == 2 ? 7 : 1));
    }

    public void boom() {
        for (int i = 0; i < spellCharge * spellCharge * 17; i++) {
            bullets.add(new Bullet(this, modelSphere, camera.position, camera.direction, spellCharge * spellCharge * 0.7f));
        }
    }

    public float getFloorHeight(float x, float z) {
        float res = -1000;
        for (float[] vertices : test1Vertices) {
            for (int i = 0; i < vertices.length; i += test1VerticesSize*3) {
                float x1 = vertices[i];
                float y1 = vertices[i+1];
                float z1 = vertices[i+2];
                float x2 = vertices[i+test1VerticesSize];
                float y2 = vertices[i+test1VerticesSize+1];
                float z2 = vertices[i+test1VerticesSize+2];
                float x3 = vertices[i+test1VerticesSize*2];
                float y3 = vertices[i+test1VerticesSize*2+1];
                float z3 = vertices[i+test1VerticesSize*2+2];
                float l1 = ((z2 - z3) * (x - x3) + (x3 - x2) * (z - z3)) / ((z2 - z3) * (x1 - x3) + (x3 - x2) * (z1 - z3));
                float l2 = ((z3 - z1) * (x - x3) + (x1 - x3) * (z - z3)) / ((z2 - z3) * (x1 - x3) + (x3 - x2) * (z1 - z3));
                float l3 = 1 - l1 - l2;
                if (0 <= l1 && l1 <= 1 && 0 <= l2 && l2 <= 1 && 0 <= l3 && l3 <= 1) {
                    res = Math.max(res, l1*y1 + l2*y2 + l3*y3);
                }
            }
        }
        return res;
    }

    @Override
    public void show() {

    }

    private void update(float delta) {
        time += delta;

        float flyDelta = delta * (1 - MathUtils.clamp(spellCharge, 0, 0.99f));

        moon.transform.setToTranslation(90*MathUtils.sin(time), 90*MathUtils.cos(time), -Consts.FIELD_WIDTH * 1.5f);
        moon.transform.rotate(0, 1, 0, -100*time);
        moon.transform.scale(10, 10, 10);

        sun.transform.setToTranslation(-Consts.FIELD_WIDTH * 2f, 170*MathUtils.cos(time*0.2f), 170*MathUtils.sin(time*0.2f));
        sun.transform.rotate(0, 1, 0, -200*time);
        sun.transform.scale(50, 50, 50);

        for (Tree tree : trees) {
            tree.update(flyDelta);
        }
        for (Animal animal : animals) {
            animal.update(flyDelta);
        }

        for (Bullet bullet : bullets) {
            bullet.update(flyDelta);
        }

        for (int i = trees.size()-1; i >= 0; i--) {
            if (trees.get(i).getDeath() > 3) {
                trees.remove(i);
            }
        }
        for (int i = animals.size()-1; i >= 0; i--) {
            if (animals.get(i).getDeath() > 2) {
                animals.get(i).modelInstance.transform.getTranslation(vec3);
                addTree(vec3.x, vec3.z);
                animals.remove(i);
            }
        }
        for (int i = bullets.size()-1; i >= 0; i--) {
            if (bullets.get(i).dead) {
                bullets.remove(i);
            }
        }

        float minheight = getFloorHeight(camera.position.x, camera.position.z) + 3;
        if (speed < 10 && downspeed < 100) {
            downspeed += flyDelta * 10;
        } else {
            downspeed = 0;
        }
        camera.translate(0, -downspeed*flyDelta, 0);

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            speed += flyDelta * 2;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            speed -= flyDelta * 7;
        }
        speed = MathUtils.clamp(speed, 0, Consts.MAX_FLIGHT_SPEED);
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.rotate(camera.up, delta*40);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            camera.rotate(camera.up, -delta*40);
        }

        camera.position.x = MathUtils.clamp(camera.position.x, -Consts.FIELD_WIDTH*0.9f, Consts.FIELD_WIDTH*0.9f);
        camera.position.y = MathUtils.clamp(camera.position.y, minheight, 500);
        camera.position.z = MathUtils.clamp(camera.position.z, -Consts.FIELD_HEIGHT*0.9f, Consts.FIELD_HEIGHT*0.9f);

        camera.rotate(camera.direction, Gdx.input.getDeltaX() * delta * 20);
        vec3.set(camera.direction).crs(camera.up);
        camera.rotate(vec3, Gdx.input.getDeltaY() * delta * 20);

        vec3.set(camera.direction).scl(flyDelta*speed);
        camera.translate(vec3);

        camera.update();

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            spellCharge += delta;
            didBoom = false;
        } else {
            if (!didBoom && spellCharge > 1) {
                boom();
                didBoom = true;
            }
            spellCharge *= 0.8f;
        }

        /*
        if (MathUtils.randomBoolean(0.001f)) {
            addTree();
        }
        */
        if (MathUtils.randomBoolean(0.01f)) {
            addAnimal();
        }
    }

    @Override
    public void render(float delta) {
        update(delta);

        frameBuffer.begin();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth() * Consts.ANTIALIASING, Gdx.graphics.getHeight() * Consts.ANTIALIASING);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        modelBatch.begin(camera);
        modelBatch.render(test1);
        for (Tree tree : trees) {
            modelBatch.render(tree.modelInstance);
        }
        for (Animal animal : animals) {
            modelBatch.render(animal.modelInstance);
        }
        for (Bullet bullet : bullets) {
            modelBatch.render(bullet.modelInstance);
        }
        modelBatch.render(sun);
        modelBatch.render(moon);
        modelBatch.end();

        frameBuffer.end();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.begin();
        postShader.setUniformf("u_time", time);
        postShader.setUniformf("u_wobble", 1f + spellCharge * 7f);
        postShader.setUniformf("u_speed", speed/25f + spellCharge * 0.3f);
        postShader.setUniformf("u_spell", spellCharge*0.1f);
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

        for (Model modelAnimal : modelAnimals) {
            modelAnimal.dispose();
        }
    }

    public List<Animal> getAnimals() {
        return animals;
    }

    public List<Tree> getTrees() {
        return trees;
    }
}
