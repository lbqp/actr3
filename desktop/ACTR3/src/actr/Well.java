/*
 * The MIT License
 *
 * Copyright 2024 Laboratorio de Bioquímica e Química de Proteínas / 
 * Instituto de Ciências Biológicas / Universidade de Brasília
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package actr;



public enum Well {
    WELL_A1                   (0x0),
    WELL_B1                   (0x1),
    WELL_C1                   (0x2),
    WELL_D1                   (0x3),
    WELL_E1                   (0x4),
    WELL_F1                   (0x5),
    WELL_G1                   (0x6),
    WELL_H1                   (0x7),

    WELL_A2                   (0x8),
    WELL_B2                   (0x9),
    WELL_C2                   (0xA),
    WELL_D2                   (0xB),
    WELL_E2                   (0xC),
    WELL_F2                   (0xD),
    WELL_G2                   (0xE),
    WELL_H2                   (0xF);


    public final int value;   
    Well(int value) {
        this.value = value;
    }
    public int value() { return value; }

    public String getShortName()
    {
        switch (this) {
            case WELL_A1: return "A1";
            case WELL_B1: return "B1";
            case WELL_C1: return "C1";
            case WELL_D1: return "D1";
            case WELL_E1: return "E1";
            case WELL_F1: return "F1";
            case WELL_G1: return "G1";
            case WELL_H1: return "H1";

            case WELL_A2: return "A2";
            case WELL_B2: return "B2";
            case WELL_C2: return "C2";
            case WELL_D2: return "D2";
            case WELL_E2: return "E2";
            case WELL_F2: return "F2";
            case WELL_G2: return "G2";
            case WELL_H2: return "H2";
            

            default:
                throw new AssertionError();
        }
    }
    
}