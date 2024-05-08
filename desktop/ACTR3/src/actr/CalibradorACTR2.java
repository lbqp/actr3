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

/**
 *
 * @author gustavo_vrr
 */
public class CalibradorACTR2 implements Calibrador {

    protected static final double SECONDS_PER_WELL = 110.0/24.0; //1 minuto e 50 segundos para 24 poços (16 well + 8 calibracao)
    private final double calKnownImpedances[] = new double[]{57 ,100 , 122,150 ,180 , 240, 330, 390 };
    private final double calMeasImpedances[] = new double[]{57, 100, 122, 150, 180, 240, 330, 390 };
    private final Well[] wellList = {Well.WELL_A1,Well.WELL_B1,Well.WELL_C1,Well.WELL_D1,
                                Well.WELL_E1, Well.WELL_F1, Well.WELL_G1, Well.WELL_H1};
    
    @Override
    public void calibrar() {
        ImpedanceReader reader = ImpedanceReaderFactory.getImpedanceReader();
        
        for(int i=0; i<wellList.length; i++)
        {
            Leitura l = reader.read(Source.SourceCall, wellList[i], SECONDS_PER_WELL);
            calMeasImpedances[i]=l.impedancia;
        }
        

    }

    @Override
    public double corrigirZ(double z_medido) {
        //achar i1 e i2
        int i1=0, i2=0;
        
        if( z_medido <= calMeasImpedances[0] )
        {
            i1 = 0;
            i2 = 1;
        }
        else if( z_medido >= calMeasImpedances[7])
        {
            i1 = 6;
            i2 = 7;
        }
        else
        {
            int i;
            for(i=0; i<7; i++)
            {
                if(z_medido >=calMeasImpedances[i] && z_medido < calMeasImpedances[i+1])
                {
                    i1 = i;
                    i2 = i+1;
                    break;
                }
            }
            if(i==7) return Double.NaN;
        }
        
        return (calKnownImpedances[i2]-calKnownImpedances[i1])/(calMeasImpedances[i2]-calMeasImpedances[i1]) * ( z_medido -  calMeasImpedances[i1]) + calKnownImpedances[i1];
        
    }
    
}
