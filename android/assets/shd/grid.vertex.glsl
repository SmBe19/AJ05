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

void main() {
    v_position = a_position;
    vec4 pos = u_worldTrans * vec4(a_position, 1.0);
    v_positionGlobal = pos;

    v_normal = a_normal;
    v_texCoord0 = a_texCoord0;

	gl_Position = u_projViewTrans * pos;
}