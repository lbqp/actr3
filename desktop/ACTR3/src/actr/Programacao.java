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


public class Programacao  implements Serializable{

    private static final long serialVersionUID = 4542764757825873980L;


    private final int qtdVarreduras;
    private final int periodoMinutes;
    private final Varredura[] varreduras;
    
    private final Agenda parent;
    
    public Programacao(Agenda parent, int qtdVarreduras, int periodoMinutes)
    {
        this.periodoMinutes = periodoMinutes;
        this.qtdVarreduras = qtdVarreduras;
        varreduras = new Varredura[qtdVarreduras];
        for(int i=0; i<qtdVarreduras; i++)
            varreduras[i] = new Varredura(this);
        
        this.parent = parent;
    }

    public Agenda getParent() {
        return parent;
    }
    
    
    
    public Varredura getVarredura(int i)
    {
        return varreduras[i];
    }
    
    public int getQtdVarreruras()
    {
        return qtdVarreduras;
    }
    
    public int getPeriodoMinutes()
    {
        return periodoMinutes;
    }
    
    @Override
    public String toString()
    {
        return "" + qtdVarreduras + " leituras separadas por " + periodoMinutes + " minuto(s)";
    }
}
