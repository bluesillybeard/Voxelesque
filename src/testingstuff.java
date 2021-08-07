public class testingstuff {
    public static void main(String[] args) {
        System.out.println(isInside(0.5f, 0f, 1f, 0f, 0.5f, 0.5f, 0.7f, 0.2f));
        System.out.println(isInside(0.5f, 0f, 1f, 0f, 0.5f, 0.5f, 0.7f, 0.3f));
        System.out.println(isInside(0.5f, 0f, 1f, 0f, 0.5f, 0.5f, 0.7f, 0.4f));

    }


    private static float triangleArea(float p1x, float p1y, float p2x, float p2y, float p3x, float p3y) {
        return (float) Math.abs((p1x*(p2y-p3y) + p2x*(p3y-p1y)+ p3x*(p1y-p2y))/2.0);
    }

    private static boolean isInside(float p1x, float p1y, float p2x, float p2y, float p3x, float p3y, float x, float y) {
        float area = triangleArea (p1x, p1y, p2x, p2y, p3x, p3y)/* + .0000177f*/;          ///area of triangle ABC //with a tiny bit of extra to avoid issues related to float precision errors
        float area1 = triangleArea (x, y, p2x, p2y, p3x, p3y);         ///area of PBC
        float area2 = triangleArea (p1x, p1y, x, y, p3x, p3y);         ///area of APC
        float area3 = triangleArea (p1x, p1y, p2x, p2y, x, y);        ///area of ABP

        return (area >= area1 + area2 + area3);        ///when three triangles are forming the whole triangle
        //I changed it to >= because floats cannot be trusted to hold perfectly accurate data,
        //same reason for the
    }
}
