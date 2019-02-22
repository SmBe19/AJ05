#ifdef GL_ES
#define LOWP lowp
#define MED mediump
#define HIGH highp
precision mediump float;
#else
#define MED
#define LOWP
#define HIGH
#endif

varying vec3 v_position;
varying vec3 v_normal;
varying vec2 v_texCoord0;

const float gridsize = 0.12;

float grid_dist(float val) {
    return smoothstep(0.5-gridsize, 0.5, abs(val - 0.5));
}

void main() {
    vec3 posmod = fract(v_position);
    float grid = 0.0;
    if (abs(v_normal.x) < 1-gridsize) {
        grid = max(grid, grid_dist(posmod.x));
    }
    if (abs(v_normal.y) < 1-gridsize) {
        grid = max(grid, grid_dist(posmod.y));
    }
    if (abs(v_normal.z) < 1-gridsize) {
        grid = max(grid, grid_dist(posmod.z));
    }

    grid = max(grid, grid_dist(v_texCoord0.x));
    grid = max(grid, grid_dist(v_texCoord0.y));

    gl_FragColor.rgb = mix(vec3(0.0, 0.0, 0.2), vec3(0.0, 1.0, 1.0), grid);
    gl_FragColor.a = 1.0;
}
