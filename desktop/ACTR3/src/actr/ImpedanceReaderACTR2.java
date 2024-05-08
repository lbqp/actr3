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

import actr.serial.ComandosACTR;
import actr.serial.Signal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 *
 * @author gustavo_vrr
 */
public class ImpedanceReaderACTR2 implements ImpedanceReader {

    private final static double MAX_IMPEDANCIA = 500;
    private final static double MIN_IMPEDANCIA = 30;
    private final static double R_REF=100;

    
    @Override
    public Leitura read(Source s, Well w, double seconds) {

        ArrayList<Double> lista_v_r=  new ArrayList<>();
        ArrayList<Double> lista_v_er=  new ArrayList<>();

        
        Leitura l = new Leitura();
        l.dataHora = new Date();
        ComandosACTR c = ComandosACTR.getSingleTon();
        if(!c.isConnected())
        {
            l.statusLeitura = StatusLeitura.NaoConectado;
            return l;
        }
        
        boolean canReadNext;
        Date start = new Date();
        
        c.selectSource(s);
        c.selectWell(w);

        
        do
        {
            
            c.selectSignal(Signal.SIGNAL_E);
            c.releaseSample();
            c.holdSample();



            //primeiro Vr
            c.selectSignal(Signal.SIGNAL_V_R);
            int valor = c.makeADConversion();
            double vr = valor/1023.0;

            //depois v_er
            c.selectSignal(Signal.SIGNAL_V_ER);
            valor = c.makeADConversion();
            double ver = valor/1023.0;

            lista_v_r.add(vr);
            lista_v_er.add(ver);
            
            double timeByInteration = (new Date().getTime() - start.getTime())/lista_v_er.size();
            double prevision = timeByInteration * (lista_v_er.size()+1);
            
            canReadNext = prevision <  (seconds*1000);
                
        }while(canReadNext);
        
        //double vr = getMediana(lista_v_r);
        //double ver = getMediana(lista_v_er);
        double vr = getMedia(lista_v_r);
        double ver = getMedia(lista_v_er);
        
        
        l.v_r = vr;
        l.v_er = ver;
                
        l.impedancia = R_REF*ver/vr;
        l.numSamples = lista_v_er.size();
        if(l.impedancia < MIN_IMPEDANCIA)
            l.statusLeitura = StatusLeitura.CurtoCircuito;
        if(l.impedancia > MAX_IMPEDANCIA)
            l.statusLeitura = StatusLeitura.CircuitoAberto;
        else
            l.statusLeitura = StatusLeitura.Normal;
        
        return l;
        
    }

    @Override
    public void start() {
        ComandosACTR c = ComandosACTR.getSingleTon();
        if(!c.isConnected())
            c.connect();
        c.energizar();
    }

    @Override
    public void end() {
        ComandosACTR c = ComandosACTR.getSingleTon();
        c.desenergizar();
        c.disconnect();
    }


    private double getMediana(ArrayList<Double> lista) {
        Collections.sort(lista);
        double mediana;
        if(lista.size() % 2 == 0) //par
        {
            mediana = lista.get(lista.size()/2) + lista.get(lista.size()/2-1);
            mediana = mediana/2;
        }
        else
        {
            mediana = lista.get(lista.size()/2);
        }
        return mediana;
            
    }
    
    private double getMedia(ArrayList<Double> lista)
    {
        ArrayList<Double> l = (ArrayList<Double>) lista.clone();
        for (int i = l.size() - 1; i >= 0; i--) {
            if (Double.isNaN(l.get(i))) {
                l.remove(i);
            }
        }
        
        if(l.isEmpty()) return Double.NaN;
        
        double m = media(lista);
        double dp = desvpad(lista, m);
        
        boolean deveRecalcular = false;
        for (int i = l.size() - 1; i >= 0; i--) {
            double dif = Math.abs( l.get(i) - m );
            if ( dif > 2.5*dp ) {
                l.remove(i);
                deveRecalcular = true;
            }
        }
        
        if(deveRecalcular)
            return getMedia(l);
        else
            return m;
        
    }
    
    private double media(ArrayList<Double> lista)
    {
        double r=0;
        for(Double d: lista)
        {
            r+=d;
        }
        r=r/lista.size();
        return r;
    }
    
    private double desvpad(ArrayList<Double> lista, double media)
    {
        double r=0;
        for(Double d: lista)
        {
            r += (d-media)*(d-media);
        }
        r = r/lista.size();
        r = Math.sqrt(r);
        return r;
    }


    @Override
    public Leitura read(Well w, double seconds, Calibrador calibrador) {
        Leitura l = read(Source.SourceWell, w, seconds);
        l.impedancia = calibrador.corrigirZ(l.impedancia);
        return l;
    }

    
}
