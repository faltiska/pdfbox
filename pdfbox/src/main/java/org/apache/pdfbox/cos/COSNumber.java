/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pdfbox.cos;

import java.io.IOException;

/**
 * This class represents an abstract number in a PDF document.
 *
 * @author Ben Litchfield
 */
public abstract class COSNumber extends COSBase
{
    /**
     * This will get the float value of this number.
     *
     * @return The float value of this object.
     */
    public abstract float floatValue();

    /**
     * This will get the integer value of this number.
     *
     * @return The integer value of this number.
     */
    public abstract int intValue();

    /**
     * This will get the long value of this number.
     *
     * @return The long value of this number.
     */
    public abstract long longValue();

    /**
     * This factory method will get the appropriate number object.
     *
     * @param number The string representation of the number.
     *
     * @return A number object, either float or int.
     *
     * @throws IOException If the string is not a number.
     */
    public static COSNumber get( String number ) throws IOException
    {
        int length = number.length();
        boolean isFloat = false;
        for (int i = 0; i < length; i++) {
            char c = number.charAt(i);
            if (c == '.' || c == 'e' || c == 'E') {
                isFloat = true;
                break;
            }
        }

        if (isFloat) {
            return new COSFloat(number);
        } else {
            try
            {
                return COSInteger.get(Long.parseLong(number));
            }
            catch( NumberFormatException e )
            {
                throw new IOException("Invalid number format: '" + number + "'", e);
            }
        }
    }
}
