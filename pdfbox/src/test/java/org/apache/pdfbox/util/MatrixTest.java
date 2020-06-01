/*
 * Copyright 2015 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pdfbox.util;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSFloat;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Neil McErlean
 * @author Tilman Hausherr
 */
public class MatrixTest
{
    @Test
    public void testConstructionAndCopy()
    {
        Matrix m1 = new Matrix();
        assertMatrixIsPristine(m1);

        Matrix m2 = m1.clone();
        assertNotSame(m1, m2);
        assertMatrixIsPristine(m2);
    }

    @Test
    public void testMultiplication()
    {
        // These matrices will not change - we use it to drive the various multiplications.
        final Matrix const1 = new Matrix();
        final Matrix const2 = new Matrix();

        // Create matrix with values
        // [ 0, 1, 2
        // 1, 2, 3
        // 2, 3, 4]
        for (int x = 0; x < 3; x++)
        {
            for (int y = 0; y < 3; y++)
            {
                const1.setValue(x, y, x + y);
                const2.setValue(x, y, 8 + x + y);
            }
        }

        float[] m1MultipliedByM1 = new float[] { 5,  8,  11,  8, 14, 20, 11, 20,  29 };
        float[] m1MultipliedByM2 = new float[] { 29, 32, 35, 56, 62, 68, 83, 92, 101 };
        float[] m2MultipliedByM1 = new float[] { 29, 56, 83, 32, 62, 92, 35, 68, 101 };

        Matrix var1 = const1.clone();
        Matrix var2 = const2.clone();

        // Multiply two matrices together producing a new result matrix.
        Matrix result = var1.multiply(var2);
        assertArrayEquals(const1.single, var1.single, 0);
        assertArrayEquals(const2.single, var2.single, 0);
        assertArrayEquals(m1MultipliedByM2, result.single, 0);

        // Multiply two matrices together with the result being written to a third matrix
        // (Any existing values there will be overwritten).
        result = var1.multiply(var2);
        assertArrayEquals(const1.single, var1.single, 0);
        assertArrayEquals(const2.single, var2.single, 0);
        assertArrayEquals(m1MultipliedByM2, result.single, 0);

        // Multiply two matrices together with the result being written into 'this' matrix
        var1 = const1.clone();
        var2 = const2.clone();
        var1.concatenate(var2);
        assertArrayEquals(const2.single, var2.single, 0);
        assertArrayEquals(m2MultipliedByM1, var1.single, 0);

        var1 = const1.clone();
        var2 = const2.clone();
        result = Matrix.concatenate(var1, var2);
        assertArrayEquals(const1.single, var1.single, 0);
        assertArrayEquals(const2.single, var2.single, 0);
        assertArrayEquals(m2MultipliedByM1, result.single, 0);

        // Multiply the same matrix with itself with the result being written into 'this' matrix
        var1 = const1.clone();
        result = var1.multiply(var1);
        assertArrayEquals(const1.single, var1.single, 0);
        assertArrayEquals(m1MultipliedByM1, result.single, 0);
    }

    /**
     * Test of PDFBOX-2872 bug
     */
    @Test
    public void testPdfbox2872()
    {
        Matrix m = new Matrix(2, 4, 5, 8, 2, 0);
        COSArray toCOSArray = m.toCOSArray();
        assertEquals(new COSFloat(2), toCOSArray.get(0));
        assertEquals(new COSFloat(4), toCOSArray.get(1));
        assertEquals(new COSFloat(5), toCOSArray.get(2));
        assertEquals(new COSFloat(8), toCOSArray.get(3));
        assertEquals(new COSFloat(2), toCOSArray.get(4));
        assertEquals(new COSFloat(0), toCOSArray.get(5));
        
    }

    /**
     * This method asserts that the matrix values for the given {@link Matrix} object are equal to the pristine, or
     * original, values.
     * 
     * @param m the Matrix to test.
     */
    private void assertMatrixIsPristine(Matrix m)
    {
        assertArrayEquals(new float[] { 1, 0, 0, 0, 1, 0, 0, 0, 1 }, m.single, 0);
    }
}
