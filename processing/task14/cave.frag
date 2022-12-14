#ifdef GL_ES
precision lowp float;
#endif

uniform sampler2D u_texture;
uniform vec2 u_resolution;

vec4 getFreeColour() {
    return vec4(0.0, 0.0, 0.0, 1.0);
}

vec4 getWallColour() {
    return vec4(1.0, 1.0, 1.0, 1.0);
}

vec4 getSandColour() {
    return vec4(246.0/255.0, 215.0/255.0, 176.0/255.0, 1.0);
}

vec4 getStationarySandColour() {
    return vec4(163.0/255.0, 59.0/255.0, 33.0/255.0, 1.0);
}

vec3 getPixel(float dx, float dy) {
    vec2 relative = (gl_FragCoord.xy + vec2(dx, dy)) / u_resolution;
    return texture2D(u_texture, vec2(relative.x, 1.0 - relative.y)).rgb;
}

bool isFree(float dx, float dy) {
    return getPixel(dx, dy) == getFreeColour().rgb;
}

bool isWall(float dx, float dy) {
    return getPixel(dx, dy) == getWallColour().rgb;
}

bool isSand(float dx, float dy) {
    return getPixel(dx, dy) == getSandColour().rgb || getPixel(dx, dy) == getStationarySandColour().rgb;
}

bool isOccupied(float dx, float dy) {
    return isWall(dx, dy) || isSand(dx, dy);
}

void main() {
    if (isFree(0.0, 0.0) && gl_FragCoord.y < u_resolution.y - 1) {
        if (isSand(0.0, 1.0)
        || (isOccupied(1.0, 0.0) && isSand(1.0, 1.0)) 
        || (isOccupied(-1.0, 0.0) && isOccupied(-2.0, 0.0) && isSand(-1.0, 1.0))) {
            gl_FragColor = getSandColour();
        } else {
            gl_FragColor = getFreeColour();
        }
    } else if (isSand(0.0, 0.0)) {
        if (isFree(0.0, -1.0)
        || isFree(-1.0, -1.0)
        || isFree(1.0, -1.0)) {
            gl_FragColor = getFreeColour();
        } else {
            gl_FragColor = getStationarySandColour();
        }
    } else if (isWall(0.0, 0.0)) {
        gl_FragColor = getWallColour();
    } else {
        gl_FragColor = vec4(getPixel(0.0, 0.0), 1.0);
    }
}
