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

uniform float u_time;

varying vec3 v_position;
varying vec4 v_positionGlobal;
varying vec3 v_normal;
varying vec2 v_texCoord0;

uniform vec4 u_diffuseColor;

const float gridsize = 0.12;

float grid_dist(float val) {
    return smoothstep(0.5-gridsize, 0.5, abs(val - 0.5));
}

void main() {
    vec3 posmod = fract(v_position);
    float grid = 0.0;
    if (abs(v_normal.x) < 1.0-gridsize) {
        grid = max(grid, grid_dist(posmod.x));
    }
    if (abs(v_normal.y) < 1.0-gridsize) {
        grid = max(grid, grid_dist(posmod.y));
    }
    if (abs(v_normal.z) < 1.0-gridsize) {
        grid = max(grid, grid_dist(posmod.z));
    }

    grid = max(grid, grid_dist(v_texCoord0.x));
    grid = max(grid, grid_dist(v_texCoord0.y));


    gl_FragColor.rgb = mix(vec3(0.0, 0.0, 0.2), vec3(0.0, 1.0, 1.0), grid);
    gl_FragColor.a = 1.0;

    float highlight = 0.0;

    if (length(v_positionGlobal) < 150.0) {
        highlight = max(highlight, (
            smoothstep(0.995, 1.0, sin((v_positionGlobal.x)*0.2 + 11.0 + u_time*0.01)) *
            smoothstep(0.98, 1.0, sin((v_positionGlobal.z + u_time*17.0 - floor(v_positionGlobal.x*0.1)*33.0)*0.1 + 7.0))
        ));

        highlight = max(highlight, (
            smoothstep(0.995, 1.0, sin((v_positionGlobal.x)*0.2 + 77.0 + u_time*0.012)) *
            smoothstep(0.98, 1.0, sin((v_positionGlobal.z*0.23 - u_time*7.0 - floor(v_positionGlobal.x*0.1)*37.0)*0.1 + 7.0))
        ));

        highlight = max(highlight, (
            smoothstep(0.995, 1.0, sin((v_positionGlobal.z)*0.2 + 11.0 + u_time*0.011)) *
            smoothstep(0.98, 1.0, sin((v_positionGlobal.x + u_time*9.0 - floor(v_positionGlobal.z*0.1)*35.0)*0.1 + 7.0))
        ));

        highlight = max(highlight, (
            smoothstep(0.995, 1.0, sin((v_positionGlobal.z)*0.2 + 77.0 + u_time*0.013)) *
            smoothstep(0.98, 1.0, sin((v_positionGlobal.x*0.23 - u_time*11.0 - floor(v_positionGlobal.z*0.1)*31.0)*0.1 + 7.0))
        ));
    }

    gl_FragColor.r = grid * highlight;
    if (gl_FragColor.r > 0.5) {
        gl_FragColor.bg *= 0.3;
    }

    gl_FragColor.rgb *= sin(u_time*0.1)*0.05+0.95;

    if (u_diffuseColor.r > 0.2) {
        gl_FragColor.rgb = clamp(gl_FragColor.rgb, 0, sin(u_time*4.0*u_diffuseColor.r)*0.5+0.5);
    }
}
