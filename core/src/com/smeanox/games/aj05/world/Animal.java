package com.smeanox.games.aj05.world;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.smeanox.games.aj05.screen.GameScreen;

public class Animal {

    public final GameScreen gameScreen;
    public final ModelInstance modelInstance;

    private Vector3 vec3;
    private float destX, destZ;
    private float jumpProgress;
    private float jumpSpeed, jumpHeight, walkSpeed;

    public Animal(GameScreen gameScreen, Model model) {
        this.gameScreen = gameScreen;
        modelInstance = new ModelInstance(model);
        float x = MathUtils.random(-90f, 90f);
        float z = MathUtils.random(-90f, 90f);
        modelInstance.transform.translate(x, gameScreen.getFloorHeight(x, z) + 1, z);

        destX = x;
        destZ = z;
        jumpSpeed = MathUtils.random(0.1f, 5f);
        jumpHeight = MathUtils.random(0.1f, 5f);
        walkSpeed = MathUtils.random(1f, 7f);

        vec3 = new Vector3();
    }

    public void update(float delta) {
        modelInstance.transform.getTranslation(vec3);
        float dx = destX - vec3.x;
        float dz = destZ - vec3.z;
        if (dx*dx + dz*dz < 20) {
            destX = vec3.x + MathUtils.random(-50f, 50f);
            destZ = vec3.z + MathUtils.random(-50f, 50f);
            Vector3 dest = new Vector3(destX, 0, destZ);
            Vector3 cur = new Vector3(vec3);
            cur.y = 0;
            modelInstance.transform.setToLookAt(cur, dest, Vector3.Y);
        } else {
            float floor = gameScreen.getFloorHeight(vec3.x, vec3.z);
            jumpProgress += delta * jumpSpeed;
            modelInstance.transform.translate(0, floor + (MathUtils.sin(jumpProgress)*0.5f+0.5f) * jumpHeight + 1 - vec3.y, 0);
            vec3.set(dx, 0, dz).nor().scl(delta*walkSpeed);
            modelInstance.transform.translate(vec3);
        }
        modelInstance.calculateTransforms();
    }
}
