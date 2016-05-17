#pragma version(1)
#pragma rs java_package_name(com.framgia.photoalbum)

#include "Clamp.rsh"

rs_allocation gIn;
rs_allocation gOut;

rs_script gScript;

static uint32_t Model = 30;

static uint32_t _width;
static uint32_t _height;

static void setup() {
	_width = rsAllocationGetDimX(gIn);
	_height = rsAllocationGetDimY(gIn);
}

void filter() {
	setup();
    rsForEach(gScript, gIn, gOut, 0, 0);
}

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
	float4 f4 = rsUnpackColor8888(*v_in);
	
	int32_t pos = rsRand(1, 10000) % Model;
    int32_t theX = (x + pos) < _width ? (x + pos) : ((int)x - pos) >= 0 ? (x - pos) : x;
    int32_t theY = (y + pos) < _height ? (y + pos) : ((int)y - pos) >= 0 ? (y - pos) : y;
    
    float4 theF4 = rsUnpackColor8888(*(const uchar4*)rsGetElementAt(gIn, theX, theY));
    
    *v_out = rsPackColorTo8888(theF4.rgb);
}