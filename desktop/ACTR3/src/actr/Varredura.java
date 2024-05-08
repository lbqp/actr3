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

import static actr.Well.WELL_A1;
import static actr.Well.WELL_A2;
import static actr.Well.WELL_B1;
import static actr.Well.WELL_B2;
import static actr.Well.WELL_C1;
import static actr.Well.WELL_C2;
import static actr.Well.WELL_D1;
import static actr.Well.WELL_D2;
import static actr.Well.WELL_E1;
import static actr.Well.WELL_E2;
import static actr.Well.WELL_F1;
import static actr.Well.WELL_F2;
import static actr.Well.WELL_G1;
import static actr.Well.WELL_G2;
import static actr.Well.WELL_H1;
import static actr.Well.WELL_H2;
import java.io.Serializable;
import java.util.Date;


public class Varredura implements Serializable{
    
    private static final long serialVersionUID = 548767767855843980L;
    
    
    private final Leitura[] leituras = new Leitura[16];
    
    private Date scheduleTime;

    private final Programacao parent;
    
    public Varredura(Programacao parent)
    {
        this.parent = parent;
    }

    public Programacao getParent() {
        return parent;
    }
    
    
    
    public Date getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(Date scheduleTime) {
        this.scheduleTime = scheduleTime;
    }
    
    private int getIndex(Well well)
    {
        
        switch(well)
        {
            case WELL_A1: return 0;
            case WELL_B1: return 1;
            case WELL_C1: return 2;
            case WELL_D1: return 3;
            case WELL_E1: return 4;
            case WELL_F1: return 5;
            case WELL_G1: return 6;
            case WELL_H1: return 7;

            case WELL_A2: return 8;
            case WELL_B2: return 9;
            case WELL_C2: return 10;
            case WELL_D2: return 11;
            case WELL_E2: return 12;
            case WELL_F2: return 13;
            case WELL_G2: return 14;
            case WELL_H2: return 15;
            default: return -1;
        }
    }
    
    public Leitura getLeitura(Well well)
    {
        int index = getIndex(well);
        
        return leituras[index];
    }
    
    public void setLeitura(Well well, Leitura leitura)
    {
        int index = getIndex(well);
        
        leituras[index] = leitura;
        
    }
}
