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

varying vec2 v_texCoords;
varying LOWP vec4 v_color;

uniform sampler2D u_texture;
uniform float u_time;
uniform float u_wobble;
uniform float u_speed;
uniform float u_spell;
const float u_spell_rings = 5.0;
const float u_spell_count = 9.0;

void main(void) {
  vec2 coord = v_texCoords;
  vec2 toCenter = coord - vec2(0.5, 0.5);
  float centerLength = length(toCenter);
  coord -= toCenter * centerLength * u_speed * 0.5;
  float wobble = u_wobble * ((sin(u_time*0.1)*0.5+0.5) + 0.5);
  coord.x += sin(u_time*3.141+v_texCoords.y*3.141*4.0)*0.0002*wobble;
  coord.y += sin(u_time*3.141*0.5+v_texCoords.x*3.141*8.0)*0.001*wobble;
  gl_FragColor = vec4(0.0);
  for(int x = -2; x < 3; x++) {
    for(int y = -2; y < 3; y++) {
        gl_FragColor += v_color * texture2D(u_texture, coord + vec2(x, y)*0.0005);
    }
  }
  gl_FragColor /= 25.0;
  gl_FragColor *= 1.5 - 2.0*length(v_texCoords - vec2(0.5, 0.5));

  if (u_spell > 0.001) {
      float dist = 100.0;
      for(float j = 0.0; j < u_spell_rings; j++) {
          for(float i = 0.0; i < u_spell_count; i++) {
            dist = min(dist, length(coord - vec2(sin(u_time * (4.0-j*0.3) + i*6.282/u_spell_count), cos(u_time * (4.0-j*0.5) + i*6.282/u_spell_count)) * (0.05 + 0.05*j) * (u_spell * 4.0) - vec2(0.5, 0.45)));
          }
      }
      vec3 rainbow = vec3(sin(u_time * 7.0 + coord.x * 3.141 * 17.0), sin(u_time * 11.0 + coord.y * 3.141 * 17.0), sin(u_time * 17.0)) * vec3(0.5) + vec3(0.5);
      gl_FragColor.rgb += (vec3(1.0) - vec3(smoothstep(0.0, 0.02 * smoothstep(u_spell, 0.0, 0.01), dist))) * rainbow;
  }

  gl_FragColor.r += smoothstep(0.5, 1.0, u_speed) * 0.12;
}
