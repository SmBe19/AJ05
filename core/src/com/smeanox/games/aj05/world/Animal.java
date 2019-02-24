package com.smeanox.games.aj05.world;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.smeanox.games.aj05.Consts;
import com.smeanox.games.aj05.screen.GameScreen;

public class Animal {

    public final GameScreen gameScreen;
    public final ModelInstance modelInstance;
    public final BoundingBox boundingBox;

    private Vector3 vec3;
    private float destX, destZ;
    private float jumpProgress;
    private float jumpSpeed, jumpHeight, walkSpeed;
    private float death;

    public Animal(GameScreen gameScreen, Model model, float speed) {
        this.gameScreen = gameScreen;
        modelInstance = new ModelInstance(model);
        float x = MathUtils.random(-Consts.FIELD_WIDTH * 0.9f, Consts.FIELD_WIDTH * 0.9f);
        float z = MathUtils.random(-Consts.FIELD_HEIGHT * 0.9f, Consts.FIELD_HEIGHT * 0.9f);
        modelInstance.transform.translate(x, gameScreen.getFloorHeight(x, z) + 1, z);
        boundingBox = new BoundingBox();

        destX = x;
        destZ = z;
        jumpSpeed = MathUtils.random(0.1f, 5f);
        jumpHeight = MathUtils.random(0.1f, 5f);
        walkSpeed = MathUtils.random(1f, 7f) * speed;

        death = 0;

        vec3 = new Vector3();
    }

    public void update(float delta) {
        modelInstance.transform.getTranslation(vec3);
        float dx = destX - vec3.x;
        float dz = destZ - vec3.z;
        if (dx*dx + dz*dz < 20 * walkSpeed) {
            destX = MathUtils.clamp(vec3.x + MathUtils.random(-30f * walkSpeed, 30f * walkSpeed), -Consts.FIELD_WIDTH * 0.9f, Consts.FIELD_WIDTH * 0.9f);
            destZ = MathUtils.clamp(vec3.z + MathUtils.random(-30f * walkSpeed, 30f * walkSpeed), -Consts.FIELD_HEIGHT * 0.9f, Consts.FIELD_HEIGHT * 0.9f);
            dx = destX - vec3.x;
            dz = destZ - vec3.z;
            modelInstance.transform.setToRotationRad(0, 1, 0, MathUtils.atan2(dx, dz));
            modelInstance.transform.setTranslation(vec3);
        } else {
            float floor = gameScreen.getFloorHeight(vec3.x, vec3.z);
            jumpProgress += delta * jumpSpeed;
            modelInstance.transform.translate(0, floor + (MathUtils.sin(jumpProgress)*0.5f+0.5f) * jumpHeight + 1 - vec3.y, 0);
            modelInstance.transform.translate(0, 0, delta*walkSpeed);
        }
        modelInstance.calculateTransforms();
        modelInstance.calculateBoundingBox(boundingBox);

        if (death >= 1) {
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
