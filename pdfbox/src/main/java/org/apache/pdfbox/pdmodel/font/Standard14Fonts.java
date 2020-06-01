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

package org.apache.pdfbox.pdmodel.font;

import org.apache.fontbox.afm.AFMParser;
import org.apache.fontbox.afm.FontMetrics;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * The "Standard 14" PDF fonts, also known as the "base 14" fonts.
 * There are 14 font files, but Acrobat uses additional names for compatibility, e.g. Arial.
 *
 * @author John Hewson
 */
final class Standard14Fonts
{
    private static final Map<String, String> ALIASES = new HashMap<>(34);
    private static final Map<String, FontMetrics> FONTS =  new HashMap<>(34);
    static
    {
        // the 14 standard fonts
        mapName("Courier-Bold");
        mapName("Courier-BoldOblique");
        mapName("Courier");
        mapName("Courier-Oblique");
        mapName("Helvetica");
        mapName("Helvetica-Bold");
        mapName("Helvetica-BoldOblique");
        mapName("Helvetica-Oblique");
        mapName("Symbol");
        mapName("Times-Bold");
        mapName("Times-BoldItalic");
        mapName("Times-Italic");
        mapName("Times-Roman");
        mapName("ZapfDingbats");

        // alternative names from Adobe Supplement to the ISO 32000
        mapName("CourierCourierNew", "Courier");
        mapName("CourierNew", "Courier");
        mapName("CourierNew,Italic", "Courier-Oblique");
        mapName("CourierNew,Bold", "Courier-Bold");
        mapName("CourierNew,BoldItalic", "Courier-BoldOblique");
        mapName("Arial", "Helvetica");
        mapName("Arial,Italic", "Helvetica-Oblique");
        mapName("Arial,Bold", "Helvetica-Bold");
        mapName("Arial,BoldItalic", "Helvetica-BoldOblique");
        mapName("TimesNewRoman", "Times-Roman");
        mapName("TimesNewRoman,Italic", "Times-Italic");
        mapName("TimesNewRoman,Bold", "Times-Bold");
        mapName("TimesNewRoman,BoldItalic", "Times-BoldItalic");

        // Acrobat treats these fonts as "standard 14" too (at least Acrobat preflight says so)
        mapName("Symbol,Italic", "Symbol");
        mapName("Symbol,Bold", "Symbol");
        mapName("Symbol,BoldItalic", "Symbol");
        mapName("Times", "Times-Roman");
        mapName("Times,Italic", "Times-Italic");
        mapName("Times,Bold", "Times-Bold");
        mapName("Times,BoldItalic", "Times-BoldItalic");

        // PDFBOX-3457: PDF.js file bug864847.pdf
        mapName("ArialMT", "Helvetica");
        mapName("Arial-ItalicMT", "Helvetica-Oblique");
        mapName("Arial-BoldMT", "Helvetica-Bold");
        mapName("Arial-BoldItalicMT", "Helvetica-BoldOblique");
    }
    
    private Standard14Fonts()
    {
    }

    /**
     * Loads the metrics for the font specified by name.
     * Metric file must exist in the pdfbox jar under /org/apache/pdfbox/resources/afm/
     * @param fontName one of the standard 14 font names for which to lod the metrics.
     */
    private static void loadMetrics(String fontName)
    {
        String resourceName = "/org/apache/pdfbox/resources/afm/" + fontName + ".afm";
        try ( InputStream afmStream = new BufferedInputStream(PDType1Font.class.getResourceAsStream(resourceName)) )
        {

            AFMParser parser = new AFMParser(afmStream);
            FontMetrics metric = parser.parse(true);
            FONTS.put(fontName, metric);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Adds a standard font name to the map of known aliases, to simplify the logic of finding font metrics by name.
     * @see #getAFM
     * @param baseName the base name of the font; must be one of the 14 standard fonts
     */
    private static void mapName(String baseName)
    {
        ALIASES.put(baseName, baseName);
    }

    /**
     * Adds an alias name for a standard font to the map of known aliases to the map of aliases
     * (alias as key, standard name as value).
     * @param alias an alias for the font
     * @param baseName the base name of the font; must
     */
    private static void mapName(String alias, String baseName)
    {
        ALIASES.put(alias, baseName);
    }

    /**
     * Returns the metrics for font specified by the given name.
     * Loads the font is not already loaded.
     * @param fontName name of font; can be one base name or an alias.
     */
    public static FontMetrics getAFM(String fontName) throws IllegalArgumentException
    {
        String baseName = ALIASES.get(fontName);
        if (baseName == null)
        {
            return null;
        }

        FontMetrics fontMetrics = FONTS.get(baseName);
        if (fontMetrics == null)
        {
            loadMetrics(baseName);
        }

        return FONTS.get(baseName);
    }

    /**
     * Returns true if the given font name a Standard 14 font.
     * @param baseName base name of font
     */
    public static boolean containsName(String baseName)
    {
        return ALIASES.containsKey(baseName);
    }

    /**
     * Returns the set of Standard 14 font names, including additional names.
     */
    public static Set<String> getNames()
    {
        return Collections.unmodifiableSet(ALIASES.keySet());
    }

    /**
     * Returns the name of the actual font which the given font name maps to.
     * @param baseName base name of font
     */
    public static String getMappedFontName(String baseName)
    {
        return ALIASES.get(baseName);
    }
}
