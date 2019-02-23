package com.smeanox.games.aj05.world;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.collision.BoundingBox;

public class Tree {
    public final ModelInstance modelInstance;
    public final BoundingBox boundingBox;
    private float death;

    public Tree(Model model, float x, float y, float z) {
        this.modelInstance = new ModelInstance(model);
        this.modelInstance.transform.translate(x, y, z);
        this.boundingBox = new BoundingBox();
        this.modelInstance.calculateBoundingBox(boundingBox);
        death = 0;
    }

    public void update(float delta) {
        if (death > 1) {
            death += delta;
        }
    }

    public float getDeath() {
        return death;
    }

    public void setDeath(float death) {
        this.death = death;
        this.modelInstance.materials.get(0).set(ColorAttribute.createDiffuse(death, 0, 0, 1));
    }
}
