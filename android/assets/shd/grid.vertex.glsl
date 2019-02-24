uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;

attribute vec3 a_position;
varying vec3 v_position;
varying vec4 v_positionGlobal;

attribute vec3 a_normal;
uniform mat3 u_normalMatrix;
varying vec3 v_normal;

attribute vec2 a_texCoord0;
varying vec2 v_texCoord0;

uniform float u_time;
uniform vec4 u_diffuseColor;

void main() {
    v_position = a_position;
    vec4 pos = vec4(a_position, 1.0);

    if (u_diffuseColor.g > 0.8) {
        pos += vec4(a_normal, 0.0) * vec4(sin(a_position*u_time*3.141)*0.5+0.5, 0.0) * 2.0;
    } else if (u_diffuseColor.g > 0.1) {
        pos += vec4(a_normal, 0.0) * vec4(sin(u_time*3.141)*0.5+0.5) * 0.8 * u_diffuseColor.g;
    }

    pos = u_worldTrans * pos;

    v_positionGlobal = pos;

    v_normal = a_normal;
    v_texCoord0 = a_texCoord0;

	gl_Position = u_projViewTrans * pos;
}