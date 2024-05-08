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

import static actr.CalibradorACTR2.SECONDS_PER_WELL;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.TimerTask;

public class ExecutaVarredura extends TimerTask implements Serializable {

    private static final long serialVersionUID = 8542794758825973281L;
    
    private final Varredura varredura;
    private final ArrayList<TimerVarredura> timers;
    
    public ExecutaVarredura(Varredura varredura, ArrayList<TimerVarredura> timers)
    {
        this.varredura = varredura;
        this.timers = timers;
    }
    
    @Override
    public void run() {
        ImpedanceReader reader = ImpedanceReaderFactory.getImpedanceReader();
        Calibrador calibrador = new CalibradorACTR2();
        
        reader.start();
        calibrador.calibrar();
        for(Well well: Well.values())
        {
            Leitura leitura = reader.read(well, SECONDS_PER_WELL, calibrador);
            varredura.setLeitura(well, leitura);
        }
        reader.end();
        
        Ensaio.removeOldTimers(timers);
        
        Ensaio ensaio = varredura.getParent().getParent().getParent();
        
        
        if(ensaio.getListener() != null)
            ensaio.getListener().varreduraRealizada(ensaio);
        
        
        if(timers.isEmpty())
            ensaio.finalizarEnsaio();
        
    }
    
}
