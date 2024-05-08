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

import java.io.Serializable;
import java.util.Date;


public class TimerVarredura implements Serializable{
    private final Date date;
    private boolean cancelado = false;
    private final ExecutaVarredura executaVarredura;
    
    public TimerVarredura(Date date, ExecutaVarredura executaVarredura)
    {
        this.date = date;
        this.executaVarredura = executaVarredura;
        start();
        
    }
    
    protected void start()
    {
        (new Thread(new Runnable() {
            @Override
            public void run() {
                while(!cancelado)
                {
                    try {
                        Thread.sleep(1000, 0);

                    } catch (InterruptedException ex) {
                        
                    }
                    if(date.before(new Date()))
                    {
                        executaVarredura.run();
                        return;
                    }
                }
            }
        })).start();        
    }

    public void cancel() {
        cancelado = true;
    }

    public boolean isOld() {
        return date.before(new Date());
    }
}
