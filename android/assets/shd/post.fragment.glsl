#version 120
#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying vec2 v_texCoords;
varying LOWP vec4 v_color;

uniform sampler2D u_texture;
uniform float u_time;
uniform float u_wobble = 1.0;
uniform float u_speed = 0.0;

void main(void) {
  vec2 coord = v_texCoords;
  vec2 toCenter = coord - vec2(0.5, 0.5);
  float centerLength = length(toCenter);
  coord -= toCenter * centerLength * u_speed * 0.5;
  float wobble = u_wobble * ((sin(u_time*0.1)*0.5+0.5) + 0.5);
  coord.x += sin(u_time*3.141+v_texCoords.y*3.141*4.0)*0.0002*wobble;
  coord.y += sin(u_time*3.141*0.5+v_texCoords.x*3.141*8.0)*0.001*wobble;
  gl_FragColor = v_color * texture2D(u_texture, coord);
  gl_FragColor *= 1.5 - 2.0*length(v_texCoords - vec2(0.5, 0.5));

  gl_FragColor.r += smoothstep(0.5, 1.0, u_speed) * 0.12;
}
