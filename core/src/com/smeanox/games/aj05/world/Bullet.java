package com.smeanox.games.aj05.world;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.smeanox.games.aj05.screen.GameScreen;

public class Bullet {

    private GameScreen gameScreen;
    public final ModelInstance modelInstance;
    public final Vector3 direction;
    private Vector3 vec3, vec33, vec333;

    public boolean dead;

    private float rotationSpeed;

    public final float angle = 3f;

    // TODO fix bullets!

    public Bullet(GameScreen gameScreen, Model model, Vector3 position, Vector3 direction, float power) {
        this.gameScreen = gameScreen;
        modelInstance = new ModelInstance(model);
        model.materials.get(0).set(ColorAttribute.createDiffuse(0.7f, 0, 0, 1));
        modelInstance.transform.translate(position);
        this.direction = new Vector3(direction);
        this.direction.rotate(MathUtils.random(-angle*power, angle*power), 0, 1, 0);
        this.direction.rotate(MathUtils.random(-angle*power, angle*power), 1, 0, 0);
        this.direction.rotate(MathUtils.random(-angle*power, angle*power), 0, 0, 1);
        vec3 = new Vector3(this.direction);
        vec33 = new Vector3();
        vec333 = new Vector3();
        modelInstance.transform.translate(vec3.scl(10));

        dead = false;
        rotationSpeed = MathUtils.random(-100f, 100f);
    }

    public void update(float delta) {
        modelInstance.transform.translate(vec3.set(direction).scl(delta * 124));
        modelInstance.transform.getTranslation(vec3);
        if (vec3.len2() > 10000 || vec3.y < -10) {
            dead = true;
            return;
        }

        for (Animal animal : gameScreen.getAnimals()) {
            animal.modelInstance.transform.getTranslation(vec33);
            if (animal.boundingBox.contains(vec333.set(vec3).sub(vec33))) {
                dead = true;
                animal.setDeath(animal.getDeath() + 0.5f);
                return;
            }
        }

        for (Tree tree : gameScreen.getTrees()) {
            tree.modelInstance.transform.getTranslation(vec33);
            if (tree.boundingBox.contains(vec333.set(vec3).sub(vec33))) {
                dead = true;
                tree.setDeath(tree.getDeath() + 0.1f);
                return;
            }
        }
    }

}
