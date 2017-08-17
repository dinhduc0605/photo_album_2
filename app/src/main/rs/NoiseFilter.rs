#pragma version(1)
#pragma rs java_package_name(com.framgia.photoalbum)

#include "Clamp.rsh"

rs_allocation gIn;
rs_allocation gOut;

rs_script gScript;

static const float Intensity = 0.2f;

void filter() {
    rsForEach(gScript, gIn, gOut);	// for each element of the input allocation,
    										// call root() method on gScript
}

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
	float4 f4 = rsUnpackColor8888(*v_in);	// extract RGBA values
	float3 f3;
	
    if (Intensity != 0) {
        f3.r = f4.r + rsRand(-1.0f, 1.0f) * Intensity;
        f3.g = f4.g + rsRand(-1.0f, 1.0f) * Intensity;
        f3.b = f4.b + rsRand(-1.0f, 1.0f) * Intensity;
        f3 = FClamp01Float3(f3);
    } else {
    	f3 = f4.rgb;
    }
    
    *v_out = rsPackColorTo8888(f3);
}